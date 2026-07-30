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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba real del análisis completo con PostgreSQL.
 */
@Tag("integration")
@SpringBootTest
class StartAnalysisIntegrationTest {

    @Autowired
    private StartAnalysisUseCase useCase;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void completesAnalysisInPostgresql() {
        UUID jobId = UUID.randomUUID();

        try {
            jdbcTemplate.update(
                """
                INSERT INTO analysis_jobs (
                    id,
                    text_content,
                    status,
                    keywords,
                    created_at,
                    updated_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    CAST('[]' AS jsonb),
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                )
                """,
                jobId,
                """
                La plataforma es excelente.
                La plataforma analiza comentarios
                de clientes rápidamente.
                """,
                "PENDIENTE"
            );

            StartAnalysisResult result =
                useCase.execute(jobId);

            assertEquals(
                JobStatus.COMPLETADO,
                result.status()
            );

            StoredResult stored =
                jdbcTemplate.queryForObject(
                    """
                    SELECT
                        status,
                        sentiment,
                        keywords::text AS keywords
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
                            ),
                            resultSet.getString(
                                "keywords"
                            )
                        ),
                    jobId
                );

            assertEquals(
                "COMPLETADO",
                stored.status()
            );

            assertEquals(
                "POSITIVO",
                stored.sentiment()
            );

            assertTrue(
                stored.keywords().contains(
                    "plataforma"
                )
            );

            assertTrue(
                stored.keywords().contains(
                    "comentarios"
                )
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
        String sentiment,
        String keywords
    ) {
    }
}