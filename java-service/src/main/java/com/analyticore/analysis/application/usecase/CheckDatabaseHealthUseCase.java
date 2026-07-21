package com.analyticore.analysis.application.usecase;

import com.analyticore.analysis.application.port.out.DatabaseHealthPort;
import org.springframework.stereotype.Service;

/**
 * Coordina la comprobación de PostgreSQL.
 */
@Service
public class CheckDatabaseHealthUseCase {

    private final DatabaseHealthPort databaseHealthPort;

    public CheckDatabaseHealthUseCase(
        DatabaseHealthPort databaseHealthPort
    ) {
        this.databaseHealthPort = databaseHealthPort;
    }

    public DatabaseHealthResult execute() {
        boolean connected =
            databaseHealthPort.canConnect();

        boolean tableAvailable =
            connected
            && databaseHealthPort.analysisJobsTableExists();

        return new DatabaseHealthResult(
            connected,
            tableAvailable
        );
    }
}