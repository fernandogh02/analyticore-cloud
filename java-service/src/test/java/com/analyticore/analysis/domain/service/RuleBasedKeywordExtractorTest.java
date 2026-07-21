package com.analyticore.analysis.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del extractor de palabras clave.
 */
class RuleBasedKeywordExtractorTest {

    private RuleBasedKeywordExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor =
            new RuleBasedKeywordExtractor();
    }

    @Test
    void ranksWordsByFrequency() {
        List<String> keywords =
            extractor.extract(
                """
                La plataforma analiza comentarios.
                La plataforma genera resultados.
                Los comentarios ayudan a los clientes.
                """
            );

        assertEquals(
            List.of(
                "plataforma",
                "comentarios",
                "analiza",
                "genera",
                "resultados"
            ),
            keywords
        );
    }

    @Test
    void normalizesAccents() {
        List<String> keywords =
            extractor.extract(
                """
                Aplicación rápida que procesa
                información útil.
                """
            );

        assertEquals(
            List.of(
                "aplicacion",
                "rapida",
                "procesa",
                "informacion",
                "util"
            ),
            keywords
        );
    }

    @Test
    void removesStopWords() {
        List<String> keywords =
            extractor.extract(
                """
                El la los las de del y para
                con por una un.
                """
            );

        assertTrue(keywords.isEmpty());
    }

    @Test
    void ignoresNumbersAndShortWords() {
        List<String> keywords =
            extractor.extract(
                "IA API 2026."
            );

        assertTrue(keywords.isEmpty());
    }

    @Test
    void limitsResultToFiveKeywords() {
        List<String> keywords =
            extractor.extract(
                """
                Plataforma análisis comentarios
                clientes resultados reportes.
                """
            );

        assertEquals(5, keywords.size());

        assertEquals(
            List.of(
                "plataforma",
                "analisis",
                "comentarios",
                "clientes",
                "resultados"
            ),
            keywords
        );
    }

    @Test
    void doesNotRepeatKeywords() {
        List<String> keywords =
            extractor.extract(
                """
                Cliente cliente cliente
                plataforma plataforma resultado.
                """
            );

        assertEquals(
            List.of(
                "cliente",
                "plataforma",
                "resultado"
            ),
            keywords
        );
    }

    @Test
    void blankTextReturnsEmptyList() {
        List<String> keywords =
            extractor.extract("   ");

        assertTrue(keywords.isEmpty());
    }
}