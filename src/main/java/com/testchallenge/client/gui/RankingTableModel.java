
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

import javax.swing.table.AbstractTableModel;

/**
 * Clase que define el modelo de la tabla que muestra el ranking de resultados.
 * 
 * La tabla tiene dos columnas: las primera muestra el nickname del usuario y la segunda la puntuación. Este tipo 
 * de objeto se utiliza para actualizar los resultados que se muestran en el objeto <code>JTable</code> que
 * muestra la tabla en el panel correspondiente.
 * 
 * @author japrada
 */
public class RankingTableModel extends AbstractTableModel {

    // Columnas de la tabla
    private final String[] columnNames = {
        "Nickname", "Puntos"
    };

    // Datos de la tabla
    private Object[][] puntuaciones = new Object[][]{};

    /**
     * Constructor.
     * 
     * Construye el modelo de la tabla que muestra el ranking de resultados, sin datos.
     * 
     */
    public RankingTableModel() {
        super();
    }
    
    /**
     * Constructor.
     * 
     * Construye un objeto del tipo <code>RankingTableModel</code> con los datos que se especifican como parámetro.
     * 
     * @param rankingTableModel datos de la tabla.
     */
    public RankingTableModel(Object[][] rankingTableModel){
        puntuaciones = rankingTableModel;
    }
    
    @Override
    public int getRowCount() {
        return puntuaciones.length;
    }
   
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

   
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return puntuaciones[rowIndex][columnIndex];
    }
    
    public Object[][] getPuntuaciones() {
        return puntuaciones;
    }
}
