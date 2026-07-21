package com.analyticore.analysis.presentation.dto;

/**
 * Información general del servicio.
 *
 * @param service nombre del servicio
 * @param application nombre de la plataforma
 * @param version versión actual
 * @param message mensaje descriptivo
 */
public record RootResponse(
    String service,
    String application,
    String version,
    String message
) {
}