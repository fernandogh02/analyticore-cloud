package com.analyticore.analysis.application.usecase;

import com.analyticore.analysis.application.port.out.AnalysisJobStatusPort;
import com.analyticore.analysis.domain.exception.AnalysisJobNotFoundException;
import com.analyticore.analysis.domain.exception.InvalidJobStateException;
import com.analyticore.analysis.domain.model.AnalysisJobSnapshot;
import com.analyticore.analysis.domain.model.JobStatus;
import com.analyticore.analysis.domain.model.Sentiment;
import com.analyticore.analysis.domain.model.SentimentAnalysisResult;
import com.analyticore.analysis.domain.service.SentimentAnalyzer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Inicia el procesamiento y analiza el sentimiento.
 */
@Service
public class StartAnalysisUseCase {

    private final AnalysisJobStatusPort jobStatusPort;
    private final SentimentAnalyzer sentimentAnalyzer;

    public StartAnalysisUseCase(
        AnalysisJobStatusPort jobStatusPort,
        SentimentAnalyzer sentimentAnalyzer
    ) {
        this.jobStatusPort = jobStatusPort;
        this.sentimentAnalyzer = sentimentAnalyzer;
    }

    /**
     * Cambia el trabajo a PROCESANDO y calcula
     * su sentimiento.
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

        validateStatus(job);

        if (job.status() == JobStatus.PENDIENTE) {
            jobStatusPort.markAsProcessing(jobId);
        }

        Sentiment sentiment = job.sentiment();

        if (sentiment == null) {
            SentimentAnalysisResult analysis =
                sentimentAnalyzer.analyze(
                    job.textContent()
                );

            sentiment = analysis.sentiment();

            jobStatusPort.saveSentiment(
                jobId,
                sentiment
            );
        }

        return new StartAnalysisResult(
            jobId,
            JobStatus.PROCESANDO
        );
    }

    private void validateStatus(
        AnalysisJobSnapshot job
    ) {
        boolean allowed =
            job.status() == JobStatus.PENDIENTE
                || job.status() == JobStatus.PROCESANDO;

        if (!allowed) {
            throw new InvalidJobStateException(
                job.id(),
                job.status()
            );
        }
    }
}