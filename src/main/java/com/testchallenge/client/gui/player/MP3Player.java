
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

package com.testchallenge.client.gui.player;

import com.testchallenge.client.gui.PopupWindow;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

/**
 * Reproductor MP3.
 * 
 * Basado en el código de Nam Ha Minh:
 * 
 * https://www.codejava.net/nam-ha-minh
 * https://www.codejava.net/coding/java-audio-player-sample-application-in-swing
 * 
 * @author japrada
 */
public class MP3Player extends PopupWindow implements ActionListener {

    private static final int SECONDS_IN_HOUR = 60 * 60;
    private static final int SECONDS_IN_MINUTE = 60;

    private InputStream multimediaDataInputStream;

    private Thread playThread;
    private PlayingTimer timer;

    private Player player;
    private final byte[] audioData;

    private boolean paused = false;
    private boolean playing = false;

    private final long secondsLength;
    private final String secondsLengthString;

    private final JLabel labelFileName = new JLabel(""); 
    private final JLabel labelTimeCounter = new JLabel("00:00:00");
    private final JLabel labelDuration = new JLabel("00:00:00");

    private final JButton buttonPlay = new JButton("Play");
    private final JButton buttonPause = new JButton("Pause");

    private final JSlider sliderTime = new JSlider();

    private final ImageIcon iconPlay = new ImageIcon(getClass().getResource(
            "/images/Play.gif"));
    
    private final ImageIcon iconPause = new ImageIcon(getClass().getResource(
            "/images/Pause.png"));

    private final static Logger logger = Logger.getLogger(MP3Player.class.getName());

    public MP3Player(String title, byte[] audioData, long secondsLength, String secondsLengthString) {
        super(title);

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.WEST;

        buttonPlay.setText("Pause");
        buttonPlay.setIcon(iconPause);
        buttonPlay.setFont(new Font("Sans", Font.BOLD, 14));

        labelTimeCounter.setFont(new Font("Sans", Font.BOLD, 12));
        labelDuration.setFont(new Font("Sans", Font.BOLD, 12));

        sliderTime.setPreferredSize(new Dimension(400, 20));
        sliderTime.setEnabled(false);
        sliderTime.setValue(0);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        add(labelFileName, constraints);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        add(labelTimeCounter, constraints);

        constraints.gridx = 1;
        add(sliderTime, constraints);

        constraints.gridx = 2;
        add(labelDuration, constraints);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panelButtons.add(buttonPlay);

        constraints.gridwidth = 3;
        constraints.gridx = 0;
        constraints.gridy = 2;
        add(panelButtons, constraints);

        buttonPlay.addActionListener(this);
        buttonPause.addActionListener(this);

        pack();
        setResizable(false);

        this.audioData = audioData;
        this.secondsLength = secondsLength;
        this.secondsLengthString = secondsLengthString;

        multimediaDataInputStream = new ByteArrayInputStream(audioData);
        
        buttonPlay.doClick();
    }

    @Override

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source instanceof JButton) {
            JButton button = (JButton) source;
            if (button == buttonPlay) {
                if (!playing) {
                    try {
                        play();
                    } catch (IOException | JavaLayerException ex) {
                        logger.severe(ex.getMessage());
                    }
                } else {
                    pause();
                }
            }
        }
    }

    /**
     * 
     * @throws IOException
     * @throws JavaLayerException 
     */
    public void play() throws IOException, JavaLayerException {

        playThread = new Thread(() -> {
            try {

                player = new Player(multimediaDataInputStream);

                buttonPlay.setText("Pause");
                buttonPlay.setIcon(iconPause);
                paused = false;

                playing = true;

                sliderTime.setMaximum((int) secondsLength);
                labelDuration.setText(secondsLengthString);

                player.play();

                if (multimediaDataInputStream.available() == 0) {
                    playing = false;
                    paused = false;

                    buttonPlay.setText("Play");
                    buttonPlay.setIcon(iconPlay);
                    timer.interrupt();

                    multimediaDataInputStream = new ByteArrayInputStream(this.audioData);
                }

            } catch (JavaLayerException | IOException ex) {
                logger.severe(ex.getMessage());
            }
        });

        playThread.start();

        if (!paused) {
            timer = new PlayingTimer(labelTimeCounter, sliderTime);
            timer.start();
        } else {
            timer.resumeTimer();
        }

    }

    /**
     * 
     */
    public void stop() {
        player.close();
        playThread.interrupt();
        timer.interrupt();
        paused = false;
        playing = false;

    }

    /**
     * 
     */
    public void pause() {
        paused = true;
        playing = false;

        buttonPlay.setText("Play");
        buttonPlay.setIcon(iconPlay);

        player.close();

        playThread.interrupt();
        timer.pauseTimer();
    }

    /**
     * 
     * @param seconds
     * @return 
     */
    public static String secondsToString(long seconds) {
        String length = "";
        long hour = 0;
        long minute = 0;

        if (seconds >= SECONDS_IN_HOUR) {
            hour = seconds / SECONDS_IN_HOUR;
            length = String.format("%02d:", hour);
        } else {
            length += "00:";
        }

        minute = seconds - hour * SECONDS_IN_HOUR;
        if (minute >= SECONDS_IN_MINUTE) {
            minute = minute / SECONDS_IN_MINUTE;
            length += String.format("%02d:", minute);

        } else {
            minute = 0;
            length += "00:";
        }

        long second = seconds - hour * SECONDS_IN_HOUR - minute * SECONDS_IN_MINUTE;

        length += String.format("%02d", second);

        return length;
    }

}
