package com.analyticore.analysis.domain.service;

import com.analyticore.analysis.domain.model.SentimentAnalysisResult;

/**
 * Contrato para analizar el sentimiento de un texto.
 */
public interface SentimentAnalyzer {

    /**
     * Analiza un texto.
     *
     * @param text texto recibido
     * @return clasificación y puntuación
     */
    SentimentAnalysisResult analyze(String text);
}