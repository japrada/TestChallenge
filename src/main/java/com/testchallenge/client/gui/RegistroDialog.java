
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Cuadro de diálogo para el establecimiento de conexión y el registro del usuario.
 *
 * @author japrada
 */
class RegistroDialog extends JDialog {

    // Labels
    private final JLabel usuarioLabel = new JLabel("Usuario", SwingConstants.RIGHT);
    private final JLabel servidorLabel = new JLabel("Servidor", SwingConstants.RIGHT);
    private final JLabel puertoLabel = new JLabel("Puerto", SwingConstants.RIGHT);
    // Campos
    private final JTextField usuarioField = new JTextField(10);
    private final JTextField servidorField = new JTextField(20);
    private final JTextField puertoField = new JTextField(5);
    // Botones
    private final JButton loginButton = new JButton("Conectar");
    private final JButton cancelButton = new JButton("Cancelar");
    
    private final JLabel imagenLabel = new JLabel("");

    // Label para mostrar los errores de conexión
    private final JTextArea erroresTextArea = new JTextArea(" ");

    // Nombre DNS del servidor (o dirección IP) de chat
    private String server;
    // Puerto en el que se encuentra escuchando el servidor de chat
    private int port;
    //Stream utilizado para la lectura de datos enviados desde el servidor de chat
    private ObjectInputStream in = null;
    //Stream utilizado para la escritura de datos hacia el servidor de chat
    private ObjectOutputStream out = null;
    //Socket para la conexión con el servidor de chat
    private Socket serverDataSocket = null;
    // Logger de la clase
    private final static Logger logger = Logger.getLogger(RegistroDialog.class.getName());

    // Dimensiones del Panel
    private final static int REGISTRO_DIALOG_ANCHO = 480;
    private final static int REGISTRO_DIALOG_ALTO = 240;

    /**
     * Constructor.
     *
     * Construye un objeto de la clase sin inicializar.
     *
     */
    public RegistroDialog() {
        this(null, true);
    }

    /**
     * Constructor.
     *
     * Construye un objeto en el que se establecen el componente contenedor y el modo de presentación (modal o no).
     *
     * @param parent componente contenedor con el que está asociado el diálogo.
     * @param modal establece si el diálogo se presenta de manera modal (<code>true</code>) o no (<code>false</code>).
     */
    public RegistroDialog(final JFrame parent, boolean modal) {
        super(parent, modal);

        setTitle("Registro de usuario");
        setResizable(false);
        setPreferredSize(new Dimension(REGISTRO_DIALOG_ANCHO, REGISTRO_DIALOG_ALTO));

        // Diseñamos el formulario sin un Layout Manager (posicionamiento absoluto)
        setLayout(null);

        // Usuario
        // -------
        usuarioLabel.setBounds(0, 10, 70, 30);
        usuarioField.setBounds(80, 15, 200, 20);
        add(usuarioLabel);
        
        //Aplicar un DocumentFilter para permitir solo letras y números
        ((AbstractDocument) usuarioField.getDocument()).setDocumentFilter(new UsuarioDocumentFilter());
        
        add(usuarioField);
        
        // Servidor
        // ---------
        servidorLabel.setBounds(4, 40, 70, 30);
        servidorField.setBounds(80, 45, 200, 20);
        add(servidorLabel);
        add(servidorField);
        
        // Puerto
        // ---------
        puertoLabel.setBounds(-7, 70, 70, 30);
        puertoField.setBounds(80, 75, 55, 20);
        add(puertoLabel);
        
        //Aplicar un DocumentFilter para permitir solo cinco dígitos
        ((AbstractDocument) puertoField.getDocument()).setDocumentFilter(new PuertoDocumentFilter());
       
        add(puertoField);

        // TextArea que muestra los errores
        erroresTextArea.setBounds(20, 110, 260, 50);
        erroresTextArea.setOpaque(false);
        erroresTextArea.setForeground(Color.RED);
        erroresTextArea.setLineWrap(true);
        erroresTextArea.setWrapStyleWord(true);
        add(erroresTextArea);

        // Botón Conectar
        // --------------
        loginButton.setBounds(90, 170, 150, 30);
        add(loginButton);

        // Imagen con el icono
        // ---------------------
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/Julio.png"));
        imagenLabel.setIcon(icon);
        imagenLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagenLabel.setBorder(BorderFactory.createTitledBorder(""));

        imagenLabel.setBounds(300, 10, 160, 160);
        add(imagenLabel);

        pack();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        loginButton.addActionListener((ActionEvent e) -> {
            // ********************************************************************************
            // Establecer conexión con el servidor y si no hay errores, arrancar la aplicación
            // ********************************************************************************
            if (establecerConexion()) {
                parent.setVisible(true);
                setVisible(false);
            }
        });

        cancelButton.addActionListener((ActionEvent e) -> {
            setVisible(false);
            parent.dispose();
            System.exit(0);
        });
    }

    /**
     * Obtiene el canal de lectura con el servidor.
     *
     * @return canal de lectura con el servidor.
     */
    public ObjectInputStream getInputStream() {
        return in;
    }

    /**
     * Obtiene el canal de escritura con el servidor.
     *
     * @return canal de escritura con el servidor.
     */
    public ObjectOutputStream getOutputStream() {
        return out;
    }

    /**
     * Socket de conexión con el servidor.
     *
     * @return obtiene el socket con el que el cliente se ha conectado con el servidor
     */
    public Socket getServerDataSocket() {
        return serverDataSocket;
    }

    /**
     * Obtiene el nickname especificado.
     *
     * @return nickname
     */
    public String getNickname() {
        return usuarioField.getText();
    }

    /**
     * Obtiene el nombre del servidor especificado.
     *
     * Host en el que se está ejecutando el servidor.
     *
     * @return nombre del servidor
     */
    public String getServer() {
        return server;
    }

    /**
     * Obtiene el número de puerto especificado.
     *
     * Número de puerto en el que se está ejecutando el servicio en el host especificado.
     *
     * @return número de puerto.
     */
    public int getPort() {
        return port;
    }

    /**
     * Establece el campo usuario con el nombre/nickname especificado.
     *
     * @param usuario usuario que se muestra en el campo que recoge este dato.
     */
    public void setUsuario(String usuario) {
        usuarioField.setText(usuario);
    }

    /**
     * Establece el campo servidor con el nombre o dirección IP especificadas.
     *
     * @param servidor nombre o dirección IP que se muestra en el campo que recoge este dato.
     */
    public void setServidor(String servidor) {
        servidorField.setText(servidor);
    }

    /**
     * Establece el campo puerto con valor especificado.
     *
     * @param puerto valor del puerto que se muestra en el campo que recoge este dato.
     */
    public void setPuerto(String puerto) {
        puertoField.setText(puerto);
    }

    /**
     * Método helper que gestiona el establecimiento de conexión con el servidor y el registro del usuario.
     *
     * @return <code>true</code> si la conexión se pudo establecer y <code>false</code> en caso contrario.
     * @throws ClassNotFoundException.
     */
    private boolean establecerConexion() {

        try {

            // Establecer una conexión con el servidor
            serverDataSocket = new Socket(servidorField.getText(), Integer.parseInt(puertoField.getText()));

            // Obtener la información de dirección IP y puerto del socket de conexión del sistema cliente
            String dirIPCliente = serverDataSocket.getLocalAddress().toString();
            int puertoCliente = serverDataSocket.getLocalPort();

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("'%s': Conexión establecida desde la dirección IP '%s' puerto '%d'\n",
                    TestChallengeClient.class.getSimpleName(), dirIPCliente, puertoCliente));

            // Enviar el nickname al servidor, para que lo valide
            String nickname = this.usuarioField.getText();
            sb.append(String.format("'%s': Registrando nickname '%s' ...\n",
                    TestChallengeClient.class.getSimpleName(), nickname));

            // Obtener el stream de escritura para la comunicación con el servidor y enviar el mensaje
            out = new ObjectOutputStream(serverDataSocket.getOutputStream());
            out.writeObject(new Mensaje(nickname));
            out.flush();

            // Leer la respuesta del server informando si el nickname es válido o ya está en uso
            in = new ObjectInputStream(serverDataSocket.getInputStream());

            Mensaje nicknameResponse = (Mensaje) in.readObject();

            // Si la respuesta del servidor es OK, entonces pasamos a "modo chat"
            if (nicknameResponse.getTipo().equals(TipoMensaje.NICKNAME_OK)) {
                // ****** El nickname se ha registrado correctamente ******
                return true;
            } else {
                erroresTextArea.setText(String.format("El nickname '%s' ya está registrado.", nickname));
                // Informar que el nickname está duplicado y no se ha podido iniciar una sesión
                logger.info(String.format("'%s': nickname '%s' duplicado. Sesión no iniciada.",
                        TestChallengeClient.class.getSimpleName(), nickname));
            }

        } catch (IOException | NumberFormatException | ClassNotFoundException e) {
            erroresTextArea.setText(String.format("%s", e.getMessage()));
            // Registrar traza de error
            logger.severe(e.getMessage());
        }

        // ****** El nickname NO se ha registrado correctamente ******
        return false;
    }
}

// Clase para crear un filtro personalizado de caracteres para el usuario/nickname (10 caracteres [a-zA-Z0-9._-])
class UsuarioDocumentFilter extends DocumentFilter {
    // Permitir letras, números, guion, guion bajo, punto
    private static final String ALLOWED_CHARACTERS = "[a-zA-Z0-9._-]";
    // Número máximo de caracteres permitidos
    private static final int MAX_CHARACTERS = 10;

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string != null && string.matches(ALLOWED_CHARACTERS + "+") &&
            (fb.getDocument().getLength() + string.length()) <= MAX_CHARACTERS) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text != null && text.matches(ALLOWED_CHARACTERS + "+") &&
            (fb.getDocument().getLength() - length + text.length()) <= MAX_CHARACTERS) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        super.remove(fb, offset, length);
    }
}

// Clase para crear un filtro personalizado de caracteres para el puerto (5 dígitos numéricos)
class PuertoDocumentFilter extends DocumentFilter {
    // Número máximo de caracteres permitidos
    private static final int MAX_DIGITS = 5;

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        // Verificar si la cadena es solo dígitos y si no excede la longitud máxima permitida
        if (string != null && string.matches("\\d+") && (fb.getDocument().getLength() + string.length()) <= MAX_DIGITS) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        // Verificar si la cadena es solo dígitos y si no excede la longitud máxima permitida
        if (text != null && text.matches("\\d+") && (fb.getDocument().getLength() - length + text.length()) <= MAX_DIGITS) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        // Permitir la eliminación de caracteres sin restricciones
        super.remove(fb, offset, length);
    }
}