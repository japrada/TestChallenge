
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

/**
 * Define el comportamiento que deben tener los paneles que permiten la selección de respuestas.
 * 
 * La interfaz debe ser implementada por aquellos paneles que permiten la selección de respuestas.
 * 
 * @author japrada
 */
public interface ISeleccionable {
    
    /**
     * Obtiene las respuestas seleccionadas.
     * 
     * @return textos de las opciones seleccionadas.
     */
    public abstract String[] getRespuestasSeleccionadas();
    
    /**
     * Establece las respuestas que se pueden seleccionar.
     * 
     * @param respuestas respuestas que se van a presentar en el panel.
     */
    public abstract void setRespuestas(String[] respuestas);
    
    /**
     * Establece las respuestas que se pueden seleccionar así como la respuesta enviada por el usuario.
     * 
     * Este método está relacionado con el modo "Ver respuestas", en el que el usuario puede avanzar hacia adelante
     * o hacia atrás en las preguntas del test para comprobar el resultado en base a las respuestas enviadas.
     * 
     * @param respuestas respuestas que se van a presentar en el panel.
     * @param respuestaEnviada respuesta enviada por el usuario.
     */
    public abstract void setRespuestas(String[] respuestas, Respuesta respuestaEnviada);
}
