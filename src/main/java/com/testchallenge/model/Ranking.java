
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

package com.testchallenge.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que modela la entidad Ranking.
 *
 * @author japrada
 */
public class Ranking implements Serializable {
    
    // Fecha de inicio del test asociado al ranking
    private Date startDate;
    // Fecha de finalización del test asociado al ranking
    private Date endDate;
    
    // Puntuaciones de los usuarios
    private Map<String, Integer> puntuaciones;

    /**
     * Construye un objeto <code>Ranking</code> vacío.
     *
     */
    public Ranking() {
        puntuaciones = new HashMap<>();
    }

    /**
     * Construye un objeto <code>Ranking</code> con las puntuaciones especificadas.
     *
     * @param puntuaciones puntuaciones del ranking.
     */
    public Ranking(Map<String, Integer> puntuaciones) {
        this.puntuaciones = puntuaciones;
    }

    /**
     * Obtiene las puntuaciones del ranking.
     *
     * @return puntuaciones del ranking.
     */
    public Map<String, Integer> getPuntuaciones() {
        return puntuaciones;
    }

    /**
     * Obtiene las puntuaciones del ranking como una matriz para ser utilizada como modelo en un objeto de tipo
     * <code>JTable</code>.
     *
     * @return matriz con las puntuaciones del ranking para ser utilizada como modelo en un objeto de tipo
     * <code>JTable</code>.
     */
    public Object[][] getPuntuacionesAsArray() {
        return puntuaciones.entrySet()
                .stream()
                .map(e -> new Object[]{e.getKey(), e.getValue()})
                .toArray(Object[][]::new);
    }

    /**
     * Establece el ranking con las puntuaciones de los usuarios.
     *
     * @param ranking ranking con las puntuaciones de los usuarios.
     */
    public void setRanking(Map<String, Integer> ranking) {
        this.puntuaciones = ranking;
    }

    /**
     * Obtiene la fecha de inicio del test.
     * 
     * @return fecha de inicio del test.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Establece la fecha de inicio del test.
     * 
     * @param startDate fecha de inicio del test.
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Obtiene la fecha de finalización del test.
     * 
     * @return fecha de finalización del test.
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Establece la fecha de finalización del test.
     * 
     * @param endDate fecha de finalización del test.
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    
    @Override
    public String toString() {
        return "Puntuaciones {" + "puntuaciones=" + puntuaciones + '}';
    }

}
