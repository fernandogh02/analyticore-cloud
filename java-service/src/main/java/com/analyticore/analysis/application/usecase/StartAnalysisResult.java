package com.analyticore.analysis.application.usecase;

import com.analyticore.analysis.domain.model.JobStatus;

import java.util.UUID;

/**
 * Resultado de aceptar un trabajo para análisis.
 *
 * @param jobId identificador del trabajo
 * @param status nuevo estado
 */
public record StartAnalysisResult(
    UUID jobId,
    JobStatus status
) {
}