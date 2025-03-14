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

import java.util.Map;

/**
 *
 * @author japrada
 */
public class Resultados {
    
    // Puntos obtenidos en la realización de un test
    private final Integer puntosObtenidos;
    // Contadores con la información del nº de preguntas respondidas según los valores del tipo enumerado   
    private final Map<Puntuacion, Long> contadores;
    
    /**
     * Constructor vacío.
     */
    public Resultados() {
        this.puntosObtenidos = 0;
        this.contadores = null;
    }

    /**
     * Construye un objeto de tipo <code>Resultados</code> a partir de los valores especificados
     * en los parámetros.
     * 
     * @param puntosObtenidos puntos obtenidos en la realización de un test.
     * @param contadores mapa conteniendo los contadores con los números de respuestas según los valores del tipo enumerado.
     */
    public Resultados(Integer puntosObtenidos, Map<Puntuacion, Long> contadores) {
      this.puntosObtenidos = puntosObtenidos;
      this.contadores = contadores;
    }
    
    /**
     * Obtiene los puntos obtenidos en la realización de un test.
     * 
     * @return puntos obtenidos en la realización de un test.
     */
    public Integer getPuntosObtenidos() {
        return puntosObtenidos;
    }

    /**
     * Obtiene los contadores con los números de respuestas según los valores del tipo enumerado.
     * 
     * @return mapa con los números de respuestas según los valores del tipo enumerado 
     */
    public Map<Puntuacion, Long> getContadores() {
        return contadores;
    }
}
