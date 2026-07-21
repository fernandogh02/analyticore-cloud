package com.analyticore.analysis.infrastructure.database;

import com.analyticore.analysis.application.usecase.CheckDatabaseHealthUseCase;
import com.analyticore.analysis.application.usecase.DatabaseHealthResult;
import com.analyticore.analysis.infrastructure.database.repository.AnalysisJobJpaRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba de integración con PostgreSQL local.
 */
@Tag("integration")
@SpringBootTest
class PostgreSqlConnectionIntegrationTest {

    @Autowired
    private CheckDatabaseHealthUseCase healthUseCase;

    @Autowired
    private AnalysisJobJpaRepository repository;

    @Test
    void connectsAndFindsAnalysisJobsTable() {
        DatabaseHealthResult result =
            healthUseCase.execute();

        assertTrue(result.databaseConnected());
        assertTrue(result.tableAvailable());
        assertTrue(result.isHealthy());
    }

    @Test
    void repositoryCanReadTable() {
        assertDoesNotThrow(() -> {
            repository.count();
        });
    }
}