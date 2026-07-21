package com.analyticore.analysis.application.port.out;

import com.analyticore.analysis.domain.model.AnalysisJobSnapshot;

import java.util.Optional;
import java.util.UUID;

/**
 * Operaciones necesarias para consultar y actualizar
 * el estado de un trabajo.
 */
public interface AnalysisJobStatusPort {

    Optional<AnalysisJobSnapshot> findById(UUID jobId);

    void markAsProcessing(UUID jobId);
}