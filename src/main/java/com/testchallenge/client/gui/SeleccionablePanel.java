
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

import com.testchallenge.model.Respuesta;
import javax.swing.JPanel;

/**
 * Clase base para los paneles que muestran respuestas seleccionables.
 * 
 * Las clases que hereden <code>SeleccionablePanel</code> deberán implementar los métodos abstractos 
 * para definir el comportamiento específico en función del tipo de pregunta que se esté presentando.
 * 
 * @author japrada
 */
public abstract class SeleccionablePanel extends JPanel implements ISeleccionable {
    
    @Override
    public abstract String[] getRespuestasSeleccionadas();
    
    @Override
    public abstract void setRespuestas(String[] respuestas);
    
    @Override
    public abstract void setRespuestas(String[] respuestas, Respuesta respuesta);
}
