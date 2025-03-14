
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

import com.testchallenge.model.Ranking;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * Panel que muestra el ranking con los puntos obtenidos por los usuarios con los resultados de la ejecución de los
 * distintos tests realizados en el transcurso de la misma sesión (mientras el servidor está levantado).
 *
 * @author japrada
 */
public class RankingPanel extends JPanel {

    // Tabla con las puntuaciones de los usuarios
    private final JTable rankingTable;
    
    // Ranking con las puntuaciones
    private Ranking ranking;

    // Dimensiones del Panel
    private final static int RANKING_PANEL_ANCHO = 125;
    private final static int RANKING_PANEL_ALTO = 600;
    private final static int RANKING_NUMERO_GANADORES = 10;
    
    private Integer puntosObtenidos = 0;

    /**
     * Constructor.
     *
     * Construye un panel con el título especificado conteniendo la tabla de resultados.
     *
     * @param title título del panel.
     */
    public RankingPanel(String title) {
        // *************************
        // *    Ranking panel      *
        // *************************      
        setLayout(new BorderLayout());

        TitledBorder border = new TitledBorder(title);
        Font font = border.getTitleFont();
        Font newFont = new Font(font.getFamily(), Font.BOLD, font.getSize() - 3);
        border.setTitleFont(newFont);

        setBorder(border);

        rankingTable = new JTable(new RankingTableModel());
        JScrollPane sp = new JScrollPane(rankingTable);
        sp.setPreferredSize(new Dimension(RANKING_PANEL_ANCHO, RANKING_PANEL_ALTO));
        add(sp, BorderLayout.NORTH);
    }

    /**
     * Obtiene el número de puntos obtenidos en el último test realizado a partir de la información del 
     * ranking que se ha facilitado.
     * 
     * @return puntos obtenidos en el último test realizado.
     * 
     */
    public Integer getPuntosObtenidos() {
        return puntosObtenidos;
    }
    
    /**
     * Obtiene  
     * @return 
     */
    public Ranking getRanking() {
        return ranking;
    }
    
    /**
     * Calcula los puntos obtenidos por el usuario y actualiza el ranking
     *
     * @param ranking ranking con los resultados obtenidos por los usuarios.
     * @param nickname usuario conectado
     */
    public void setRanking(Ranking ranking, String nickname) {

        this.ranking = ranking;
        
        // Calcular los puntos que ha obtenido (nuevo ranking - ranking actual)
        calcularPuntosObtenidos (nickname);
        
        // Crear el modelo a partir de las puntuaciones del ranking
        TableModel newRankingTableModel = new RankingTableModel(ranking.getPuntuacionesAsArray());

        // Establecer el modelo en la tabla
        rankingTable.setModel(newRankingTableModel);

        // Si hay más de una puntuación en el ranking, las mostramos ordenadas.
        if (ranking.getPuntuacionesAsArray().length > 1) {

            // Establecemos las columnas que se utilizarán para hacer la clasificación, y el filtro
            TableRowSorter<TableModel> tableRowSorter = new TableRowSorter<>(newRankingTableModel);

            // Establecemos las columnas para la ordenación de la tabla
            List<RowSorter.SortKey> sortKeys = new ArrayList<>();

            // Ordenamos descendentemente por la columna que muestra la puntuación (index order = 1)
            sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));

            // Nos quedamos sólo con los mejores del ranking (los n-primeros)
            RowFilter<TableModel, Integer> filter = new RowFilter<TableModel, Integer>() {
                @Override
                public boolean include(RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
                    int modelRow = entry.getIdentifier();
                    int viewRow = rankingTable.convertRowIndexToView(modelRow);

                    return viewRow < RANKING_NUMERO_GANADORES;
                }
            };

            // Establecemos la ordenación
            rankingTable.setRowSorter(tableRowSorter);

            tableRowSorter.setRowFilter(filter);
            tableRowSorter.setSortKeys(sortKeys);
            tableRowSorter.sort();

        }

    }
    
    /**
     * Calcula el número de puntos obtenidos en el último test ejecutado haciendo la diferencia entre el total
     * de puntos acumulados y el número de puntos del ranking actual.
     * 
     * @param nickname usuario conectado
     */
    private void calcularPuntosObtenidos(String nickname) {
        Integer nuevaPuntuacion = ranking.getPuntuaciones().get(nickname);

        if (nuevaPuntuacion != null) {
            // Obtener el número respuestas acertadas por el usuario según el ranking actual
            TableModel tableModelRankingActual = rankingTable.getModel();

            Object[][] puntuacionesRankingActual = ((RankingTableModel) tableModelRankingActual).getPuntuaciones();

            // Convertir el Object[][] en un Stream<Object[]>
            Stream<Object[]> stream = Arrays.stream(puntuacionesRankingActual);

            // Mapear cada Object[] a un Map.Entry<String, Integer>        
            Map<String, Integer> puntuacionesRankingActualMap = stream
                    .map(entry -> new AbstractMap.SimpleEntry<>((String) entry[0], (Integer) entry[1]))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            Integer puntuacionActual = puntuacionesRankingActualMap.get(nickname);
            
            if (puntuacionActual != null) {
                puntosObtenidos = nuevaPuntuacion - puntuacionActual;
            } else {
                puntosObtenidos = nuevaPuntuacion;
            }
        }
    }
}
