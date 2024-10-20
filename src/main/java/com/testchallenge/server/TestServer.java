
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
package com.testchallenge.server;

import com.testchallenge.model.Configuracion;
import com.testchallenge.model.Mensaje;
import com.testchallenge.model.Pregunta;
import com.testchallenge.model.Puntuacion;
import com.testchallenge.model.Ranking;
import com.testchallenge.model.Respuesta;
import com.testchallenge.model.TipoMensaje;
import com.testchallenge.model.TipoPregunta;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

/**
 * Clase que implementa un servidor de test que se ejecuta en un hilo independiente.
 *
 * @author japrada
 */
public class TestServer extends Thread {

    // Referencia al hilo que ejecuta el servidor de chat desde el que se arranca el servicio de test
    private TestChallengeServer testChallengeServer;
    // Colección con todas las preguntas definidas actualmente en el banco de preguntas
    private final List<Pregunta> preguntas;
    // Subconjunto con las preguntas seleccionadas para la ejecución del test
    private final List<Pregunta> preguntasSeleccionadas;
    // Pregunta enviada a los clientes conectados
    private Pregunta preguntaEnviada;
    // Puntuaciones parciales de la pregunta que se ha enviado
    private Map<String, Puntuacion> puntuaciones;
    // Puntuaciones finales del test 
    private final Map<String, Integer> resultados;
    // Usuario que solicita el test
    private final String nickname;
    // Temática del test
    private final String tematica;
    // Nivel del test
    private final String nivel;
    // Tipos de preguntas
    private final String[] tipoPreguntas;
    // Tiempo límite establecido
    private final int tiempoLimite;
    // Número de preguntas solicitado
    private final int numeroPreguntas;
    // Generador de números aleatorios
    private static final Random random = new Random();
    // Intervalo de cuenta atrás (cada 1 segundo)
    private static final int SLEEP_TIME = 1000;
    // Segundos para la cuenta atrás del test (por defecto, arranca 10 segundos después de la haber hecho la solicitud)
    private static final int DEFAULT_START_TIME = 10;
    // Número de segundos que dispone el usuario para enviar la respuesta
    private int segundos = DEFAULT_START_TIME;
    // Subdirectorio en el que se encuentran las imagenes de las preguntas  
    private static final String SUBDIRECTORIO_MULTIMEDIA = "Multimedia";
    // Flag para indicar que se desea interrumpir la cuenta atrás
    private boolean interrumpirCuentaAtras = false;
    // Ampliación de tiempo de respuesta (en segundos)
    private int segundosAmpliacionTiempoRespuesta = 0;
    // Flag para indicar que la pregunta ha sido respondida correctamente (y es la primera vez)
    private boolean preguntaContestadaCorrectamente = false;
    // Flag que indica si el test ha sido pausado o no para controlar la cuenta atrás
    private Boolean isPaused;
    // Flag que indica que el test ha sido terminado por el TestChallengeServer (no hay que actualizar el ranking)
    private Boolean isTerminatedByServer;

    // Logger de la clase
    private final static Logger logger = Logger.getLogger(TestServer.class.getName());

    /**
     * Construye un objeto de tipo <code>TestServer</code> con la configuración especificada.
     *
     * @param nickname nombre de registro del usuario que inicia el test.
     * @param configuracion configuración especificada en el panel por el usuario que inicia el test.
     *
     */
    public TestServer(String nickname, Configuracion configuracion) {
        super();
        this.nickname = nickname;
        tematica = configuracion.getTematica();
        nivel = configuracion.getNivel();
        tipoPreguntas = configuracion.getTiposPreguntas();
        numeroPreguntas = configuracion.getNumeroPreguntas();
        tiempoLimite = configuracion.getTiempoLimite();
        preguntas = new ArrayList<>();
        preguntasSeleccionadas = new ArrayList<>();
        resultados = new HashMap<>();
        isPaused = Boolean.FALSE;
        isTerminatedByServer = Boolean.FALSE;
    }

    @Override
    public void run() {
        logger.info(String.format("'%s': Iniciando el TestServer ...", TestServer.class.getSimpleName()));

        try {
            // Notificar a todos los clientes que se ha solicitado la ejecución de un test
            enviarMensaje(new Mensaje(mensajeInicioTest()));
            // Notificar a todos los clientes que se está preparando el test
            enviarMensaje(new Mensaje("[•] Preparando el test ... "));
            // Cargar en memoria el banco de preguntas
            cargarPreguntas();
            // Seleccionar el subconjunto de preguntas a partir de los parámetros de configuración establecidos
            seleccionarPreguntasTest();

            if (!preguntasSeleccionadas.isEmpty()) {
                // Notificar a los usuarios que la preparación del test ha finalizado 
                enviarMensaje(new Mensaje(
                        String.format(
                                "[•] Preparación del test finalizada.\n[•] El test tiene '%d' preguntas y comenzará en '%d' segundos.",
                                preguntasSeleccionadas.size(), DEFAULT_START_TIME)));

                // Iniciar la cuenta atrás para el comienzo del test
                startCountDown(DEFAULT_START_TIME, SLEEP_TIME);
                // Notificar a todos los clientes que la cuenta atrás ha finalizado y comienza la ejecución del test
                enviarMensaje(new Mensaje("[•] El test comienza ¡YA!. ¡Buena suerte!\n"));
                // Ejecutar el test: enviar las preguntas a todos los clientes conectados
                enviarPreguntas();
                // Notificar que el test ha finalizado y
                enviarMensaje(new Mensaje(String.format("\n[•] El test ha finalizado a las %s.",
                        new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()))));
                // actualizar el ranking con los resultados finales.
                if (!isTerminatedByServer) {
                    actualizarRanking();
                }
            } else {
                // No se han encontrado preguntas para los criterios seleccionados. El test no se ejecutará
                enviarMensaje(new Mensaje(null, TipoMensaje.TEST_PARAR));
            }

        } catch (InterruptedException | IOException ex) {
            logger.severe(ex.getMessage());
        } finally {
            logger.info(String.format("'%s': servidor finalizado.", TestServer.class.getSimpleName()));
            testChallengeServer.stopTest();
        }
    }

    /**
     * Método helper que actualiza el ranking y lo envía a los clientes.
     *
     * @throws IOException excepción al enviar el mensaje por el canal de escritura.
     */
    private void actualizarRanking() throws IOException {
        logger.info(String.format("Resultados del test [%s]:", resultados));

        enviarMensaje(new Mensaje(String.format("\n[•] Resultados del test: %s", resultados)));

        enviarMensaje(new Mensaje(getRankingActualizado(), TipoMensaje.TEST_PARAR));
    }

    /**
     * Método helper que construye el mensaje que notifica a los clientes conectados que se va a realizar un test.
     *
     * @return mensaje que notifica a los clientes conectados que se va a realizar un test.
     */
    private String mensajeInicioTest() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n ------> Test solicitado por el usuario @%s a las %s.",
                nickname, sdf.format(new Date())));
        sb.append(String.format("\n[•] [Temática = %s, Nivel = %s, Tipo = %s, Nº preguntas = %d, Tiempo límite = %d segundos].",
                tematica, nivel, Arrays.toString(tipoPreguntas), numeroPreguntas, tiempoLimite));
        return sb.toString();
    }

    /**
     * Recibe la respuesta enviada por el usuario indicado.
     *
     * El método se sincroniza para garantizar que sólo se procesa una petición de forma completa en exclusión.
     *
     * @param nickname usuario que envía la respuesta.
     * @param respuestaRecibida respuesta recibida por el servidor.
     * @throws IOException excepción al enviar el mensaje por el canal de escritura.
     */
    public synchronized void recibirRespuesta(String nickname, Respuesta respuestaRecibida) throws IOException {
        logger.info(String.format("'%s': respuesta '%s' recibida de '%s'",
                TestServer.class.getSimpleName(),
                respuestaRecibida,
                nickname));

        // Recibida la respuesta se comprueba si es correcta (o no) para el conteo de puntos
        Respuesta respuestaEnviada = new Respuesta(
                preguntaEnviada.getRespuestas(),
                preguntaEnviada.getTipo(),
                preguntaEnviada.getNumeroOrden());

        // Si la respuesta recibida coincide con la respuesta de la pregunta que se ha enviado ...
        if (respuestaRecibida.equals(respuestaEnviada)) {
            // El usuario ha contestado correctamente a la pregunta: analizar qué puntos se lleva en función
            // de si es el primero en contestarla o no
            boolean blnYaContestada = false;

            for (String nicknameKey : puntuaciones.keySet()) {
                Puntuacion puntuacion = puntuaciones.get(nicknameKey);
                blnYaContestada = puntuacion != null && puntuacion.equals(Puntuacion.CORRECTA_Y_PRIMERA);
                if (blnYaContestada) {
                    break;
                }
            }

            if (!blnYaContestada) {
                // Acierta y contesta el primero
                puntuaciones.put(nickname, Puntuacion.CORRECTA_Y_PRIMERA);
                preguntaContestadaCorrectamente = true;
                enviarMensaje(new Mensaje(TipoMensaje.PREGUNTA_CONTESTADA_CORRECTAMENTE_Y_PRIMERA), nickname);
                enviarMensaje(new Mensaje(
                        String.format("\n'%s' ha contestado el primero correctamente :-)", nickname)));
            } else {
                // Acierta, pero no ha contestado el primero
                puntuaciones.put(nickname, Puntuacion.CORRECTA);
                enviarMensaje(new Mensaje(TipoMensaje.PREGUNTA_CONTESTADA_CORRECTAMENTE), nickname);
                enviarMensaje(new Mensaje(
                        String.format("\n'%s' ha contestado correctamente, pero no ha sido el primero :-(", nickname)));
            }

        } else {
            // Respuesta incorrecta
            puntuaciones.put(nickname, Puntuacion.INCORRECTA);
            enviarMensaje(new Mensaje(TipoMensaje.PREGUNTA_NO_CONTESTADA_CORRECTAMENTE), nickname);
            enviarMensaje(new Mensaje(
                    String.format("\n'%s' no ha contestado correctamente :-(", nickname)));
        }

        // Si todos los usuarios conectados han enviado su respuesta, interrumpimos la cuenta atrás
        // NOTA: tener en cuenta que un cliente se puede desconectar en cualquier momento a la hora de hacer el conteo
        if (todasLasRespuestasHanSidoEnviadas()) {
            interrumpirCuentaAtras = true;
            // Antes de lanzar la siguiente pregunta, actualizar la tabla de resultados con las puntuaciones obtenidas
            logger.info("Todos los clientes han enviado su respuesta. Se pasa automáticamente a la siguiente pregunta.");
            enviarMensaje(new Mensaje("Todos los clientes han enviado su respuesta. Se pasa automáticamente a la siguiente pregunta."));
        }

        // NOTA (2): esta implementación es para cuando sólo hay un cliente conectado y ha enviado la respuesta.
        /*
        if (testChallengeServer.getClientesConectados().size() == 1) {
            interrumpirCuentaAtras = true;
        }
         */
    }

    /**
     * Establece la referencia al servidor de chat desde el que se ha creado el servidor de test.
     *
     * @param testChallengeServer referencia al servidor de chat desde el que se ha creado el servidor de test.
     */
    public void setTestChallengeServer(TestChallengeServer testChallengeServer) {
        this.testChallengeServer = testChallengeServer;
    }

    /**
     * Recoge el valor de la ampliación del tiempo de respuesta solicitada por el cliente, en segundos.
     *
     * @param segundosAdicionales segundos adicionales a añadir al tiempo límite para enviar una respuesta
     */
    public synchronized void ampliarTiempoRespuesta(String segundosAdicionales) {
        segundosAmpliacionTiempoRespuesta = Integer.parseInt(segundosAdicionales);

        //if (!preguntaContestadaCorrectamente && segundosAmpliacionTiempoRespuesta > 0) {
        if (segundosAmpliacionTiempoRespuesta > 0) {
            segundos += segundosAmpliacionTiempoRespuesta;
            segundosAmpliacionTiempoRespuesta = 0;
        }
    }

    /**
     * Detiene la ejecución del test a solicitud de un usuario.
     *
     * @param nickname nickname del usuario que solicita detener el test.
     */
    public synchronized void stopTest(String nickname) {
        logger.info(String.format("Parada de test solicitada por '%s'", nickname));
        interrupt();
    }

    /**
     * Detiene la ejecución del test a solicitud del servidor (no hay más usuarios conectados).
     *
     */
    public synchronized void stopTest() {
        logger.info("El test se ha detenido porque no hay usuarios conectados");
        isTerminatedByServer = Boolean.TRUE;
        interrupt();
    }

    /**
     * Pausa la ejecución del test a solicitud de un usuario.
     *
     * @param nickname nickname del usuario que solicita pausar el test.
     */
    public synchronized void pauseTest(String nickname) {
        logger.info(String.format("Pausa del test solicitada por '%s'", nickname));
        // Notificar a los clientes conectados que TODAVÍA NO HAN enviado la respuesta para ajustar la UI
        // NOTA: además, no se debe notificar al que ha enviado el mensaje (nickname)
        if (!isPaused) {
            isPaused = Boolean.TRUE;
            // Notificar a los clientes conectados que TODAVÍA NO HAN enviado la respuesta para ajustar la UI
            enviarPauseResume(new Mensaje(TipoMensaje.TEST_PAUSADO), nickname);
        }
    }

    /**
     * Envía un mensaje Pause/Resume a todos los clientes conectados que no han enviado una respuesta.
     *
     * @param mensaje mensaje Pause/Resume a todos los clientes conectados que no han enviado una respuesta.
     * @param nickname nickname del cliente conectado que recibe el mensaje.
     */
    private void enviarPauseResume(Mensaje mensaje, String nickname) {

        for (String aNickname : puntuaciones.keySet()) {
            if (!aNickname.equals(nickname)) {
                Puntuacion puntuacion = puntuaciones.get(aNickname);
                if (puntuacion.equals(Puntuacion.NO_CONTESTADA)) {
                    enviarMensaje(mensaje, aNickname);
                }
            }
        }
    }

    /**
     * Resume la ejecución del test a solicitud de un usuario.
     *
     * @param nickname nickname del usuario que solicita pausar el test
     */
    public synchronized void resumeTest(String nickname) {
        logger.info(String.format("Reanudación del test solicitada por '%s'", nickname));
        if (isPaused) {
            isPaused = Boolean.FALSE;
            // @TODO: Notificar a los clientes conectados que TODAVÍA NO HAN enviado la respuesta para ajustar la UI
            enviarPauseResume(new Mensaje(TipoMensaje.TEST_REANUDADO), nickname);
        }
    }

    /**
     * Cuenta atrás para el inicio del test y el envío de la siguiente pregunta.
     *
     * @param tiempoLimite tiempoLimite de la cuenta atrás
     * @param sleepTime número de milisegundos para hacer el conteo
     * @throws InterruptedException excepción al interrumpir la ejecución del hilo que lleva la cuenta atrás
     */
    private void startCountDown(int tiempoLimite, int sleepTime) throws InterruptedException {

        Integer segundosTranscurridos = 0;

        segundos = tiempoLimite;
        int tiempoRestante = segundos;

        interrumpirCuentaAtras = false;

        while (segundosTranscurridos <= segundos && !interrumpirCuentaAtras) {
            // Enviar el contador a los clientes conectados
            enviarMensaje(new Mensaje(Integer.toString(tiempoRestante), TipoMensaje.TIMER_TICK));

            Thread.sleep(sleepTime);

            // Si el test está pausado, la cuenta atrás se detiene
            if (!isPaused) {
                // En caso contrario, el test está en ejecución y se descuentan los segundos transcurridos al tiempo restante
                segundosTranscurridos += sleepTime / SLEEP_TIME;
                tiempoRestante = segundos - segundosTranscurridos;
            }
        }
    }

    /**
     * Método helper para carga en memoria las preguntas del banco de preguntas.
     */
    private void cargarPreguntas() {

        String rutaCompleta = getRutaCompletaTematica();

        // Cargar en memoria todas las preguntas que se encuentran en el directorio especificado
        File directorio = new File(rutaCompleta);

        Collection<File> files = FileUtils.listFiles(directorio, new String[]{"json"}, false);

        for (File f : files) {
            try {
                String json = FileUtils.readFileToString(f, StandardCharsets.UTF_8.name());
                Pregunta pregunta = new Pregunta(json);

                // Si se ha especificado un fichero multimedia recuperamos su contenido (byte[])
                if (!pregunta.getFicheroMultimedia().isEmpty()) {
                    cargarFicheroMultimedia(pregunta);
                }
                preguntas.add(pregunta);
            } catch (IOException ex) {
                logger.severe(ex.getMessage());
            }
        }
    }

    /**
     * Obtiene la ruta completa del directorio raíz en el que se encuentran las preguntas a partir de la temática
     * seleccionada y el directorio base especificado como parámetro en el arranque del servidor.
     *
     * @return obtiene la ruta completa a partir de la temática seleccionada.
     *
     */
    private String getRutaCompletaTematica() {

        String rutaBase = testChallengeServer.getDirectorioRaizPreguntas();
        String rutaCompleta = rutaBase;

        if (!rutaBase.endsWith("/")) {
            rutaCompleta = rutaCompleta.concat("/").concat(tematica);
        } else {
            rutaCompleta = rutaCompleta.concat(tematica);
        }
        return rutaCompleta;
    }

    /**
     * Carga el fichero multimedia como un <code>byte[]</code> en la pregunta.
     *
     * @param pregunta pregunta con el fichero multimedia que hay que enviar a los clientes.
     */
    private void cargarFicheroMultimedia(Pregunta pregunta) {

        FileInputStream fis = null;
        try {
            String ficheroMultimediaConRutaCompleta
                    = getRutaCompletaTematica().concat("/").
                            concat(SUBDIRECTORIO_MULTIMEDIA).concat("/").
                            concat(pregunta.getFicheroMultimedia());

            if (pregunta.isFicheroMultimediaUnAudio()) {
                // Si el el archivo multimedia es un .mp3, obtenemos la duración en segundos
                File mp3File = new File(ficheroMultimediaConRutaCompleta);
                AudioFile audioFile = AudioFileIO.read(mp3File);
                long secondsLength = audioFile.getAudioHeader().getTrackLength();
                pregunta.setDuracionDelAudioEnSegundos(secondsLength);
            }

            // Leemos el fichero como un array de bytes
            File file = new File(ficheroMultimediaConRutaCompleta);
            fis = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            // Le pasamos el array de bytes a la pregunta, para que se envíe al cliente
            pregunta.setFicheroMultimediaData(bytes);
            pregunta.setFicheroMultimedia(ficheroMultimediaConRutaCompleta);
        } catch (IOException
                | CannotReadException
                | TagException
                | ReadOnlyFileException
                | InvalidAudioFrameException ex) {
            logger.severe(ex.getMessage());
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ioex) {
                logger.severe(ioex.getMessage());
            }
        }
    }

    /**
     * Método helper para seleccionar las preguntas teniendo en cuenta la la configuración establecida por el usuario
     * que inicia el test.
     */
    private void seleccionarPreguntasTest() {

        // Primero creamos la colección de todas las preguntas que han sido definidas con ese nivel
        List<Pregunta> preguntasSeleccionadasConUnNivel = preguntas.stream()
                .filter(pregunta -> pregunta.getNivel().equals(nivel))
                .collect(Collectors.toList());

        // A continuación, elegimos sólo las preguntas del tipo especificado
        List<String> tipoPreguntasList = Arrays.asList(tipoPreguntas);

        List<Pregunta> preguntasSeleccionadasConUnNivelYUnTipo = preguntasSeleccionadasConUnNivel.stream()
                .filter(pregunta -> tipoPreguntasList.contains(pregunta.getTipo().getTipo()))
                .collect(Collectors.toList());

        // Después comprobamos que el número de preguntas del test que se ha pasado como parámetro desde la interfaz
        // de usuario no supera el número de preguntas del banco de preguntas. Nos quedamos con el mínimo de los dos.
        int numeroFinalPreguntas = numeroPreguntas > preguntasSeleccionadasConUnNivelYUnTipo.size()
                ? preguntasSeleccionadasConUnNivelYUnTipo.size() : numeroPreguntas;

        // Seleccionar aleatoriamente un subconjunto de ellas en función del nivel especificado
        for (int i = 0; i < numeroFinalPreguntas; i++) {
            // Genera un número aleatorio entre [0 y size-1]
            int indicePreguntaSeleccionada = random.nextInt(preguntasSeleccionadasConUnNivelYUnTipo.size());
            Pregunta preguntaSeleccionada = preguntasSeleccionadasConUnNivelYUnTipo.get(indicePreguntaSeleccionada);
            preguntaSeleccionada.setPuntuacion(Puntuacion.NO_CONTESTADA);
            preguntasSeleccionadas.add(preguntaSeleccionada);
            // Quitamos la pregunta seleccionada de la colección para que no se repita 
            preguntasSeleccionadasConUnNivelYUnTipo.remove(indicePreguntaSeleccionada);
        }
    }

    /**
     * Método helper que envía las preguntas a los clientes conectados.
     */
    private void enviarPreguntas() {
        // Enviar las preguntas
        int i = 1;

        try {
            for (Pregunta pregunta : preguntasSeleccionadas) {
                // Flag para controlar el panel que permite ampliar el tiempo de respuesta
                preguntaContestadaCorrectamente = false;
                // Inicializar las puntuaciones para la pregunta
                inicializarPuntuaciones();
                // Pregunta enviada
                preguntaEnviada = pregunta;
                // Título de la pregunta
                String preguntaTitle = String.format("Pregunta '%d / %d'", i, preguntasSeleccionadas.size());
                pregunta.setTitle(preguntaTitle);
                pregunta.setNumeroOrden(i);

                // Comprobar si la pregunta tiene el flag desordenar_opciones activado
                if (pregunta.getDesordenarOpcionesFlag()) {
                    // Barajar las opciones
                    barajarOpciones(pregunta);
                }

                // Informar del número de la pregunta del Test que se está ejecutando (útil para ver conteos por pregunta)
                enviarMensaje(new Mensaje("[•] " + preguntaTitle));

                // Envíar la pregunta a todos los clientes conectados
                enviarMensaje(new Mensaje(pregunta));

                // Informar del tiempo restante hasta enviar la siguiente pregunta
                startCountDown(tiempoLimite, SLEEP_TIME);

                // Antes de lanzar la siguiente pregunta, actualizar la tabla de resultados con las puntuaciones obtenidas
                logger.info(String.format("Resultados de la pregunta '%d':%s", i, puntuaciones));

                actualizarResultados();

                // y enviar un mensaje con las puntuaciones obtenidas
                enviarMensaje(new Mensaje(String.format("\nPuntuaciones: %s\n", puntuacionesToString())));

                // Enviamos la siguiente pregunta
                i++;

            }
        } catch (InterruptedException ie) {
            // Si un usuario ha detenido el test ...
            logger.info(ie.getMessage());

            // Se actualizan los resultados a partir de los puntos obtenidos en la última pregunta  
            // teniendo encuenta, además, la penalización que se le aplica al usuario que ha detenido el test.
            Integer penalizacion = (preguntasSeleccionadas.size() - i + 1);
            actualizarResultados(penalizacion, nickname);

            enviarMensaje(new Mensaje(
                    String.format(
                            "\n[•] El usuario '%s' ha sido penalizado con '%d' puntos por cancelar el test.",
                            nickname, penalizacion)));
        }

    }

    /**
     * Genera una nueva secuencia en orden aleatorio de las opciones para las preguntas de tipo:
     *
     * - <code>TipoPregunta.RESPUESTA_MULTIPLE</code>, - <code>TipoPregunta.RESPUESTA_UNICA</code> -
     * <code>TipoPregunta.RESPUESTA_EMPAREJADA</code> - <code>TipoPregunta.RESPUESTA_MULTIVALOR</code>.
     *
     * @param pregunta pregunta que se está procesando.
     */
    private void barajarOpciones(Pregunta pregunta) {

        TipoPregunta tipoPregunta = pregunta.getTipo();

        if (tipoPregunta.equals(TipoPregunta.RESPUESTA_MULTIPLE)
                || tipoPregunta.equals(TipoPregunta.RESPUESTA_UNICA)
                || tipoPregunta.equals(TipoPregunta.RESPUESTA_EMPAREJADA)
                || tipoPregunta.equals(TipoPregunta.RESPUESTA_MULTIVALOR)) {

            // Barajar las opciones
            // --------------------
            // NOTA: para meter la pregunta de tipo "Emparejada", hay que reordenar también 
            // las respuestas. Además, en la de tipo "Multivalor" hay que reordenar los valores de las opciones.
            // Mapa de correspondencia original:
            Map<String, String> opcionesYRespuestasSegunJSON = new HashMap<>();
            Map<String, List<String>> opcionesYValoresSegunJSON = new HashMap<>();

            if (tipoPregunta.equals(TipoPregunta.RESPUESTA_EMPAREJADA)
                    || tipoPregunta.equals(TipoPregunta.RESPUESTA_MULTIVALOR)) {

                // 1. Obtener las opciones en el orden original:
                List<String> opcionesOrdenSegunJSON = pregunta.getOpciones();

                // 2. Obtener el mapping  de las opciones con las respuestas en el orden original:
                List<String> respuestasOrdenSegunJSON = pregunta.getRespuestas();

                for (int i = 0; i < opcionesOrdenSegunJSON.size(); i++) {
                    opcionesYRespuestasSegunJSON.put(opcionesOrdenSegunJSON.get(i),
                            respuestasOrdenSegunJSON.get(i));
                }

                // Además ...
                if (tipoPregunta.equals(TipoPregunta.RESPUESTA_MULTIVALOR)) {
                    // 3. Obtener los valores de las opciones en el orden original:
                    List<List<String>> valoresOpcionesOrdenSegunJSON = pregunta.getValoresOpciones();

                    for (int i = 0; i < valoresOpcionesOrdenSegunJSON.size(); i++) {
                        opcionesYValoresSegunJSON.put(opcionesOrdenSegunJSON.get(i),
                                valoresOpcionesOrdenSegunJSON.get(i));
                    }
                }
            }

            // Reordenación de las opciones
            Collections.shuffle(pregunta.getOpciones());

            // Reajuste de las respuestas si la pregunta es de tipo "Emparejada"
            if (tipoPregunta.equals(TipoPregunta.RESPUESTA_EMPAREJADA)
                    || tipoPregunta.equals(TipoPregunta.RESPUESTA_MULTIVALOR)) {
                // Construir la nueva lista de respuestas a partir del nuevo orden de las preguntas:
                List<String> opcionesReordenadas = pregunta.getOpciones();

                List<String> respuestasReordenadas = new ArrayList<>();

                for (String opcion : opcionesReordenadas) {
                    respuestasReordenadas.add(opcionesYRespuestasSegunJSON.get(opcion));
                }

                pregunta.setRespuestas(respuestasReordenadas);

                // Además ...
                if (tipoPregunta.equals(TipoPregunta.RESPUESTA_MULTIVALOR)) {
                    // Reajustar los valores de las opciones en función de la nueva ordenación  
                    List<List<String>> valoresOpcionesReordenadas = new ArrayList<>();

                    for (String opcion : opcionesReordenadas) {
                        valoresOpcionesReordenadas.add(opcionesYValoresSegunJSON.get(opcion));
                    }

                    pregunta.setValoresOpciones(valoresOpcionesReordenadas);
                }
            }
        }
    }

    /**
     * Método helper para enviar un mensaje a todos los clientes conectados.
     *
     * @param mensaje mensaje a enviar a todos los clientes.
     * @throws IOException excepción al enviar el mensaje por el canal de escritura.
     */
    void enviarMensaje(Mensaje mensaje) {
        // Enviar un mensaje a todos los clientes conectados
        List<TestChallengeServerThread> clientesConectados = testChallengeServer.getClientesConectados();

        try {
            for (TestChallengeServerThread cst : clientesConectados) {
                cst.getClientDataOut().writeObject(mensaje);
                cst.getClientDataOut().flush();
            }
        } catch (IOException ioe) {
            logger.severe(ioe.getMessage());
        }
    }

    /**
     * Método helper para enviar un mensaje al cliente conectado con el nickname especificado.
     *
     * @param mensaje mensaje a enviar al cliente con el nickname <code>nickname</code>.
     * @param nickname cliente conectado con el identificados <code>nickname</code> al que se le envía el mensaje.
     */
    private void enviarMensaje(Mensaje mensaje, String nickname) {
        try {
            TestChallengeServerThread cst = testChallengeServer.getClienteConectado(nickname);
            cst.getClientDataOut().writeObject(mensaje);
            cst.getClientDataOut().flush();
        } catch (IOException ioe) {
            logger.severe(ioe.getMessage());
        }
    }

    /**
     * Método helper para inicializar las puntuaciones de una pregunta de todos los clientes conectados
     */
    private void inicializarPuntuaciones() {
        // Registro de las respuestas (acertadas, no contestadas, falladas, y acertadas pero no la primera
        puntuaciones = new HashMap<>();
        for (String aNickname : testChallengeServer.getNicknames()) {
            puntuaciones.put(aNickname, Puntuacion.NO_CONTESTADA);
        }
    }

    /**
     * Evalúa si todos los clientes han enviado su respuesta devolviendo <code>true</code> en ese caso o
     * <code>false</code> e.c.c.
     *
     * Este método se utiliza para determinar si el servidor debe enviar la siguiente pregunta en el caso en el que
     * todos los clientes hayan enviado su respuesta.
     *
     * @return <code>true</code> si todos los clientes han enviado su respuesta o <code>false</code> e.c.c.
     */
    private boolean todasLasRespuestasHanSidoEnviadas() {
        boolean todasLasRespuestasEnviadas = true;
        for (String nicknameKey : puntuaciones.keySet()) {
            Puntuacion puntuacion = puntuaciones.get(nicknameKey);
            if (puntuacion == Puntuacion.NO_CONTESTADA) {
                todasLasRespuestasEnviadas = false;
                break;
            }
        }
        return todasLasRespuestasEnviadas;
    }

    /**
     * Muestra las puntuaciones obtenidas por los usuarios en una pregunta en formato <nickname> = puntos.
     *
     * @return puntuaciones obtenidas por los usuarios en una pregunta en formato <nickname> = puntos.
     */
    private String puntuacionesToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        Iterator<Map.Entry<String, Puntuacion>> iterator = puntuaciones.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Puntuacion> entry = iterator.next();
            String key = entry.getKey();
            Puntuacion value = entry.getValue();
            sb.append(key).append("=").append(value.getPuntos());
            if (iterator.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Método helper para actualizar la puntuación total del test (resultados) a partir de las puntuaciones parciales
     * obtenidas los usuarios que han enviado una pregunta.
     */
    private void actualizarResultados() {
        for (String nicknameKey : puntuaciones.keySet()) {
            Puntuacion puntuacion = puntuaciones.get(nicknameKey);
            if (puntuacion != null) {
                Integer resultadoParcial = resultados.get(nicknameKey);
                if (resultadoParcial != null) {
                    resultados.put(nicknameKey, resultadoParcial + puntuacion.getPuntos());
                } else {
                    resultados.put(nicknameKey, puntuacion.getPuntos());
                }
            }
        }
    }

    /**
     * Método helper para actualizar los resultados teniendo en cuenta la penalización.
     * 
     * Además, a todos los que no hayan enviado la respuesta cuando se detiene el test se le restará 
     * un punto y, adicionalmente, al usuario que ha detenido el test, también, por respuesta no enviada.
     * 
     * @param penalizacion número de preguntas no contestadas (nº preguntas totales - nº pregunta actual + 1)
     * @param nickname nickname del usuario que ha detenido el test y al que se le aplica la penalización
     */
    private void actualizarResultados(Integer penalizacion, String nickname) {
        // y a la puntuación obtenida por el usuario que ha detenido el test se le restan los puntos 
        // correspondientes a las preguntas que el servidor no ha enviado al detenerse el test.
        actualizarResultados();

        Integer resultadoActual = resultados.get(nickname);

        if (resultadoActual != null) {
            resultados.put(nickname, resultadoActual - penalizacion);
        } else {
            resultados.put(nickname, -penalizacion);
        }
    }

    /**
     * Método helper que actualiza el ranking con los resultados obtenidos al finalizar el test.
     */
    private Ranking getRankingActualizado() throws IOException {
        Map<String, Integer> ranking = testChallengeServer.getRanking();

        for (String nicknameKey : resultados.keySet()) {
            Integer resultado = resultados.get(nicknameKey);
            if (resultado != null) {
                Integer puntuacionRanking = ranking.get(nicknameKey);
                if (puntuacionRanking == null) {
                    // Es la primera vez que el usuario obtiene un punto (0 o 1)
                    ranking.put(nicknameKey, resultado);
                } else {
                    // El usuario ya tenía algún punto (o cero, después de la primera ejecución del test)
                    ranking.put(nicknameKey, puntuacionRanking + resultado);
                }
            } else {
                // El usuario no ha enviado ninguna respuesta en este test (su contador se pone a 0)
                ranking.put(nicknameKey, Integer.valueOf("0"));
            }
        }

        // Envía una copia del ranking actualizado almacenado en el servidor
        return new Ranking(new HashMap<>(ranking));
    }
}
