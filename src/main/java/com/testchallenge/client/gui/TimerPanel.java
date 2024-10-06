
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Panel que muestra el tiempo límite del que dispone el usuario para enviar una respuesta.
 * 
 * @author japrada
 */
public class TimerPanel extends JPanel {

    private final JLabel timerLabel;

    // Dimensiones del Panel
    private final static int TIMER_PANEL_ANCHO = 125;
    private final static int TIMER_PANEL_ALTO = 50;

    private final static Logger logger = Logger.getLogger(TimerPanel.class.getName());

    /**
     * Construye el panel que muestra el tiempo límite del que dispone el usuario para enviar una respuesta.
     * 
     * @param title título del panel.
     */
    public TimerPanel(String title) {

        // *************************
        // *    Timer panel      *
        // *************************      
        setBorder(BorderFactory.createTitledBorder(title));

        timerLabel = new JLabel();

        setPreferredSize(new Dimension(TIMER_PANEL_ANCHO, TIMER_PANEL_ALTO));
        timerLabel.setForeground(Color.RED);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(timerLabel, BorderLayout.CENTER);
    }

    /**
     * Establece el tiempo límite que se visualiza en el panel.
     * 
     * @param time tiempo límite que se visualiza en el panel.
     */
    public void setTimer(String time) {
        timerLabel.setText(time);
    }
}
