
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
package com.testchallenge.client;

import com.testchallenge.client.gui.TestChallengeClient;
import com.testchallenge.client.gui.TimerPanel;
import com.testchallenge.model.Mensaje;
import com.testchallenge.model.Pregunta;
import com.testchallenge.model.Puntuacion;
import com.testchallenge.model.Ranking;
import com.testchallenge.model.TipoMensaje;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Logger;

/**
 * Thread de servicio creado por <code>TestChallengeClient</code> que lee asíncronamente los mensajes que los otros
 * clientes envían al chat (actuando el servidor como intermediario), así como las notificaciones enviadas por el
 * servidor.
 *
 * @author jprada
 */
public class TestChallengeClientThread extends Thread {

    // nickname o alias del usuario al que da servicio el hilo
    private final String nickname;
    // Stream para la lectura de los mensajes enviados por el servidor
    private ObjectInputStream serverDataIn;
    // Referencia al objeto padre que ha creado el hilo de servicio
    private TestChallengeClient testChallengeClient;

    private final static Logger logger = Logger.getLogger(TestChallengeClientThread.class.getName());

    /**
     * Constructor de la clase.
     *
     * Construye un objeto del tipo especificado para la gestión de los mensajes que el servidor de chat envía al
     * usuario con el nickname especificado.
     *
     * @param nickname Nickname o alias del usuario que se ha conectado al chat.
     */
    public TestChallengeClientThread(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Establece el stream a través del cual el cliente recibe mensajes desde el servidor.
     *
     * @param serverDataIn stream a través del cual el cliente recibe mensajes desde el servidor.
     */
    public void setServerDataIn(ObjectInputStream serverDataIn) {
        this.serverDataIn = serverDataIn;
    }

    /**
     * Establece la referencia al objeto que ha creado el hilo.
     *
     * @param testChallengeClient referencia al objeto que ha creado el hilo.
     */
    public void setTestChallengeClient(TestChallengeClient testChallengeClient) {
        this.testChallengeClient = testChallengeClient;
    }

    @Override
    public void run() {
        try {
            Mensaje mensaje;
            do {
                // Leer un mensaje enviado desde el servidor
                mensaje = (Mensaje) serverDataIn.readObject();

                // Procesar el mensaje
                // NOTA: Si el mensaje es null es porque el stream de lectura se ha cerrado desde el servidor
                if (mensaje != null) {

                    TipoMensaje tipoMensaje = mensaje.getTipo();

                    // Analizar si el mensaje contiene un comando del lado del servidor
                    switch (tipoMensaje) {
                        case TIMER_TICK:
                            // Extraer el tiempo restante y mostrarlo en el TimerPanel
                            String tiempoRestante = mensaje.getTexto();
                            TimerPanel timerPanel = (TimerPanel) testChallengeClient.getTestPanel().getTimerPanel();
                            timerPanel.setTimer(String.format("%s segundos", tiempoRestante));
                            break;
                        case TEST_PREGUNTA:
                            Pregunta pregunta = mensaje.getPregunta();
                            testChallengeClient.getTestPanel().setPregunta(pregunta);
                            break;
                        case INICIAR_TEST:
                            // Mensaje enviado por el servidor a todos los clientes (menos el que solicita iniciar 
                            // el test) para que reseteen el panel de preguntas porque un usuario ha solicitado
                            // iniciar un nuevo test
                            testChallengeClient.getTestPanel().resetPanelPreguntas();
                            break;
                        case TEST_PARAR:
                            // Desde el lado del servidor se notifica a los clientes que el test se para ... 
                            // ----------------
                            // Resetear el panel de preguntas y 
                            testChallengeClient.getTestPanel().setPregunta();
                            // actualizar el ranking
                            Ranking ranking = mensaje.getRanking();
                            if (ranking != null) {
                                testChallengeClient.getTestPanel().setRanking(ranking, nickname);
                                testChallengeClient.getTestPanel().setModoRevisionEnabled(true);
                                // Mostrar un diálogo para informar del número de puntos obtenidos
                                testChallengeClient.getTestPanel().popUpResultados();
                            } else {
                                testChallengeClient.getTestPanel().setModoRevisionEnabled(false);
                                testChallengeClient.getChatPanel().addMessage("No hay preguntas con los criterios especificados.");
                                // El test no se ha ejecutado porque no hay preguntas que cumplan los criterios especificados
                                testChallengeClient.getTestPanel().popUpTestSinPreguntas();
                            }

                            testChallengeClient.getTestPanel().getConfiguracionPanel().setEnabled(true);
                            testChallengeClient.getTestPanel().getIniciarTestButton().setEnabled(true);
                            break;

                        case TEST_PAUSADO:
                            // Desde el lado del servidor se notifica a los clientes que el test se pausa ...
                            // *****************
                            // En la interfaz de usuario el cliente tiene que cambiar el icono de "Pause" por "Play"
                            // *****************
                            testChallengeClient.getTestPanel().getPreguntasPanel().setResumeButtonEnabled();
                            break;
                        case TEST_REANUDADO:
                            // Desde el lado del servidor se notifica a los clientes que el test se para ... 
                            // *****************
                            // En la interfaz de usuario el cliente tiene que cambiar el icono de "Play" por "Pause"
                            // *****************
                            testChallengeClient.getTestPanel().getPreguntasPanel().setPauseButtonEnabled();
                            break;
                        case PREGUNTA_CONTESTADA_CORRECTAMENTE_Y_PRIMERA:
                            // El cliente es notificado de que un usuario ha contestado correctamente la pregunta.
                            // Este mensaje se utiliza para desactivar el panel que permite ampliar el tiempo restante.
                            // De ese modo, una vez que el primer usuario haya contestado correctamente a la pregunta 
                            // no se podrán enviar nuevas solicitudes para ampliar el tiempo restante
                            testChallengeClient.getTestPanel().getPreguntasPanel().setAmpliarSegundosPanelEnabled(false);

                            // Si la respuesta enviada por el usuario es correcta y, además, es la primera ...
                            // se actualiza la puntuación en la pregunta
                            testChallengeClient.getTestPanel().getPreguntasPanel().
                                    setPuntuacionPreguntaActual(Puntuacion.CORRECTA_Y_PRIMERA);
                            break;
                        case PREGUNTA_CONTESTADA_CORRECTAMENTE:
                            // Si la respuesta enviada es correcta, pero el usuario no ha sido el primero en contestar
                            testChallengeClient.getTestPanel().getPreguntasPanel().
                                    setPuntuacionPreguntaActual(Puntuacion.CORRECTA);
                            break;
                        case PREGUNTA_NO_CONTESTADA_CORRECTAMENTE:
                            // Si la respuesta enviada por el usuario es incorrecta, se actualiza la puntuación
                            testChallengeClient.getTestPanel().getPreguntasPanel().
                                    setPuntuacionPreguntaActual(Puntuacion.INCORRECTA);
                            break;
                        case PREGUNTA_NO_RESPONDIDA:
                            // Si la respuesta enviada no contiene opciones seleccionadas
                            testChallengeClient.getTestPanel().getPreguntasPanel().
                                    setPuntuacionPreguntaActual(Puntuacion.NO_RESPONDIDA);
                            break;
                        default:
                            // En cualquier otro caso, se asume que se trata de un intercambio de mensajes de texto en el chat
                            testChallengeClient.getChatPanel().addMessage(mensaje.getTexto());
                            break;
                    }
                }
            } while (mensaje != null);

        } catch (IOException | ClassNotFoundException ioe) {
            logger.severe(ioe.getMessage());
        } finally {
            logger.info(String.format("'%s': Servicio finalizado para '%s'.",
                    TestChallengeClientThread.class.getSimpleName(), nickname));
            // Si el hilo de servicio se finaliza (servidor terminado), finalizamos también el hilo padre
            testChallengeClient.terminar();
        }
    }

}
