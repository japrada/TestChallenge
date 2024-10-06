
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Clase que modela la entidad Pregunta.
 *
 * @author japrada
 */
public class Pregunta implements Serializable {

    // Identificador de la pregunta
    private int id;
    // Texto que describe la pregunta en cuestión
    private String texto;
    // Temática de la pregunta
    private String tematica;
    // Nivel de la pregunta
    private String nivel;
    // Tipo de la pregunta: respuesta simple, respuesta múltiple, texto
    private TipoPregunta tipo;
    // Fichero multimedia (puede ser una imagen o un audio)
    private String ficheroMultimedia;
    // Array de bytes con los datos del fichero multimedia
    private byte[] ficheroMultimediaData;
    // En el caso de que la pregunta lleve asociado un .mp3, obtenemos su duración en segundos
    private long duracionDelAudioEnSegundos;
    // Opciones de la pregunta (en los casos de respuesta de tipo simple o múltiple)
    private List<String> opciones;
    // Valores de la opción (cuando se trata de un combo)
    private List<List<String>> valoresOpciones;
    // Desordenar opciones (por defecto, las opciones se desordenan)
    private boolean desordenarOpcionesFlag;
    // Respuestas válidas de la pregunta
    private List<String> respuestas;
    // Respuesta enviada por el cliente para la pregunta
    private Respuesta respuesta;
    // Título del borde cuando la pregunta se visualiza en un panel
    private String title;
    // Número de orden de la pregunta cuando se envía
    private Integer numeroOrden;
    // Puntuación
    private Puntuacion puntuacion;

    // Extensiones de los ficheros multimedia 
    private static final String[] EXTENSIONES_IMAGEN = new String[]{"jpeg", "jpg", "png", "gif"};
    private static final String[] EXTENSIONES_AUDIO = new String[]{"mp3"};

    /**
     * Construye un objeto <code>Pregunta</code> inicializada con valores por defecto.
     *
     */
    public Pregunta() {
        this.id = 0;
        this.texto = "";
        this.tipo = TipoPregunta.RESPUESTA_UNICA;
        this.tematica = "Inglés";
        this.nivel = Nivel.NORMAL.getNivel();
        this.ficheroMultimedia = "";
        this.ficheroMultimediaData = new byte[]{};
        this.opciones = new ArrayList<>();
        this.desordenarOpcionesFlag = false;
        this.respuestas = new ArrayList<>();
        this.valoresOpciones = new ArrayList<>();
    }

    /**
     * Construye un objeto <code>Pregunta</code> a partir de un texto conteniendo la definición de la pregunta en
     * formato JSON.
     *
     * @param pregunta texto con la definición de la pregunta en formato JSON.
     */
    public Pregunta(String pregunta) {
        build(pregunta);
    }

    /**
     * Construye un objeto <code>Pregunta</code> a partir de los valores de las propiedades especificados en los
     * parámetros.
     *
     * @param id identificador
     * @param texto texto con la pregunta
     * @param tematica temática de la pregunta
     * @param nivel nivel de la pregunta
     * @param tipo tipo de pregunta
     * @param ficheroMultimedia fichero multimedia (imagen o .mp3)
     * @param opciones opciones que se pueden seleccionar como respuesta
     * @param valoresOpciones
     * @param respuestas respuesta (o respuestas) correctas a la pregunta
     */
    public Pregunta(int id,
            String texto,
            String tematica,
            String nivel,
            TipoPregunta tipo,
            String ficheroMultimedia,
            List<String> opciones,
            List<List<String>> valoresOpciones,
            List<String> respuestas) {
        this.id = id;
        this.texto = texto;
        this.tematica = tematica;
        this.nivel = nivel;
        this.tipo = tipo;
        this.ficheroMultimedia = ficheroMultimedia;
        this.opciones = opciones;
        this.valoresOpciones = valoresOpciones;
        this.respuestas = respuestas;
        this.desordenarOpcionesFlag = true;
    }

    /**
     * Establece el identificador de la pregunta.
     *
     * @param id identificador de la pregunta.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el identificador de la pregunta.
     *
     * @return identificador de la pregunta.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el texto de la pregunta.
     *
     * @param texto texto de la pregunta.
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }

    /**
     * Obtiene el texto de la pregunta.
     *
     * @return texto de la pregunta.
     */
    public String getTexto() {
        return texto;
    }

    /**
     * Establece el texto de la temática.
     *
     * NOTA: La temática por defecto en esta aplicación se ha establecido a "Inglés".
     *
     * @param tematica texto de la temática.
     */
    public void setTematica(String tematica) {
        this.tematica = tematica;
    }

    /**
     * Obtiene el texto con la temática de la pregunta.
     *
     * @return texto con la temática de la pregunta.
     */
    public String getTematica() {
        return tematica;
    }

    /**
     * Establece el nivel de la pregunta.
     *
     * @param nivel nivel de la pregunta.
     */
    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    /**
     * Obtiene el texto con el nivel de la pregunta.
     *
     * @return texto con el nivel de la pregunta.
     */
    public String getNivel() {
        return nivel;
    }

    /**
     * Establece el tipo de la pregunta según los tipos definidos en <code>TipoPregunta</code>.
     *
     * @param tipo tipo de la pregunta según los tipos definidos en <code>TipoPregunta</code>.
     */
    public void setTipo(TipoPregunta tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene el tipo de la pregunta según los tipos definidos en <code>TipoPregunta</code>.
     *
     * @return tipo de la pregunta según los tipos definidos en <code>TipoPregunta</code>.
     */
    public TipoPregunta getTipo() {
        return tipo;
    }

    /**
     * Obtiene el nombre del fichero multimedia que hay que mostrar en la pregunta.
     *
     * @return nombre del fichero multimedia que hay que mostrar en la pregunta.
     */
    public String getFicheroMultimedia() {
        return ficheroMultimedia;
    }

    /**
     * Establece el nombre del fichero multimedia que hay que mostrar en la pregunta.
     *
     * @param ficheroMultimedia nombre del fichero multimedia.
     */
    public void setFicheroMultimedia(String ficheroMultimedia) {
        this.ficheroMultimedia = ficheroMultimedia;
    }

    /**
     * Establece el array de bytes del fichero multimedia asociado a la pregunta.
     *
     * @param data array de bytes del fichero multimedia asociado a la pregunta.
     */
    public void setFicheroMultimediaData(byte[] data) {
        ficheroMultimediaData = data;
    }

    /**
     * Establece la duración del audio en segundos.
     *
     * @param duracionDelAudioEnSegundos duración del audio en segundos.
     */
    public void setDuracionDelAudioEnSegundos(long duracionDelAudioEnSegundos) {
        this.duracionDelAudioEnSegundos = duracionDelAudioEnSegundos;
    }

    /**
     * Obtiene el array de bytes del fichero multimedia asociado a la pregunta.
     *
     * @return el array de bytes del fichero multimedia asociado a la pregunta.
     */
    public byte[] getFicheroMultimediaData() {
        return ficheroMultimediaData;
    }

    /**
     * Obtiene la duración del audio en segundos.
     *
     * @return duración del audio en segundos.
     */
    public long getDuracionDelAudioEnSegundos() {
        return duracionDelAudioEnSegundos;
    }

    /**
     * Analiza si la extensión del fichero multimedia asociado a la pregunta es la de una imagen.
     *
     * @return true si el fichero multimedia es una imagen y false en caso contrario.
     */
    public boolean isFicheroMultimediaUnaImagen() {
        return isFicheroMultimedia(EXTENSIONES_IMAGEN);
    }

    /**
     * Analiza si la extensión del fichero multimedia asociado a la pregunta es la de un audio.
     *
     * @return true si el fichero multimedia es un audio y false en caso contrario.
     */
    public boolean isFicheroMultimediaUnAudio() {
        return isFicheroMultimedia(EXTENSIONES_AUDIO);
    }

    /**
     * Establece las opciones de la pregunta a partir de los valores de una colección.
     *
     * @param opciones colección con las opciones de la pregunta.
     */
    public void setOpciones(List<String> opciones) {
        this.opciones = opciones;
    }

    /**
     * Establece las opciones de la pregunta a partir de los valores de un array.
     *
     * @param opciones array con las opciones de la pregunta.
     */
    public void setOpciones(String[] opciones) {
        this.opciones = Arrays.asList(opciones);
    }

    /**
     * Establece los valores de las opciones de la pregunta a partir de los valores de la colección.
     *
     * Se utiliza en preguntas de tipo Multivalor y Emparejada.
     *
     * @param valoresOpciones colección con los valores de las opciones de la pregunta.
     */
    public void setValoresOpciones(List<List<String>> valoresOpciones) {
        this.valoresOpciones = valoresOpciones;
    }

    /**
     * Establece los valores de las opciones de la pregunta a partir de los valores del array.
     *
     * Se utiliza en preguntas de tipo Multivalor y Emparejada.
     *
     * @param valoresOpciones array con los valores de las opciones de la pregunta.
     */
    public void setValoresOpciones(String[][] valoresOpciones) {
        for (String[] valoresOpcion : valoresOpciones) {
            List<String> valoresList = Arrays.asList(valoresOpcion);
            this.valoresOpciones.add(valoresList);
        }
    }

    public void setPuntuacion(Puntuacion puntuacion) {
        this.puntuacion = puntuacion;
    }

    /**
     * Establece el orden de las opciones.
     *
     * @param desordenarOpcionesFlag
     */
    public void setDesordenarOpcionesFlag(boolean desordenarOpcionesFlag) {
        this.desordenarOpcionesFlag = desordenarOpcionesFlag;
    }

    /**
     * Obtiene los valores de las opciones de la pregunta.
     *
     * Se utiliza en preguntas de tipo Multivalor y Emparejada.
     *
     * @return colección con los valores de las opciones de la pregunta.
     */
    public List<List<String>> getValoresOpciones() {
        return valoresOpciones;
    }

    /**
     * Obtiene los valores de las opciones de la pregunta como un array.
     *
     * @return array con los valores de las opciones de la pregunta.
     */
    public String[][] getValoresOpcionesAsArray() {

        String[][] valoresOpcionesArray = new String[valoresOpciones.size()][];

        int i = 0;
        for (List<String> valoresOpcion : valoresOpciones) {
            valoresOpcionesArray[i++] = valoresOpcion.stream().toArray(String[]::new);
        }

        return valoresOpcionesArray;
    }

    /**
     * Obtiene las opciones de la pregunta como una colección.
     *
     * @return colección con las opciones de la pregunta.
     */
    public List<String> getOpciones() {
        return opciones;
    }

    /**
     * Obtiene las opciones de la pregunta como un array.
     *
     * @return array con las opciones de la pregunta
     */
    public String[] getOpcionesAsArray() {
        return opciones.stream().toArray(String[]::new);
    }

    /**
     * Establece las respuestas correctas de la pregunta.
     *
     * @param respuestas colección con las respuestas correctas de la pregunta.
     */
    public void setRespuestas(List<String> respuestas) {
        this.respuestas = respuestas;
    }

    /**
     * Establece las respuestas de la pregunta.
     *
     * @param respuestas array con las respuestas correctas de la pregunta.
     */
    public void setRespuestas(String[] respuestas) {
        this.respuestas = Arrays.asList(respuestas);
    }

    /**
     * Obtiene las respuestas de la pregunta como una colección.
     *
     * @return colección con las respuestas de la pregunta.
     */
    public List<String> getRespuestas() {
        return respuestas;
    }

    /**
     * Obtiene las respuestas de la pregunta como un array.
     *
     * @return array con las respuestas de la pregunta.
     */
    public String[] getRespuestasAsArray() {
        return respuestas.stream().toArray(String[]::new);
    }

    /**
     *
     * @return puntuación obtenida en la pregunta:
     */
    public Puntuacion getPuntuacion() {
        return puntuacion;
    }

    /**
     * Establece el título asociado a la pregunta.
     *
     * @param title título asociado a la pregunta.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Obtiene el título asociado a la pregunta.
     *
     * @return título asociado a la pregunta.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Obtiene el número de orden de la pregunta cuando se presenta en un test.
     *
     * @return número de orden de la pregunta cuando se presenta en un test.
     */
    public Integer getNumeroOrden() {
        return numeroOrden;
    }

    /**
     * Establece el objeto <code>Respuesta</code> que representa la respuesta a la pregunta.
     *
     * @param respuesta objeto <code>Respuesta</code> que representa la respuesta a la pregunta.
     */
    public void setRespuesta(Respuesta respuesta) {
        this.respuesta = respuesta;
    }

    /**
     * Obtiene el objeto <code>Respuesta</code> que representa la respuesta a la pregunta.
     *
     * @return objeto <code>Respuesta</code> que representa la respuesta a la pregunta
     */
    public Respuesta getRespuesta() {
        return respuesta;
    }

    /**
     * Obtiene el valor del flag "desordenar_opciones".
     *
     * @return 1 si las opciones se muestran desordenadas (valor por defecto) y 0 en caso contrario.
     */
    public boolean getDesordenarOpcionesFlag() {
        return desordenarOpcionesFlag;
    }

    /**
     * Establece el número de orden de la pregunta cuando se presenta en un test.
     *
     * @param numeroOrden número de orden de la pregunta cuando se presenta en un test.
     */
    public void setNumeroOrden(Integer numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("\t\"id\":%s,\n", id));
        sb.append(String.format("\t\"texto\":\"%s\",\n", escaparComillaDoble(texto)));
        sb.append(String.format("\t\"tematica\":\"%s\",\n", tematica));
        sb.append(String.format("\t\"nivel\":\"%s\",\n", nivel));
        sb.append(String.format("\t\"tipo\":\"%s\",\n", tipo.getTipo()));

        // Archivo de audio o .mp3 asociado a la pregunta
        if (!ficheroMultimedia.isEmpty()) {
            sb.append(String.format("\t\"fichero_multimedia\":\"%s\",\n", ficheroMultimedia));
        }

        // OPCIONES
        // --------
        sb.append("\t\"opciones\": [\n");

        for (int i = 0; i < opciones.size(); i++) {
            sb.append(String.format("\t\t \"%s\"", escaparComillaDoble(opciones.get(i))));
            if (i + 1 < opciones.size()) {
                sb.append(",\n");
            } else {
                sb.append("\n");
            }
        }
        sb.append("\t],\n");

        // VALORES OPCIONES
        // ----------------
        if (tipo.equals(TipoPregunta.RESPUESTA_MULTIVALOR) ||
                tipo.equals(TipoPregunta.RESPUESTA_EMPAREJADA)) {

            sb.append("\t\"valores_opciones\": [\n");
            
            int numeroValoresOpciones = tipo.equals(TipoPregunta.RESPUESTA_EMPAREJADA)? 1: valoresOpciones.size();
            
            for (int i = 0; i < numeroValoresOpciones; i++) {
                List<String> valores = valoresOpciones.get(i);
                sb.append("\t\t[");
                for (int j = 0; j < valores.size(); j++) {
                    sb.append(String.format("\"%s\"", escaparComillaDoble(valores.get(j))));
                    if (j + 1 < valores.size()) {
                        sb.append(",");
                    }
                }
                sb.append("]");

                if (i + 1 < numeroValoresOpciones) {
                    sb.append(",\n");
                } else {
                    sb.append("\n");
                }
            }
            sb.append("\t],\n");
        }

        // RESPUESTAS
        // -------------
        sb.append("\t\"respuestas\": [\n");

        for (int i = 0; i < respuestas.size(); i++) {
            sb.append(String.format("\t\t \"%s\"", escaparComillaDoble(respuestas.get(i))));
            if (i + 1 < respuestas.size()) {
                sb.append(",\n");
            } else {
                sb.append("\n");
            }
        }

        sb.append("\t],\n");

        // FLAG DESORDENAR OPCIONES: sólo se añade si no es true (valor por defecto)
        if (!desordenarOpcionesFlag) {
            sb.append(String.format("\t\"desordenar_opciones\":\"%s\"\n", desordenarOpcionesFlag));
        }

        sb.append("}\n");

        return sb.toString();

    }

    /**
     * Inicializa los atributos de un objeto <code>Pregunta</code> a partir de la definición de la pregunta en formato
     * JSON.
     *
     * @param pregunta texto con la definición de la pregunta en formato JSON.
     */
    private void build(String pregunta) {

        // Parsear la pregunta (en formato JSON) 
        JsonParser parser = new JsonParser();

        // para extraer la información y construir el objeto Pregunta
        JsonObject gsonObj = parser.parse(pregunta).getAsJsonObject();

        // Extraemos los campos de información del documento
        id = gsonObj.get("id").getAsInt();
        texto = gsonObj.get("texto").getAsString();
        tematica = gsonObj.get("tematica").getAsString();
        nivel = gsonObj.get("nivel").getAsString();
        tipo = TipoPregunta.valueOfLabel(gsonObj.get("tipo").getAsString());

        // El campo desordenarOpcionesFlag es opcional
        JsonElement desordenarOpcionesElement = gsonObj.get("desordenar_opciones");
        desordenarOpcionesFlag = desordenarOpcionesElement == null ? true : desordenarOpcionesElement.getAsBoolean();

        // El campo ficheroMultimediaElement es opcional
        JsonElement ficheroMultimediaElement = gsonObj.get("fichero_multimedia");
        ficheroMultimedia = ficheroMultimediaElement == null ? "" : ficheroMultimediaElement.getAsString();

        // Lista de opciones del test
        JsonArray opcionesArray = gsonObj.get("opciones").getAsJsonArray();

        // Opciones del test
        opciones = new ArrayList<>();
        for (JsonElement opcion : opcionesArray) {
            opciones.add(opcion.getAsString());
        }

        switch (tipo) {
            case RESPUESTA_MULTIVALOR:

                // Cada pregunta tiene sus posibles respuestas, y se presentan en un combo
                valoresOpciones = new ArrayList<>();

                JsonArray valoresOpcionesArray = gsonObj.get("valores_opciones").getAsJsonArray();

                for (JsonElement jsonElement : valoresOpcionesArray) {
                    // Array que define la opción y los valores posibles a presentar en el combo
                    JsonArray valoresArray = jsonElement.getAsJsonArray();
                    List<String> valoresCombo = new ArrayList<>();
                    for (JsonElement valor : valoresArray) {
                        valoresCombo.add(valor.getAsString());
                    }
                    valoresOpciones.add(valoresCombo);
                }
                break;

            case RESPUESTA_EMPAREJADA:
                // Todas las preguntas tienen las mismas posibles respuestas, y hay que emparejar la pregunta con la respuesta
                // En el JSON se presenta un array con un único elemento que se replica para todas las preguntas
                JsonArray valoresOpcionesEmparejadasArray = gsonObj.get("valores_opciones").getAsJsonArray();

                JsonArray valoresArray = valoresOpcionesEmparejadasArray.get(0).getAsJsonArray();
                List<String> valoresCombo = new ArrayList<>();
                for (JsonElement valor : valoresArray) {
                    valoresCombo.add(valor.getAsString());
                }

                // Asignamos como valores que se presetan en el combo de cada pregunta la misma lista de opciones
                valoresOpciones = new ArrayList<>();
                for (String opcion : opciones) {
                    valoresOpciones.add(valoresCombo);
                }
                break;
        }

        // Respuestas del test
        respuestas = new ArrayList<>();

        JsonArray respuestasArray = gsonObj.get("respuestas").getAsJsonArray();
        respuestas = new ArrayList<>();
        for (JsonElement aRespuesta : respuestasArray) {
            respuestas.add(aRespuesta.getAsString());
        }

    }

    /**
     * Método helper para escapar la doble comilla ("") en un texto.
     *
     * @param texto texto en el que se escapa la doble comilla.
     * @return texto en la que la doble comilla aparece escapada
     */
    private String escaparComillaDoble(String texto) {
        return texto.replace("\"", "\\\"");
    }

    /**
     * Analiza si el fichero asociado a la pregunta es de tipo multimedia.
     *
     * @param extensiones extensiones de tipos multimedia que se están considerando.
     * @return true si la extensión del fichero multimedia asociado a la pregunta es de alguno de los tipos considerados
     * y false en caso contrario.
     *
     */
    private boolean isFicheroMultimedia(String[] extensiones) {
        String extension = obtenerExtensionFicheroMultimedia();
        return Arrays.asList(extensiones).contains(extension);
    }

    /**
     * Obtiene la extensión del fichero multimedia asociado a la pregunta.
     *
     * @return extensión del fichero multimedia asociado a la pregunta.
     */
    private String obtenerExtensionFicheroMultimedia() {
        String extension = "";

        int posicion = ficheroMultimedia.lastIndexOf('.');

        if (posicion > 0 && posicion < ficheroMultimedia.length() - 1) {
            extension = ficheroMultimedia.substring(posicion + 1);
        }
        return extension;
    }
}
