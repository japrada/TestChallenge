
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

    private Map<Integer, String> respuestas;
    private final JLabel[] labels;
    private static final String VALOR_POR_DEFECTO = "-";

    /**
     * Construye un panel de objetos <code>JComboBox</code> con las opciones especificadas.
     *
     * @param opciones opciones a mostrar.
     * @param valoresOpciones valores de los objetos <code>JComboBox</code> asociados.
     */
    public RespuestaMultiComboPanel(String opciones[], String[][] valoresOpciones) {
        super(opciones);

        // Array con las preguntas, mostradas en un JLabel
        labels = new JLabel[opciones.length];
        // Mapa con la respuesta correcta a cada pregunta
        respuestas = new HashMap<>();

        setLayout(new GridBagLayout());
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;

        for (int i = 0; i < opciones.length; i++) {

            // Creamos la JLabel del Combo (Multi-line), posición x=0, y=i
            // ****************************************
            JLabel opcionLabel = new JLabel();
            opcionLabel.setName(Integer.toString(i));

            StringBuilder sbTextoOpcion = new StringBuilder();
            sbTextoOpcion.append("<html>").append(opciones[i]).append("</html>");
            opcionLabel.setText(sbTextoOpcion.toString());

            constraints.gridx = 0;
            constraints.gridy = i;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.weightx = 1.0;

            // Añadimos la etiqueta en la posición correcta en el GridBagLayout (x=0, y=i)
            add(opcionLabel, constraints);

            // Y la registramos en el array
            labels[i] = opcionLabel;

            // Combo con las opciones:
            // ************************
            // 1. Reajustamos la longitud del array para poner en la posición 0 el elemento seleccionado por defecto
            String[] valoresOpcionesConOpcionPorDefecto = new String[valoresOpciones[i].length + 1];
            System.arraycopy(valoresOpciones[i], 0,
                    valoresOpcionesConOpcionPorDefecto, 1, valoresOpciones[i].length);
            valoresOpcionesConOpcionPorDefecto[0] = VALOR_POR_DEFECTO;
            valoresOpciones[i] = valoresOpcionesConOpcionPorDefecto;

            // 2. Creamos el JComboBox con las opciones y el valor por defecto, que se muestra como primera opción
            JComboBox<String> valoresOpcionComboBox = new JComboBox<>(valoresOpciones[i]);
            valoresOpcionComboBox.setName(Integer.toString(i));

            // Dejamos seleccionado el elemento por defecto
            valoresOpcionComboBox.setSelectedIndex(0);

            constraints.gridx = 1;
            constraints.gridy = i;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.weightx = 1.0;

            // Añadimos el combo en la posición correcta en el GridBagLayout (x=1, y=i)
            add(valoresOpcionComboBox, constraints);

            // Registramos un listener en el combo para gestionar los eventos de selección
            valoresOpcionComboBox.addActionListener((new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    JComboBox cb = (JComboBox) event.getSource();
                    String numeroOpcion = cb.getName();
                    String opcion = cb.getSelectedItem().toString();
                    respuestas.put(Integer.valueOf(numeroOpcion), opcion);
                }
            }));
        }
    }

    @Override
    public String[] getRespuestasSeleccionadas() {
        String[] respuestasArray = new String[opciones.length];
        for (int i = 0; i < opciones.length; i++) {
            respuestasArray[i] = respuestas.get(i) == null ? VALOR_POR_DEFECTO : respuestas.get(i);
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
                if (respuestaEnviada == null) {
                    // Seleccionamos en el combo la respuesta correcta
                    opcionComboBox.setSelectedItem(respuestasCorrectas[comboIndex]);
                } else {
                    // Establecemos un renderer para mostrar la respuesta enviada y la correcta en el color adecuado
                    String respuestaSeleccionada = respuestaEnviada.getOpcionesSeleccionadas().get(comboIndex);
                    opcionComboBox.setRenderer(
                            new ComboRenderer(
                                    respuestaSeleccionada,
                                    respuestasCorrectas[comboIndex],
                                    labels[comboIndex]));
                    opcionComboBox.setSelectedItem(respuestaSeleccionada);
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

        /**
         *
         * @param respuestaEnviada
         * @param respuestaCorrecta
         * @param opcionLabel
         */
        public ComboRenderer(String respuestaEnviada, String respuestaCorrecta, JLabel opcionLabel) {
            this.respuestaEnviada = respuestaEnviada;
            this.respuestaCorrecta = respuestaCorrecta;
            this.opcionLabel = opcionLabel;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value.toString().equals(respuestaCorrecta)) {
                // Set icon to display for value 
                label.setForeground(Color.GREEN);
                if (respuestaEnviada.equals(respuestaCorrecta)) {
                    opcionLabel.setForeground(Color.GREEN);
                }
            } else if (value.toString().equals(respuestaEnviada)) {
                label.setForeground(Color.RED);
                opcionLabel.setForeground(Color.RED);
            }
            return label;
        }
    }
}
