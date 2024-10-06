
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.swing.JLabel;
import javax.swing.JSlider;

/**
 * Muestra el tiempo de reproducción el audio.
 * 
 * Basado en el código de Nam Ha Minh:
 * 
 * https://www.codejava.net/nam-ha-minh
 * https://www.codejava.net/coding/java-audio-player-sample-application-in-swing
 * 
 * @author japrada
 */
public class PlayingTimer extends Thread {

    private final DateFormat dateFormater = new SimpleDateFormat("HH:mm:ss");
    private final int TIME_TO_SLEEP = 1_000;

    private boolean isRunning = false;
    private boolean isPaused = false;

    private long startTime;

    private long runningTime;
    private long pausingTime;

    private final JLabel labelRecordTime;
    private final JSlider slider;

    /**
     * 
     * @param labelRecordTime
     * @param slider 
     */
    PlayingTimer(JLabel labelRecordTime, JSlider slider) {
        this.labelRecordTime = labelRecordTime;
        this.slider = slider;
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        isRunning = true;

        startTime = System.currentTimeMillis();

        while (isRunning) {
            try {
                Thread.sleep(TIME_TO_SLEEP);
                if (!isPaused) {
                    labelRecordTime.setText(toTimeString());
                    int currentSecond = (int) runningTime / TIME_TO_SLEEP;
                    slider.setValue(currentSecond);

                    runningTime += TIME_TO_SLEEP;
                } else {
                    pausingTime += TIME_TO_SLEEP;
                }
            } catch (InterruptedException ex) {
                slider.setValue(0);
                labelRecordTime.setText("00:00:00");
                isRunning = false;
                isPaused = false;
                break;

            }
        }
    }

    /**
     * 
     */
    void pauseTimer() {
        isPaused = true;
    }

    /**
     * 
     */
    void resumeTimer() {
        isPaused = false;
    }

    /**
     * 
     * @return cadena de caracteres con el tiempo de reproducción
     */
    private String toTimeString() {
        long now = System.currentTimeMillis();
        Date current = new Date(((now - pausingTime) - startTime) + runningTime / TIME_TO_SLEEP);
        dateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
        String timeCounter = dateFormater.format(current);
        return timeCounter;
    }
}
