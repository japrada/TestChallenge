
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

import com.testchallenge.model.Configuracion;
import com.testchallenge.model.Mensaje;
import com.testchallenge.model.Pregunta;
import com.testchallenge.model.Puntuacion;
import com.testchallenge.model.Ranking;
import com.testchallenge.model.Resultados;
import com.testchallenge.model.TipoMensaje;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

/**
 * Panel que construye la parte de la interfaz que se utiliza en la realización de tests.
 *
 * Está compuesto de tres subpaneles principales: el que muestra las preguntas, el que muestra el contador con la cuenta
 * atrás y el que muestra las opciones de configuración del test. El panel que muestra las preguntas, a su vez contiene
 * el panel de respuestas.
 *
 * @author japrada
 */
public class TestPanel extends ConectablePanel {

    // Sub-panel que contiene las preguntas
    private PreguntasPanel preguntasPanel;
    // Sub-panel que muestra la cuenta atrás
    private final TimerPanel timerPanel;
    // Sub-panel que muestra la tabla de resultados
    private final RankingPanel rankingPanel;
    // Sub-panel que agrupa el panel de preguntas y el ranking
    private final JPanel rankingYPreguntasPanel;
    // Sub-panel que permite establecer la configuración del test
    private final ConfiguracionPanel configuracionPanel;
    // Botón para iniciar el test
    private final JButton iniciarTestButton;
    // Preguntas que se van presentando en el test
    private List<Pregunta> preguntas;
    // Resultados de la ejecución del test
    private Resultados resultados;
    
    // Componente contenedor
    private JFrame parent;

    /**
     * Constructor.
     *
     * Construye el panel para la realización de tests con el título del borde especificado, y el canal para el envío de
     * mensajes hacia el servidor.
     *
     * Este panel está compuesto de un <code>TimerPanel</code>, un <code>RankingPanel</code> y un
     * <code>ConfiguracionPanel</code>.
     *
     * @param title título del borde del panel.
     * @param out canal para el envío de mensajes al servidor.
     */
    public TestPanel(String title, ObjectOutputStream out) {
        super(title, out);

        // ********************
        // *    Test panel    *
        // ********************
        setLayout(new BorderLayout());

        // **********************************
        // *    Preguntas y Ranking panel   *
        // **********************************
        //rankingYPreguntasPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rankingYPreguntasPanel = new JPanel(new BorderLayout());

        // ***************************
        // *  Timer y Ranking panel  *
        // ***************************
        JPanel timerYRankingPanel = new JPanel(new BorderLayout());

        // *************************
        // *    Timer panel        *
        // *************************
        timerPanel = new TimerPanel("Tiempo restante");
        timerYRankingPanel.add(timerPanel, BorderLayout.NORTH);

        // *************************
        // *    Ranking panel      *
        // *************************        
        rankingPanel = new RankingPanel("Ranking (10 primeros)");
        timerYRankingPanel.add(rankingPanel, BorderLayout.CENTER);

        rankingYPreguntasPanel.add(timerYRankingPanel, BorderLayout.WEST);

        // *************************
        // *    Preguntas  panel   *
        // *************************
        // Construye un panel de preguntas vacío
        preguntasPanel = new PreguntasPanel("Preguntas", out);
        preguntasPanel.setParent(parent);

        rankingYPreguntasPanel.add(preguntasPanel, BorderLayout.CENTER);

        // *************************
        // * Configuraciones panel *
        // *************************
        configuracionPanel = new ConfiguracionPanel("Configuración", out);
        configuracionPanel.setParent(parent);

        // **************************
        // *  Botón "Iniciar Test"  *
        // **************************
        iniciarTestButton = new JButton("Iniciar Test");
        iniciarTestButton.setMnemonic(KeyEvent.VK_I);
        // Añadimos el botón que envía la respuesta
        iniciarTestButton.addActionListener((ActionEvent e) -> {
            iniciarTestButtonSwingWorker();
        });

        // Construcción del panel de Test
        add(rankingYPreguntasPanel, BorderLayout.NORTH);
        add(configuracionPanel, BorderLayout.CENTER);
        add(iniciarTestButton, BorderLayout.SOUTH);
        
        // Listener asociado al panel para mostrar los resultados del último test en ejecución
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                // Doble clic detectado
                if (event.getClickCount() == 2) {
                    if (resultados != null) {
                        // Mostramos los resultados del último test ejecutado
                        popUpResultados(resultados.getPuntosObtenidos(), resultados.getContadores());
                    }
                }
            }
        });
    }

    /**
     * Obtiene la referencia al panel <code>TimerPanel</code>.
     *
     * @return referencia al panel <code>TimerPanel</code>.
     */
    public TimerPanel getTimerPanel() {
        return timerPanel;
    }

    /**
     * Obtiene la referencia al panel <code>ConfiguracionPanel</code>.
     *
     * @return referencia al panel <code>ConfiguracionPanel</code>.
     */
    public ConfiguracionPanel getConfiguracionPanel() {
        return configuracionPanel;
    }

    /**
     * Obtiene la referencia al boton que inicia el test.
     *
     * @return referencia al botón que inicia el test.
     */
    public JButton getIniciarTestButton() {
        return iniciarTestButton;
    }

    /**
     * Obtiene la referencia al panel <code>PreguntasPanel</code>.
     *
     * @return referencia al panel <code>PreguntasPanel</code>.
     */
    public PreguntasPanel getPreguntasPanel() {
        return preguntasPanel;
    }

    /**
     * Activa/Desactiva el "modo revisión", en el que se pueden revisar los resultados del último test realizado.
     *
     * @param isEnabled <code>true</code>, para activar el modo revisión y <code>false</code> para desactivarlo
     */
    public void setModoRevisionEnabled(boolean isEnabled) {
        
        if (!isEnabled) {
            preguntas = new ArrayList<>();
        }
        
        preguntasPanel.setPreguntas(preguntas);
        preguntasPanel.setAmpliarSegundosPanelEnabled(false);
        preguntasPanel.setBotonRevisarEnabled(isEnabled);
        preguntasPanel.setPauseResumeButtonEnabled(false);
        preguntasPanel.setStopButtonEnabled(false);
    }

    /**
     * Muestra un cuadro de diálogo con los puntos obtenidos al finalizar el test.
     *
     */
    public void popUpResultados() {
        Integer puntosObtenidos = rankingPanel.getPuntosObtenidos();
        
         // Obtener los contadores usando Streams
        Map<Puntuacion, Long> contadores = preguntas.stream()
                .collect(Collectors.groupingBy(
                        p -> (p.getPuntuacion() == Puntuacion.CORRECTA) ? Puntuacion.CORRECTA :
                             (p.getPuntuacion() == Puntuacion.CORRECTA_Y_PRIMERA ) ? Puntuacion.CORRECTA_Y_PRIMERA :
                             (p.getPuntuacion() == Puntuacion.INCORRECTA) ? Puntuacion.INCORRECTA :
                             (p.getPuntuacion() == Puntuacion.NO_RESPONDIDA) ? Puntuacion.NO_RESPONDIDA : Puntuacion.NO_CONTESTADA, 
                        Collectors.counting()
                ));
        
        // Almacenamos los resultados de la ejecución del test para poderlos visualizar durante la revisión
        resultados = new Resultados(puntosObtenidos, contadores);
        // Mostramos los resultados
        popUpResultados(puntosObtenidos, contadores);
    }

    /**
     * 
     * @param puntosObtenidos
     * @param contadores 
     */
    private void popUpResultados(Integer puntosObtenidos, Map<Puntuacion, Long> contadores) {
        StringBuilder sbTextoResultadosObtenidos = new StringBuilder();
        
        sbTextoResultadosObtenidos.append(String.format("Ha obtenido '%d' puntos en el test realizado.\n\n", puntosObtenidos));
        sbTextoResultadosObtenidos.append(String.format("[•] Ha respondido el primero correctamente '%d' preguntas.\n", contadores.getOrDefault(Puntuacion.CORRECTA_Y_PRIMERA, 0L)));        
        sbTextoResultadosObtenidos.append(String.format("[•] Ha respondido correctamente, sin ser el primero, '%d' preguntas.\n", contadores.getOrDefault(Puntuacion.CORRECTA, 0L)));
        sbTextoResultadosObtenidos.append(String.format("[•] Ha respondido incorrectamente '%d' preguntas.\n", contadores.getOrDefault(Puntuacion.INCORRECTA, 0L)));
        sbTextoResultadosObtenidos.append(String.format("[•] No ha respondido '%d' preguntas.\n", contadores.getOrDefault(Puntuacion.NO_RESPONDIDA, 0L)));
        sbTextoResultadosObtenidos.append(String.format("[•] No ha enviado respuesta en '%d' preguntas.\n", contadores.getOrDefault(Puntuacion.NO_CONTESTADA, 0L)));
        
        JOptionPane.showMessageDialog(parent, sbTextoResultadosObtenidos.toString());
    }
    
    /**
     * Muestra un cuadro de diálogo indicando que no hay preguntas que cumplan los criterios seleccionados para ejecutar
     * el test.
     *
     */
    public void popUpTestSinPreguntas() {
        JOptionPane.showMessageDialog(parent,
                "No se han encontrado preguntas para los criterios especificados.");
    }

    /**
     * Inicializa el panel de preguntas antes de empezar y después de finalizar un test.
     *
     */
    public void setPregunta() {
        if (preguntasPanel.getPopupWindow() != null) {
            preguntasPanel.getPopupWindow().dispose();
        }
        rankingYPreguntasPanel.remove(preguntasPanel);
        timerPanel.setTimer("");

        preguntasPanel = new PreguntasPanel("Preguntas", out);
        preguntasPanel.setEnviarRespuestaButtonEnabled(false);
        preguntasPanel.setAnteriorButtonEnabled(true);
        preguntasPanel.setSiguienteButtonEnabled(true);
        preguntasPanel.setStopButtonEnabled(false);
        preguntasPanel.setPauseResumeButtonEnabled(false);

        rankingYPreguntasPanel.add(preguntasPanel);
        rankingYPreguntasPanel.revalidate();
    }

    /**
     * Establece la pregunta en el panel de preguntas.
     *
     * @param pregunta pregunta que se muestra en el panel de preguntas.
     */
    public void setPregunta(Pregunta pregunta) {
        if (preguntasPanel.getPopupWindow() != null) {
            preguntasPanel.getPopupWindow().dispose();
        }
        // Almacenar la pregunta recibida para mostrarla después en la revisión (Anterior, Siguiente)
        preguntas.add(pregunta);

        rankingYPreguntasPanel.remove(preguntasPanel);
        preguntasPanel = new PreguntasPanel(pregunta, out);
        preguntasPanel.setEnviarRespuestaButtonEnabled(true);
        preguntasPanel.setAmpliarSegundosPanelEnabled(true);
        preguntasPanel.setStopButtonEnabled(true);
        preguntasPanel.setPauseResumeButtonEnabled(true);
        rankingYPreguntasPanel.add(preguntasPanel);
        rankingYPreguntasPanel.revalidate();
    }

    /**
     * Establece el ranking en la tabla del panel que muestra el ranking.
     *
     * @param ranking ranking que se establece en la tabla del panel que muestra el ranking.
     * @param nickname usuario conectado
     */
    public void setRanking(Ranking ranking, String nickname) {
        rankingPanel.setRanking(ranking, nickname);
    }

    /**
     * Resetea el panel de preguntas antes de empezar un nuevo test.
     *
     */
    public void resetPanelPreguntas() {
        // Resetear el panel antes de empezar un nuevo test
        resultados = null;
        rankingYPreguntasPanel.remove(preguntasPanel);
        preguntasPanel = new PreguntasPanel("Preguntas", out);
        rankingYPreguntasPanel.add(preguntasPanel);
        rankingYPreguntasPanel.revalidate();

        // Deshabilitar el panel de configuración y el botón Iniciar Test
        configuracionPanel.setEnabled(false);
        iniciarTestButton.setEnabled(false);
        // inicializar la variable que almacenará las preguntas del test (las preguntas anteriores se pierden)
        setModoRevisionEnabled(false);
    }

    /**
     * <code>SwingWorker</code> para solicitar el inicio de la ejecución del test</code>.
     */
    private void iniciarTestButtonSwingWorker() {
        SwingWorker sw;
        sw = new SwingWorker() {
            @Override
            protected String doInBackground() throws Exception {
                // *********  Solicita al servidor el inicio de un test *********
                if (configuracionPanel.getTiposPreguntas().length == 0) {
                    // Si el usuario no ha seleccionado ningún tipo de pregunta, no se inicia la ejecución del test:
                    JOptionPane.showMessageDialog(parent,
                            "Por favor, seleccione al menos un tipo de pregunta.");
                } else {
                    // En caso contrario, inicar la ejecución de un nuevo test:
                    // 1. Resetear el panel antes de empezar un nuevo test
                    resetPanelPreguntas();

                    // 2. Obtener la arametrización: materia, nivel, tipo y número de preguntas, y tiempo límite
                    Configuracion configuracion = new Configuracion(
                            configuracionPanel.getTematica(),
                            configuracionPanel.getNivel(),
                            configuracionPanel.getTiposPreguntas(),
                            configuracionPanel.getNumeroPreguntas(),
                            configuracionPanel.getTiempoLimite());

                    // 3. Enviar el mensaje INICIAR_TEST al servidor para que arranque el test
                    out.writeObject(new Mensaje(configuracion, TipoMensaje.INICIAR_TEST));
                    out.flush();
                    // *************************************************
                }
                return "Ejecución completada.";
            }
        };
        // Ejecuta el SwingWorker en un thread distinto del de la ejecución principal
        sw.execute();
    }

    /**
     * Establece el <code>JFrame</code> padre del panel para que los componentes hijos lo puedan referenciar.
     *
     * @param testChallengeClient componesnte padre del panel
     */
    public void setParent(JFrame testChallengeClient) {
        this.parent = testChallengeClient;
    }

}
