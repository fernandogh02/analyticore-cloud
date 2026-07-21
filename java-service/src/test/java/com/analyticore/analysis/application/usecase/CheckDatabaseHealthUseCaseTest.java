package com.analyticore.analysis.application.usecase;

import com.analyticore.analysis.application.port.out.DatabaseHealthPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de salud de PostgreSQL.
 */
class CheckDatabaseHealthUseCaseTest {

    @Test
    void reportsHealthyDatabase() {
        DatabaseHealthPort port =
            new FakeDatabaseHealthPort(true, true);

        CheckDatabaseHealthUseCase useCase =
            new CheckDatabaseHealthUseCase(port);

        DatabaseHealthResult result =
            useCase.execute();

        assertTrue(result.databaseConnected());
        assertTrue(result.tableAvailable());
        assertTrue(result.isHealthy());
    }

    @Test
    void reportsUnavailableDatabase() {
        DatabaseHealthPort port =
            new FakeDatabaseHealthPort(false, false);

        CheckDatabaseHealthUseCase useCase =
            new CheckDatabaseHealthUseCase(port);

        DatabaseHealthResult result =
            useCase.execute();

        assertFalse(result.databaseConnected());
        assertFalse(result.tableAvailable());
        assertFalse(result.isHealthy());
    }

    private record FakeDatabaseHealthPort(
        boolean connected,
        boolean tableExists
    ) implements DatabaseHealthPort {

        @Override
        public boolean canConnect() {
            return connected;
        }

        @Override
        public boolean analysisJobsTableExists() {
            return tableExists;
        }
    }
}