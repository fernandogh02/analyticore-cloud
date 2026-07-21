package com.analyticore.analysis.presentation.dto;

/**
 * Información básica del estado del servicio.
 *
 * @param status estado actual
 * @param service nombre del servicio
 */
public record HealthResponse(
    String status,
    String service
) {
}