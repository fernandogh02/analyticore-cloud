package com.analyticore.analysis.domain.model;

import java.util.UUID;

/**
 * Información mínima de un trabajo necesaria
 * para iniciar su procesamiento.
 *
 * @param id identificador del trabajo
 * @param status estado actual
 */
public record AnalysisJobSnapshot(
    UUID id,
    JobStatus status
) {
}