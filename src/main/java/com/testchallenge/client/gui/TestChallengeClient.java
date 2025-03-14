
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
package com.testchallenge.client.gui;

import com.testchallenge.client.TestChallengeClientThread;
import com.testchallenge.model.Mensaje;
import com.testchallenge.model.Pregunta;
import com.testchallenge.model.Ranking;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Clase principal con la que se construye la interfaz gráfica de usuario (GUI) del cliente.
 *
 * @author japrada
 */
public class TestChallengeClient extends JFrame {

    // Referencia al componente contenedor de la interfaz gráfica 
    private static TestChallengeClient testChallengeClient;
    // Panel que muestra la interfaz gráfica para el intercambio de mensajes en el Chat
    private static ChatPanel chatPanel;
    // Panel que muestra la interfaz gráfica para la realización de los Tests    
    private static TestPanel testPanel;
    // Diálogo con el que se inicia la aplicación para realizar el registro del usuario
    private static RegistroDialog establecerConexionDialog;
    //Stream utilizado para la lectura de datos enviados desde el servidor
    private static ObjectInputStream in = null;
    //Stream utilizado para la escritura de datos hacia el servidor
    private static ObjectOutputStream out = null;
    // Socket para la conexión con el servidor
    private static Socket serverDataSocket = null;
    // Logger de la clase
    private final static Logger logger = Logger.getLogger(TestChallengeClient.class.getName());

    // Dimensiones del Frame
    private final static int CHAT_CLIENT_FRAME_ANCHO = 1430; // 1410
    private final static int CHAT_CLIENT_FRAME_ALTO = 830;
    // Posicion del Divider del JSplitPane
    private final static int DIVIDER_LOCATION = 290;

    /**
     * Muestra el diálogo de registro de usuario y presenta la GUI de la aplicación.
     *
     * @param args (opcionales) usuario, servidor y puerto.
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            if (args.length == 3) {
                testChallengeClient = new TestChallengeClient(args[0], args[1], args[2]);
            } else {
                testChallengeClient = new TestChallengeClient("", "", "");
            }

            
            serverDataSocket = establecerConexionDialog.getServerDataSocket();
            in = establecerConexionDialog.getInputStream();
            out = establecerConexionDialog.getOutputStream();

            try {

                String nickname = establecerConexionDialog.getNickname();
                String server = establecerConexionDialog.getServer();
                int port = establecerConexionDialog.getPort();
                
                // Construir la GUI
                buildGUI(nickname, server, port);

                StringBuilder sb = new StringBuilder();
                // Recibir los nicknames de los clientes conectados en ese momento
                Mensaje nicknamesMensaje = (Mensaje) in.readObject();

                String nicknamesConectadosMessage = nicknamesMensaje.getTexto();
                // Informar al usuario de las opciones disponibles
                sb.append("¡¡Bienvenido a la aplicación!!\n\n");
                sb.append("[•] Puede enviar un chat a un grupo específico de usuarios escribiendo en el mensaje ");
                sb.append("su nickname precedido del símbolo @ (p.e. @jprada)\n\n");
                sb.append("[•] Puede enviar el mensaje BYE para cerrar la sesión y desconectarse del sistema.\n\n");
                sb.append("[•] Para ejecutar un test, establezca la configuración y pulse \"Iniciar Test\".\n\n");
                sb.append(nicknamesConectadosMessage).append("\n");

                chatPanel.addMessage(sb.toString());

                // TEMÁTICAS: Recibir las temáticas (obtenidas a partir de la lista de subdirectorios del directorio base)
                Mensaje tematicasMensaje = (Mensaje) in.readObject();
                String[] tematicas = tematicasMensaje.getTextArray();
                testPanel.getConfiguracionPanel().setTematicas(tematicas);

                // RANKING: Recuperar el ranking actual
                Mensaje rankingActualMensaje = (Mensaje) in.readObject();
                Ranking rankingActual = rankingActualMensaje.getRanking();
                testPanel.setRanking(rankingActual, nickname);

            
                // FLAG TEST EN EJECUCIÓN: Recuperar el flag que indica si hay un test en ejecución
                Mensaje testEnEjecucionFlagMensaje = (Mensaje) in.readObject();

                if (testEnEjecucionFlagMensaje.getFlag().equals(Boolean.TRUE)) {
                    // Recuperar el mensaje de arranque del test
                    Mensaje testEnEjecucionInfoMensaje = (Mensaje) in.readObject();

                    // Informar que hay un test en ejecución y mostrar el mensaje de arranque del test
                    chatPanel.addMessage("\n[•] Hay un test en ejecución!");
                    chatPanel.addMessage(testEnEjecucionInfoMensaje.getTexto());

                    // Recibir la pregunta y presentarla en la UI
                    Mensaje preguntaEnviadaMensaje = (Mensaje) in.readObject();
                    Pregunta preguntaEnviada = preguntaEnviadaMensaje.getPregunta();
                    testPanel.resetPanelPreguntas();
                    testPanel.setPregunta(preguntaEnviada);
                    chatPanel.addMessage("\n[•] ".concat(preguntaEnviada.getTitle()));
                }

                //FLAG TEST_PAUSADO: Recuperar el flag que indica si el test en ejecución está pausado
                Mensaje testPausadoMensaje = (Mensaje) in.readObject();

                if (testPausadoMensaje.getFlag().equals(Boolean.TRUE)) {
                    chatPanel.addMessage("\n[•] El test, además, está pausado!");
                    // Si el test está pausado, hay que mostrar el botón de play activado
                    // Deshabilitar el panel de configuración y el botón Iniciar Test
                    testPanel.getConfiguracionPanel().setEnabled(false);
                    testPanel.getIniciarTestButton().setEnabled(false);
                    testPanel.getPreguntasPanel().setAmpliarSegundosPanelEnabled(false);
                    testPanel.getPreguntasPanel().setStopButtonEnabled(true);
                    testPanel.getPreguntasPanel().setResumeButtonEnabled();
                }

                // Arrancar el hilo de servicio
                startClientThread(nickname);

            } catch (IOException
                    | ClassNotFoundException ex) {
                // Registrar traza de error
                logger.severe(ex.getMessage());
            }
        });
    }

    /**
     * Construye la GUI del cliente.
     *
     * @param usuario usuario/alias/nickname con el que el usuario se registra en la aplicación.
     * @param servidor dirección IP o nombre de host en el que se encuentra en ejecución el servicio.
     * @param puerto puerto en el que se ejecuta el servicio en el servidor.
     */
    public TestChallengeClient(String usuario, String servidor, String puerto) {
        /*
        try {
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
         */
        establecerConexionDialog = new RegistroDialog(this, true);
        establecerConexionDialog.setUsuario(usuario);
        establecerConexionDialog.setServidor(servidor);
        establecerConexionDialog.setPuerto(puerto);
        establecerConexionDialog.setVisible(true);

        // Añade un WindowApapter al JFrame
        addWindowListener(new WindowAdapter() {
            public void windowClosing() {
                System.exit(0);
            }
        });
        /*
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            logger.severe(ex.getMessage());
        }
         */
    }

    /**
     * Método helper para la construcción de la GUI de la aplicación.
     *
     * @param nickname alias del usuario para el que se construye la GUI.
     * @param server servidor con el que el usuario ha establecido la conexión.
     * @param port puerto en el servidor que está ofreciendo el servicio.
     */
    private static void buildGUI(String nickname, String server, int port) {

        testChallengeClient.setTitle(
                String.format("Usuario conectado: @%s [Servidor: %s, Puerto: %d]", nickname, server, port));

        // ********************
        // *    Chat panel    *
        // ********************
        chatPanel = new ChatPanel("Chat", out);

        // ********************
        // *    Test panel    *
        // ********************
        testPanel = new TestPanel("Test", out);
        testPanel.setParent(testChallengeClient);
        testPanel.setModoRevisionEnabled(false);

        // ********************
        // *    Splitpane     *
        // ********************
        // Crear el panel principal
        JSplitPane splitPane = new JSplitPane(
                SwingConstants.VERTICAL, chatPanel, testPanel);
        splitPane.setOneTouchExpandable(true);

        // Establecer la orientación del separador
        splitPane.setOrientation(SwingConstants.VERTICAL);

        splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
                (PropertyChangeEvent pce) -> {
                    int location = (Integer) pce.getNewValue();

                    if (location > DIVIDER_LOCATION) {
                        ((JSplitPane) pce.getSource()).setDividerLocation(CHAT_CLIENT_FRAME_ANCHO);
                    } else {
                        ((JSplitPane) pce.getSource()).setDividerLocation(DIVIDER_LOCATION);
                    }
                });

        // Añadir el Splitpane al JFrame
        Container contentPane = testChallengeClient.getContentPane();
        contentPane.setLayout(new FlowLayout());
        contentPane.add(splitPane);

        // Mostrar la interfaz gráfica
        testChallengeClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        testChallengeClient.setPreferredSize(new Dimension(CHAT_CLIENT_FRAME_ANCHO, CHAT_CLIENT_FRAME_ALTO));
        testChallengeClient.pack();
        testChallengeClient.setLocationRelativeTo(null);
        testChallengeClient.setVisible(true);
        testChallengeClient.setResizable(false);
    }

    /**
     * Método helper para arrancar el thread que recibe los mensajes asíncronos dirigidos al usuario con el nickname
     * específicado.
     *
     * @param nickname alias del usuario para el que se construye el thread de servicio.
     */
    private static void startClientThread(String nickname) {

        //Crear el thread del cliente que escucha los mensajes recibidos por los otros usuarios del chat
        TestChallengeClientThread cct = new TestChallengeClientThread(nickname);

        // Pasar la referencia al stream de lectura desde el server (para leer los mensajes que llegan del servidor)
        cct.setServerDataIn(in);

        // Se pasa la referencia al objeto padre que ha instanciado el thread de servicio para que el hilo
        // hijo pueda acceder a los métodos del padre y realizar determinadas operaciones, como por ejemplo,
        // finalizar la ejecución del mismo cuando el thread de servicio detecta que la ejecución del 
        // server ha finalizado.
        cct.setTestChallengeClient(testChallengeClient);

        //Arrancar el thread 
        cct.start();
    }

    /**
     * Obtiene la referencia al panel de chat.
     *
     * @return referencia al panel de chat.
     */
    public ChatPanel getChatPanel() {
        return chatPanel;
    }

    /**
     * Obtiene la referencia al panel de test.
     *
     * @return referencia al panel de test.
     */
    public TestPanel getTestPanel() {
        return testPanel;
    }

    /**
     * Este método le permite al hilo de servicio <code>TestChallengeClientThread</code> creado por
     * <code>TestChallengeClient</code> finalizar la ejecución del cliente en el caso que el hijo detecte que el
     * servidor ya no está disponible.
     */
    public void terminar() {
        try {
            if (in != null) {
                in.close();
            }

            if (out != null) {
                out.close();
            }

            // Cerramos el socket de conexión con el servidor
            if (serverDataSocket != null) {
                serverDataSocket.close();
            }
        } catch (IOException ioe) {
            // Registrar traza de error
            logger.severe(ioe.getMessage());
        }
        // Terminar la aplicación
        System.exit(0);
    }

}
