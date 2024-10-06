
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
 *
 * @author japrada
 */
public enum Puntuacion implements Serializable {
    
    CORRECTA_Y_PRIMERA(2),
    CORRECTA(1),
    INCORRECTA (0),
    NO_CONTESTADA(-1);
    
    public static final String CORRECTA_Y_PRIMERA_MENSAJE = "Respuesta correcta y primero en acertar, 2 puntos :-)";
    public static final String CORRECTA_MENSAJE = "Respuesta correcta, 1 punto :-)";
    public static final String INCORRECTA_MENSAJE = "Respuesta incorrecta, 0 puntos :-(";
    public static final String NO_CONTESTADA_MENSAJE = "Respuesta no enviada, -1 puntos :-(";
    
    // Tipo del mensaje
    private final Integer puntuacion;

    /**
     * Construye un objeto <code>TipoPuntuacion</code> a partir de la puntuación especificada.
     * 
     * @param puntuacion puntuación. 
     */
    Puntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

     /**
     * Obtiene la puntuación asociada al tipo.
     * 
     * @return tipo de mensaje.
     */
    public Integer getPuntos() {
        return puntuacion;
    }

    /**
     * Obtiene el objeto <code>Puntuacion</code> asociado con el valor especificado.
     * 
     * @param puntuacion puntuacion.
     * @return <code>Puntuacion</code> asociada con el valor especificado.
     */
    public static Puntuacion valueOfLabel(Integer puntuacion) {
        for (Puntuacion e : values()) {
            if (e.puntuacion.equals(puntuacion)) {
                return e;
            }
        }
        return null;
    }
    
}
