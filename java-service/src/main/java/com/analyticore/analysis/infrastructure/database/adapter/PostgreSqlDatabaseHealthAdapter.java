package com.analyticore.analysis.infrastructure.database.adapter;

import com.analyticore.analysis.application.port.out.DatabaseHealthPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Comprueba la conexión y la tabla mediante PostgreSQL.
 */
@Component
public class PostgreSqlDatabaseHealthAdapter
    implements DatabaseHealthPort {

    private final JdbcTemplate jdbcTemplate;

    public PostgreSqlDatabaseHealthAdapter(
        JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean canConnect() {
        try {
            Integer result = jdbcTemplate.queryForObject(
                "SELECT 1",
                Integer.class
            );

            return result != null && result == 1;
        } catch (RuntimeException exception) {
            return false;
        }
    }

    @Override
    public boolean analysisJobsTableExists() {
        try {
            Boolean result = jdbcTemplate.queryForObject(
                """
                SELECT EXISTS (
                    SELECT 1
                    FROM information_schema.tables
                    WHERE table_schema = 'public'
                      AND table_name = 'analysis_jobs'
                )
                """,
                Boolean.class
            );

            return Boolean.TRUE.equals(result);
        } catch (RuntimeException exception) {
            return false;
        }
    }
}