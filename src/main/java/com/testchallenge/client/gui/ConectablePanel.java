
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
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Un panel conectable es aquel que tiene la posibilidad de enviar/recibir mensajes hacia/desde el servidor.
 * 
 * El panel recibe los streams de conexión de lectura y de escritura a través de los cuales puede intercambiar
 * mensajes con el hilo de servicio con el que el cliente está conectado.
 * 
 * Se utiliza como clase base de la que heredan los paneles que tienen componentes gráficos que permiten generar
 * eventos que se traducen en mensajes hacia al servidor.
 * 
 * @author japrada
 */
public class ConectablePanel extends JPanel implements IConectable {
    
    protected ObjectInputStream in = null;
    protected ObjectOutputStream out = null;
    
    /**
     * Constructor de la clase.
     * 
     * Crea un objeto <code>ConectablePanel</code> sin inicializar.
     * 
     */
    public ConectablePanel(){
        super();
    }
    
    /**
     * Constructor de la clase.
     * 
     * Crea un objeto <code>ConectablePanel</code> con el título especificado en el borde y el canal de escritura de
     * mensajes inicializado.
     * 
     * @param title título del marco alrededor del panel.
     * @param out canal para el envío de mensajes al servidor.
     */
    public ConectablePanel(String title, ObjectOutputStream out){
        this(title);
        this.out = out;
    }
    
    /**
     * Constructor de la clase.
     * 
     * @param title título del marco alrededor del panel.
     */
    public ConectablePanel(String title) {
        super();
        setBorder(BorderFactory.createTitledBorder(title));
    }

    @Override
    public void setInputStream(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void setOutputStream(ObjectOutputStream out) {
        this.out = out;
    }

    @Override
    public ObjectInputStream getInputStream() {
        return this.in;
    }

    @Override
    public ObjectOutputStream getOutputStream() {
        return this.out;
    }
}
