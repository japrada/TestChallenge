
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

import com.testchallenge.model.Mensaje;
import com.testchallenge.model.TipoMensaje;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/**
 * Panel que construye la parte de la interfaz que se utiliza para el intercambio de mensajes de chat.
 *
 * Este panel contiene dos áreas de texto: una para recibir los mensajes enviados por el servidor y/o los usuarios y
 * otra para enviar los mensajes al grupo de usuarios conectados o el comando de desconexión BYE.
 *
 * @author japrada
 */
public class ChatPanel extends ConectablePanel {

    // Area en la que se muestran los mesajes intercambiados en el chat
    private final JTextArea chatTextArea;
    // Area en la que se escribe el mensaje que se va a enviar
    private final JTextArea messageTextArea;
    // Botón para enviar el mensaje
    private final JButton enviarChatButton;
    // Botón para borrar los mensajes intercambiados hasta el momento en el chat
    private final JButton borrarMensajesButton;
    
    // Dimensiones del Panel
    private final static int CHAT_PANEL_ANCHO = 200;
    private final static int CHAT_PANEL_ALTO = 500;

    // Logger de la clase
    private final static Logger logger = Logger.getLogger(ChatPanel.class.getName());

    /**
     * Constructor de la clase.
     *
     * @param title título del panel
     * @param out canal para el envío de datos al servidor
     */
    public ChatPanel(String title, ObjectOutputStream out) {
        super(title, out);

        setLayout(new BorderLayout());

        // TextArea que muestra los mensajes de chat intercambiados
        chatTextArea = new JTextArea();
        chatTextArea.setLineWrap(true);
        chatTextArea.setWrapStyleWord(true);
        chatTextArea.setEditable(false);
        
        JScrollPane spChatTextArea = new JScrollPane(chatTextArea);
        spChatTextArea.setPreferredSize(new Dimension(CHAT_PANEL_ANCHO, CHAT_PANEL_ALTO));

        add(spChatTextArea, BorderLayout.NORTH);

        // TextArea que permite enviar los chats
        messageTextArea = new JTextArea();
        messageTextArea.setLineWrap(true);
        messageTextArea.setWrapStyleWord(true);

        JScrollPane spMessageTextArea = new JScrollPane(messageTextArea);
        spMessageTextArea.setPreferredSize(new Dimension(CHAT_PANEL_ANCHO, 110));
        add(spMessageTextArea, BorderLayout.CENTER);

        // Añadir los botones del panel
        JPanel buttonsPanel = new JPanel();

        enviarChatButton = new JButton("Enviar chat");
        borrarMensajesButton = new JButton("Borrar mensajes");

        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.add(enviarChatButton);
        buttonsPanel.add(borrarMensajesButton);

        enviarChatButton.setMnemonic(KeyEvent.VK_E);
        borrarMensajesButton.setMnemonic(KeyEvent.VK_B);

        // Manejadores de eventos (handlers) de los botones
        enviarChatButton.addActionListener((ActionEvent e) -> {
            enviarChatButtonSwingWorker();
        });

        borrarMensajesButton.addActionListener((ActionEvent e) -> {
            borrarMensajesButtonSwingWorker();
        });

        add(buttonsPanel, BorderLayout.SOUTH);
    }

    /**
     * Añade un mensaje al área de chat.
     *
     * @param message mensaje que se añade al área de chat.
     */
    public void addMessage(String message) {
        addMessageToChat(message);
    }

    /**
     * Método helper para añadir el mensaje al área de chat.
     *
     * @param message mensaje a añadir al área de chat.
     */
    private void addMessageToChat(String message) {
        if (chatTextArea != null) {
            chatTextArea.append(message);
            chatTextArea.append("\n");
            chatTextArea.setCaretPosition(chatTextArea.getText().length());
            chatTextArea.getCaret().setVisible(true);
            logger.info(String.format("'%s': %s", ChatPanel.class
                    .getSimpleName(), message));
        }
    }

    /**
     * Método helper que gestiona en un Thread independiente los eventos generados por el botón
     * <code>enviarChatButton</code>.
     */
    private void enviarChatButtonSwingWorker() {

        SwingWorker sw;
        sw = new SwingWorker() {

            @Override
            protected String doInBackground() throws Exception {

                String texto = messageTextArea.getText();

                if (!texto.equals("")) {
                    // Escribir el mensaje en el ChatTextArea
                    chatTextArea.append(texto);
                    chatTextArea.append("\n");

                    // *********  Enviar el mensaje al servidor (hilo de servicio TestChallengeServerThread) *********
                    Mensaje mensaje;

                    if (texto.equals(TipoMensaje.BYE.toString())) {
                        mensaje = new Mensaje(TipoMensaje.BYE);
                    } else {
                        mensaje = new Mensaje(texto);

                    }
                    out.writeObject(mensaje);
                    out.flush();
                    // ***************************************************************************************

                    // Establece la nueva posición del cursor
                    chatTextArea.setCaretPosition(chatTextArea.getText().length());
                    //chatTextArea.getCaret().setVisible(true);
                }

                logger.info(String.format("'%s': \"Enviando mensaje: '%s' \n",
                        ChatPanel.class.getSimpleName(), messageTextArea.getText()));

                messageTextArea.setText("");
                messageTextArea.requestFocusInWindow();

                return "Ejecución completada.";
            }
        };

        // El SwingWorker se ejecuta en un thread distinto del hilo principal
        sw.execute();
    }

    /**
     * Método helper que gestiona en un Thread independiente los eventos generados por el por el botón
     * <code>borrarMensajesButton</code>.
     *
     */
    private void borrarMensajesButtonSwingWorker() {

        SwingWorker sw;
        sw = new SwingWorker() {

            @Override
            protected String doInBackground() throws Exception {
                chatTextArea.setText("");
                // Establecer la nueva posición del cursor
                chatTextArea.setCaretPosition(chatTextArea.getText().length());
                chatTextArea.getCaret().setVisible(true);

                return "Ejecución completada.";
            }
        };

        // El SwingWorker se ejecuta en un thread distinto del de la ejecución principal
        sw.execute();
    }

}
