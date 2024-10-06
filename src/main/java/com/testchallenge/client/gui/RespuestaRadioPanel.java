
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
import java.awt.GridLayout;
import java.util.Enumeration;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JRadioButton;

/**
 * Panel que muestra una o varias opciones seleccionables mediante componentes de tipo JRadioButton.
 *
 * @author japrada
 */
public class RespuestaRadioPanel extends RespuestasPanel {

    // Agrupa las opciones para que sólo se pueda seleccionar una
    private final ButtonGroup opcionesGroup;

    /**
     * Construye un panel de objetos <code>JRadioButton</code> con las opciones especificadas.
     * 
     * @param opciones array con las opciones a mostrar.
     */
    public RespuestaRadioPanel(String opciones[]) {
        super(opciones);

        // Layout utilizado para mostrar las opciones
        setLayout(new GridLayout(opciones.length + 1, 1));

        opcionesGroup = new ButtonGroup();

        for (String opcion : opciones) {
            JRadioButton opcionButton = new JRadioButton(opcion);
            opcionButton.setActionCommand(opcion);
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

        if (respuestaEnviada != null) {
            opcionesSeleccionadas = respuestaEnviada.getOpcionesSeleccionadas();
        }

        for (Enumeration<AbstractButton> opcionesButton = opcionesGroup.getElements(); opcionesButton.hasMoreElements();) {
            AbstractButton opcionButton = opcionesButton.nextElement();
            String respuesta = opcionButton.getText();
            if (respuesta.equals(respuestas[0])) {
                opcionButton.setSelected(true);
                if (opcionesSeleccionadas != null && opcionesSeleccionadas.indexOf(respuesta) >= 0) {
                    opcionButton.setForeground(Color.GREEN);
                }
            } else {
                opcionButton.setSelected(false);
                if (opcionesSeleccionadas != null && opcionesSeleccionadas.indexOf(respuesta) >= 0) {
                    opcionButton.setForeground(Color.RED);
                }
            }
        }
    }
}
