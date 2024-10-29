
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
 * Tipos de mensajes intercambiados.
 *
 * @author japrada
 */
public enum TipoMensaje implements Serializable {
    
    // Mensaje para el envío de una respuesta
    RESPUESTA_ENVIAR("RESPUESTA_ENVIAR"),
    // Mensaje para notificar al cliente el nº de segundos del temporizador
    TIMER_TICK("TIMER_TICK"),      
    // Mensaje para iniciar la ejecución de un test
    INICIAR_TEST("TEST_INICIAR"),
    // Mensaje para realizar el envío de una pregunta desde el servidor de Test
    TEST_PREGUNTA("TEST_PREGUNTA"),
    // Mensaje para realizar el envío de una respuesta desde el cliente al servidor de Test
    TEST_RESPUESTA("TEST_RESPUESTA"),
    // Mensaje enviado por el servidor de Test para informar al cliente de la finalización del Test
    TEST_PARAR("TEST_PARAR"),   
    // Mensaje enviado por el cliente para subir un fichero con una pregunta al servidor
    PREGUNTA_ENVIAR("PREGUNTA_ENVIAR"),    
    // Mensaje de chat intercambiado entre los clientes
    TEXTO("TEXTO"),   
    // Mensaje enviado por el servidor de Chat para notificar que el usuario se pudo registrar
    NICKNAME_OK("NICKNAME_OK"),
    // Mensaje enviado por el servidor de Chat para notificar que el usuario no se pudo registrar
    NICKNAME_KO("NICKNAME_KO"),
    // Mensaje en el que se envía el ranking actual al usuario que se acaba de conectar
    RANKING_ACTUAL("RANKING_ACTUAL"),
    // Mensaje utilizado por el cliente para preguntar al servidor si hay un test en ejecución
    TEST_EN_EJECUCION("TEST_EN_EJECUCION"),
    // Mensaje enviado por el cliente para ampliar el tiempo de respuesta
    AMPLIAR_TIEMPO_RESPUESTA("AMPLIAR_TIEMPO_RESPUESTA"),
    // Pregunta contestada correctamente (la primera vez)
    PREGUNTA_CONTESTADA_CORRECTAMENTE_Y_PRIMERA("PREGUNTA_CONTESTADA_CORRECTAMENTE_Y_PRIMERA"),
    // Pregunta contestada correctamente (la primera vez)
    PREGUNTA_CONTESTADA_CORRECTAMENTE("PREGUNTA_CONTESTADA_CORRECTAMENTE"),
    // Pregunta NO contestada correctamente
    PREGUNTA_NO_CONTESTADA_CORRECTAMENTE("PREGUNTA_NO_CONTESTADA_CORRECTAMENTE"),
    // Pregunta NO contestada
    PUNTUACION_PREGUNTA_NO_CONTESTADA("PUNTUACION_PREGUNTA_NO_CONTESTADA"),
    // Detener el test en ejecución
    DETENER_TEST("DETENER_TEST"),
    // Pausar el test en ejecución,
    PAUSAR_TEST("PAUSAR_TEST"),
    // Test pausado: mensaje de notificación enviado del servidor a los clientes
    TEST_PAUSADO("TEST_PAUSADO"),
    // Reanudar el test en ejecución,
    REANUDAR_TEST("REANUDAR_TEST"),
    // Test reanudado:mensaje de notificación enviado del servidor a los clientes
    TEST_REANUDADO("TEST_REANUDADO"),
    // Mensaje no tipificado (para cuando se infiere el tipo a partir del objeto)
    UNKNOWN("UNKNOWN"),
    // Mensaje enviado por el cliente para abandonar la aplicación,
    BYE("BYE"),
    // Array de tipo String
    TEXTO_ARRAY("TEXTO_ARRAY");

    // Tipo del mensaje
    private final String tipo;

    /**
     * Construye un objeto <code>TipoMensaje</code> con el tipo especificado.
     * 
     * @param tipo tipo de mensaje. 
     */
    TipoMensaje(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene el tipo de mensaje.
     * 
     * @return tipo de mensaje.
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Obtiene el objeto <code>TipoMensaje</code> asociado con la etiqueta especificada.
     * 
     * @param label etiqueta del tipo de mensaje.
     * @return <code>TipoMensaje</code> asociado con la etiqueta especificada.
     */
    public static TipoMensaje valueOfLabel(String label) {
        for (TipoMensaje e : values()) {
            if (e.tipo.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
