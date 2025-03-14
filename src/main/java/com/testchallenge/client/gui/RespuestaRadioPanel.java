
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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

/**
 * Panel que muestra una o varias opciones seleccionables mediante componentes de tipo JRadioButton.
 *
 * @author japrada
 */
public class RespuestaRadioPanel extends RespuestasPanel {

    // Agrupa las opciones para que sólo se pueda seleccionar una
    private final ButtonGroup opcionesGroup;
    // Referencia a la opción seleccionada para poder deseleccionarla
    private JRadioButton opcionSeleccionada;
    // Flag para bloquear la selección de otra opción
    private boolean opcionBloqueada = false;
    
    // Respuesta enviada: se guarda la referencia cuando se establecen las opciones para ser utilizada por el listener
    private Respuesta respuestaEnviada;

    /**
     * Construye un panel de objetos <code>JRadioButton</code> con las opciones especificadas con el 
     * modo de revisión desactivado.
     * 
     * @param opciones 
     */
    public RespuestaRadioPanel(String opciones[]) {
        this(opciones, false);
    }
    
    /**
     * Construye un panel de objetos <code>JRadioButton</code> con las opciones especificadas y el 
     * modo de revisión indicado.
     *
     * @param opciones array con las opciones a mostrar.
     * @param revisionEnabled <code>true</code> si el panel se muestra con el modo de revisión activado o
     * <code>false</code> en caso contrario.
     */
    public RespuestaRadioPanel(String opciones[], boolean revisionEnabled) {
        super(opciones, revisionEnabled);

        // Layout utilizado para mostrar las opciones
        setLayout(new GridLayout(opciones.length + 1, 1));

        opcionesGroup = new ButtonGroup();

        for (String opcion : opciones) {

            JRadioButton opcionButton = new JRadioButton(toHTML(opcion));

            opcionButton.setActionCommand(opcion);

            // ¿Se está monstrando la respuesta en modo revisión?
            if (!revisionEnabled) {
                // Si no estamos revisando las respuestas enviadas, ponemos un actionListener que permita 
                // deseleccionar la opción seleccionada.
                ActionListener deselectListener = (ActionEvent e) -> {
                    JRadioButton clickedButton = (JRadioButton) e.getSource();
                    if (clickedButton == opcionSeleccionada) {
                        opcionesGroup.clearSelection();
                        opcionSeleccionada = null;
                    } else {
                        opcionSeleccionada = clickedButton;
                    }
                };
                opcionButton.addActionListener(deselectListener);
            } else {
                // Si estamos revisando las respuestas enviadas, modificamos el comportamiento del botón para evitar
                // que el usuario pueda deseleccionar la opción.
                JToggleButton.ToggleButtonModel model = new JToggleButton.ToggleButtonModel() {
                    @Override
                    public void setSelected(boolean b) {
                        if (!opcionBloqueada || super.isSelected()) {
                            // Si el usuario no ha enviado respuesta o esta es vacía, entonces no dejamos hacer la selección
                            if (respuestaEnviada != null && !respuestaEnviada.isEmpty())
                                super.setSelected(b);
                            // Bloqueamos cambios después de la primera selección
                            opcionBloqueada = true; 
                        }
                    }
                };
                opcionButton.setModel(model);
            }

            // Cambiar el modelo de selección del grupo
            opcionesGroup.add(opcionButton);
            add(opcionButton);
        }
    }

    @Override
    public String[] getRespuestasSeleccionadas() {
        ButtonModel opcionModel = opcionesGroup.getSelection();

        if (opcionModel != null) {
            return new String[]{opcionesGroup.getSelection().getActionCommand()};
        }

        return new String[]{};
    }

    @Override
    public void setRespuestas(String[] respuestas) {
        for (Enumeration<AbstractButton> opcionesButton = opcionesGroup.getElements(); opcionesButton.hasMoreElements();) {
            AbstractButton opcionButton = opcionesButton.nextElement();
            if (opcionButton.getText().equals(respuestas[0])) {
                opcionButton.setSelected(true);
            }
            opcionButton.setEnabled(false);
        }
    }

    @Override
    public void setRespuestas(String[] respuestas, Respuesta respuestaEnviada) {
        List<String> opcionesSeleccionadas = null;

        this.respuestaEnviada = respuestaEnviada;
        
        if (respuestaEnviada != null) {
            opcionesSeleccionadas = respuestaEnviada.getOpcionesSeleccionadas();
        }

        for (Enumeration<AbstractButton> opcionesButton = opcionesGroup.getElements(); opcionesButton.hasMoreElements();) {
            AbstractButton opcionButton = opcionesButton.nextElement();

            String opcion = toText(opcionButton.getText());

            // Si la opción que se está procesando coincide con la respuesta correcta ...
            if (opcion.equals(respuestas[0])) {
                // la mostramos subrayada y en verde y, además, si coincide con la seleccionada por el usuario, en negrita
                setOpcionCorrecta(opcionButton, opcionesSeleccionadas, opcion);
            } else {
                // en caso contrario, la mostramos en rojo
                setOpcionIncorrecta(opcionButton, opcionesSeleccionadas, opcion);
            }
        }
    }
}
