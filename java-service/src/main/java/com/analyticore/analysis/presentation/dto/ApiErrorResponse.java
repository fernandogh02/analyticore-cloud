package com.analyticore.analysis.presentation.dto;

/**
 * Respuesta estándar para errores HTTP.
 *
 * @param error código interno
 * @param message explicación segura
 */
public record ApiErrorResponse(
    String error,
    String message
) {
}