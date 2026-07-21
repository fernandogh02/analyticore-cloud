package com.analyticore.analysis.application.usecase;

/**
 * Resultado de la comprobación de PostgreSQL.
 *
 * @param databaseConnected conexión activa
 * @param tableAvailable tabla disponible
 */
public record DatabaseHealthResult(
    boolean databaseConnected,
    boolean tableAvailable
) {

    public boolean isHealthy() {
        return databaseConnected && tableAvailable;
    }
}