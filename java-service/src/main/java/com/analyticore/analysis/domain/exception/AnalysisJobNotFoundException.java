package com.analyticore.analysis.domain.exception;

import java.util.UUID;

/**
 * Se produce cuando no existe el trabajo solicitado.
 */
public class AnalysisJobNotFoundException
    extends RuntimeException {

    private final UUID jobId;

    public AnalysisJobNotFoundException(UUID jobId) {
        super(
            "No se encontró el trabajo solicitado: "
                + jobId
        );

        this.jobId = jobId;
    }

    public UUID getJobId() {
        return jobId;
    }
}