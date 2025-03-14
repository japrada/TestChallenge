
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

import com.testchallenge.client.gui.player.MP3Player;
import static com.testchallenge.client.gui.player.MP3Player.secondsToString;
import com.testchallenge.model.Mensaje;
import com.testchallenge.model.Pregunta;
import com.testchallenge.model.Puntuacion;
import com.testchallenge.model.Respuesta;
import com.testchallenge.model.TipoMensaje;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * Panel que contiene los componentes que permiten mostrar las preguntas del test que se reciben desde el servidor,
 * enviar las respuestas al servidor, y ver los resultados del último test realizado mediante las funciones de
 * paginación que se han habilitado.
 *
 * @author japrada
 */
public class PreguntasPanel extends ConectablePanel {

    // Botón "Enviar respuesta"
    private final JButton enviarRespuestaButton;
    // Botón "Pregunta anterior"
    private final JButton preguntaAnteriorButton;
    // Botón "Pregunta siguiente"
    private final JButton preguntaSiguienteButton;
    // Panel "Ampliar segundos panel"
    private final JPanel ampliarSegundosPanel;
    // Etiqueta para mostrar la pregunta (con multilinea habilitada)
    private final JLabel preguntaLabel;
    // Panel que muestra la etiqueta con la pregunta y el fichero multimedia
    private JPanel preguntaPanel;
    // Label que muestra la imagen
    private JLabel imagenLabel;
    // Label para detener el test
    private JButton stopButton;
    // Label que muestra la imagen en la ventana emergente
    private JLabel popupWindowLabel;
    // Ventana emergente que muestra una imagen;
    private JFrame popupWindow;
    // Byte array con los datos del fichero multimedia
    private byte[] byteArrayData;
    // Duración en segundos del Mp3
    private long duracionDelAudioEnSegundos;
    // Panel con los botones "Anterior", "Enviar respuesta" y "Siguiente"
    private JPanel buttonsPanel;
    // Selector "Ampliar" para solicitar una ampliación de tiempo para responder a una pregunta
    private final JComboBox<String> ampliarSegundosComboBox;
    // Botón "Enviar segundos"
    private final JButton ampliarSegundosButton;
    // Botón "Pause/Resume"
    private final JButton pauseResumeButton;
    // Panel de respuestas en el que se muestran las respuestas de la pregunta
    private RespuestasPanel respuestasPanel;
    // ScrollPane que muestra el panel de respuestas
    private JScrollPane spRespuestasPanel;
    // Pregunta que se está mostrando: recibe la respuesta y la pregunta se almacena en una colección en el TestPanel
    private Pregunta pregunta;
    // Apunta a la pregunta que se está visualizando y permite recuperar la pregunta actual en la paginación
    private int preguntaIndex;
    // Lista de las preguntas del test que se van a revisar mediante las funciones de paginación
    private List<Pregunta> preguntas;
    // Número de orden de la pregunta (si estamos en un test)
    private Integer orden;
    // Referencia al JFrame principal para pasarle la referencia al JFileChooser
    private JFrame parent;

    // Constantes para la definición de la anchura y altura de los elementos del panel
    private final static int PREGUNTAS_PANEL_ANCHO = 500;
    private final static int PREGUNTAS_PANEL_ALTO = 500;

    private final static int PREGUNTA_PANEL_ANCHO = 900;
    private final static int PREGUNTA_PANEL_ALTO = 80;//110;

    private final static int PREGUNTA_LABEL_ANCHO = PREGUNTA_PANEL_ANCHO - 200;
    private final static int PREGUNTA_LABEL_ALTO = 70;//100;

    private final static int MULTIMEDIA_LABEL_ANCHO = 100;
    private final static int MULTIMEDIA_LABEL_ALTO = 100;

    private final static int POPUP_WINDOW_ANCHO = 500;
    private final static int POPUP_WINDOW_ALTO = 500;

    // Constantes para intercambiar el comando "Pause" y "Resume" y hacer el tratamiento
    public final static String PAUSE_ACTION_COMMAND = "PAUSE";
    public final static String RESUME_ACTION_COMMAND = "RESUME";

    private final static Logger logger = Logger.getLogger(PreguntasPanel.class.getName());

    /**
     * Constructor de la clase.
     *
     * Contruye un panel de preguntas vacío (sin pregunta) con el marco y el canal de envío de mensajes especificados.
     *
     * @param title título del marco alrededor del panel.
     * @param out canal para el envío de datos al servidor.
     */
    public PreguntasPanel(String title, ObjectOutputStream out) {
        this(title, "", "", null, 0, false, new RespuestasPanel(new String[]{}), null, out);
    }

    /**
     * Constructor de la clase.
     *
     * Construye un panel de preguntas para mostrar la información de la pregunta que se pasa como parámetro.
     *
     * @param pregunta objeto con la información de una pregunta.
     * @param out canal para el envío de datos al servidor.
     */
    public PreguntasPanel(Pregunta pregunta, ObjectOutputStream out) {
        this(pregunta.getTitle(),
                pregunta.getTexto(),
                pregunta.getFicheroMultimedia(),
                pregunta.getFicheroMultimediaData(),
                pregunta.getDuracionDelAudioEnSegundos(),
                pregunta.isFicheroMultimediaUnaImagen(),
                new RespuestasPanel(pregunta),
                pregunta.getNumeroOrden(), out);

        this.pregunta = pregunta;
    }

    /**
     * Constructor de la clase.
     *
     * Construye un panel de preguntas para mostrar la pregunta a partir de los datos que se especifican.
     *
     * @param title título del marco alrededor del panel.
     * @param pregunta texto de la pregunta.
     * @param ficheroMultimedia nombre del fichero multimedia que acompaña a la pregunta.
     * @param ficheroMultimediaData fichero multimedia (imagen o audio) que acompaña a la pregunta.
     * @param duracionDelAudioEnSegundos duración del audio
     * @param isAnImage
     * @param respuestas panel conteniendo las respuestas de la pregunta.
     * @param orden número de orden de la pregunta en el test.
     * @param out canal para el envío de datos al servidor.
     */
    private PreguntasPanel(String title,
            String pregunta,
            String ficheroMultimedia,
            byte[] ficheroMultimediaData,
            long duracionDelAudioEnSegundos,
            boolean isAnImage,
            RespuestasPanel respuestas,
            Integer orden,
            ObjectOutputStream out) {
        super(title, out);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // NOTA: este panel se actualizará con la pregunta enviada por el servidor
        respuestasPanel = respuestas;
        this.orden = orden;

        // *****************************************
        // Pregunta (JPanel y JLabel con multiline)
        // *****************************************
        preguntaPanel = new JPanel(new BorderLayout());

        preguntaPanel.setPreferredSize(new Dimension(PREGUNTA_PANEL_ANCHO, PREGUNTA_PANEL_ALTO));

        // Texto de la pregunta
        preguntaLabel = new JLabel();
        preguntaLabel.setPreferredSize(new Dimension(PREGUNTA_LABEL_ANCHO, PREGUNTA_LABEL_ALTO));
        setTextoPregunta(pregunta);
        preguntaPanel.add(preguntaLabel, BorderLayout.LINE_START);

        // Imagen asociada a la pregunta
        imagenLabel = new JLabel();
        imagenLabel.setPreferredSize(new Dimension(MULTIMEDIA_LABEL_ANCHO, MULTIMEDIA_LABEL_ALTO));

        // Botón que permite ir a la respuesta anterior
        imagenLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                // Doble clic detectado
                if (event.getClickCount() == 2) {
                    if (imagenLabel.getName().equals("imagen")) {
                        mostrarImagenEnVentanaEmergente();
                    } else if (imagenLabel.getName().equals("audio")) {
                        reproducirAudioEnVentanaEmergente();
                    }
                }
            }
        });

        // ¿Hay un fichero multimedia (una imagen o un audio) acompañando a la pregunta?
        setMultimediaPregunta(ficheroMultimedia, ficheroMultimediaData, duracionDelAudioEnSegundos, isAnImage);

        preguntaPanel.add(imagenLabel, BorderLayout.LINE_END);
        add(preguntaPanel);

        // ************************************
        // Respuestas (JPanel)
        // ************************************
        spRespuestasPanel = new JScrollPane(respuestas);
        spRespuestasPanel.setPreferredSize(new Dimension(PREGUNTAS_PANEL_ANCHO, PREGUNTAS_PANEL_ALTO));
        //spRespuestasPanel.setVerticalScrollBarPolicy(JScrollPane.WHEN_FOCUSED);
        spRespuestasPanel.setBorder(null);
        add(spRespuestasPanel);

        // *********************************************************************
        // Botones: "Pregunta anterior", "Pregunta siguiente", "Enviar respuesta"
        // **********************************************************************
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        preguntaAnteriorButton = new JButton("< Anterior");
        preguntaSiguienteButton = new JButton("Siguiente >");
        enviarRespuestaButton = new JButton("Enviar respuesta");

        // Solicitar más tiempo para responder a la pregunta (JPanel)
        // *********************************************************
        ampliarSegundosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel ampliarSegundosLabel = new JLabel("Ampliar tiempo en");
        ampliarSegundosPanel.add(ampliarSegundosLabel);
        ampliarSegundosComboBox = new JComboBox<>(new String[]{"5", "10", "20", "40", "60"});
        ampliarSegundosPanel.add(ampliarSegundosComboBox);

        JLabel segundosLabel = new JLabel("(segs)");
        ampliarSegundosPanel.add(segundosLabel);

        ampliarSegundosButton = new JButton("Ampliar");
        ampliarSegundosPanel.add(ampliarSegundosButton);

        // Panel para ampliar el número de segundos y los botones de paginación
        // ******************************************************************************
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 50));
        buttonsPanel.add(emptyPanel);

        buttonsPanel.add(preguntaAnteriorButton);
        buttonsPanel.add(enviarRespuestaButton);
        buttonsPanel.add(preguntaSiguienteButton);

        JPanel panel2 = new JPanel();
        panel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 25));
        buttonsPanel.add(panel2);
        buttonsPanel.add(ampliarSegundosPanel);

        // Stop button
        stopButton = new JButton(new ImageIcon(getClass().getResource(
                "/images/Stop.gif")));
        stopButton.setToolTipText("Detener el test en ejecución");
        buttonsPanel.add(stopButton);

        // Pause/Resume button
        pauseResumeButton = new JButton(new ImageIcon(getClass().getResource(
                "/images/Pause.png")));
        pauseResumeButton.setToolTipText("Pausar el test en ejecución");
        pauseResumeButton.setActionCommand(PAUSE_ACTION_COMMAND);
        buttonsPanel.add(pauseResumeButton);

        // Mnemonics: para asociar un comando (TBD)
        // ****************************************
        preguntaAnteriorButton.setMnemonic(KeyEvent.VK_A);
        preguntaSiguienteButton.setMnemonic(KeyEvent.VK_S);
        enviarRespuestaButton.setMnemonic(KeyEvent.VK_R);
        ampliarSegundosButton.setMnemonic(KeyEvent.VK_PLUS);

        // Handlers para los eventos del botón
        // ****************************************
        // Botón que envía la respuesta
        enviarRespuestaButton.addActionListener((ActionEvent e) -> {
            enviarRespuestaButtonSwingWorker();
        });

        // Botón que permite ir a la respuesta anterior
        preguntaAnteriorButton.addActionListener((ActionEvent e) -> {
            preguntaAnteriorButtonSwingWorker();
        });

        // Botón que permite ir a la respuesta anterior
        preguntaSiguienteButton.addActionListener((ActionEvent e) -> {
            preguntaSiguienteButtonSwingWorker();
        });

        ampliarSegundosButton.addActionListener((ActionEvent e) -> {
            ampliarSegundosButtonSwingWorker();
        });

        stopButton.addActionListener((ActionEvent e) -> {
            stopButtonSwingWorker();
        });

        pauseResumeButton.addActionListener((ActionEvent e) -> {
            pauseResumeButtonSwingWorker();
        });

        // El boton "Enviar respuesta" se activa cuando hay un test en ejecución
        setEnviarRespuestaButtonEnabled(false);

        // Los botones "Anterior" y "Siguiente" se activan cuando se ha terminado un test
        setAnteriorButtonEnabled(false);
        setSiguienteButtonEnabled(false);

        // El panel "Ampliar segundos" se activa cuando hay un test en ejecución
        setAmpliarSegundosPanelEnabled(false);

        // Botón para detener un test en ejecución
        setStopButtonEnabled(false);

        // Botón para Pausar/Reanudar la ejecución de un test
        setPauseResumeButtonEnabled(false);

        add(buttonsPanel);
    }

    /**
     * Obtiene la referencia a la ventana emergente.
     *
     * @return referencia a la ventana emergente.
     */
    public JFrame getPopupWindow() {
        return popupWindow;
    }

    /**
     * Establece la puntuación de la pregunta.
     *
     * @param puntuacion puntuación de la pregunta.
     */
    public void setPuntuacionPreguntaActual(Puntuacion puntuacion) {
        if (pregunta != null) {
            pregunta.setPuntuacion(puntuacion);
        }
    }

    /**
     * Establece la referencia al objeto contenedor del panel.
     *
     * @param parent referencia al objeto contenedor del panel.
     */
    public void setParent(JFrame parent) {
        this.parent = parent;
    }

    /**
     * Colección con las preguntas que se van a visualizar en el panel.
     *
     * @param preguntas preguntas que se van a visualizar en el panel.
     */
    public void setPreguntas(List<Pregunta> preguntas) {
        this.preguntas = preguntas;
    }

    /**
     * Establece el modo "ver resultados" o "revisión" para ver los resultados del test.
     *
     * En este modo el botón "Enviar respuesta" se cambia para mostrar "Ver respuestas" y activar las funciones de
     * navegación.
     *
     * @param isEnabled <code>true</code> para habilitar el modo "Ver respuestas" y <code>false</code> en caso
     * contrario.
     */
    public void setBotonRevisarEnabled(boolean isEnabled) {
        if (isEnabled) {
            enviarRespuestaButton.setText("Ver respuestas");
        } else {
            enviarRespuestaButton.setText("Enviar respuesta");
        }
        preguntaAnteriorButton.setEnabled(false);
        preguntaSiguienteButton.setEnabled(false);
        enviarRespuestaButton.setEnabled(isEnabled);
    }

    /**
     * Establece el texto de la pregunta.
     *
     * @param textoPregunta texto de la pregunta.
     */
    private void setTextoPregunta(String textoPregunta) {
        StringBuilder sbTextoPregunta = new StringBuilder();
        sbTextoPregunta.append("<html>").append(textoPregunta).append("</html>");
        preguntaLabel.setText(sbTextoPregunta.toString());
    }

    /**
     * Establece la pregunta cuando ésta contiene un fichero multimedia (imagen o audio).
     *
     * @param pregunta pregunta conteniendo un fichero multimedia (imagen o audio).
     */
    private void setMultimediaPregunta(Pregunta pregunta) {
        String ficheroMultimedia = pregunta.getFicheroMultimedia();
        byte[] arrayData = pregunta.getFicheroMultimediaData();
        boolean isImage = pregunta.isFicheroMultimediaUnaImagen();
        long duracionEnSegundos = pregunta.getDuracionDelAudioEnSegundos();
        setMultimediaPregunta(ficheroMultimedia, arrayData, duracionEnSegundos, isImage);
    }

    /**
     * Método helper para establecer la información del fichero multimedia asociada a la pregunta en el panel de
     * preguntas
     *
     * @param ficheroMultimedia nombre del fichero multimedia.
     * @param byteArrayData array de bytes conteniendo la imagen o el audio.
     * @param duracionEnSegundos duración en segundos del audio asociado al fichero multimedia.
     * @param isAnImage flag que indica si el fichero multimedia es una imagen o un audio.
     */
    private void setMultimediaPregunta(String ficheroMultimedia, byte[] byteArrayData, long duracionEnSegundos, boolean isAnImage) {
        // ¿Hay un fichero multimedia (una imagen o un audio) acompañando a la pregunta?
        if (!ficheroMultimedia.equals("")) {
            if (isAnImage) {
                scaledImageInLabel(imagenLabel, byteArrayData, MULTIMEDIA_LABEL_ANCHO, MULTIMEDIA_LABEL_ALTO);
                imagenLabel.setToolTipText("Haga doble click para agrandar la imagen");
                imagenLabel.setName("imagen");
            } else {
                // Es un audio: mostrar un icono con el símbolo del PLAY
                ImageIcon iconPlay = new ImageIcon(getClass().getResource("/images/mp3.png"));
                imagenLabel.setIcon(iconPlay);
                imagenLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imagenLabel.setVerticalAlignment(SwingConstants.CENTER);
                imagenLabel.setToolTipText("Haga doble click para reproducir el audio");
                imagenLabel.setName("audio");
                imagenLabel.setPreferredSize(new Dimension(MULTIMEDIA_LABEL_ANCHO, MULTIMEDIA_LABEL_ALTO));

                duracionDelAudioEnSegundos = duracionEnSegundos;
            }
            imagenLabel.setBorder(BorderFactory.createTitledBorder(""));
            this.byteArrayData = byteArrayData;
            imagenLabel.setVisible(true);
        } else {
            imagenLabel.setName("");
            this.byteArrayData = null;
            duracionDelAudioEnSegundos = 0;
            imagenLabel.setVisible(false);
        }
    }

    /**
     * Establece el panel de respuestas con las opciones de la pregunta en función del tipo de la misma.
     *
     * @param respuestas panel que muestra las opciones de la pregunta en función del tipo de la misma.
     */
    public void setRespuestasPanel(RespuestasPanel respuestas) {
        respuestasPanel = respuestas;
        spRespuestasPanel = new JScrollPane(respuestasPanel);
        spRespuestasPanel.setPreferredSize(new Dimension(PREGUNTAS_PANEL_ANCHO, PREGUNTAS_PANEL_ALTO));
        spRespuestasPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        spRespuestasPanel.setBorder(null);
    }

    /**
     * Habilita/Deshabilita el botón para el envío de la respuesta
     *
     * @param isEnabled <code>true</code> para habilitar el botón y <code>false</code> en caso contrario
     */
    public final void setEnviarRespuestaButtonEnabled(boolean isEnabled) {
        enviarRespuestaButton.setEnabled(isEnabled);
    }

    /**
     * Habilita/Deshabilita el botón para paginar a la pregunta anterior.
     *
     * @param isEnabled <code>true</code> para habilitar el botón y <code>false</code> en caso contrario.
     */
    public final void setAnteriorButtonEnabled(boolean isEnabled) {
        preguntaAnteriorButton.setEnabled(isEnabled);
    }

    /**
     * Habilita/Deshabilita el botón para paginar a la pregunta siguiente.
     *
     * @param isEnabled <code>true</code> para habilitar el botón y <code>false</code> en caso contrario.
     */
    public final void setSiguienteButtonEnabled(boolean isEnabled) {
        preguntaSiguienteButton.setEnabled(isEnabled);
    }

    /**
     * Habilita/Deshabilita el botón para paginar a la pregunta siguiente.
     *
     * @param isEnabled <code>true</code> para habilitar el botón y <code>false</code> en caso contrario.
     */
    public final void setStopButtonEnabled(boolean isEnabled) {
        stopButton.setEnabled(isEnabled);
    }

    /**
     * Habilita/Deshabilita el botón para pausar/resumir.
     *
     * @param isEnabled <code>true</code> para habilitar el botón y <code>false</code> en caso contrario.
     */
    public final void setPauseResumeButtonEnabled(boolean isEnabled) {
        pauseResumeButton.setEnabled(isEnabled);
    }

    /**
     * Habilita el botón "Play".
     *
     */
    public final void setResumeButtonEnabled() {
        // Poner el icono del Play
        pauseResumeButton.setIcon(new ImageIcon(getClass().getResource(
                "/images/Play.gif")));
        pauseResumeButton.setToolTipText("Reanudar el test en ejecución");
        pauseResumeButton.setActionCommand(RESUME_ACTION_COMMAND);
        // Inhabilitar el botón de "Enviar respuesta" y el panel de "Ampliar segundos"
        setEnviarRespuestaButtonEnabled(false);
        setAmpliarSegundosPanelEnabled(false);
        setPauseResumeButtonEnabled(true);
    }

    /**
     * Habilita el botón "Pause".
     *
     */
    public final void setPauseButtonEnabled() {
        // Poner el icono del Play
        pauseResumeButton.setIcon(new ImageIcon(getClass().getResource(
                "/images/Pause.png")));
        pauseResumeButton.setToolTipText("Pausar el test en ejecución");
        pauseResumeButton.setActionCommand(PAUSE_ACTION_COMMAND);

        // Inhabilitar el botón de "Enviar respuesta" y el panel de "Ampliar segundos"
        setEnviarRespuestaButtonEnabled(true);
        setAmpliarSegundosPanelEnabled(true);
        setPauseResumeButtonEnabled(true);
    }

    /**
     * Habilita/Deshabilita el panel que muestra los componentes para ampliar el número de segundos para contestar a una
     * pregunta.
     *
     * @param isEnabled <code>true</code> para habilitar el panel y <code>false</code> en caso contrario.
     */
    public final void setAmpliarSegundosPanelEnabled(boolean isEnabled) {
        Component[] components = ampliarSegundosPanel.getComponents();
        for (Component component : components) {
            component.setEnabled(isEnabled);
        }
    }

    /**
     * Método helper que gestiona en un Thread independiente los eventos generados por el botón
     * <code>enviarRespuestaButton</code>.
     */
    private void enviarRespuestaButtonSwingWorker() {
        SwingWorker sw = new SwingWorker() {
            @Override
            protected String doInBackground() throws Exception {
                if (enviarRespuestaButton.getText().equals("Enviar respuesta")) {

                    if (respuestasPanel.getRespuestasSeleccionadas() != null) {
                        Respuesta respuesta = new Respuesta(
                                respuestasPanel.getRespuestasSeleccionadas(),
                                respuestasPanel.getTipoPregunta(),
                                orden);
                        //if (respuesta.esValida()) {
                            // *********  Enviar la respuesta al servidor *********
                            pregunta.setRespuesta(respuesta);
                            out.writeObject(new Mensaje(respuesta, TipoMensaje.RESPUESTA_ENVIAR));
                            out.flush();
                            // *************************************************
                            // Sólo se deja enviar la respuesta una vez
                            enviarRespuestaButton.setEnabled(false);
                            // Inhabilita las opciones de la pregunta
                            respuestasPanel.setEnabled(false);
                            // El panel que permite ampliar los segundos se desactiva al enviar la respuesta
                            setAmpliarSegundosPanelEnabled(false);
                            // El botón de Pausar/Reanudar se desactiva al enviar la respuesta
                            setPauseResumeButtonEnabled(false);
                            // El botón de Stop test se desactiva al enviar la respuesta.
                            setStopButtonEnabled(false);
                        //} else {
                            //JOptionPane.showMessageDialog(parent, "La respuesta no puede ser vacía.");
                        //}
                    }
                } else {
                    // Reactivar la funcionalidad "Enviar respuesta" y, además, habilitar los botones de navegación
                    enviarRespuestaButton.setText("Enviar respuesta");
                    // NOTA: el botón no se puede volver a seleccionar
                    enviarRespuestaButton.setEnabled(false);
                    // Empezamos paginando en la pregunta "0" (el primer elemento de la lista)
                    preguntaIndex = 0;
                    cargarPregunta(preguntaIndex);
                }
                return "Ejecución completada.";
            }
        };
        // El SwingWorker se ejecuta en un thread distinto del hilo principal
        sw.execute();
    }

    /**
     * Método helper que gestiona en un Thread independiente los eventos generados por el botón
     * <code>preguntaAnteriorButton</code>.
     */
    private void preguntaAnteriorButtonSwingWorker() {
        SwingWorker sw = new SwingWorker() {
            @Override
            protected String doInBackground() throws Exception {
                preguntaIndex = preguntaIndex - 1;
                cargarPregunta(preguntaIndex);
                return "Ejecución completada.";
            }
        };
        // El SwingWorker se ejecuta en un thread distinto del hilo principal
        sw.execute();
    }

    /**
     * Método helper que gestiona en un Thread independiente los eventos generados por el botón
     * <code>preguntaSiguienteButton</code>.
     */
    private void preguntaSiguienteButtonSwingWorker() {
        SwingWorker sw = new SwingWorker() {
            @Override
            protected String doInBackground() throws Exception {
                preguntaIndex = preguntaIndex + 1;
                cargarPregunta(preguntaIndex);
                return "Ejecución completada.";
            }
        };
        // El SwingWorker se ejecuta en un thread distinto del hilo principal
        sw.execute();
    }

    /**
     * Método helper que gestiona en un Thread independiente los eventos generados por el botón <code>stopButton</code>.
     */
    private void stopButtonSwingWorker() {
        SwingWorker sw = new SwingWorker() {
            @Override
            protected String doInBackground() throws Exception {
                int option = JOptionPane.showConfirmDialog(parent,
                        "¿Desea realmente detener el test? \n Se sumarán puntos negativos por las preguntas canceladas si acepta esta operación.",
                        "Confirmar detener test", JOptionPane.OK_CANCEL_OPTION);

                // Comprobar la opción seleccionada por el usuario
                if (option == JOptionPane.OK_OPTION) {
                    out.writeObject(new Mensaje(TipoMensaje.DETENER_TEST));
                    out.flush();
                }

                return "Ejecución completada.";
            }
        };
        // El SwingWorker se ejecuta en un thread distinto del hilo principal
        sw.execute();
    }

    /**
     * Método helper que gestiona en un Thread independiente los eventos generados por el botón
     * <code>pauseResumeButton</code>.
     */
    private void pauseResumeButtonSwingWorker() {
        SwingWorker sw = new SwingWorker() {
            @Override
            protected String doInBackground() throws Exception {
                String buttonActionCommand = pauseResumeButton.getActionCommand();
                if (buttonActionCommand.equals(PAUSE_ACTION_COMMAND)) {
                    int option = JOptionPane.showConfirmDialog(parent,
                            "¿Desea realmente pausar la ejecución del test?",
                            "Confirmar pausar test", JOptionPane.OK_CANCEL_OPTION);

                    // Comprobar la opción seleccionada por el usuario
                    if (option == JOptionPane.OK_OPTION) {
                        // Enviar mensaje al servidor
                        out.writeObject(new Mensaje(TipoMensaje.PAUSAR_TEST));
                        out.flush();
                        // Actualizar la UI: Poner el icono del Play
                        setResumeButtonEnabled();
                    }
                } else if (buttonActionCommand.equals(RESUME_ACTION_COMMAND)) {
                    int option = JOptionPane.showConfirmDialog(parent,
                            "¿Desea realmente reanudar la ejecución del test?",
                            "Confirmar reanudar test", JOptionPane.OK_CANCEL_OPTION);

                    // Comprobar la opción seleccionada por el usuario
                    if (option == JOptionPane.OK_OPTION) {
                        // Enviar mensaje al servidor
                        out.writeObject(new Mensaje(TipoMensaje.REANUDAR_TEST));
                        out.flush();

                        //  Actualizar la UI: Poner el icono del Pause
                        setPauseButtonEnabled();
                    }
                }
                return "Ejecución completada.";
            }
        };
        // El SwingWorker se ejecuta en un thread distinto del hilo principal
        sw.execute();
    }

    /**
     * Carga la pregunta que se muestra durante la revisión.
     *
     * @param preguntaIndex número de orden de la pregunta que se muestra.
     */
    private void cargarPregunta(int preguntaIndex) {
        Pregunta preguntaMostrada = preguntas.get(preguntaIndex);

        remove(spRespuestasPanel);
        remove(buttonsPanel);

        respuestasPanel = new RespuestasPanel(preguntaMostrada, true);
        respuestasPanel.setRespuestas(preguntaMostrada.getRespuestasAsArray(),
                preguntaMostrada.getRespuesta());

        respuestasPanel.setEnabled(true);
     
        spRespuestasPanel = new JScrollPane(respuestasPanel);
        spRespuestasPanel.setPreferredSize(new Dimension(PREGUNTAS_PANEL_ANCHO, PREGUNTAS_PANEL_ALTO));
        spRespuestasPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        spRespuestasPanel.setBorder(null);
        add(spRespuestasPanel);
        add(buttonsPanel);

        preguntaAnteriorButton.setEnabled(true);
        preguntaSiguienteButton.setEnabled(true);

        setTextoPregunta(preguntaMostrada.getTexto());
        setMultimediaPregunta(preguntaMostrada);
        setBorder(BorderFactory.createTitledBorder(preguntaTitle(preguntaMostrada)));

        revalidate();

        if (preguntaIndex == 0) {
            preguntaAnteriorButton.setEnabled(false);
        } else {
            preguntaAnteriorButton.setEnabled(true);
        }

        if (preguntaIndex == preguntas.size() - 1) {
            preguntaSiguienteButton.setEnabled(false);
        } else {
            preguntaSiguienteButton.setEnabled(true);
        }
    }

    /**
     * Método helper que gestiona en un Thread independiente los eventos generados por el botón
     * <code>ampliarSegundosButton</code>.
     */
    private void ampliarSegundosButtonSwingWorker() {
        SwingWorker sw;
        sw = new SwingWorker() {
            @Override
            protected String doInBackground() throws Exception {
                out.writeObject(new Mensaje(ampliarSegundosComboBox.getSelectedItem(), TipoMensaje.AMPLIAR_TIEMPO_RESPUESTA));
                out.flush();
                return "Ejecución completada.";
            }
        };
        // El SwingWorker se ejecuta en un thread distinto del hilo principal
        sw.execute();
    }

    /**
     * Establece la imagen contenida en el array de bytes <code>imageData</code> como contenido de la etiqueta
     * <code>imageLabel</code> con la anchura y altura especificadas.
     *
     * @param imageLabel etiqueta que se utiliza para mostrar la imagen.
     * @param imageData array de bytes con la imagen.
     * @param width anchura (en pixels).
     * @param height altura (en pixels).
     */
    private void scaledImageInLabel(JLabel imageLabel, byte[] imageData, int width, int height) {
        try {
            BufferedImage bi = ImageIO.read(new ByteArrayInputStream(imageData));
            Image scaledImage = bi.getScaledInstance(width, height, Image.SCALE_FAST);
            ImageIcon imageIcon = new ImageIcon(scaledImage);
            imageLabel.setIcon(imageIcon);
            imageLabel.setPreferredSize(new Dimension(width, height));
        } catch (IOException ioe) {
            logger.severe(ioe.getMessage());
        }
    }

    /**
     * Método helper para mostrar el reproductor de audio en una ventana emergente.
     *
     */
    private void reproducirAudioEnVentanaEmergente() {

        SwingUtilities.invokeLater(() -> {

            popupWindow = new MP3Player("Audio Mp3",
                    byteArrayData,
                    duracionDelAudioEnSegundos,
                    secondsToString(duracionDelAudioEnSegundos));

            popupWindow.setVisible(true);
            popupWindow.setLocationRelativeTo(this);
            popupWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            popupWindow.addWindowFocusListener(new WindowAdapter() {
                @Override
                public void windowLostFocus(WindowEvent e) {
                    // Captura el evento de pérdida de foco
                    JFrame frame = (JFrame) e.getComponent();
                    ((MP3Player) popupWindow).stop();
                    frame.dispose();
                }
            });

        });
    }

    /**
     * Método helper para mostrar la imagen en una eventana emergente.
     *
     * @param image array de bytes con la imagen.
     */
    private void mostrarImagenEnVentanaEmergente() {

        SwingUtilities.invokeLater(() -> {

            popupWindow = new PopupWindow("Imagen");

            popupWindowLabel = new JLabel();
            scaledImageInLabel(popupWindowLabel, byteArrayData, POPUP_WINDOW_ANCHO, POPUP_WINDOW_ALTO);
            popupWindowLabel.setToolTipText("Redimensione la ventana para ajustar el tamaño");

            popupWindow.getContentPane().add(popupWindowLabel);
            popupWindow.setSize(POPUP_WINDOW_ANCHO, POPUP_WINDOW_ALTO);
            popupWindow.setLocationRelativeTo(this);
            popupWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            popupWindow.setVisible(true);
            popupWindow.setResizable(true);

            popupWindow.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    // Captura el evento de redimensionamiento
                    Dimension newSize = e.getComponent().getSize();
                    scaledImageInLabel(popupWindowLabel, byteArrayData, newSize.width, newSize.height);
                }
            });

            popupWindow.addWindowFocusListener(new WindowAdapter() {
                @Override
                public void windowLostFocus(WindowEvent e) {
                    // Captura el evento de pérdida de foco
                    JFrame frame = (JFrame) e.getComponent();
                    // Cierra el JFrame en respuesta al evento de pérdida de foco
                    frame.dispose();
                }
            });

        });
    }

    /**
     * Obtiene el texto que se muestra como título en el borde de la pregunta.
     *
     * @return texto que se muestra como título en el borde de la pregunta.
     */
    private String preguntaTitle(Pregunta pregunta) {
        String puntuacionTitle = "";

        Puntuacion puntuacion = pregunta.getPuntuacion();

        switch (puntuacion) {
            case CORRECTA_Y_PRIMERA:
                puntuacionTitle = Puntuacion.CORRECTA_Y_PRIMERA_MENSAJE;
                break;
            case CORRECTA:
                puntuacionTitle = Puntuacion.CORRECTA_MENSAJE;
                break;
            case INCORRECTA:
                puntuacionTitle = Puntuacion.INCORRECTA_MENSAJE;
                break;
            case NO_RESPONDIDA:
                puntuacionTitle = Puntuacion.NO_RESPONDIDA_MENSAJE;
                break;
            case NO_CONTESTADA:
                puntuacionTitle = Puntuacion.NO_CONTESTADA_MENSAJE;
        }

        return String.format("%s - [Puntuación: %s ]", pregunta.getTitle(), puntuacionTitle);
    }

}
