package com.analyticore.analysis.presentation.controller;

import com.analyticore.analysis.application.usecase.CheckDatabaseHealthUseCase;
import com.analyticore.analysis.application.usecase.DatabaseHealthResult;
import com.analyticore.analysis.presentation.dto.DatabaseHealthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expone el estado de PostgreSQL.
 */
@RestController
public class DatabaseHealthController {

    private final CheckDatabaseHealthUseCase useCase;

    public DatabaseHealthController(
        CheckDatabaseHealthUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping("/health/database")
    public ResponseEntity<DatabaseHealthResponse> health() {
        DatabaseHealthResult result =
            useCase.execute();

        DatabaseHealthResponse response =
            new DatabaseHealthResponse(
                result.isHealthy() ? "UP" : "DOWN",
                "postgresql",
                "analyticore",
                "analysis_jobs"
            );

        HttpStatus status = result.isHealthy()
            ? HttpStatus.OK
            : HttpStatus.SERVICE_UNAVAILABLE;

        return ResponseEntity
            .status(status)
            .body(response);
    }
}