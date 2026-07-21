package com.analyticore.analysis.presentation.dto;

/**
 * Estado público de PostgreSQL.
 *
 * @param status estado general
 * @param service servicio comprobado
 * @param database base utilizada
 * @param table tabla requerida
 */
public record DatabaseHealthResponse(
    String status,
    String service,
    String database,
    String table
) {
}