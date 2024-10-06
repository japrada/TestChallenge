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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests que validan los casos de envío de respuestas correctas e incorrectas para algunas preguntas definidas.
 *
 * @author japrada
 */
public class TestModel {

    public TestModel() {

    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testConstruirPregunta_01() {
           // Definir la pregunta
        Pregunta pregunta = new Pregunta();
        pregunta.setId(1);
        pregunta.setTexto("Empareja cada opción con la respuesta correcta:");
        pregunta.setTipo(TipoPregunta.RESPUESTA_EMPAREJADA);
        pregunta.setOpciones(new String[]{"Opción 1", "Opción 2", "Opción 3", "Opción 4"});
        pregunta.setValoresOpciones(
                new String[][]{
                    {"Opción 1 - Respuesta 1", "Opción 1 - Respuesta 2", "Opción 1 - Respuesta 3", "Opción 1 - Respuesta 4"},
                    {"Opción 2 - Respuesta 1", "Opción 2 - Respuesta 2", "Opción 2 - Respuesta 3", "Opción 2 - Respuesta 4"},
                    {"Opción 3 - Respuesta 1", "Opción 3 - Respuesta 2", "Opción 3 - Respuesta 3", "Opción 3 - Respuesta 4"},
                    {"Opción 4 - Respuesta 1", "Opción 4 - Respuesta 2", "Opción 4 - Respuesta 3", "Opción 4 - Respuesta 4"}
                });
        pregunta.setRespuestas(
                new String[]{
                    "Opción 1 - Respuesta 1",
                    "Opción 2 - Respuesta 2",
                    "Opción 3 - Respuesta 3",
                    "Opción 4 - Respuesta 4"
                });

        pregunta.setDesordenarOpcionesFlag(false);
        
        // Preparar un objeto Respuesta para la pregunta a partir de las respuestas
        Respuesta respuesta = new Respuesta(
                pregunta.getRespuestas(),
                TipoPregunta.RESPUESTA_EMPAREJADA);

        pregunta.setRespuesta(respuesta);

        // Preparar un objeto Respuesta para la respuesta enviada
        Respuesta respuestaEnviada = new Respuesta();
        respuestaEnviada.setTipoPregunta(TipoPregunta.RESPUESTA_EMPAREJADA);
        respuestaEnviada.setOpciones(
                new String[]{
                    "Opción 1 - Respuesta 1",
                    "Opción 2 - Respuesta 2",
                    "Opción 3 - Respuesta 3",
                    "Opción 4 - Respuesta 4"
                });

        // Comparar la respuesta de la pregunta con la respuesta enviada 
        assertEquals(respuestaEnviada, pregunta.getRespuesta());
    }
    
    /**
     * Test que compara una respuesta enviada correcta con la respuesta a una pregunta de tipo único.
     *
     * Resultado: el test pasa porque la respuesta enviada es correcta y del mismo tipo.
     */
    @Test
    public void testPreguntaConRespuestaUnica_01() {

        // Definir la pregunta 
        Pregunta pregunta = new Pregunta();
        pregunta.setId(1);
        pregunta.setTexto("¿Cómo se dice en inglés 'cerrar un trato'?");
        pregunta.setTipo(TipoPregunta.RESPUESTA_UNICA);
        pregunta.setOpciones(new String[]{"To cut a deal", "To do a deal", "To close a treat"});
        pregunta.setRespuestas(new String[]{"To cut a deal"});

        // Preparar un objeto Respuesta para la pregunta a partir de las respuestas
        Respuesta respuesta = new Respuesta(
                pregunta.getRespuestas(),
                TipoPregunta.RESPUESTA_UNICA);

        pregunta.setRespuesta(respuesta);

        // Preparar un objeto Respuesta para la respuesta enviada
        Respuesta respuestaEnviada = new Respuesta();
        respuestaEnviada.setTipoPregunta(TipoPregunta.RESPUESTA_UNICA);
        respuestaEnviada.setOpciones(new String[]{"To cut a deal"});

        // Comparar la respuesta de la pregunta con la respuesta enviada
        assertEquals(respuestaEnviada, pregunta.getRespuesta());
    }

    /**
     * Test que compara una respuesta enviada incorrecta con la respuesta a una pregunta de tipo único.
     *
     * Resultado: el test falla porque la respuesta enviada no es correcta.
     */
    @Test
    public void testPreguntaConRespuestaUnica_02() {

        // Definir la pregunta 
        Pregunta pregunta = new Pregunta();
        pregunta.setId(1);
        pregunta.setTexto("¿Cómo se dice en inglés 'cerrar un trato'?");
        pregunta.setTipo(TipoPregunta.RESPUESTA_UNICA);
        pregunta.setOpciones(new String[]{"To cut a deal", "To do a deal", "To close a treat"});
        pregunta.setRespuestas(new String[]{"To cut a deal"});

        // Preparar un objeto Respuesta para la pregunta a partir de las respuestas
        Respuesta respuesta = new Respuesta(
                pregunta.getRespuestas(),
                TipoPregunta.RESPUESTA_UNICA);

        pregunta.setRespuesta(respuesta);

        // Preparar un objeto Respuesta para la respuesta enviada
        Respuesta respuestaEnviada = new Respuesta();
        respuestaEnviada.setTipoPregunta(TipoPregunta.RESPUESTA_UNICA);
        respuestaEnviada.setOpciones(new String[]{"To close a deal"});

        // Comparar la respuesta de la pregunta con la respuesta enviada
        assertFalse(respuestaEnviada.equals(pregunta.getRespuesta()));

        fail("La respuesta enviada no es una respuesta correcta");
    }

    /**
     * Test que compara una respuesta enviada correcta con la respuesta a una pregunta de tipo múltiple.
     *
     * Resultado: el test pasa porque la respuesta enviada es correcta y del mismo tipo.
     */
    @Test
    public void testPreguntaConRespuestaMultiple_01() {

        // Definir la pregunta
        Pregunta pregunta = new Pregunta();
        pregunta.setId(2);
        pregunta.setTexto("¿Cómo se dice en inglés 'cerrar un trato'?");
        pregunta.setTipo(TipoPregunta.RESPUESTA_MULTIPLE);
        pregunta.setOpciones(new String[]{"To cut a deal", "To make a deal", "To do a deal", "To close a treat"});
        pregunta.setRespuestas(new String[]{"To cut a deal", "To make a deal"});

        // Preparar un objeto Respuesta para la pregunta a partir de las respuestas
        Respuesta respuesta = new Respuesta(
                pregunta.getRespuestas(),
                TipoPregunta.RESPUESTA_MULTIPLE);

        pregunta.setRespuesta(respuesta);

        // Preparar un objeto Respuesta para la respuesta enviada
        Respuesta respuestaEnviada = new Respuesta();
        respuestaEnviada.setTipoPregunta(TipoPregunta.RESPUESTA_MULTIPLE);
        respuestaEnviada.setOpciones(new String[]{"To cut a deal", "To make a deal"});

        // Comparar la respuesta de la pregunta con la respuesta enviada 
        assertEquals(respuestaEnviada, pregunta.getRespuesta());
    }

    /**
     * Test que compara una respuesta enviada incorrecta con la respuesta a una pregunta de tipo múltiple.
     *
     * Resultado: el test falla porque la respuesta enviada no es correcta.
     */
    @Test
    public void testPreguntaConRespuestaMultiple_02() {

        // Definir la pregunta
        Pregunta pregunta = new Pregunta();
        pregunta.setId(2);
        pregunta.setTexto("¿Cómo se dice en inglés 'cerrar un trato'?");
        pregunta.setTipo(TipoPregunta.RESPUESTA_MULTIPLE);
        pregunta.setOpciones(new String[]{"To cut a deal", "To make a deal", "To do a deal", "To close a treat"});
        pregunta.setRespuestas(new String[]{"To cut a deal", "To make a deal"});

        // Preparar un objeto Respuesta para la pregunta a partir de las respuestas
        Respuesta respuesta = new Respuesta(
                pregunta.getRespuestas(),
                TipoPregunta.RESPUESTA_MULTIPLE);

        pregunta.setRespuesta(respuesta);

        // Preparar un objeto Respuesta para la respuesta enviada
        Respuesta respuestaEnviada = new Respuesta();
        respuestaEnviada.setTipoPregunta(TipoPregunta.RESPUESTA_MULTIPLE);
        respuestaEnviada.setOpciones(new String[]{"To cut a deal"});

        // Comparar la respuesta de la pregunta con la respuesta enviada
        assertFalse(respuestaEnviada.equals(pregunta.getRespuesta()));

        fail("La respuesta enviada no es una respuesta correcta");
    }

    /**
     * Test que compara una respuesta enviada correcta con la respuesta a una pregunta de tipo emparejada.
     *
     * Resultado: el test pasa porque la respuesta enviada es correcta.
     */
    @Test
    public void testPreguntaConRespuestaEmparejada_01() {

        // Definir la pregunta
        Pregunta pregunta = new Pregunta();
        pregunta.setId(3);
        pregunta.setTexto("Empareja cada opción con la respuesta correcta:");
        pregunta.setTipo(TipoPregunta.RESPUESTA_EMPAREJADA);
        pregunta.setOpciones(new String[]{"Opción 1", "Opción 2", "Opción 3", "Opción 4"});
        pregunta.setValoresOpciones(
                new String[][]{
                    {"Opción 1 - Respuesta 1", "Opción 1 - Respuesta 2", "Opción 1 - Respuesta 3", "Opción 1 - Respuesta 4"},
                    {"Opción 2 - Respuesta 1", "Opción 2 - Respuesta 2", "Opción 2 - Respuesta 3", "Opción 2 - Respuesta 4"},
                    {"Opción 3 - Respuesta 1", "Opción 3 - Respuesta 2", "Opción 3 - Respuesta 3", "Opción 3 - Respuesta 4"},
                    {"Opción 4 - Respuesta 1", "Opción 4 - Respuesta 2", "Opción 4 - Respuesta 3", "Opción 4 - Respuesta 4"}
                });
        pregunta.setRespuestas(
                new String[]{
                    "Opción 1 - Respuesta 1",
                    "Opción 2 - Respuesta 2",
                    "Opción 3 - Respuesta 3",
                    "Opción 4 - Respuesta 4"
                });

        pregunta.setDesordenarOpcionesFlag(false);
        
        // Preparar un objeto Respuesta para la pregunta a partir de las respuestas
        Respuesta respuesta = new Respuesta(
                pregunta.getRespuestas(),
                TipoPregunta.RESPUESTA_EMPAREJADA);

        pregunta.setRespuesta(respuesta);

        // Preparar un objeto Respuesta para la respuesta enviada
        Respuesta respuestaEnviada = new Respuesta();
        respuestaEnviada.setTipoPregunta(TipoPregunta.RESPUESTA_EMPAREJADA);
        respuestaEnviada.setOpciones(
                new String[]{
                    "Opción 1 - Respuesta 1",
                    "Opción 2 - Respuesta 2",
                    "Opción 3 - Respuesta 3",
                    "Opción 4 - Respuesta 4"
                });

        // Comparar la respuesta de la pregunta con la respuesta enviada 
        assertEquals(respuestaEnviada, pregunta.getRespuesta());
    }

    /**
     * Test que compara una respuesta enviada correcta con la respuesta a una pregunta de tipo emparejada.
     *
     * Resultado: el test pasa porque la respuesta enviada es correcta.
     */
    @Test
    public void testPreguntaConRespuestaEmparejada_02() {

        // Definir la pregunta
        Pregunta pregunta = new Pregunta();
        pregunta.setId(3);
        pregunta.setTexto("Empareja cada opción con la respuesta correcta:");
        pregunta.setTipo(TipoPregunta.RESPUESTA_EMPAREJADA);
        pregunta.setOpciones(new String[]{"Opción 1", "Opción 2", "Opción 3", "Opción 4"});
        pregunta.setValoresOpciones(
                new String[][]{
                    {"Respuesta 1", "Respuesta 2", "Respuesta 3", "Respuesta 4"},
                    {"Respuesta 1", "Respuesta 2", "Respuesta 3", "Respuesta 4"},
                    {"Respuesta 1", "Respuesta 2", "Respuesta 3", "Respuesta 4"},
                    {"Respuesta 1", "Respuesta 2", "Respuesta 3", "Respuesta 4"}
                });
        pregunta.setRespuestas(
                new String[]{
                    "Respuesta 1",
                    "Respuesta 2",
                    "Respuesta 3",
                    "Respuesta 4"
                });

        // Preparar un objeto Respuesta para la pregunta a partir de las respuestas
        Respuesta respuesta = new Respuesta(
                pregunta.getRespuestas(),
                TipoPregunta.RESPUESTA_EMPAREJADA);

        pregunta.setRespuesta(respuesta);

        // Preparar un objeto Respuesta para la respuesta enviada
        Respuesta respuestaEnviada = new Respuesta();
        respuestaEnviada.setTipoPregunta(TipoPregunta.RESPUESTA_EMPAREJADA);
        respuestaEnviada.setOpciones(
                new String[]{
                    "Respuesta 1",
                    "Respuesta 2",
                    "Respuesta 3",
                    "Respuesta 4"
                });

        // Comparar la respuesta de la pregunta con la respuesta enviada 
        assertEquals(respuestaEnviada, pregunta.getRespuesta());
    }

    /**
     * Test que compara una respuesta incorrecta con la respuesta correcta a una pregunta de tipo emparejada.
     *
     * Resultado: el test no pasa porque la respuesta enviada es incorrecta.
     */
    @Test
    public void testPreguntaConRespuestaEmparejada_03() {

        // Definir la pregunta
        Pregunta pregunta = new Pregunta();
        pregunta.setId(3);
        pregunta.setTexto("Empareja cada opción con la respuesta correcta:");
        pregunta.setTipo(TipoPregunta.RESPUESTA_EMPAREJADA);
        pregunta.setOpciones(new String[]{"Opción 1", "Opción 2", "Opción 3", "Opción 4"});
        pregunta.setValoresOpciones(
                new String[][]{
                    {"Respuesta 1", "Respuesta 2", "Respuesta 3", "Respuesta 4"},
                    {"Respuesta 1", "Respuesta 2", "Respuesta 3", "Respuesta 4"},
                    {"Respuesta 1", "Respuesta 2", "Respuesta 3", "Respuesta 4"},
                    {"Respuesta 1", "Respuesta 2", "Respuesta 3", "Respuesta 4"}
                });
        pregunta.setRespuestas(
                new String[]{
                    "Respuesta 1",
                    "Respuesta 2",
                    "Respuesta 3",
                    "Respuesta 4"
                });

        // Preparar un objeto Respuesta para la pregunta a partir de las respuestas
        Respuesta respuesta = new Respuesta(
                pregunta.getRespuestas(),
                TipoPregunta.RESPUESTA_EMPAREJADA);

        pregunta.setRespuesta(respuesta);

        // Preparar un objeto Respuesta para la respuesta enviada
        Respuesta respuestaEnviada = new Respuesta();
        respuestaEnviada.setTipoPregunta(TipoPregunta.RESPUESTA_EMPAREJADA);
        respuestaEnviada.setOpciones(
                new String[]{
                    "Respuesta 1",
                    "Respuesta 1",
                    "Respuesta 1",
                    "Respuesta 1"
                });

        // Comparar la respuesta de la pregunta con la respuesta enviada
        assertFalse(respuestaEnviada.equals(pregunta.getRespuesta()));

        fail("La respuesta enviada no es una respuesta correcta");
    }

    /**
     * Test que compara una respuesta enviada correcta con la respuesta a una pregunta de tipo multivalor.
     *
     * Resultado: el test pasa porque la respuesta enviada es correcta.
     */
    @Test
    public void testPreguntaConRespuestaMultivalor_01() {

        // Definir la pregunta
        Pregunta pregunta = new Pregunta();
        pregunta.setId(3);
        pregunta.setTexto("Selecciona la respuesta correcta en cada uno de los casos:");
        pregunta.setTipo(TipoPregunta.RESPUESTA_MULTIVALOR);
        pregunta.setOpciones(new String[]{"Caso 1", "Caso 2", "Caso 3", "Caso 4"});
        pregunta.setValoresOpciones(
                new String[][]{
                    {"Verdadero", "Falso", "Depende"},
                    {"Respuesta 1", "Respuesta 2", "Respuesta 3"},
                    {"Respuesta 1", "Respuesta 2", "Respuesta 3", "Respuesta 4"},
                    {"Valor 1", "Valor 2", "Valor 3", "Valor 4"}
                });
        pregunta.setRespuestas(
                new String[]{
                    "Depende",
                    "Respuesta 2",
                    "Respuesta 3",
                    "Valor 4"
                });

        // Preparar un objeto Respuesta para la pregunta a partir de las respuestas
        Respuesta respuesta = new Respuesta(
                pregunta.getRespuestas(),
                TipoPregunta.RESPUESTA_MULTIVALOR);

        pregunta.setRespuesta(respuesta);

        // Preparar un objeto Respuesta para la respuesta enviada
        Respuesta respuestaEnviada = new Respuesta();
        respuestaEnviada.setTipoPregunta(TipoPregunta.RESPUESTA_MULTIVALOR);
        respuestaEnviada.setOpciones(
                new String[]{
                    "Depende",
                    "Respuesta 2",
                    "Respuesta 3",
                    "Valor 4"
                });

        // Comparar la respuesta de la pregunta con la respuesta enviada 
        assertEquals(respuestaEnviada, pregunta.getRespuesta());
    }

    /**
     * Test que compara una respuesta enviada incorrecta con la respuesta correcta a una pregunta de tipo multivalor.
     *
     * Resultado: el test no pasa porque la respuesta enviada es incorrecta.
     */
    @Test
    public void testPreguntaConRespuestaMultivalor_02() {

        // Definir la pregunta
        Pregunta pregunta = new Pregunta();
        pregunta.setId(3);
        pregunta.setTexto("Selecciona la respuesta correcta en cada uno de los casos:");
        pregunta.setTipo(TipoPregunta.RESPUESTA_MULTIVALOR);
        pregunta.setOpciones(new String[]{"Caso 1", "Caso 2", "Caso 3", "Caso 4"});
        pregunta.setValoresOpciones(
                new String[][]{
                    {"Verdadero", "Falso", "Depende"},
                    {"Respuesta 1", "Respuesta 2", "Respuesta 3"},
                    {"Respuesta 1", "Respuesta 2", "Respuesta 3", "Respuesta 4"},
                    {"Valor 1", "Valor 2", "Valor 3", "Valor 4"}
                });
        pregunta.setRespuestas(
                new String[]{
                    "Depende",
                    "Respuesta 2",
                    "Respuesta 3",
                    "Valor 4"
                });

        // Preparar un objeto Respuesta para la pregunta a partir de las respuestas
        Respuesta respuesta = new Respuesta(
                pregunta.getRespuestas(),
                TipoPregunta.RESPUESTA_MULTIVALOR);

        pregunta.setRespuesta(respuesta);

        // Preparar un objeto Respuesta para la respuesta enviada
        Respuesta respuestaEnviada = new Respuesta();
        respuestaEnviada.setTipoPregunta(TipoPregunta.RESPUESTA_MULTIVALOR);
        respuestaEnviada.setOpciones(
                new String[]{
                    "Verdadero",
                    "Respuesta 1",
                    "Respuesta 2",
                    "Valor 1"
                });

        // Comparar la respuesta de la pregunta con la respuesta enviada
        assertFalse(respuestaEnviada.equals(pregunta.getRespuesta()));

        fail("La respuesta enviada no es una respuesta correcta");
    }

    /**
     * Test que compara una respuesta enviada correcta con la respuesta a una pregunta de tipo texto.
     *
     * Resultado: el test pasa porque la respuesta enviada es una respuesta correcta y del mismo tipo.
     */
    @Test
    public void testPreguntaConRespuestaTexto_01() {

        // Definir la pregunta
        Pregunta pregunta = new Pregunta();
        pregunta.setId(3);
        pregunta.setTexto("¿Cómo se dice en inglés 'cerrar un trato'?");
        pregunta.setTipo(TipoPregunta.RESPUESTA_TEXTO_LIBRE);
        pregunta.setOpciones(new String[]{});
        pregunta.setRespuestas(
                new String[]{
                    "To cut a deal",
                    "to cut a deal",
                    "cut a deal",
                    "Cut a deal",
                    "To make a deal",
                    "to make a deal",
                    "make a deal",
                    "Make a deal"
                });

        // Preparar un objeto Respuesta para la pregunta a partir de las respuestas
        Respuesta respuesta = new Respuesta(
                pregunta.getRespuestas(),
                TipoPregunta.RESPUESTA_TEXTO_LIBRE);

        pregunta.setRespuesta(respuesta);

        // Preparar un objeto Respuesta para la respuesta enviada
        Respuesta respuestaEnviada = new Respuesta();
        respuestaEnviada.setTipoPregunta(TipoPregunta.RESPUESTA_TEXTO_LIBRE);
        respuestaEnviada.setOpciones(new String[]{"To make a deal"});

        // Comparar la respuesta de la pregunta con la respuesta enviada 
        assertEquals(respuestaEnviada, pregunta.getRespuesta());
    }

    /**
     * Test que compara una respuesta enviada incorrecta con la respuesta de una pregunta de tipo texto.
     *
     * Resultado: el test falla porque la respuesta enviada no es una respuesta correcta.
     */
    @Test
    public void testPreguntaConRespuestaTexto_02() {

        // Definir la pregunta
        Pregunta pregunta = new Pregunta();
        pregunta.setId(3);
        pregunta.setTexto("¿Cómo se dice en inglés 'cerrar un trato'?");
        pregunta.setTipo(TipoPregunta.RESPUESTA_TEXTO_LIBRE);
        pregunta.setOpciones(new String[]{});
        pregunta.setRespuestas(
                new String[]{
                    "To cut a deal",
                    "to cut a deal",
                    "cut a deal",
                    "Cut a deal",
                    "To make a deal",
                    "to make a deal",
                    "make a deal",
                    "Make a deal"
                });

        // Preparar un objeto Respuesta para la pregunta a partir de las respuestas
        Respuesta respuesta = new Respuesta(
                pregunta.getRespuestas(),
                TipoPregunta.RESPUESTA_TEXTO_LIBRE);

        pregunta.setRespuesta(respuesta);

        // Preparar un objeto Respuesta para la respuesta enviada
        Respuesta respuestaEnviada = new Respuesta();
        respuestaEnviada.setTipoPregunta(TipoPregunta.RESPUESTA_TEXTO_LIBRE);
        respuestaEnviada.setOpciones(new String[]{"To close a deal"});

        // Comparar la respuesta de la pregunta con la respuesta enviada        
        assertFalse(respuestaEnviada.equals(pregunta.getRespuesta()));

        fail("La respuesta enviada no es una respuesta correcta");
    }

    /**
     * Test que compara una respuesta enviada de un tipo con la respuesta de una pregunta de otro tipo.
     *
     * Resultado: el test falla porque la respuesta enviada no es del mismo tipo que el tipo de respuesta de la
     * pregunta.
     */
    @Test
    public void testPreguntaConRespuestaTexto_03() {

        // Definir la pregunta
        Pregunta pregunta = new Pregunta();
        pregunta.setId(3);
        pregunta.setTexto("¿Cómo se dice en inglés 'cerrar un trato'?");
        pregunta.setTipo(TipoPregunta.RESPUESTA_TEXTO_LIBRE);
        pregunta.setOpciones(new String[]{});
        pregunta.setRespuestas(
                new String[]{
                    "To cut a deal",
                    "to cut a deal",
                    "cut a deal",
                    "Cut a deal",
                    "To make a deal",
                    "to make a deal",
                    "make a deal",
                    "Make a deal"
                });

        // Prepar un objeto Respuesta para la pregunta a partir de las respuestas
        Respuesta respuesta = new Respuesta(
                pregunta.getRespuestas(),
                TipoPregunta.RESPUESTA_TEXTO_LIBRE);

        pregunta.setRespuesta(respuesta);

        // Preparar un objeto Respuesta para la respuesta enviada
        Respuesta respuestaEnviada = new Respuesta();
        respuestaEnviada.setTipoPregunta(TipoPregunta.RESPUESTA_MULTIPLE);
        respuestaEnviada.setOpciones(new String[]{"To make a deal"});

        // Comparar la respuesta de la pregunta con la respuesta enviada        
        assertFalse(respuestaEnviada.equals(pregunta.getRespuesta()));

        fail("La respuesta enviada no es del mismo tipo que la pregunta");
    }

}
