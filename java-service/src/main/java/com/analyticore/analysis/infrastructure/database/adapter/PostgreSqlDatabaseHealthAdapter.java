package com.analyticore.analysis.infrastructure.database.adapter;

import com.analyticore.analysis.application.port.out.DatabaseHealthPort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Comprueba la conexión y la existencia de la tabla en PostgreSQL.
 */
@Component
public class PostgreSqlDatabaseHealthAdapter
    implements DatabaseHealthPort {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Crea el adaptador utilizando el JdbcTemplate administrado por Spring.
     *
     * @param jdbcTemplate componente utilizado para ejecutar consultas SQL
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = """
            JdbcTemplate es una dependencia administrada por Spring
            y se conserva intencionalmente mediante inyección por constructor.
            """
    )
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