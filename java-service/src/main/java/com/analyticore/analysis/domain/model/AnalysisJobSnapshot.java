package com.analyticore.analysis.domain.model;

import java.util.UUID;

/**
 * Información del trabajo requerida por los casos de uso.
 *
 * @param id identificador
 * @param textContent texto enviado por el usuario
 * @param status estado actual
 * @param sentiment sentimiento guardado
 */
public record AnalysisJobSnapshot(
    UUID id,
    String textContent,
    JobStatus status,
    Sentiment sentiment
) {
}