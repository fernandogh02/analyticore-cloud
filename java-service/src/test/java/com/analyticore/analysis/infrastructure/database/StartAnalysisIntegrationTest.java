package com.analyticore.analysis.infrastructure.database;

import com.analyticore.analysis.application.usecase.StartAnalysisResult;
import com.analyticore.analysis.application.usecase.StartAnalysisUseCase;
import com.analyticore.analysis.domain.model.JobStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Prueba real del cambio PENDIENTE a PROCESANDO.
 */
@Tag("integration")
@SpringBootTest
class StartAnalysisIntegrationTest {

    @Autowired
    private StartAnalysisUseCase useCase;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void changesStatusInPostgresql() {
        UUID jobId = UUID.randomUUID();

        try {
            jdbcTemplate.update(
                """
                INSERT INTO analysis_jobs (
                    id,
                    text_content,
                    status
                )
                VALUES (?, ?, ?)
                """,
                jobId,
                "Prueba de endpoint Java.",
                "PENDIENTE"
            );

            StartAnalysisResult result =
                useCase.execute(jobId);

            assertEquals(
                JobStatus.PROCESANDO,
                result.status()
            );

            String storedStatus =
                jdbcTemplate.queryForObject(
                    """
                    SELECT status
                    FROM analysis_jobs
                    WHERE id = ?
                    """,
                    String.class,
                    jobId
                );

            assertEquals(
                "PROCESANDO",
                storedStatus
            );
        } finally {
            jdbcTemplate.update(
                """
                DELETE FROM analysis_jobs
                WHERE id = ?
                """,
                jobId
            );
        }
    }
}