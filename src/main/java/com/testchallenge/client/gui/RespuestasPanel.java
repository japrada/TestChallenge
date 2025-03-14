
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

import com.testchallenge.model.Pregunta;
import com.testchallenge.model.Respuesta;
import com.testchallenge.model.TipoPregunta;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

/**
 * Panel que muestra y recoge las respuestas seleccionadas por el usuario.
 *
 * En el sistema se han definido los siguientes tipos de preguntas/respuestas:
 *
 * - <code>RespuestaRadioPanel</code>: muestra varias respuestas y sólo se puede seleccionar una. -
 * <code>RespuestaMultiComboPanel</code>: muestra una lista de opciones seleccionables para cada respuesta. -
 * <code>RespuestaMultiCheckPanel</code>: muestra varias respuestas y se pueden seleccionar una o varias. -
 * <code>RespuestaTextoPanel</code>: muestra un área de texto en la que se puede escribir una respuesta.
 *
 * @author japrada
 */
public class RespuestasPanel extends SeleccionablePanel {

    // Opciones que se pueden seleccionar como respuestas
    protected String[] opciones;
    // Panel de respuestas que se va a construir en función del tipo de pregunta
    private RespuestasPanel respuestasPanel;
    // Tipo de la pregunta en función de los tipos de respuestas
    protected TipoPregunta tipoPregunta;
    // Número de pregunta
    protected Integer numeroPregunta;
    // Flag que indica si el panel de respuestas se está mostrando en modo revisión, para la gestión de los listeners
    protected boolean revisionEnabled;

    // Dimensiones del Panel
    private final static int RESPUESTAS_PANEL_ANCHO = 500;
    private final static int RESPUESTAS_PANEL_ALTO = 500; //> 500 (600,700, ...) para activar el scroll en el PreguntasPanel

    /**
     * Constructor.
     *
     * Construye un panel de respuestas vacío.
     */
    public RespuestasPanel() {
        this(new String[]{}, false);
    }
    
    /**
     * Constructor.
     * 
     * Construye un panel de respuestas con las opciones indicadas.
     * 
     * @param opciones respuestas que se pueden seleccionar.
     */
    public RespuestasPanel(String opciones[]) {
        this (opciones, false);
    }
    
    /**
     * Constructor.
     * 
     * Construye un panel de respuestas con las opciones indicadas.
     *
     * @param opciones respuestas que se pueden seleccionar.
     * @param revisionEnabled <code>true</code> si el modo de revisión está enabled o <code>false</code> en caso contrario.
     */
    public RespuestasPanel(String opciones[], boolean revisionEnabled) {
        this.opciones = opciones;
        this.revisionEnabled = revisionEnabled;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(RESPUESTAS_PANEL_ANCHO, RESPUESTAS_PANEL_ALTO));
    }
    
    /**
     * Constructor.
     *
     * Construye un panel de respuestas a partir de la pregunta, con el modo de revisión desactivado.
     *
     * @param pregunta pregunta a partir de la cual se construye el panel de respuestas.
     */
    public RespuestasPanel(Pregunta pregunta) {
        this(pregunta, false);
    }

    /**
     * Constructor.
     *
     * Construye un panel de respuestas a partir de la pregunta.
     *
     * @param pregunta pregunta a partir de la cual se construye el panel de respuestas.
     * @param revisionEnabled <code>true</code> si la pregunta se muestra con el modo de revisión activado o
     * <code>false</code> en caso contrario.
     */
    public RespuestasPanel(Pregunta pregunta, boolean revisionEnabled) {
        this(pregunta.getOpcionesAsArray(), revisionEnabled);
        
        this.tipoPregunta = pregunta.getTipo();
        this.numeroPregunta = pregunta.getNumeroOrden();

        switch (tipoPregunta) {
            case RESPUESTA_MULTIPLE:
                respuestasPanel = new RespuestaMultiCheckPanel(opciones, revisionEnabled);
                add(respuestasPanel, BorderLayout.NORTH);
                break;
            case RESPUESTA_UNICA:
                respuestasPanel = new RespuestaRadioPanel(opciones, revisionEnabled);
                add(respuestasPanel, BorderLayout.NORTH);
                break;
            case RESPUESTA_TEXTO_LIBRE:
                respuestasPanel = new RespuestaTextoPanel();
                add(respuestasPanel, BorderLayout.SOUTH);
                break;
            case RESPUESTA_EMPAREJADA:
            case RESPUESTA_MULTIVALOR:
                respuestasPanel = new RespuestaMultiComboPanel(opciones,
                        pregunta.getValoresOpcionesAsArray(), revisionEnabled);
                add(respuestasPanel, BorderLayout.NORTH);
                break;
        }
    }

    /**
     * Obtiene el tipo de pregunta.
     *
     * @return tipo de pregunta.
     */
    public TipoPregunta getTipoPregunta() {
        return tipoPregunta;
    }

    /**
     * Obtiene el número de la pregunta.
     *
     * @return número de la pregunta.
     */
    public Integer getNumeroPregunta() {
        return numeroPregunta;
    }

    /**
     * Habilita/Deshabilita los componentes del panel.
     *
     * @param isEnabled <code>true</code> para habilitar los componentes del panel y <code>false</code> en caso
     * contrario.
     */
    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);

        Component[] components = respuestasPanel.getComponents();
        for (Component component : components) {
            enableComponents(component, isEnabled);
        }

    }

    /**
     * Habilita/Deshabilita un componente teniendo en cuenta si tiene componentes hijos o no.
     *
     * @param componente componente a habilitar/deshabilitar.
     * @param isEnabled <code>true</code> para habilitar y <code>false</code> para deshabilitar.
     */
    @SuppressWarnings("null")
    private void enableComponents(Component componente, boolean isEnabled) {
        // ¿Es el componente un contenedor?
        if (componente instanceof Container) {
            // En ese caso, obtener todos los compponentes hijos
            Component[] componentesHijos = ((Container) componente).getComponents();
            for (Component hijo : componentesHijos) {
                // aplicando el mismo proceso si el componente hijo es un contenedor
                enableComponents(hijo, isEnabled);
            }
        }
        // Habilitar/Deshabilitar el componente actual
        componente.setEnabled(isEnabled);
    }

    @Override
    public String[] getRespuestasSeleccionadas() {
        return respuestasPanel.getRespuestasSeleccionadas();
    }

    @Override
    public void setRespuestas(String[] respuestas) {
        respuestasPanel.setRespuestas(respuestas);
    }

    @Override
    public void setRespuestas(String[] respuestas, Respuesta respuestaEnviada) {
        respuestasPanel.setRespuestas(respuestas, respuestaEnviada);
    }

   
}
