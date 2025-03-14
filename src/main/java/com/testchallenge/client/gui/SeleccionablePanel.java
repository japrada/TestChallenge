
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
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Clase base para los paneles que muestran respuestas seleccionables.
 *
 * Las clases que hereden <code>SeleccionablePanel</code> deberán implementar los métodos abstractos para definir el
 * comportamiento específico en función del tipo de pregunta que se esté presentando.
 *
 * @author japrada
 */
public abstract class SeleccionablePanel extends JPanel implements ISeleccionable {

    @Override
    public abstract String[] getRespuestasSeleccionadas();

    @Override
    public abstract void setRespuestas(String[] respuestas);

    @Override
    public abstract void setRespuestas(String[] respuestas, Respuesta respuesta);

    /**
     * Establece el texto del componente en color verde y subrayado para mostrar la opción correcta. Si, además, el
     * usuario ha seleccionado la opción correcta en la respuesta enviada, el texto se muestra en negrita.
     *
     * NOTA: el texto se formatea en HTML para que aparezca en negrita y subrayado, pero el color de la fuente se
     * establece invocando al método <code>setForeground</code> del componente.
     *
     * @param opcionButton componente que contiene el texto de una de las opciones que se muestran en el panel.
     * @param opcionesSeleccionadas opciones seleccionadas por el usuario en la respuesta enviada.
     * @param opcion opción correcta asociada a la pregunta.
     */
    protected void setOpcionCorrecta(AbstractButton opcionButton, List<String> opcionesSeleccionadas, String opcion) {
        // Si la opción coincide con la que ha seleccionado el usuario ...
        if (opcionesSeleccionadas != null && opcionesSeleccionadas.indexOf(opcion) >= 0) {
            // además, la ponemos en negrita.
            opcionButton.setText(toHTMLBoldAndUnderlined(opcion));
            // Seleccionamos la opción
            opcionButton.setSelected(true);
        } else {
            // En caso contrario, sólo la subrayamos y, además, no la seleccionamos la opción
            opcionButton.setText(toHTMLUnderlined(opcion));
        }

        opcionButton.setForeground(Color.GREEN);
    }

    /**
     * Establece el texto del componente en rojo para mostrar una opción incorrecta seleccionada por el usuario.
     *
     * @param opcionButton componente que contiene el texto de una de las opciones que se muestran en el panel.
     * @param opcionesSeleccionadas opciones seleccionadas por el usuario en la respuesta enviada.
     * @param opcion opción incorrecta asociada a la pregunta.
     */
    protected void setOpcionIncorrecta(AbstractButton opcionButton, List<String> opcionesSeleccionadas, String opcion) {
        if (opcionesSeleccionadas != null && opcionesSeleccionadas.indexOf(opcion) >= 0) {
            // Si la opción incorrecta está entre las seleccionadas por el usuario, la marcamos en rojo
            opcionButton.setSelected(true);
            opcionButton.setForeground(Color.RED);
        }
    }

    /**
     * Establece el texto de la label <code>label</code> con el texto <code>opcion</code> en formato HTML
     * subrayado y en negrita, que se muestra en color verde indicando una opción correcta.
     * 
     * @param label componente al que se le asigna el texto especificado en <code>opcion</code> con formato HTML, subrayado y en negrita.
     * @param opcion texto que se asigna con formato HTML, subrayado y en negrita.
     */
    protected void setOpcionCorrecta(JLabel label, String opcion) {
        label.setText(toHTMLBoldAndUnderlined(opcion));
        label.setForeground(Color.GREEN);
    }

    /**
     * Establece el texto de la label <code>label</code> con el texto <code>opcion</code> en formato HTML
     * que se muestra en color rojo indicando una opción correcta.
     * 
     * @param label
     * @param opcion 
     */
    protected void setOpcionIncorrecta(JLabel label, String opcion) {
        label.setText(toHTML(opcion));
        label.setForeground(Color.RED);
    }

    /**
     * Extrae el texto de una cadena que empieza y termina con un tag html.
     *
     * @param htmlText texto contenido en una cadena que empieza y termina con un tag html.
     * @return devuelve el texto contenido en una cadena que empieza y termina con un tag html.
     */
    protected String toText(String htmlText) {
        int index1 = htmlText.indexOf("<html>");
        int index2 = htmlText.indexOf("</html>");

        if (index1 == 0 && index2 >= 6) {
            return htmlText.substring(6, index2);
        }

        return htmlText;
    }

    /**
     * Inicia y termina una cadena de texto con un tag html.
     *
     * @param optionText cadena de texto que se recubre con un tag html.
     * @return devuelve el texto recubierto por un tag html.
     */
    protected String toHTML(String optionText) {
        int index1 = optionText.indexOf("<html>");
        int index2 = optionText.indexOf("</html>");

        // Si el texto ya se inicia con la etiqueta <html>, no se recubre
        if (index1 == 0 && index2 >= 6) {
            return optionText;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<html>").append(optionText).append("</html>");

        return sb.toString();
    }

    /**
     * Añade los tags de negrita y subrayado a un texto contenido en un tag html.
     *
     * @param optionTextHTML texto contenido en un tag html.
     * @return devuelve el texto en negrita y subrayado contenido en una cadena html.
     */
    protected String addBoldAndUnderlineToHTML(String optionTextHTML) {
        String optionText = toText(optionTextHTML);

        return toHTMLBoldAndUnderlined(optionText);
    }

    /**
     * Transforma una cadena de texto en otra que comienza y finaliza en un tag html, mostrando además el texto en
     * negrita y subrayado.
     *
     * @param optionText texto que se va transformar en una cadena de texto que comienza y finaliza en un tag html
     * mostrándolo, además, en negrita y subrayado.
     *
     * @return devuelve el texto recubierto por un tag html mostrando, además, el texto en negrita y subrayado.
     */
    protected String toHTMLBoldAndUnderlined(String optionText) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><u><b>").append(optionText).append("</b></u></html>");
        return sb.toString();
    }
    
    protected String toHTMLUnderlined(String optionText) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><u>").append(optionText).append("</u</html>");
        return sb.toString();
    }
}
