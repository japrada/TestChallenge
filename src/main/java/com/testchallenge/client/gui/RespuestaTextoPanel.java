
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
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Panel que muestra un área de texto en la que se puede escribir una respuesta.
 *
 * @author japrada
 */
public class RespuestaTextoPanel extends RespuestasPanel {

    // Area de texto en la que se puede escribir la respuesta a enviar
    private final JTextArea respuestaTextArea;
    // Label que muestra la respuesta enviada (se utiliza en el modo "Ver resultados")
    private final JLabel respuestaEnviadaLabel;

    // Dimensiones del Panel
    private final static int RESPUESTA_TEXTO_PANEL_ANCHO = 500;
    private final static int RESPUESTA_TEXTO_PANEL_ALTO = 150;

    public RespuestaTextoPanel() {
        super();

        respuestaEnviadaLabel = new JLabel();

        add(respuestaEnviadaLabel, BorderLayout.NORTH);

        // El área de texto se utiliza para escribir la respuesta a enviar y para mostrar las
        // opciones que son correctas para este tipo de pregunta.
        respuestaTextArea = new JTextArea("");

        JScrollPane respuestaScrollPane = new JScrollPane(respuestaTextArea);
        respuestaScrollPane.setPreferredSize(new Dimension(RESPUESTA_TEXTO_PANEL_ANCHO, RESPUESTA_TEXTO_PANEL_ALTO));

        add(respuestaScrollPane, BorderLayout.SOUTH);
    }

    @Override
    public String[] getRespuestasSeleccionadas() {
        String respuesta = respuestaTextArea.getText().trim();

        // Si no se ha escrito ninguna respuesta, entonces se devuelve un String[] vacío.
        if (respuesta.isBlank()) {
            return new String[]{};
        }

        // En caso contrario, se devuelve un String con un único elemento conteniendo la respuesta.
        return new String[]{respuesta};
    }

    @Override
    public void setRespuestas(String[] respuestas) {
        setRespuestas(respuestas, null);
    }

    @Override
    public void setRespuestas(String[] respuestas, Respuesta respuestaEnviada) {

        List<String> opcionesSeleccionadas = null;

        respuestaEnviadaLabel.setVisible(false);

        if (respuestaEnviada != null) {
            opcionesSeleccionadas = respuestaEnviada.getOpcionesSeleccionadas();
        }

        // Si una de las respuestas coincide con la respuesta enviada la marcamos en verde, subrayada y en negrita 
        StringBuilder sbRespuestas = new StringBuilder();
        
        boolean blnCorrectaEncontrada = false;
        for (String aRespuesta : respuestas) {
            if (!blnCorrectaEncontrada) {
                if (opcionesSeleccionadas != null && !opcionesSeleccionadas.isEmpty()) {
                    if (opcionesSeleccionadas.indexOf(aRespuesta) >= 0) {
                        setOpcionCorrecta(respuestaEnviadaLabel, aRespuesta);
                        blnCorrectaEncontrada = true;
                    } else {
                        setOpcionIncorrecta(respuestaEnviadaLabel, opcionesSeleccionadas.get(0));
                    }
                }
            }
            sbRespuestas.append(aRespuesta).append("\n");
        }

        respuestaEnviadaLabel.setVisible(true);
        respuestaTextArea.setText(sbRespuestas.toString());
        respuestaTextArea.setEditable(false);

    }

}
