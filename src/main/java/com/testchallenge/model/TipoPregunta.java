
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
 * Tipos de preguntas que se han definido.
 *
 * @author japrada
 */
public enum TipoPregunta implements Serializable {
    // Pregunta con respuésta única, a seleccionar de una lista de opciones
    RESPUESTA_UNICA("Unica"),
    // Pregunta con respuesta múltiple, a seleccionar de una lista de opciones
    RESPUESTA_MULTIPLE("Múltiple"),
    // Pregunta en la que la respuesta se escribe como un texto
    RESPUESTA_TEXTO_LIBRE("Texto"),
    // Pregunta en la que se presentan las respuestas en un combo y hay que emparejar las preguntas con las respuestas
    RESPUESTA_EMPAREJADA("Emparejada"),
    // Pregunta en la que las respuestas se presentan mediante una lista de opciones en un combo, y son distintas para cada pregunta
    RESPUESTA_MULTIVALOR("Multivalor");

    // Tipo de pregunta expresado como texto
    private final String tipo;

    /**
     * Construye un objeto <code>TipoPregunta</code> a partir del tipo especificado.
     *
     * @param tipo tipo de pregunta.
     */
    TipoPregunta(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene el tipo de pregunta.
     *
     * @return tipo de pregunta.
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Obtiene los tipos de preguntas definidos.
     *
     * @return array con el texto de los tipos de preguntas definidos.
     */
    public static String[] getTipos() {
        String[] tipos = new String[values().length];

        int i = 0;
        for (TipoPregunta n : values()) {
            tipos[i++] = n.getTipo();
        }

        return tipos;
    }

    /**
     * Obtiene el texto del tipo de pregunta por defecto.
     *
     * @return texto del tipo de pregunta por defecto.
     */
    public static String getTipoPorDefecto() {
        return TipoPregunta.RESPUESTA_UNICA.getTipo();
    }

    /**
     * Obtiene el objeto <code>TipoPregunta</code> asociado con la etiqueta especificada.
     *
     * @param label texto del tipo de pregunta.
     * @return <code>TipoPregunta</code> asociado con la etiqueta especificada
     */
    public static TipoPregunta valueOfLabel(String label) {
        for (TipoPregunta e : values()) {
            if (e.tipo.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
