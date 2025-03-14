
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

import com.testchallenge.model.Respuesta;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Panel para mostrar las respuestas de tipo Multivalor y Emparejada.
 *
 * @author japrada
 */
public class RespuestaMultiComboPanel extends RespuestasPanel {

    // Respuestas correctas asociadas a las opciones teniendo en cuenta su posición
    private Map<Integer, String> respuestasOpciones;
    // Textos de las opciones (JLabel) que acompañan a los combos
    private final JLabel[] labelsOpciones;
    private Respuesta respuestaEnviada;

    /**
     * Construye un panel de objetos <code>JComboBox</code> con las opciones especificadas.
     *
     * @param opciones opciones a mostrar.
     * @param valoresOpciones valores de los objetos <code>JComboBox</code> asociados.
     * @param revisionEnabled <code>true</code> si el panel se muestra con el modo de revisión activado o
     * <code>false</code> en caso contrario.
     */
    public RespuestaMultiComboPanel(String opciones[], String[][] valoresOpciones, boolean revisionEnabled) {
        super(opciones, revisionEnabled);

        // Array con las JLabel de las opciones
        labelsOpciones = new JLabel[opciones.length];
        // Mapa con la respuesta correcta a cada pregunta
        respuestasOpciones = new HashMap<>();

        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;

        for (int i = 0; i < opciones.length; i++) {

            // Creamos la JLabel del Combo (Multi-line), posición x=0, y=i
            // ****************************************
            JLabel opcionLabel = new JLabel();

            opcionLabel.setName(Integer.toString(i));
            opcionLabel.setText(toHTML(opciones[i]));

            constraints.gridx = 0;
            constraints.gridy = i;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.weightx = 1.0;

            // Añadimos la etiqueta en la posición correcta en el GridBagLayout (x=0, y=i)
            add(opcionLabel, constraints);

            // Y la registramos en el array
            labelsOpciones[i] = opcionLabel;

            // Combo con las opciones:
            // ************************
            // 1. Reajustamos la longitud del array para poner en la posición 0 el elemento seleccionado por defecto
            String[] valoresOpcionesConOpcionPorDefecto = new String[valoresOpciones[i].length + 1];
            System.arraycopy(valoresOpciones[i], 0,
                    valoresOpcionesConOpcionPorDefecto, 1, valoresOpciones[i].length);
            valoresOpcionesConOpcionPorDefecto[0] = Respuesta.OPCION_POR_DEFECTO_EMPAREJADA_MULTIVALOR;
            valoresOpciones[i] = valoresOpcionesConOpcionPorDefecto;

            // 2. Creamos el JComboBox con las opciones y el valor por defecto, que se muestra como primera opción
            JComboBox<String> valoresOpcionComboBox = new JComboBox<>(valoresOpciones[i]);
            valoresOpcionComboBox.setName(Integer.toString(i));

            // Dejamos seleccionado el elemento 0 que contiene el valor por defecto
            valoresOpcionComboBox.setSelectedIndex(0);

            constraints.gridx = 1;
            constraints.gridy = i;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.weightx = 1.0;

            // Añadimos el combo en la posición correcta en el GridBagLayout (x=1, y=i)
            add(valoresOpcionComboBox, constraints);

            // Si no estamos en modo revisión, registramos un listener en el combo para gestionar los eventos de selección.
            // En caso contrario, se registra otro listener cuando se establecen las respuestas para reestablecer el valor
            // seleccionado si el usuario lo cambia. 
            if (!revisionEnabled) {
                valoresOpcionComboBox.addActionListener((new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        JComboBox cb = (JComboBox) event.getSource();
                        String numeroOpcion = cb.getName();
                        String opcion = cb.getSelectedItem().toString();
                        respuestasOpciones.put(Integer.valueOf(numeroOpcion), opcion);
                    }
                }));
            }
        }
    }

    @Override
    public String[] getRespuestasSeleccionadas() {
        String[] respuestasArray = new String[opciones.length];
        for (int i = 0; i < opciones.length; i++) {
            respuestasArray[i] = respuestasOpciones.get(i) == null
                    ? Respuesta.OPCION_POR_DEFECTO_EMPAREJADA_MULTIVALOR : respuestasOpciones.get(i);
        }
        return respuestasArray;
    }

    @Override
    public void setRespuestas(String[] respuestas) {
        setRespuestas(respuestas, null);
    }

    @Override
    public void setRespuestas(String[] respuestasCorrectas, Respuesta respuestaEnviada) {

        for (Component component : getComponents()) {
            if (component instanceof JComboBox) {
                JComboBox opcionComboBox = (JComboBox) component;
                // Establecer las opciones correctas que deben aparecer seleccionadas
                String comboName = opcionComboBox.getName();
                int comboIndex = Integer.parseInt(comboName);
                // ¿Se ha enviado una respuesta por parte del usuario?
                if (respuestaEnviada == null
                        || respuestaEnviada.isRespuestaPorDefecto()) {
                    String respuestaCorrecta = respuestasCorrectas[comboIndex];
                    opcionComboBox.setRenderer(
                            new ComboRenderer(respuestaCorrecta, 
                                    respuestaCorrecta, labelsOpciones[comboIndex], 
                                    true));
                    // Seleccionamos en el combo la respuesta correcta
                    opcionComboBox.setSelectedItem(respuestaCorrecta);
                    // Guardamos la respuesta correcta, para reestablecerla si el usuario cambia el valor seleccionado
                    respuestasOpciones.put(comboIndex, respuestaCorrecta);
                } else {
                    // Establecemos un renderer para mostrar la respuesta enviada y la correcta en el color adecuado
                    String respuestaSeleccionada = respuestaEnviada.getOpcionesSeleccionadas().get(comboIndex);
                    opcionComboBox.setRenderer(
                            new ComboRenderer(respuestaSeleccionada, 
                                    respuestasCorrectas[comboIndex], 
                                    labelsOpciones[comboIndex], false));
                    opcionComboBox.setSelectedItem(respuestaSeleccionada);
                    // Guardamos la respuesta correcta, para reestablecerla si el usuario cambia el valor seleccionado
                    respuestasOpciones.put(comboIndex, respuestaSeleccionada);
                }

                if (revisionEnabled) {
                    // Si estamos en el modo "revisión" no se permite cambiar el elemento seleccionado
                    opcionComboBox.addItemListener((ItemEvent event) -> {
                        if (event.getStateChange() == ItemEvent.SELECTED) {
                            JComboBox cb = (JComboBox) event.getSource();
                            cb.setSelectedItem(respuestasOpciones.get(Integer.valueOf(cb.getName())));
                        }
                    });
                }
            }
        }
    }

    /**
     * Renderer por defecto del <code>JComboBox</code> que muestra las opciones en el color correspondiente según si la
     * respuesta se ha enviado o no, y si es correcta o no.
     *
     */
    class ComboRenderer extends DefaultListCellRenderer {

        private String respuestaEnviada = null;
        private String respuestaCorrecta = null;
        private JLabel opcionLabel = null;
        private final boolean objetoRespuestaEnviadaNulaOEmpty;

        /**
         *
         * @param respuestaEnviada
         * @param respuestaCorrecta
         * @param opcionLabel
         * @param objetoRespuestaEnviadaNulaOEmpty
         */
        public ComboRenderer(String respuestaEnviada, String respuestaCorrecta, JLabel opcionLabel, boolean objetoRespuestaEnviadaNulaOEmpty) {
            this.respuestaEnviada = respuestaEnviada;
            this.respuestaCorrecta = respuestaCorrecta;
            this.opcionLabel = opcionLabel;
            this.objetoRespuestaEnviadaNulaOEmpty = objetoRespuestaEnviadaNulaOEmpty;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value.toString().equals(respuestaCorrecta)) {
                // Mostramos el elemento en el combo (la label que representa el elemento) en verde
                label.setForeground(Color.GREEN);
                // Además, si la respuesta enviada por el usuario coincide con la respuesta correcta y el objeto que 
                // contiene la respuesta no es nul (respuesta no enviada) o vacío (respuesta no respondida) ...
                if (respuestaEnviada.equals(respuestaCorrecta) && !objetoRespuestaEnviadaNulaOEmpty) {
                    // mostramos el texto de la opción, en negrita y subrayado, y, además, en verde
                    setOpcionCorrecta(opcionLabel, opcionLabel.getText());
                }
            } else if (value.toString().equals(respuestaEnviada)) {
                // Mostramos el elemento en el combo en rojo
                label.setForeground(Color.RED);
                // y el texto de la etiqueta que acompaña a la opción, en rojo
                setOpcionIncorrecta(opcionLabel, opcionLabel.getText());
            }
            return label;
        }
    }
}
