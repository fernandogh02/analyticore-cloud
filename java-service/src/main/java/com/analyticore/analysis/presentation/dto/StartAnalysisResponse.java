package com.analyticore.analysis.presentation.dto;

import com.analyticore.analysis.domain.model.JobStatus;

import java.util.UUID;

/**
 * Respuesta al aceptar el trabajo.
 *
 * @param jobId identificador del trabajo
 * @param status estado alcanzado
 * @param message mensaje descriptivo
 */
public record StartAnalysisResponse(
    UUID jobId,
    JobStatus status,
    String message
) {
}