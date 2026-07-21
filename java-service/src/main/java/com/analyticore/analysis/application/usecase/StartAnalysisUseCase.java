package com.analyticore.analysis.application.usecase;

import com.analyticore.analysis.application.port.out.AnalysisJobStatusPort;
import com.analyticore.analysis.domain.exception.AnalysisJobNotFoundException;
import com.analyticore.analysis.domain.exception.InvalidJobStateException;
import com.analyticore.analysis.domain.model.AnalysisJobSnapshot;
import com.analyticore.analysis.domain.model.JobStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Inicia el procesamiento de un trabajo.
 */
@Service
public class StartAnalysisUseCase {

    private final AnalysisJobStatusPort jobStatusPort;

    public StartAnalysisUseCase(
        AnalysisJobStatusPort jobStatusPort
    ) {
        this.jobStatusPort = jobStatusPort;
    }

    /**
     * Cambia un trabajo pendiente a procesamiento.
     *
     * Un trabajo que ya está en PROCESANDO se considera
     * una solicitud repetida válida.
     *
     * @param jobId identificador recibido desde Python
     * @return estado resultante
     */
    @Transactional
    public StartAnalysisResult execute(UUID jobId) {
        AnalysisJobSnapshot job = jobStatusPort
            .findById(jobId)
            .orElseThrow(
                () -> new AnalysisJobNotFoundException(
                    jobId
                )
            );

        if (job.status() == JobStatus.PROCESANDO) {
            return new StartAnalysisResult(
                jobId,
                JobStatus.PROCESANDO
            );
        }

        if (job.status() != JobStatus.PENDIENTE) {
            throw new InvalidJobStateException(
                jobId,
                job.status()
            );
        }

        jobStatusPort.markAsProcessing(jobId);

        return new StartAnalysisResult(
            jobId,
            JobStatus.PROCESANDO
        );
    }
}