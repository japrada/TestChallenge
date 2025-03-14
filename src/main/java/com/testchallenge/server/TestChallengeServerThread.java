
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
import com.testchallenge.model.Respuesta;
import com.testchallenge.model.TipoMensaje;
import java.io.EOFException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

/**
 * Esta clase implementa el hilo de servicio de un cliente conectado al chat, en el lado del servidor, para la lectura
 * asíncrona de los mensajes que se envían.
 *
 * Se encarga de recepcionar los mensajes enviados por el cliente al que da servicio, reenviarlos a los demás usuarios
 * conectados al chat o ejecutar una acción en el lado del servidor.
 *
 * @author jprada
 */
public final class TestChallengeServerThread extends Thread {

    // Referencia al hilo padre que ejecuta el servidor de chat
    private TestChallengeServer testChallengeServer;
    // Referencia al socket para la comunicación con el cliente que se ha conectado
    private Socket clientDataSocket;
    // Nickname o alias del usuario que se ha conectado al chat
    private String nickname;
    // Stream para la escritura de datos al cliente conectado
    private ObjectOutputStream clientDataOut;
    // Stream para la lectura de datos del cliente conectado
    private ObjectInputStream clientDataIn;
    // Patrón para encontrar referencias a nicknames en un mensaje
    //private final Pattern pattern = Pattern.compile("@[a-zA-Z0-9._-]+", Pattern.CASE_INSENSITIVE); 
    private final Pattern pattern = Pattern.compile("@[^@\\s,.;\"'?!#]+", Pattern.CASE_INSENSITIVE); 
    
    // Logger de la clase
    private final static Logger logger = Logger.getLogger(TestChallengeServerThread.class.getName());
    // Directorio en el que se almacenan los archivos multimedia de una temática
    private final static String CARPETA_ARCHIVOS_MULTIMEDIA = "/Multimedia/";

    /**
     * Construye el hilo de servicio para el usuario cuyo nickname se especifica.
     *
     * @param nickname nombre de registro del usuario al que da servicio el thread
     */
    public TestChallengeServerThread(String nickname) {
        this.nickname = nickname;
        testChallengeServer = null;
    }

    /**
     * Devuelve el nickname del usuario al que da servicio el hilo en el proceso servidor.
     *
     * @return nickname del usuario al que da servicio el thread
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Establece el nickname del usuario al que da servicio el thread.
     *
     * @param nickname nombre de registro del usuario al que da servicio el thread
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Establece el socket para el intercambio de datos con el que el hilo servidor se comunica con el cliente.
     *
     * @param clientDataSocket socket paa el intercambio de datos con el que el hilo servidor se comunica con el cliente
     */
    public void setClientDataSocket(Socket clientDataSocket) {
        this.clientDataSocket = clientDataSocket;
    }

    /**
     * Obtiene el socket para el intercambio de datos con el que el hilo servidor se comunica con el cliente.
     *
     * @return socket para el intercambio de datos con el que el hilo servidor se comunica con el cliente
     */
    public Socket getClientDataSocket() {
        return clientDataSocket;
    }

    /**
     * Establece el Stream por el que se leen los datos enviados por el cliente.
     *
     * @param clientDataIn Stream por el que se leen los datos enviados por el cliente
     */
    public void setClientDataIn(ObjectInputStream clientDataIn) {
        this.clientDataIn = clientDataIn;
    }

    /**
     * Establece el Stream por el que se escriben los datos hacia el cliente.
     *
     * @param clientDataOut Stream por el que se escriben los datos hacia el cliente
     */
    public void setClientDataOut(ObjectOutputStream clientDataOut) {
        this.clientDataOut = clientDataOut;
    }

    /**
     * Obtiene el Stream por el que se escriben los datos hacia el cliente.
     *
     * @return Stream por el que se escriben los datos hacia el cliente
     */
    public ObjectOutputStream getClientDataOut() {
        return clientDataOut;
    }

    /**
     * Establece la referencia al servidor de chat desde el que se ha creado este hilo de servicio.
     *
     * @param testChallengeServer referencia al servidor de chat desde el que se ha creado este hilo de servicio
     */
    public void setTestChallengeServer(TestChallengeServer testChallengeServer) {
        this.testChallengeServer = testChallengeServer;
    }

    /**
     * Obtiene la referencia al servidor de chat desde el que se ha creado este hilo de servicio.
     *
     * @return referencia al servidor de chat desde el que se ha creado este hilo de servicio
     */
    public TestChallengeServer getTestChallengeServer() {
        return testChallengeServer;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof TestChallengeServerThread) {
            return ((TestChallengeServerThread) o).getNickname().equals(nickname);
        }
        return false;
    }
    
    @Override
    public void run() {
        try {
            // Informar a los usuarios del chat que el usuario con el nickname especificado se acaba de conectar
            enviarMensajeAlResto(String.format("------> El usuario @%s se ha conectado.", nickname));
            
            Mensaje mensaje;
            
            do {
                // Tratar el mensaje enviado por el cliente
                // ***********************************************
                mensaje = (Mensaje) clientDataIn.readObject();
                
                if (mensaje != null) {
                    TipoMensaje tipoMensaje = mensaje.getTipo();
                    // Analizar si el mensaje contiene la expresión regular @<nickname> de uno o más usuarios. Si el nickname
                    // se corresponde con alguno de los usuarios conectados, se lo envía sólo a ese usuario.
                    if (!tipoMensaje.equals(TipoMensaje.BYE)) {
                        switch (tipoMensaje) {
                            // Se recibe la solicitud de inicio de test enviada por parte del usuario
                            case INICIAR_TEST:
                                if (testChallengeServer.isTestInProgress()) {
                                    logger.info("Ya hay un test en ejecución!!!");
                                } else {
                                    Configuracion configuracion = mensaje.getConfiguracion();
                                    testChallengeServer.startTest(nickname, configuracion);
                                }
                                break;
                            case RESPUESTA_ENVIAR:
                                // Se recibe la respuesta enviada por el usuario
                                Respuesta respuesta = mensaje.getRespuesta();
                                // y se almacena en la lista de respuestas enviadas
                                testChallengeServer.getTestServer().recibirRespuesta(nickname, respuesta);
                                break;
                            case PREGUNTA_ENVIAR:
                                // Se recibe la petición de crear un fichero con la pregunta en el lado del servidor
                                crearPregunta(mensaje.getPregunta());
                                break;
                            case AMPLIAR_TIEMPO_RESPUESTA:
                                String numeroSegundos = mensaje.getTexto();
                                // El cliente solicita ampliar el tiempo de respuesta en el número de segundos indicado
                                testChallengeServer.getTestServer().ampliarTiempoRespuesta(numeroSegundos);
                                // Se envía un mensaje a todos los usuarios de la solicitud (incluyendo el usuario que la ha originado)
                                enviarMensaje(
                                        String.format(
                                                "El usuario @%s ha solicitado ampliar el tiempo de respuesta en %s segundos.",
                                                nickname, numeroSegundos));
                                break;
                            case DETENER_TEST:
                                testChallengeServer.getTestServer().stopTest(nickname);
                                enviarMensaje(
                                        String.format(
                                                "El usuario @%s ha solicitado detener el test.",
                                                nickname));
                                break;
                            case PAUSAR_TEST:
                                testChallengeServer.getTestServer().pauseTest(nickname);
                                enviarMensaje(
                                        String.format(
                                                "El usuario @%s ha solicitado pausar el test.",
                                                nickname));
                                break;
                            case REANUDAR_TEST:
                                testChallengeServer.getTestServer().resumeTest(nickname);
                                enviarMensaje(
                                        String.format(
                                                "El usuario @%s ha solicitado reanudar el test.",
                                                nickname));
                            default:
                                // El cliente envía un mensaje de texto que se debe mostrar en el chat
                                reenviarMensaje(mensaje.getTexto());
                                break;
                        }
                    }
                }
            } while (!(mensaje == null || mensaje.getTipo().equals(TipoMensaje.BYE)));
            
        } catch (IOException | ClassNotFoundException ex) {
            if (!(ex instanceof EOFException)) {
                // Analizar si la excepción no es de tipo EOFException: este tipo de excepción se levanta
                // cuanto un cliente se desconecta del servidor
                logger.severe(ex.getMessage());
            }
        } finally {
            try {
                // Informar al resto de usuarios de la desconexión
                enviarMensajeAlResto(
                        String.format("------> El usuario @%s se ha desconectado.", nickname));
            } catch (IOException ex) {
                logger.severe(ex.getMessage());
            }

            // Registrar la desconexión del usuario en el servidor
            this.testChallengeServer.registrarDesconexion(this);
        }
    }

    /**
     * Método helper para enviar un mensaje a todos los usuarios conectados (incluyendo el usuario que realiza la
     * acción)
     *
     * Este método se utiliza para enviar una notificación a todos los usuarios conectados.
     *
     * @param mensaje mensaje que se envía a todos los usuarios
     * @throws IOException excepción generada al enviar el mensaje por el canal de escritura
     */
    private void enviarMensaje(String mensaje) throws IOException {
        logger.info(mensaje);
        // y se lo reenvía a todos los clientes conectados (menos a él mismo) al servidor de chat
        List<TestChallengeServerThread> clientesConectados = testChallengeServer.getClientesConectados();
        for (TestChallengeServerThread cst : clientesConectados) {
            enviarMensaje(cst, mensaje);
        }
    }

    /**
     * Método helper para envía un mensaje a todos los usuarios conectados (menos al que origina la acción).
     *
     * @param mensaje mensaje que se envía a todos los usuarios (menos al que origina la acción)
     * @throws IOException excepción generada al enviar el mensaje por el canal de escritura
     */
    private void enviarMensajeAlResto(String mensaje) throws IOException {
        reenviarMensajeAlResto(mensaje);
    }

    /**
     * Método helper para reenviar un mensaje intercambiado en el chat al resto de usuarios.
     *
     * @param mensaje mensaje que se reenvía a todos los usuarios
     * @throws IOException excepción generada al enviar el mensaje por el canal de escritura
     */
    private void reenviarMensaje(String mensaje) throws IOException {
        logger.info(String.format("Reenvío del mensaje '%s'.\n", mensaje));
        Set<String> nicknames = getNicknames(mensaje);
        String chatMessage = String.format("@%s:%s", nickname, mensaje);
        if (!nicknames.isEmpty()) {
            reenviarMensajeALosUsuarios(nicknames, chatMessage);
        } else {
            // En caso contrario, reenvíar el mensaje a todos los clientes conectados (menos a él mismo)
            reenviarMensajeAlResto(chatMessage);
        }
    }

    /**
     * Método helper para reenviar un mensaje a todos los usuarios conectados al chat, menos al que lo ha enviado.
     *
     * @param mensaje mensaje a enviar a todos los usuarios conectados al chat
     * @throws IOException excepción generada al enviar el mensaje por el canal de escritura
     */
    private void reenviarMensajeAlResto(String mensaje) throws IOException {
        logger.info(String.format("El mensaje '%s' se reenvía al resto de usuarios.\n",mensaje));
        // y se lo reenvía a todos los clientes conectados (menos a él mismo) al servidor de chat
        List<TestChallengeServerThread> clientesConectados = testChallengeServer.getClientesConectados();
        
        for (TestChallengeServerThread cst : clientesConectados) {
            if (!cst.getNickname().equals(nickname)) {
                enviarMensaje(cst, mensaje);
            }
        }
    }

    /**
     * Método helper para reenviar el mensaje enviado por un usuario sólo a los usuarios con los nicknames indicados
     *
     * @param nicknames nicknames de los usuarios a los que hay que reenviar el mensaje
     * @param mensaje mensaje a enviar a los usuarios cuyos nicknames se especifican
     * @throws IOException excepción generada al enviar el mensaje por el canal de escritura
     */
    private void reenviarMensajeALosUsuarios(Set<String> nicknames, String mensaje) throws IOException {
        List<TestChallengeServerThread> clientesConectados = testChallengeServer.getClientesConectados();
        
        for (String aNickname : nicknames) {
            // Comprobar si el usuario se está reenviando el mensaje a sí mismo 
            if (!aNickname.equals(nickname)) {
                int posicion = clientesConectados.indexOf(new TestChallengeServerThread(aNickname));
                if (posicion != -1) {
                    TestChallengeServerThread cst = clientesConectados.get(posicion);
                    enviarMensaje(cst, mensaje);
                }
            }
        }
    }

    /**
     * Método helper para enviar un mensaje al cliente conectado a través del stream de escritura del socket de datos.
     *
     * @param cst Thread de servicio del cliente conectado
     * @param mensaje mensaje a enviar al cliente conectado a través del stream de escritura del socket de datos
     * @throws IOException excepción generada al enviar el mensaje por el canal de escritura
     */
    private void enviarMensaje(TestChallengeServerThread cst, String mensaje) throws IOException {
        cst.getClientDataOut().writeObject(new Mensaje(mensaje));
        cst.getClientDataOut().flush();
    }

    /**
     * Devuelve los nicknames de los usuarios que se han identificado en el mensaje buscando la expresión regular
     *
     * Se utiliza una colección de tipo Set para no devolver más de una ocurrencia por nickname. Esto
     * evitaría reenvío de un mensaje varias veces a un mismo usuario en el caso de que su nickname apareciera más de
     * una vez en el texto del mensaje.
     *
     * @param mensaje mensaje a enviar al cliente en el que se buscan las ocurrencias de la expresión regular @[A-Z0-9]+
     * @return conjunto (sin duplicados) con los nicknames de los usuarios que aparecen en el mensaje (sin la @)
     */
    private Set<String> getNicknames(String mensaje) {

        // Se utiliza una colección tipo Set para evitar duplicados
        Set<String> nicknamesEncontrados = new HashSet<>();
        
        Matcher matcher = pattern.matcher(mensaje);
        
        while (matcher.find()) {
            // Obtenemos el nickname (sin el carácter @)
            String aNickname = mensaje.substring(matcher.start() + 1, matcher.end());
            // Validar el nickname: el nickname debe corresponderse con el de un usuario conectado al chat.
            if (testChallengeServer.getClientesConectados().contains(new TestChallengeServerThread(aNickname))) {
                nicknamesEncontrados.add(aNickname);
            }
        }
        return nicknamesEncontrados;
    }

    /**
     * Crea una nuevo fichero pregunta en la ruta base establecida como parámetro en el arranque del servidor.
     *
     * @param pregunta la nueva pregunta que se va a crear.
     * @throws IOException excepción generada si hubo algún problema en la creación del fichero.
     */
    private synchronized void crearPregunta(Pregunta pregunta) throws IOException {
        
        String rutaBase = testChallengeServer.getDirectorioRaizPreguntas();
        String rutaCompleta = rutaBase;
        
        if (!rutaBase.endsWith("/")) {
            rutaCompleta = rutaCompleta.concat("/").concat(pregunta.getTematica());
        } else {
            rutaCompleta = rutaCompleta.concat(pregunta.getTematica());
        }
        
        File directorio = new File(rutaCompleta);
        
        FilenameFilter filter = (File f, String name1) -> name1.startsWith("0");
        String[] ficheros = directorio.list(filter);
        
        Integer orden = 1;
        if (ficheros.length > 0) {
            List<String> listFile = Arrays.asList(ficheros);
            Collections.sort(listFile, Collections.reverseOrder());
            String nombreFichero = listFile.get(0);
            orden = Integer.parseInt(nombreFichero.substring(0, nombreFichero.indexOf("."))) + 1;
        }
        
        pregunta.setId(orden);
        
        String nuevaPregunta = String.format("%05d.json", orden);
        // Crear la nueva pregunta
        File preguntaFile = new File(rutaCompleta, nuevaPregunta);
        
        FileUtils.write(preguntaFile, pregunta.toString(), StandardCharsets.UTF_8.name());

        // Si la pregunta tiene asociada un fichero multimedia, lo creamos también.
        if (!pregunta.getFicheroMultimedia().isEmpty()) {
            String ficheroMultimediaNombre = pregunta.getFicheroMultimedia();
            
            File ficheroMultimediaFile = new File(rutaCompleta.concat(CARPETA_ARCHIVOS_MULTIMEDIA), ficheroMultimediaNombre);
            byte[] ficheroMultimediaData = pregunta.getFicheroMultimediaData();
            
            FileUtils.writeByteArrayToFile(ficheroMultimediaFile, ficheroMultimediaData);
        }

        // Enviar un mensaje a todos los usuarios (incluído el que sube la pregunta)
        enviarMensaje(String.format("------> El usuario @%s ha subido una nueva pregunta de tipo '%s'.\n",
                nickname, pregunta.getTipo().getTipo()));
    }
    
}
