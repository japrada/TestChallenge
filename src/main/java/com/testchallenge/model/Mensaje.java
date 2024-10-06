
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
 * Clase que modela la entidad Mensaje.
 *
 * Los mensajes se utilizan para el intercambio de información entre los clientes y el servidor.
 *
 * @author japrada
 */
public class Mensaje implements Serializable {

    // Objeto que contiene el mensaje
    private Object object;
    // Tipo de mensaje
    private TipoMensaje tipo;

    /**
     * Construye un mensaje de tipo <code>TipoMensaje.TEXTO</code>, que son los que intercambian los clientes para el
     * envío y recepción de chats.
     *
     * @param texto texto del mensaje intercambiado entre los clientes
     */
    public Mensaje(String texto) {
        this(texto, TipoMensaje.TEXTO);
    }

    /**
     * Construye un mensaje de tipo <code>TipoMensaje.TEXTO_ARRAY</code> a partir de los textos almacenado en un array.
     *
     * @param textoArray array con los textos que va a contener el mensaje.
     */
    public Mensaje(String[] textoArray) {
        this(textoArray, TipoMensaje.TEXTO_ARRAY);
    }

    /**
     * Construye un mensaje vacío del tipo especificado.
     *
     * @param tipo tipo del mensaje.
     */
    public Mensaje(TipoMensaje tipo) {
        this("", tipo);
    }

    /**
     * Construye un mensaje texto y tipo especificados.
     *
     * @param texto texto del mensaje.
     * @param tipo tipo del mensaje.
     */
    public Mensaje(String texto, TipoMensaje tipo) {
        this.object = texto;
        this.tipo = tipo;
    }

    /**
     * Construye un mensaje para transportar un objeto asignándole un tipo de los especificados en
     * <code>TipoMensaje</code>.
     *
     * El tipo se infiere a partir del tipo del objeto.
     *
     * @param object objeto transportado en el mensaje.
     */
    public Mensaje(Object object) {
        this.object = object;
        setTipo(object);
    }

    /**
     * Construye un mensaje para transportar un objeto del tipo especificado.
     *
     * @param object objeto transportado en el mensaje.
     * @param tipo tipo del mensaje.
     */
    public Mensaje(Object object, TipoMensaje tipo) {
        this.object = object;
        this.tipo = tipo;
    }

    /**
     * Establece el tipo de un mensaje a partir del tipo de objeto transportado.
     *
     * @param object objeto transportado en el mensaje
     */
    private void setTipo(Object object) {
        if (isTexto(object)) {
            this.tipo = TipoMensaje.TEXTO;
        } else if (isArrayTexto(object)) {
            this.tipo = TipoMensaje.TEXTO_ARRAY;
        } else if (isPregunta(object)) {
            this.tipo = TipoMensaje.TEST_PREGUNTA;
        } else if (isRespuesta(object)) {
            this.tipo = TipoMensaje.TEST_RESPUESTA;
        } else if (isRanking(object)) {
            this.tipo = TipoMensaje.RANKING_ACTUAL;
        } else if (isConfiguracion(object)) {
            this.tipo = TipoMensaje.INICIAR_TEST;
        } else if (isFlag(object)) {
            this.tipo = TipoMensaje.FLAG_TEST_EN_EJECUCION;
        } else {
            this.tipo = TipoMensaje.UNKNOWN;
        }
    }

    /**
     * Analiza si el objeto es de tipo <code>String</code>.
     *
     * @param objeto objeto que se va a evaluar.
     * @return true si el objeto es de tipo <code>String</code> y false en caso contrario.
     */
    private boolean isTexto(Object objeto) {
        return objeto instanceof String;
    }

    /**
     * Analiza si el objeto es de tipo <code>String[]</code>.
     *
     * @param objeto objeto que se va a evaluar.
     * @return true si el objeto es de tipo <code>String[]</code> y false en caso contrario.
     */
    private boolean isArrayTexto(Object objeto) {
        return objeto instanceof String[];
    }

    /**
     * Analiza si el objeto es de tipo <code>Pregunta</code>.
     *
     * @param objeto objeto que se va a evaluar.
     * @return true si el objeto es de tipo <code>Pregunta</code> y false en caso contrario.
     */
    private boolean isPregunta(Object objeto) {
        return objeto instanceof Pregunta;
    }

    /**
     * Analiza si el objeto es de tipo <code>Respuesta</code>.
     *
     * @param objeto objeto que se va a evaluar.
     * @return true si el objeto es de tipo <code>Respuesta</code> y false en caso contrario.
     */
    private boolean isRespuesta(Object objeto) {
        return objeto instanceof Respuesta;
    }

    /**
     * Analiza si el objeto es de tipo <code>Ranking</code>.
     *
     * @param objeto objeto que se va a evaluar.
     * @return true si el objeto es de tipo <code>Ranking</code> y false en caso contrario.
     */
    private boolean isRanking(Object objeto) {
        return objeto instanceof Ranking;
    }

    /**
     * Analiza si el objeto es de tipo <code>Configuracion</code>.
     *
     * @param objeto objeto que se va a evaluar.
     * @return true si el objeto es de tipo <code>Configuracion</code> y false en caso contrario.
     */
    private boolean isConfiguracion(Object objeto) {
        return objeto instanceof Configuracion;
    }

    /**
     * Analiza si el objeto es de tipo <code>Boolean</code>.
     *
     * @param objeto objeto que se va a evaluar.
     * @return true si el objeto es de tipo <code>Boolean</code> y false en caso contrario.
     */
    private boolean isFlag(Object objeto) {
        return objeto instanceof Boolean;
    }

    /**
     * Obtiene el texto transportado en un mensaje.
     *
     * @return texto transportado en el mensaje.
     */
    public String getTexto() {
        return (String) object;
    }

    /**
     * Obtiene la pregunta transportada en un mensaje.
     *
     * @return pregunta transportada en un mensaje.
     */
    public Pregunta getPregunta() {
        return (Pregunta) object;
    }

    /**
     * Obtiene la respuesta transportada en un mensaje.
     *
     * @return respuesta transportada en un mensaje.
     */
    public Respuesta getRespuesta() {
        return (Respuesta) object;
    }

    /**
     * Obtiene el ranking transportado en un mensaje.
     *
     * @return ranking transportado en un mensaje.
     */
    public Ranking getRanking() {
        return (Ranking) object;
    }

    /**
     * Obtiene la configuración transportada en un mensaje.
     *
     * @return configuración transportada en un mensaje.
     */
    public Configuracion getConfiguracion() {
        return (Configuracion) object;
    }

    /**
     * Obtiene el valor de un flag transportado en un mensaje.
     *
     * @return flag transportado en un mensaje.
     */
    public Boolean getFlag() {
        return (Boolean) object;
    }

    /**
     * Obtiene el array de textos del mensaje.
     *
     * @return array de textos del mensaje.
     */
    public String[] getTextArray() {
        return (String[]) object;
    }

    /**
     * Obtiene el tipo de un mensaje.
     *
     * @return tipo de un mensaje
     */
    public TipoMensaje getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return this.object.toString();
    }
}
