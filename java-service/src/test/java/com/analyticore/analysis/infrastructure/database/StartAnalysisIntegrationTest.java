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
 * Prueba real del análisis de sentimiento.
 */
@Tag("integration")
@SpringBootTest
class StartAnalysisIntegrationTest {

    @Autowired
    private StartAnalysisUseCase useCase;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void savesPositiveSentimentInPostgresql() {
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
                "La plataforma es excelente y fácil.",
                "PENDIENTE"
            );

            StartAnalysisResult result =
                useCase.execute(jobId);

            assertEquals(
                JobStatus.PROCESANDO,
                result.status()
            );

            StoredResult stored =
                jdbcTemplate.queryForObject(
                    """
                    SELECT status, sentiment
                    FROM analysis_jobs
                    WHERE id = ?
                    """,
                    (resultSet, rowNumber) ->
                        new StoredResult(
                            resultSet.getString(
                                "status"
                            ),
                            resultSet.getString(
                                "sentiment"
                            )
                        ),
                    jobId
                );

            assertEquals(
                "PROCESANDO",
                stored.status()
            );

            assertEquals(
                "POSITIVO",
                stored.sentiment()
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

    private record StoredResult(
        String status,
        String sentiment
    ) {
    }
}