
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Clase que modela la entidad Respuesta enviada por el cliente.
 *
 * @author japrada
 */
public class Respuesta implements Serializable {

    // Opciones de la pregunta que son enviadas por el cliente como respuesta
    private List<String> opciones;
    // Tipo de pregunta con la que está asociada la respuesta
    private TipoPregunta tipoPregunta;
    // Número de orden de la pregunta (en un test) con la que está asociada la respuesta
    private Integer numeroPregunta;
    // Valor por defecto seleccionado en las respuestas de tipo "Emparejada" y "Multivalor"
    public static final String OPCION_POR_DEFECTO_EMPAREJADA_MULTIVALOR = "-";

    /**
     * Construye un objeto <code>Respuesta</code> con sus valores por defecto.
     */
    public Respuesta() {
        this.tipoPregunta = TipoPregunta.RESPUESTA_UNICA;
        this.opciones = new ArrayList<>();
    }

    /**
     * Construye un objeto <code>Respuesta</code> a partir de las opciones de la pregunta.
     *
     * @param opciones array de opciones de la pregunta que forman la respuesta.
     */
    public Respuesta(String[] opciones) {
        this(Arrays.asList(opciones), TipoPregunta.RESPUESTA_UNICA, null);
    }

    /**
     * Construye un objeto <code>Respuesta</code> a partir de las opciones de la pregunta.
     *
     * @param opciones array de opciones de la pregunta que forman la respuesta.
     * @param tipoPregunta tipo de pregunta con la que está asociada la respuesta.
     */
    public Respuesta(String[] opciones, TipoPregunta tipoPregunta) {
        this(Arrays.asList(opciones), tipoPregunta, null);
    }

    /**
     * Construye un objeto <code>Respuesta</code> a partir de las opciones de la pregunta.
     *
     * @param opciones array de opciones de la pregunta que forman la respuesta.
     * @param tipoPregunta tipo de pregunta con la que está asociada la respuesta.
     * @param numeroPregunta número de orden de la pregunta (en un test) con la que está asociada la respuesta.
     */
    public Respuesta(String[] opciones, TipoPregunta tipoPregunta, Integer numeroPregunta) {
        this(Arrays.asList(opciones), tipoPregunta, numeroPregunta);
    }

    /**
     * Construye un objeto <code>Respuesta</code> a partir de las opciones de la pregunta.
     *
     * @param opciones lista de opciones de la pregunta que forman parte de la respuesta.
     * @param tipoPregunta tipo de pregunta con la que está asociada la respuesta.
     */
    public Respuesta(List<String> opciones, TipoPregunta tipoPregunta) {
        this(opciones, tipoPregunta, null);
    }

    /**
     * Construye un objeto <code>Respuesta</code> a partir de los valores de sus atributos.
     *
     * @param opciones lista de opciones de la pregunta que forman parte de la respuesta.
     * @param tipoPregunta tipo de pregunta con la que está asociada la respuesta.
     * @param numeroPregunta número de orden de la pregunta (en un test) con la que está asociada la respuesta.
     */
    public Respuesta(List<String> opciones, TipoPregunta tipoPregunta, Integer numeroPregunta) {
        this.tipoPregunta = tipoPregunta;
        this.numeroPregunta = numeroPregunta;
        this.opciones = opciones;
    }

    /**
     * Establece las opciones seleccionadas de la respuesta como una lista.
     *
     * @param opciones opciones seleccionadas de la respuesta como una lista.
     */
    public void setOpciones(List<String> opciones) {
        this.opciones = opciones;
    }

    /**
     * Establece las opciones seleccionadas de la respuesta como un array.
     *
     * @param opciones opciones seleccionadas de la respuesta como un array.
     */
    public void setOpciones(String[] opciones) {
        this.opciones = Arrays.asList(opciones);
    }

    /**
     * Obtiene la lista de opciones que forman la respuesta.
     *
     * @return lista de opciones que forman la respuesta.
     */
    public List<String> getOpciones() {
        return opciones;
    }

    /**
     * Establece el número de orden de la pregunta (dentro del test) con el que está asociada la respuesta.
     *
     * @param numeroPregunta número de orden de la pregunta (dentro del test) con el que está asociada la respuesta.
     */
    public void setNumeroPregunta(Integer numeroPregunta) {
        this.numeroPregunta = numeroPregunta;
    }

    /**
     * Obtiene el número de orden de la pregunta (dentro del test) con la que está asociada la respuesta.
     *
     * @return número de orden de la pregunta (dentro del test) con la que está asociada la respuesta.
     */
    public Integer getNumeroPregunta() {
        return numeroPregunta;
    }

    /**
     * Obtiene la lista con las opciones seleccionadas como respuesta.
     *
     * @return lista con las opciones que forman la respuesta.
     */
    public List<String> getOpcionesSeleccionadas() {
        return this.opciones;
    }

    /**
     * Establece el tipo de la pregunta con la que está asociada la respuesta.
     *
     * @param tipoPregunta tipo de la pregunta con la que está asociada la respuesta.
     */
    public void setTipoPregunta(TipoPregunta tipoPregunta) {
        this.tipoPregunta = tipoPregunta;
    }

    /**
     * Obtiene el tipo de la pregunta con la que está asociada la respuesta.
     *
     * @return tipo de la pregunta con la que está asociada la respuesta.
     */
    public TipoPregunta getTipoPregunta() {
        return this.tipoPregunta;
    }

    /**
     * Determina si la respuesta a la pregunta es válida o no en función del número de respuestas seleccionadas.
     *
     * @return la respuesta es válida si en el caso de preguntas de tipo respuesta única y texto libre el número de
     * opciones seleccionadas es exactamente una, y en el caso de preguntas de tipo respuesta múltiple, emparejada y
     * multivalor el número de opciones seleccionadas es mayor o igual que uno.
     */
    public boolean esValida() {

        boolean blnEsValida = false;

        switch (tipoPregunta) {
            case RESPUESTA_UNICA:
            case RESPUESTA_TEXTO_LIBRE:
                blnEsValida = opciones.size() == 1;
                break;
            case RESPUESTA_MULTIPLE:
            case RESPUESTA_EMPAREJADA:
            case RESPUESTA_MULTIVALOR:
                blnEsValida = (opciones.size() >= 1);
                break;
        }
        return blnEsValida;
    }

    /**
     * En las preguntas de tipo "Emparejada" o "Multivalor", cuando el usuario envía la respuesta
     * sin haber seleccionado ningún valor, por defecto se envía el valor establecido en la constante
     * <code>OPCION_POR_DEFECTO_EMPAREJADA_MULTIVALOR</code>.
     * 
     * La respuesta a una pregunta de alguno de estos dos tipos, cuando tiene el valor por defecto en 
     * todas las opciones, se considera que ha sido "saltada" o "no contestada" por el usuario 
     * (se ha enviado, pero no se ha contestado).
     * 
     * Este método devuelve <code>true</code> si se ha enviado una respuesta con el valor por defecto
     * en todas las opciones para los tipos de pregunta indicados, o <code>false</code> en caso 
     * contrario.
     * 
     * @return <code>true</code> si se ha enviado una respuesta con el valor por defecto
     * en todas las opciones para los tipos de pregunta indicados, o <code>false</code> en caso 
     * contrario.
     */
    public boolean isRespuestaPorDefecto(){
        boolean blnEsRespuestaPorDefecto;

         switch (tipoPregunta) {
            case RESPUESTA_EMPAREJADA:
            case RESPUESTA_MULTIVALOR:
                blnEsRespuestaPorDefecto = 
                        opciones.stream().allMatch(OPCION_POR_DEFECTO_EMPAREJADA_MULTIVALOR::equals);
                break;
            default:
                blnEsRespuestaPorDefecto = false;
         }
         return blnEsRespuestaPorDefecto;
    }
    
    /**
     * Devuelve <code>true</code> si la lista de opciones de la pregunta es vacía (i.e no se ha seleccionado
     * ninguna opción en la pregunta cuando se envía la respuesta), o <code>false</code> en caso contrario.
     * 
     * @return <code>true</code> si la lista de opciones de la pregunta es vacía (i.e no se ha seleccionado
     * ninguna opción en la pregunta cuando se envía la respuesta), o <code>false</code> en caso contrario.
     */
    public boolean isEmpty() {
        return opciones.isEmpty();
    }
    
    @Override
    public String toString() {
        return "Respuesta{" + "respuestas=" + this.opciones + '}';
    }

    @Override
    public boolean equals(Object obj) {
        // Si el objeto con el que queremos comparar es nulo entonces no son iguales
        if (obj == null) {
            return false;
        }

        // Si el objeto con el que queremos comparar no es una respuesta, entonces no son iguales
        if (!(obj instanceof Respuesta)) {
            return false;
        }

        // Si el objeto con el que queremos comparar tiene la misma referencia entonces son iguales
        if (this == obj) {
            return true;
        }

        // En cualquier otro caso, comparamos la lista de opciones
        Respuesta respuesta = (Respuesta) obj;

        if (!this.tipoPregunta.equals(respuesta.getTipoPregunta())) {
            return false;
        }

        boolean blnSonIguales = true;

        switch (tipoPregunta) {
            case RESPUESTA_UNICA:
                if (opciones.size() != respuesta.getOpcionesSeleccionadas().size()) {
                    blnSonIguales = false;
                } else {
                    blnSonIguales = opciones.get(0).equals(respuesta.getOpcionesSeleccionadas().get(0));
                }
                break;
            case RESPUESTA_MULTIPLE:
                if (opciones.size() != respuesta.getOpcionesSeleccionadas().size()) {
                    blnSonIguales = false;
                } else {
                    for (int i = 0; i < opciones.size() && blnSonIguales; i++) {
                        String opcion = opciones.get(i);
                        blnSonIguales = respuesta.getOpcionesSeleccionadas().indexOf(opcion) >= 0;
                    }
                }
                break;
            case RESPUESTA_TEXTO_LIBRE:
                if (opciones.isEmpty() && !respuesta.isEmpty()) {
                    blnSonIguales = false;
                } else if (respuesta.isEmpty() && !opciones.isEmpty()) {
                    blnSonIguales = false;
                } else {
                    if (opciones.size() == 1) {
                        String opcion = opciones.get(0);
                        blnSonIguales = respuesta.getOpcionesSeleccionadas().indexOf(opcion) >= 0;
                    } else if (respuesta.getOpcionesSeleccionadas().size() == 1) {
                        String opcion = respuesta.getOpcionesSeleccionadas().get(0);
                        blnSonIguales = opciones.indexOf(opcion) >= 0;
                    }
                }
                break;
            case RESPUESTA_EMPAREJADA:
            case RESPUESTA_MULTIVALOR:
                if (opciones.size() != respuesta.getOpcionesSeleccionadas().size()) {
                    blnSonIguales = false;
                } else {
                    for (int i = 0; i < opciones.size() && blnSonIguales; i++) {
                        String opcion = opciones.get(i);
                        blnSonIguales = respuesta.getOpcionesSeleccionadas().get(i).equals(opcion);
                    }
                }
                break;
        }

        return blnSonIguales;
    }
}
