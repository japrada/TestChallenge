
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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Interfaz que define los métodos (abstractos) que deben ser implementados por aquellos componentes gráficos de
 * de la GUI que se pueden conectar directamente con el servidor para intercambiar mensajes.
 * 
 * @author japrada
 */
public interface IConectable {
    
    /**
     * Establece el stream de lectura.
     * 
     * @param in stream de lectura.
     */
    public void setInputStream(ObjectInputStream in);
    
    /**
     * Establece el stream de escritura.
     * 
     * @param out stream de escritura.
     */
    public void setOutputStream(ObjectOutputStream out);
    
    /**
     * Obtiene el stream de lectura.
     * 
     * @return stream de lectura.
     */
    public ObjectInputStream getInputStream();
    
    /**
     * Obtiene el stream de escritura.
     * 
     * @return stream de escritura.
     */
    public ObjectOutputStream getOutputStream();
}
