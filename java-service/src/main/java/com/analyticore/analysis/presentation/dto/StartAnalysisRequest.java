package com.analyticore.analysis.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Solicitud recibida desde el servicio Python.
 *
 * @param jobId identificador del trabajo
 */
public record StartAnalysisRequest(

    @NotNull(
        message = "jobId es obligatorio"
    )
    UUID jobId

) {
}