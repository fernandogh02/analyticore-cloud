package com.analyticore.analysis.application.port.out;

import com.analyticore.analysis.domain.model.AnalysisJobSnapshot;
import com.analyticore.analysis.domain.model.Sentiment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Operaciones necesarias para consultar y actualizar
 * un trabajo de análisis.
 */
public interface AnalysisJobStatusPort {

    Optional<AnalysisJobSnapshot> findById(UUID jobId);

    void markAsProcessing(UUID jobId);

    /**
     * Guarda todos los resultados y termina el trabajo.
     *
     * @param jobId identificador
     * @param sentiment sentimiento obtenido
     * @param keywords palabras clave
     */
    void completeAnalysis(
        UUID jobId,
        Sentiment sentiment,
        List<String> keywords
    );
}