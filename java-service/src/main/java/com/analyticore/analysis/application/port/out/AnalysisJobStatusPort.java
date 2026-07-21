package com.analyticore.analysis.application.port.out;

import com.analyticore.analysis.domain.model.AnalysisJobSnapshot;
import com.analyticore.analysis.domain.model.Sentiment;

import java.util.Optional;
import java.util.UUID;

/**
 * Operaciones necesarias para consultar y actualizar
 * un trabajo de análisis.
 */
public interface AnalysisJobStatusPort {

    Optional<AnalysisJobSnapshot> findById(UUID jobId);

    void markAsProcessing(UUID jobId);

    void saveSentiment(
        UUID jobId,
        Sentiment sentiment
    );
}