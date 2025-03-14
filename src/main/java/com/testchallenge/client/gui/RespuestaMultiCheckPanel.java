
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
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JToggleButton;

/**
 * Panel que muestra una o varias opciones seleccionables mediante componentes de tipo <code>JCheckBox </code>.
 *
 * @author japrada
 */
public class RespuestaMultiCheckPanel extends RespuestasPanel {

    // Colección que almacena las opciones que se presentan
    private Map<Integer, String> respuestas;

     /**
     * Construye un panel de objetos <code>JCheckBox</code> con las opciones indicadas y el 
     * modo de revisión desactivado.
     * 
     * @param opciones 
     */
    public RespuestaMultiCheckPanel(String opciones[]) {
        this(opciones, false);
    }
    /**
     * Construye un panel de objetos <code>JCheckBox</code> con las opciones indicadas y el modo de revisión indicado.
     *
     * @param opciones array con las opciones que se muestran en el panel.
     * @param revisionEnabled <code>true</code> si el panel se muestra con el modo de revisión activado o
     * <code>false</code> en caso contrario.
     */
    public RespuestaMultiCheckPanel(String opciones[], boolean revisionEnabled) {
        super(opciones, revisionEnabled);

        //setPreferredSize(new Dimension(RESPUESTA_MULTI_CHECK_PANEL_ANCHO, RESPUESTA_MULTI_CHECK_PANEL_ALTO));
        // Layout utilizado para mostrar las opciones
        setLayout(new GridLayout(opciones.length + 1, 1));

        respuestas = new HashMap<>();

        for (int i = 0; i < opciones.length; i++) {

            JCheckBox opcionJCheckBox = new JCheckBox(toHTML(opciones[i]));

            opcionJCheckBox.setName(Integer.toString(i));

            if (!revisionEnabled) {
                opcionJCheckBox.addItemListener((new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        String numeroOpcion = ((JCheckBox) e.getSource()).getName();
                        JCheckBox cb = (JCheckBox) e.getItemSelectable();

                        if (cb.isSelected()) {
                            String opcion = toText(cb.getText());
                            respuestas.put(Integer.valueOf(numeroOpcion), opcion);
                        } else {
                            respuestas.remove(Integer.valueOf(numeroOpcion));
                        }
                    }
                }));
            }
            add(opcionJCheckBox);
        }
    }

    @Override
    public String[] getRespuestasSeleccionadas() {
        String[] respuestasArray = new String[respuestas.values().size()];
        respuestas.values().toArray(respuestasArray);
        return respuestasArray;
    }

    @Override
    public void setRespuestas(String[] respuestas) {
        for (String respuesta : respuestas) {
            for (Component component : getComponents()) {
                JCheckBox opcionJCheck = (JCheckBox) component;
                if (opcionJCheck.getText().equals(respuesta)) {
                    opcionJCheck.setSelected(true);
                } else {
                    opcionJCheck.setEnabled(false);
                }

            }
        }
    }

    @Override
    public void setRespuestas(String[] respuestas, Respuesta respuestaEnviada) {
        List<String> opcionesSeleccionadas = null;

        if (respuestaEnviada != null) {
            opcionesSeleccionadas = respuestaEnviada.getOpcionesSeleccionadas();
        }

        List<String> respuestasCorrectas = Arrays.asList(respuestas);

        for (Component component : getComponents()) {
            JCheckBox opcionJCheck = (JCheckBox) component;
            // Establecer las opciones correctas que deben aparecer seleccionadas
            String opcion = toText(opcionJCheck.getText());

            // Si la opción que se está procesando es una de las opciones correctas ...
            if (respuestasCorrectas.indexOf(opcion) >= 0) {
                // la mostramos subrayada y en verde y, además, si coincide con la seleccionada por el usuario, en negrita
                setOpcionCorrecta(opcionJCheck, opcionesSeleccionadas, opcion);
            } else {
                setOpcionIncorrecta(opcionJCheck, opcionesSeleccionadas, opcion);
            }
            
            // Si el modo de revisión está activado, modificamos el modelo para evitar cambiar el estado de selección
            if (revisionEnabled)
                opcionJCheck.setModel(new FixedCheckBoxModel(opcionJCheck.isSelected()));
        }
    }
}

/**
 * Modelo personalizado que evita los cambios de estado.
 * 
 * @author japrada
 */
final class FixedCheckBoxModel extends JToggleButton.ToggleButtonModel {
    private final boolean estadoSeleccionInicial;

    public FixedCheckBoxModel(boolean estadoSeleccionInicial) {
        this.estadoSeleccionInicial = estadoSeleccionInicial;
        setSelected(estadoSeleccionInicial);
    }

    @Override
    public void setSelected(boolean b) {
        // Solo permite la selección inicial, después bloquea los cambios
        super.setSelected(estadoSeleccionInicial);
    }
}