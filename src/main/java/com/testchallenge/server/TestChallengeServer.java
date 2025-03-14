
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
import com.testchallenge.model.TipoMensaje;
import com.testchallenge.model.Mensaje;
import com.testchallenge.model.Pregunta;
import com.testchallenge.model.Ranking;
import java.io.File;
import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Esta clase implementa el servidor de chat.
 *
 * Este servidor intercambia mensajes de tipo <code>Mensaje</code> con los clientes los cuales contienen objetos de
 * distinto tipo según la comunicación cliente-servidor que se esté llevando a cabo.
 *
 * El funcionamiento básico es el siguiente:
 *
 * En la inicialización, tras recibir el puerto de escucha del servidor como un parámetro por consola, el servidor se
 * arranca en un nuevo hilo y se queda a la espera de recibir solicitudes de conexión por parte de los clientes.
 *
 * Una vez el cliente se conecta, se lleva a cabo el siguiente protocolo:
 *
 * 1º.- El servidor recibe el nickname.
 *
 * 2º.- Después, el servidor comprueba que el nickname no está siendo utilizado por otro usuario del chat.
 *
 * 3º.- Si el nickname no está en uso, el servidor envía un OK al cliente y a continuación la lista de usuarios que
 * están conectados al chat en ese momento.
 *
 * 4º.- Después, añade el nuevo usuario a la lista de usuarios conectados y arranca la ejecución del hilo de servicio
 * <code>TestChallengeServerThread</code> para gestionar el intercambio de mensajes con el nuevo cliente conectado.
 *
 * En el caso de que el nickname ya esté en uso, el servidor informa al cliente que no se ha podido iniciar la sesión y
 * cierra la conexión.
 *
 * @author jprada
 */
public class TestChallengeServer extends Thread {

    // Lista de clientes conectados
    private final List<TestChallengeServerThread> clientesConectados;
    // Puerto de escucha del servidor
    private final int listeningPort;
    // Flag que indica si el servicio de test está iniciado o no
    private Boolean testIniciado;
    // Flag que indica si el test está pausado o no
    private Boolean testPausado;

    // Servicio de test
    private TestServer testServer;
    // Directorio raíz o base en el que se encuentran las preguntas organizadas por materias
    private final String directorioRaizPreguntas;
    // Ranking mantenido por el servidor durante la sesión
    private final Map<String, Integer> ranking;
    // Logger de la clase
    private final static Logger logger = Logger.getLogger(TestChallengeServer.class.getName());

    public static void main(String[] args) {
        // Recogemos los parámetros en el hilo principal
        int listeningPort = Integer.parseInt(args[0]);
        String directorio = args[1];

        // y arrancamos un nuevo thread de servicio
        new TestChallengeServer(listeningPort, directorio).start();
    }

    /**
     * Construye una instancia que recibe solicitudes de conexiones en el puerto especificado y gestiona el envío y la
     * recepción de mensajes entre los usuarios conectados al chat mediante un hilo de servicio específico para cada
     * cliente de chat conectado.
     *
     * @param listeningPort puerto en el que se encuentra a la escucha el servidor de chat.
     * @param directorioRaizPreguntas directorio raíz en el que se almacenan las preguntas.
     *
     */
    public TestChallengeServer(int listeningPort, String directorioRaizPreguntas) {
        this.listeningPort = listeningPort;
        this.directorioRaizPreguntas = directorioRaizPreguntas;
        clientesConectados = new ArrayList<>();
        ranking = new HashMap<>();
        testIniciado = Boolean.FALSE;
        testPausado = Boolean.FALSE;
    }

    @Override
    public void run() {

        logger.info(String.format("'%s': Iniciando el servidor ...", TestChallengeServer.class.getSimpleName()));

        // Registramos el hook para capturar la combinación de teclas CTRL+C para detener el proceso
        addShutDownHook();

        try {
            // Creamos un socket servidor para aceptar las peticiones de conexión de los clientes en el puerto indicado
            ServerSocket serverSocket = new ServerSocket(listeningPort);
            logger.info(String.format("'%s': Servidor iniciado y escuchando en el puerto '%d'.",
                    TestChallengeServer.class.getSimpleName(), listeningPort));

            // Bucle de lectura que recibe las solicitudes de conexión y las pasa a un hilo de servicio
            while (true) {
                // El servidor se queda bloqueado a la espera de recibir una conexión
                Socket clientDataSocket = serverSocket.accept();

                // Obtener la información de dirección IP y puerto del socket de conexión del sistema cliente
                String dirIPCliente = clientDataSocket.getInetAddress().toString();
                int puertoCliente = clientDataSocket.getPort();

                logger.info(String.format("'%s': Conexión establecida desde la dirección IP '%s' puerto '%d'.",
                        TestChallengeServer.class.getSimpleName(), dirIPCliente, puertoCliente));

                // Se recibe la conexión y se obtienen los streams para la comunicación con el cliente
                ObjectInputStream in = new ObjectInputStream(clientDataSocket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(clientDataSocket.getOutputStream());

                // Se ejecuta el siguiente protocolo:
                // ---------------------------------
                // 1º.- Recibir el nickname
                Mensaje mensaje = (Mensaje) in.readObject();
                String nickname = mensaje.getTexto();
                logger.info(String.format("'%s': Validando el nickname '%s' del usuario ...",
                        TestChallengeServer.class.getSimpleName(), nickname));

                // 2º.- Comprobar si el nickname del cliente es único o ya está en uso
                TestChallengeServerThread testChallengeServerThread = new TestChallengeServerThread(nickname);

                // La clase TestChallengeServerThread reescribe el método equals para poder hacer comparaciones entre objetos
                if (!clientesConectados.contains(testChallengeServerThread)) {
                    // 3º.- Comunicar al cliente que su sesión se ha registrado en el chat
                    out.writeObject(new Mensaje(TipoMensaje.NICKNAME_OK));
                    out.flush();

                    // 4º.- Comunicar al cliente los nicknames de los usuarios que están conectados
                    String nicknamesConectados = getNicknamesConectadosMessage(nickname);
                    out.writeObject(new Mensaje(nicknamesConectados));
                    out.flush();

                    // 5º.- TEMÁTICAS: Indicar al cliente las temáticas disponibles (carpetas en el directorio base)
                    String[] tematicas = getTematicas();
                    out.writeObject(new Mensaje(tematicas));
                    out.flush();

                    logger.info(String.format("'%s': Sesión con el nickname '%s' registrada correctamente.",
                            TestChallengeServer.class.getSimpleName(), nickname));

                    // 6º.- Inicializar el hilo de procesamiento del cliente en el lado del servidor
                    testChallengeServerThread.setClientDataSocket(clientDataSocket);
                    // Se pasa al hilo los streams de lectura y escritura para la comunicación, que ya están inicializados
                    testChallengeServerThread.setClientDataIn(in);
                    testChallengeServerThread.setClientDataOut(out);
                    // Se pasa la referencia al objeto padre que ha instanciado el thread de servicio. Esto le permitirá
                    // al hilo hijo acceder a los métodos del padre para realizar determinadas operaciones. 
                    testChallengeServerThread.setTestChallengeServer(this);

                    // 8º.- RANKING: Enviar el ranking actual al nuevo cliente
                    logger.info(String.format("'%s': Enviando el ranking actual a '%s'.",
                            TestChallengeServer.class.getSimpleName(), nickname));
                    out.writeObject(new Mensaje(new Ranking(ranking), TipoMensaje.RANKING_ACTUAL));
                    out.flush();

                    // 9º.- FLAG TEST EN EJECUCION: Enviar el flag de test iniciado al nuevo cliente                    
                    out.writeObject(new Mensaje(testIniciado, TipoMensaje.TEST_EN_EJECUCION));
                    out.flush();

                    if (testIniciado) {
                        logger.info(String.format("'%s': Enviando el flag que indica que hay un test en ejecución a '%s'.",
                                TestChallengeServer.class.getSimpleName(), nickname));

                        // Enviar un mensaje al cliente con la parametrización del test
                        out.writeObject((new Mensaje(testServer.getMensajeInicioTest())));
                        out.flush();
                        
                        // Enviar la pregunta al cliente 
                        Pregunta preguntaEnviada = testServer.getPreguntaEnviada();
                        logger.info(String.format("'%s': Enviando la pregunta al usuario '%s' recién conectado.",
                                TestChallengeServer.class.getSimpleName(), nickname));
                        
                        out.writeObject(new Mensaje(preguntaEnviada, TipoMensaje.TEST_PREGUNTA));
                        out.flush();

                        // Incializar la puntuación del usuario para la pregunta enviada cuando se incorpora a un test iniciado
                        testServer.inicializarPuntuacionConTestIniciado(nickname);
                    }

                    // 10º.- FLAG TEST PAUSADO: Enviar el flag de test pausado al nuevo cliente
                    out.writeObject(new Mensaje(testPausado, TipoMensaje.TEST_PAUSADO));
                    out.flush();
                    
                    if (testPausado) {
                        logger.info(String.format("'%s': Enviando el flag que indica que el test está pausado a '%s'.",
                                TestChallengeServer.class.getSimpleName(), nickname));
                    }

                    // NOTA: el orden de las operaciones 11 y 12 es importante para que no se produzcan problemas
                    // en el envío de mensajes.
                    
                    // 11º.- Añadir el cliente a la lista de clientes conectados
                    registrarConexion(testChallengeServerThread);

                    // 12º.- Arrancar el hilo de servicio para el nuevo cliente de chat (de ese modo,
                    // el cliente empieza a recibir notificaciones desde el servidor (TIMER_TICK, etc):
                    // Ver clase TestChallengeClientThread.java
                    testChallengeServerThread.start();

                    logger.info(String.format("'%s': Thread de servicio para '%s' arrancado.",
                            TestChallengeServer.class.getSimpleName(), nickname));

                } else {
                    // Se le informa al cliente que el nickname ya está en uso y que no se puede iniciar la sesión
                    out.writeObject(new Mensaje(TipoMensaje.NICKNAME_KO));
                    out.flush();

                    // y se muestra el mensaje en la consola del servidor
                    logger.info(String.format("'%s': El nickname '%s' ya se encuentra registrado.",
                            TestChallengeServer.class.getSimpleName(), nickname));

                    clientDataSocket.close();
                }
            } // while (true)

        } catch (IOException | ClassNotFoundException ex) {
            logger.severe(ex.getMessage());
        } finally {
            logger.info(String.format("'%s': Servidor finalizado.", TestChallengeServer.class.getSimpleName()));
        }

    }

    /**
     * Obtiene el ranking con las puntuaciones de la sesión actual.
     *
     * @return ranking con las puntuaciones de la sesión actual.
     */
    public Map<String, Integer> getRanking() {
        return ranking;
    }

    /**
     * Devuelve la lista de los objetos de tipo <code>TestChallengeServerThread</code> que gestionan las comunicaciones
     * con cada uno de los clientes conectados al chat.
     *
     * @return lista de objetos de tipo <code>TestChallengeServerThread</code> que gestionan las comunicaciones de cada
     * uno de los clientes de clientes conectados al chat.
     */
    public List<TestChallengeServerThread> getClientesConectados() {
        return clientesConectados;
    }

    /**
     * Devuelve el objeto de tipo <code>TestChallengeServerThread</code> que gestionan las comunicaciones con el cliente
     * cuyo nickname es el especificado como parámetro.
     *
     * @param nickname nickname del usuario conectado.
     * @return objeto de tipo <code>TestChallengeServerThread</code> que gestionan las comunicaciones con el cliente.
     */
    public TestChallengeServerThread getClienteConectado(String nickname) {
        TestChallengeServerThread cst = null;

        int posicion = clientesConectados.indexOf(new TestChallengeServerThread(nickname));
        if (posicion != -1) {
            cst = clientesConectados.get(posicion);
        }
        return cst;
    }

    /**
     * Arranca un test a petición del usuario registrado el alias <code>nickname</code>, con la configuración indicada.
     *
     * @param nickname nickname del usuario que solicita la ejecución del test.
     * @param configuracion valores seleccionados en el panel de configuración.
     */
    public synchronized void startTest(String nickname, Configuracion configuracion) {
        if (!testIniciado) {
            logger.info(String.format("------> El usuario @%s ha solicitado ejecutar un test.\n", nickname));
            // Notificar a todos los clientes (salvo el que ha solicitado el inicio del test) 
            // que empieza un nuevo Test, para que reajusten la interfaz 
            enviarTestIniciar(nickname);

            // Iniciar el servidor para ejecutar el test
            testIniciado = Boolean.TRUE;
            testServer = new TestServer(nickname, configuracion);
            testServer.setTestChallengeServer(this);
            testServer.start();
        }
    }

    /**
     * Método helper que envía una notificación a todos los usuarios conectados (menos al que ha realizado la solicitud)
     * de que va a empezar la ejecución de un test.
     *
     * @param nickname nombre de registro del usuario que ha solicitado la ejecución del test
     */
    private void enviarTestIniciar(String nickname) {
        for (TestChallengeServerThread cst : clientesConectados) {
            if (!cst.getNickname().equals(nickname)) {
                try {
                    cst.getClientDataOut().writeObject(new Mensaje(TipoMensaje.INICIAR_TEST));
                    cst.getClientDataOut().flush();
                } catch (IOException ex) {
                    logger.severe(ex.getMessage());
                }
            }
        }
    }

    /**
     * Obtiene una referencia al servidor de test que se ha iniciado.
     *
     * @return referencia al servidor de test que se ha iniciado
     */
    public TestServer getTestServer() {
        return testServer;
    }

    /**
     * Determina si hay un test en ejecución.
     *
     * @return flag que indica si hay un test en ejecución o no.
     */
    public Boolean isTestInProgress() {
        return testIniciado;
    }

    /**
     * Determina si un test en ejecución esta pausado o no.
     *
     * @return flag que indica si un test en ejecución esta pausado o no.
     */
    public Boolean isTestPaused() {
        return testPausado;
    }

    /**
     * Establece a <code>false</code> el valor de la variable de estado que indica si hay un test en ejecución o no.
     */
    public synchronized void stopTest() {
        testIniciado = Boolean.FALSE;
        testPausado = Boolean.FALSE;
    }

    /**
     * Establece a <code>TRUE</code> el valor de la variable de estado que indica que el test está pausado.
     */
    public synchronized void pauseTest() {
        testPausado = Boolean.TRUE;
    }

    /**
     * Establece a <code>FALSE</code> el valor de la variable de estado que indica que el test no está pausado.
     */
    public synchronized void resumeTest() {
        testPausado = Boolean.FALSE;
    }

    /**
     * Obtiene el directorio raíz en el que se encuentra el banco de preguntas.
     *
     * @return directorio raíz en el que se encuentra el banco de preguntas
     */
    public String getDirectorioRaizPreguntas() {
        return directorioRaizPreguntas;
    }

    /**
     * Obtiene la lista de los usuarios conectados.
     *
     * @return lista de los usuarios conectados.
     */
    public List<String> getNicknames() {
        List<String> nicknames = new ArrayList<>();

        for (TestChallengeServerThread cst : clientesConectados) {
            nicknames.add(cst.getNickname());
        }
        return nicknames;
    }

    /**
     * Obtiene los nicknames de los usuarios conectados al servidor, excluyendo el que se pasa como parámetro.
     *
     * @param nickname nickname del usuario que queremos excluir de la lista de usuarios conectados.
     * @return cadena con los nicknames de los usuarios conectados separados por comas.
     */
    private String getNicknamesConectadosMessage(String nickname) {
        StringBuilder sbUsuariosConectados = new StringBuilder();

        for (TestChallengeServerThread cst : clientesConectados) {
            if (!cst.getNickname().equals(nickname)) {
                sbUsuariosConectados.append("@").append(cst.getNickname()).append(", ");
            }
        }

        String mensaje = sbUsuariosConectados.toString();
        if (mensaje.length() == 0) {
            mensaje = "[•] No hay más usuarios conectados.";
        } else {
            String usuarios = mensaje.substring(0, mensaje.length() - 2);
            if (usuarios.contains(",")) {
                String ultimoUsuario = usuarios.substring(usuarios.lastIndexOf(","));
                usuarios = usuarios.replace(ultimoUsuario, " y ".concat(ultimoUsuario.substring(2)));
                mensaje = "[•] Están conectados los usuarios " + usuarios + ".";
            } else {
                mensaje = "[•] Está conectado el usuario " + usuarios + ".";
            }
        }

        return mensaje;
    }

    /**
     * Obtiene las temáticas de los tests a partir de los subdirectorios contenidos en la ruta base.
     *
     * @return array con los nombres de los subdirectorios contenidos en la ruta base.
     */
    private String[] getTematicas() {
        File directorioRaiz = new File(directorioRaizPreguntas);
        return Arrays.stream(directorioRaiz.listFiles(File::isDirectory)).sorted()
                .map(File::getName)
                .toArray(String[]::new);
    }

    /**
     * Registra la desconexión de un usuario del chat, eliminándolo de la lista de usuarios conectados y cerrando el
     * socket de conexión con el cliente.
     *
     * @param cst referencia al hilo que gestiona el cliente conectado del lado del servidor que se va a desconectar.
     */
    public synchronized void registrarDesconexion(TestChallengeServerThread cst) {
        try {
            // Deregistramos el usuario
            clientesConectados.remove(cst);

            // ***********
            // NOTA: no le quitamos los puntos porque tendríamos que actualizar el ranking de todos los usuarios conectados
            // Darle una vuelta por si vemos que tiene sentido hacer esta operación
            //ranking.remove(cst.getNickname());
            // ***********
            // Finalmente, cerramos la conexión.
            cst.getClientDataSocket().close();
            logger.info(String.format("'%s': El usuario '%s' se ha desconectado.",
                    TestChallengeServer.class.getSimpleName(), cst.getNickname()));

            // Si no quedan más usuarios conectados y hay un test en ejecución, detenerlo.
            if (clientesConectados.isEmpty() && (isTestInProgress() || isTestPaused())) {
                testServer.stopTest();

            }
        } catch (IOException ioe) {
            logger.severe(ioe.getMessage());
        }
    }

    /**
     * Registra la conexión de un usuario al chat, añadiéndolo a la lista de usuarios conectados.
     *
     * @param cst referencia al hilo que gestiona el cliente del lado del servidor que se va a conectar.
     */
    public synchronized void registrarConexion(TestChallengeServerThread cst) {
        clientesConectados.add(cst);
    }

    /**
     * Hook para capturar la finalización del proceso mediante CTRL+C
     */
    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info(String.format("'%s': Finalizando el proceso servidor. Enviando notificaciones a los clientes conectados ...",
                    TestChallengeServer.class.getSimpleName()));

            // Enviar un mensaje de notificación a los clientes conectados cuando el proceso servidor se finaliza
            if (!clientesConectados.isEmpty()) {
                for (TestChallengeServerThread cst : clientesConectados) {
                    logger.info(String.format("'%s': Enviando notificación a '%s'.",
                            TestChallengeServer.class.getSimpleName(), cst.getNickname()));
                    ObjectOutputStream out = cst.getClientDataOut();
                    Mensaje mensaje = new Mensaje(
                            String.format("'%s': El proceso servidor ha finalizado.",
                                    TestChallengeServer.class.getSimpleName()));
                    try {
                        out.writeObject(mensaje);
                        out.flush();
                    } catch (IOException ex) {
                        logger.severe(ex.getMessage());
                    }
                }
            } else {
                logger.info(String.format("'%s': No hay clientes conectados.",
                        TestChallengeServer.class.getSimpleName()));
            }
            // Registrar el mensaje en la consola del servidor
            logger.info(String.format("'%s': El proceso servidor ha finalizado.",
                    TestChallengeServer.class.getSimpleName()));
        }));
    }

}
