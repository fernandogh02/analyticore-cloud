package com.analyticore.analysis.domain.model;

import java.util.List;

/**
 * Resultado del análisis de sentimiento.
 *
 * @param sentiment clasificación obtenida
 * @param score puntuación total
 * @param evidence palabras que influyeron
 */
public record SentimentAnalysisResult(
    Sentiment sentiment,
    int score,
    List<String> evidence
) {

    public SentimentAnalysisResult {
        evidence = List.copyOf(evidence);
    }
}