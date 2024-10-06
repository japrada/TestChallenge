
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
 * Niveles de las preguntas.
 * 
 * El texto asociado al nivel se muestra en el <code>JComboBox</code> de selección de niveles.
 * 
 * @author japrada
 */
public enum Nivel implements Serializable {
    // Nivel "Bajo"
    BAJO("Bajo"), 
    // Nivel "Normal"
    NORMAL("Normal"),
    // Nivel "Medio"
    MEDIO("Medio"), 
    // Nivel "Alto"
    ALTO("Alto"), 
    // Nivel "Muy alto"
    MUY_ALTO("Muy Alto"); 

    // Texto del nivel 
    private final String nivel;

    /**
     * Constructor.
     * 
     * Construye un objeto con el nivel especificado.
     * 
     * @param nivel texto con el nivel especificado
     */
    Nivel(String nivel) {
        this.nivel = nivel;
    }

    /**
     * Obtiene el nivel como texto.
     * 
     * @return texto del nivel.
     */
    public String getNivel() {
        return nivel;
    }

    /**
     * Obtiene los niveles definidos.
     * 
     * @return array con el texto de los niveles definidos.
     */
    public static String[] getNiveles() {
        String[] niveles = new String[values().length];

        int i = 0;
        for (Nivel n : values()) {
            niveles[i++] = n.getNivel();
        }

        return niveles;
    }

    /**
     * Obtiene el nivel por defecto.
     * 
     * @return nivel texto con el nivel por defecto.
     */
    public static String getNivelPorDefecto() {
        return Nivel.NORMAL.getNivel();
    }

    /**
     * Obtiene el objeto <code>Nivel</code> correspondiente al texto que se pasa como etiqueta.
     * 
     * @param label etiqueta con el texto del nivel.
     * @return objeto <code>Nivel</code> correspondiente al texto que se pasa como etiqueta.
     */
    public static Nivel valueOfLabel(String label) {
        for (Nivel n : values()) {
            if (n.nivel.equals(label)) {
                return n;
            }
        }
        return null;
    }
}
