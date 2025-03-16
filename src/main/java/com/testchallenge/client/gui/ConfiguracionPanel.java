
/*
 * This file is part of 'TestsChallenge' project.
 * 
 * 'TestChallenge' is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * 'TestChallenge' is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with 'TestChallenge'. If not, see <https://www.gnu.org/licenses/>.
 */

package com.testchallenge.client.gui;

import com.testchallenge.client.gui.combomultiseleccion.CheckableItem;
import com.testchallenge.client.gui.combomultiseleccion.CheckedComboBox;
import com.testchallenge.model.Mensaje;
import com.testchallenge.model.Nivel;
import com.testchallenge.model.Pregunta;
import com.testchallenge.model.TipoMensaje;
import com.testchallenge.model.TipoPregunta;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.FileUtils;

/**
 * Panel que contiene los componentes que permiten establecer la configuración de los tests y subir nuevas preguntas al
 * servidor.
 *
 * @author japrada
 */
public class ConfiguracionPanel extends ConectablePanel {

    // Selector para establecer la temática
    private final JComboBox<String> tematicaComboBox;
    // Selector para establecer el nivel
    private final JComboBox<String> nivelComboBox;
    // Selector para establecer el tipo de preguntas
    private final CheckedComboBox<CheckableItem> tipoPreguntaComboBox;
    // Selector para establecer el número de preguntas
    private final JSpinner numeroPreguntasSpinner;
    // Selector para establecer el tiempo límite
    private final JSpinner tiempoLimiteSpinner;
    // Botón para el envío de la pregunta
    private final JButton enviarPreguntaButton;
    // Referencia al JFrame principal para pasarle la referencia al JFileChooser
    private JFrame parent;
    // Carpeta en la que se busca el archivo multimedia asociado con la pregunta a enviar al servidor
    private final static String CARPETA_ARCHIVOS_MULTIMEDIA = "/Multimedia/";
    // Logger de la clase
    private final static Logger logger = Logger.getLogger(ConfiguracionPanel.class.getName());

    /**
     * Constructor de la clase.
     *
     * @param title título del marco alrededor del panel.
     * @param out canal para el envío de mensajes al servidor.
     */
    public ConfiguracionPanel(String title, ObjectOutputStream out) {
        super(title, out);

        // *************************
        // * Configuración panel *
        // *************************
        setLayout(new FlowLayout(FlowLayout.LEFT));
     
        // "Temática"
        // ********
        JLabel tematicaLabel = new JLabel("Temática");
        add(tematicaLabel);
        tematicaComboBox = new JComboBox<>(new String[]{});
        // Limitamos la anchura del componente que muestra las temáticas para ajustar la interfaz si el texto de la 
        // temática supera el tamaño establecido.
        tematicaComboBox.setPreferredSize(new Dimension(120,25));
        add(tematicaComboBox);

        // "Nivel"
        // ********
        JLabel nivelLabel = new JLabel("Nivel");
        add(nivelLabel);
        nivelComboBox = new JComboBox<>(Nivel.getNiveles());
        nivelComboBox.setSelectedItem(Nivel.getNivelPorDefecto());
        add(nivelComboBox);

        // "Tipo de preguntas"
        JLabel tipoPreguntaLabel = new JLabel("Tipo");
        add(tipoPreguntaLabel);
        tipoPreguntaComboBox = new CheckedComboBox<>();
        tipoPreguntaComboBox.setModel(new DefaultComboBoxModel<>(getTiposPreguntaComboBoxModel()));
        add(tipoPreguntaComboBox);

        // "Nº Preguntas"
        // **************
        JLabel numeroPreguntasLabel = new JLabel("Nº Preguntas");
        add(numeroPreguntasLabel);

        // Spinner "Nº Preguntas"
        SpinnerNumberModel numeroPreguntasSpinnerModel = new SpinnerNumberModel();
        numeroPreguntasSpinnerModel.setMinimum(1);
        numeroPreguntasSpinnerModel.setMaximum(999);
        numeroPreguntasSpinnerModel.setValue(10);

        numeroPreguntasSpinner = new JSpinner(numeroPreguntasSpinnerModel);
        ((DefaultEditor) numeroPreguntasSpinner.getEditor()).getTextField().setEditable(false);

        add(numeroPreguntasSpinner);

        // "Tiempo Límite"
        // ***************
        JLabel tiempoLimiteLabel = new JLabel("Tpo. Límite");
        add(tiempoLimiteLabel);

        // Spinner "Tiempo Límite"
        SpinnerNumberModel tiempoLimiteSpinnerModel = new SpinnerNumberModel();
        tiempoLimiteSpinnerModel.setMinimum(1);
        tiempoLimiteSpinnerModel.setMaximum(999);
        tiempoLimiteSpinnerModel.setValue(10);

        tiempoLimiteSpinner = new JSpinner(tiempoLimiteSpinnerModel);
        ((DefaultEditor) tiempoLimiteSpinner.getEditor()).getTextField().setEditable(false);
        add(tiempoLimiteSpinner);

        JLabel segundosLabel = new JLabel(" (segs)");
        add(segundosLabel);

        JPanel emptyPanel = new JPanel();
        emptyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(emptyPanel);
        
        // "Enviar Pregunta"
        // ****************
        enviarPreguntaButton = new JButton("Subir pregunta");
        enviarPreguntaButton.setMnemonic(KeyEvent.VK_P);
        add(enviarPreguntaButton);

        // Añadimos el botón que envía la respuesta
        enviarPreguntaButton.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            fileChooser.setDialogTitle("Enviar pregunta");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Ficheros JSON", "json");
            fileChooser.setFileFilter(filter);
            int returnOption = fileChooser.showOpenDialog(parent);
            if (JFileChooser.APPROVE_OPTION == returnOption) {
                try {
                    String json = FileUtils.readFileToString(
                            new File(fileChooser.getSelectedFile().getAbsolutePath()),
                            StandardCharsets.UTF_8.name());
                    // Crear la pregunta a partir del JSON
                    Pregunta pregunta = new Pregunta(json);
                    
                    // Si la pregunta tiene asociada un fichero multimedia, hay que cargarlo y enviarlo también
                    if (!pregunta.getFicheroMultimedia().isEmpty()){
                        String ficheroMultimediaAbsolutePath = 
                                fileChooser.getSelectedFile().getParent().concat(CARPETA_ARCHIVOS_MULTIMEDIA).concat(pregunta.getFicheroMultimedia());
                        byte[] ficheroMultimediaData = FileUtils.readFileToByteArray(new File(ficheroMultimediaAbsolutePath));
                        pregunta.setFicheroMultimediaData(ficheroMultimediaData);
                    }
                    
                    pregunta.setTematica(getTematica());
                    // Enviar la pregunta al servidor
                    Mensaje mensaje = new Mensaje(pregunta, TipoMensaje.PREGUNTA_ENVIAR);
                    out.writeObject(mensaje);
                    out.flush();
                } catch (IOException ex) {
                    logger.severe(ex.getMessage());
                }
            }
        });
        
    }

    /**
     * Obtiene los tipos de preguntas como un array de elementos <code>CheckableItem</code>
     * para presentarlos en el <code>JComboBox</code> como elementos seleccionables.
     * 
     * @return array de elementos <code>CheckableItem</code> que se utiliza como modelo del 
     * <code>JComboBox</code> que muestra los tipos de preguntas.
     */
    private CheckableItem[] getTiposPreguntaComboBoxModel() {

        CheckableItem[] tipoPreguntas = new CheckableItem[TipoPregunta.values().length];

        int i = 0;
        for (TipoPregunta tipoPregunta : TipoPregunta.values()) {
            String tipo = tipoPregunta.getTipo();
            boolean selected = tipo.equals(TipoPregunta.getTipoPorDefecto());
            tipoPreguntas[i++] = new CheckableItem(tipo, selected);
        }
        return tipoPreguntas;
    }

    /**
     * Obtiene los tipos de preguntas como un array de <code>String</code>.
     * 
     * @return array de <code>String</code> con los nombres de los tipos de las preguntas.
     */
    public String[] getTiposPreguntas() {
        List<String> tipos = new ArrayList<>();
        ComboBoxModel cbm = tipoPreguntaComboBox.getModel();
        for (int i = 0; i < cbm.getSize(); i++) {
            CheckableItem item = (CheckableItem) cbm.getElementAt(i);
            if (item.isSelected()) {
                tipos.add(item.toString());
            }
        }
        
        return tipos.toArray(String[]::new);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        for (Component component : this.getComponents()) {
            component.setEnabled(enabled);
        }
    }

    /**
     * Establece la referencia al objeto contenedor del panel.
     *
     * @param parent referencia al objeto contenedor del panel.
     */
    public void setParent(JFrame parent) {
        this.parent = parent;
    }

    /**
     * Obtiene la temática seleccionada.
     *
     * @return temática seleccionada.
     */
    public String getTematica() {
        return tematicaComboBox.getSelectedItem().toString();
    }

    /**
     * Obtiene el nivel seleccionado.
     *
     * @return nivel seleccionado.
     */
    public String getNivel() {
        return nivelComboBox.getSelectedItem().toString();
    }

    /**
     * Obtiene el número de preguntas que se ha establecido.
     *
     * @return número de preguntas que se ha establecido.
     */
    public int getNumeroPreguntas() {
        return Integer.parseInt(numeroPreguntasSpinner.getValue().toString());
    }

    /**
     * Obtiene el tiempo límite que se ha establecido.
     *
     * @return tiempo límite que se ha establecido.
     */
    public int getTiempoLimite() {
        return Integer.parseInt(tiempoLimiteSpinner.getValue().toString());
    }

    /**
     * Establece las temáticas seleccionables en el <code>JComboBox</code> de 
     * materias/temáticas de los tests.
     * 
     * @param tematicas array de <code>String</code> con las materias/temáticas 
     * de los tests. 
     */
    public void setTematicas(String[] tematicas){
        tematicaComboBox.setModel(new DefaultComboBoxModel<>(tematicas));
    }
}
