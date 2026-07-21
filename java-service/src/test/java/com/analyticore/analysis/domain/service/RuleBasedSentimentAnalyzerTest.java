package com.analyticore.analysis.domain.service;

import com.analyticore.analysis.domain.model.Sentiment;
import com.analyticore.analysis.domain.model.SentimentAnalysisResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del algoritmo de sentimiento.
 */
class RuleBasedSentimentAnalyzerTest {

    private RuleBasedSentimentAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new RuleBasedSentimentAnalyzer();
    }

    @Test
    void classifiesPositiveText() {
        SentimentAnalysisResult result =
            analyzer.analyze(
                "La plataforma es excelente y fácil."
            );

        assertEquals(
            Sentiment.POSITIVO,
            result.sentiment()
        );

        assertTrue(result.score() > 0);
    }

    @Test
    void classifiesNegativeText() {
        SentimentAnalysisResult result =
            analyzer.analyze(
                "La aplicación es lenta y terrible."
            );

        assertEquals(
            Sentiment.NEGATIVO,
            result.sentiment()
        );

        assertTrue(result.score() < 0);
    }

    @Test
    void classifiesNeutralText() {
        SentimentAnalysisResult result =
            analyzer.analyze(
                "La plataforma contiene tres módulos."
            );

        assertEquals(
            Sentiment.NEUTRAL,
            result.sentiment()
        );

        assertEquals(0, result.score());
    }

    @Test
    void normalizesAccents() {
        SentimentAnalysisResult result =
            analyzer.analyze(
                "Es una herramienta rápida, fácil y útil."
            );

        assertEquals(
            Sentiment.POSITIVO,
            result.sentiment()
        );
    }

    @Test
    void recognizesNegativeExpression() {
        SentimentAnalysisResult result =
            analyzer.analyze(
                "No funciona bien."
            );

        assertEquals(
            Sentiment.NEGATIVO,
            result.sentiment()
        );
    }

    @Test
    void negatesNegativeWord() {
        SentimentAnalysisResult result =
            analyzer.analyze(
                "No es malo."
            );

        assertEquals(
            Sentiment.POSITIVO,
            result.sentiment()
        );
    }

    @Test
    void appliesIntensifier() {
        SentimentAnalysisResult result =
            analyzer.analyze(
                "Es muy bueno."
            );

        assertEquals(
            Sentiment.POSITIVO,
            result.sentiment()
        );

        assertEquals(2, result.score());
    }

    @Test
    void blankTextIsNeutral() {
        SentimentAnalysisResult result =
            analyzer.analyze("   ");

        assertEquals(
            Sentiment.NEUTRAL,
            result.sentiment()
        );

        assertEquals(0, result.score());
    }
}