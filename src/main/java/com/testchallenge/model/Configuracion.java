
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

package com.testchallenge.model;

import java.io.Serializable;

/**
 * Clase que modela la entidad Configuración.
 * 
 * Almacena los parámetros de configuración establecidos en la ejecución del test.
 * 
 * @author japrada
 */
public class Configuracion implements Serializable {
    
    // Parámetros de configuración
    private String tematica;
    private String nivel;
    private String[] tipoPreguntas;
    private int numeroPreguntas;
    private int tiempoLimite;
    
    /**
     * Construye un objeto configuración con los parámetros establecidos.
     * 
     * @param tematica tematica del test.
     * @param nivel nivel de dificultad de las preguntas.
     * @param tipoPreguntas tipos de preguntas que se pueden seleccionar según se han definido en el tipo enumerado
     * <code>TipoPregunta</code>.
     * @param numeroPreguntas número de preguntas del test.
     * @param tiempoLimite tiempo máximo del que se dispone para enviar una respuesta a una pregunta (en segundos).
     */
    public Configuracion (String tematica, 
                          String nivel,
                          String[] tipoPreguntas,
                          int numeroPreguntas,
                          int tiempoLimite) {
        
      this.tematica = tematica;
      this.nivel = nivel;
      this.tipoPreguntas = tipoPreguntas;
      this.numeroPreguntas = numeroPreguntas;
      this.tiempoLimite = tiempoLimite;
    }

    /**
     * Obtiene la temática.
     * 
     * @return temática.
     */
    public String getTematica() {
        return tematica;
    }

    /**
     * Establece la temática.
     * 
     * @param tematica temática.
     */
    public void setTematica(String tematica) {
        this.tematica = tematica;
    }

    /**
     * Obtiene el nivel de dificultad de las preguntas que se van a seleccionar en el test.
     * 
     * @return nivel de las preguntas que se van a seleccionar en el test.
     */
    public String getNivel() {
        return nivel;
    }

    /**
     * Establece el nivel de dificultad de las preguntas que se van a seleccionar en el test.
     * 
     * @param nivel 
     */
    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    /**
     * Obtiene los tipos de las preguntas según se han definido en el tipo enumerado <code>TipoPregunta</code>.
     * 
     * @return array con los tipos de las preguntas según se han definido en el tipo enumerado <code>TipoPregunta</code>.
     */
    public String[] getTiposPreguntas() {
        return tipoPreguntas;
    }

    /**
     * Establece los tipos de las preguntas según se han definido en el tipo enumerado <code>TipoPregunta</code>.
     * 
     * @param tipoPreguntas.
     */
    public void setTipoPreguntas(String[] tipoPreguntas) {
        this.tipoPreguntas = tipoPreguntas;
    }

    /**
     * Obtiene el número de preguntas del test.
     * 
     * @return número de preguntas del test.
     */
    public int getNumeroPreguntas() {
        return numeroPreguntas;
    }

    /**
     * Establece el número de preguntas del test.
     * 
     * @param numeroPreguntas 
     */
    public void setNumeroPreguntas(int numeroPreguntas) {
        this.numeroPreguntas = numeroPreguntas;
    }

    /**
     * Obtiene el tiempo máximo del que se dispone para enviar una respuesta a una pregunta (en segundos).
     * 
     * @return tiempo máximo del que se dispone para enviar una respuesta a una pregunta (en segundos).
     */
    public int getTiempoLimite() {
        return tiempoLimite;
    }

    /**
     * Establece el tiempo máximo del que se dispone para enviar una respuesta a una pregunta (en segundos).
     * 
     * @param tiempoLimite tiempo máximo del que se dispone para enviar una respuesta a una pregunta (en segundos).
     */
    public void setTiempoLimite(int tiempoLimite) {
        this.tiempoLimite = tiempoLimite;
    }
    
}
