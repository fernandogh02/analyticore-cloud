package com.analyticore.analysis.domain.exception;

import com.analyticore.analysis.domain.model.JobStatus;

import java.util.UUID;

/**
 * Indica que un trabajo no puede iniciar su análisis
 * desde el estado actual.
 */
public class InvalidJobStateException
    extends RuntimeException {

    private final UUID jobId;
    private final JobStatus currentStatus;

    public InvalidJobStateException(
        UUID jobId,
        JobStatus currentStatus
    ) {
        super(
            "El trabajo "
                + jobId
                + " no puede procesarse desde el estado "
                + currentStatus
        );

        this.jobId = jobId;
        this.currentStatus = currentStatus;
    }

    public UUID getJobId() {
        return jobId;
    }

    public JobStatus getCurrentStatus() {
        return currentStatus;
    }
}