package com.analyticore.analysis.application.usecase;

import com.analyticore.analysis.application.port.out.AnalysisJobStatusPort;
import com.analyticore.analysis.domain.exception.AnalysisJobNotFoundException;
import com.analyticore.analysis.domain.exception.InvalidJobStateException;
import com.analyticore.analysis.domain.model.AnalysisJobSnapshot;
import com.analyticore.analysis.domain.model.JobStatus;
import com.analyticore.analysis.domain.model.Sentiment;
import com.analyticore.analysis.domain.model.SentimentAnalysisResult;
import com.analyticore.analysis.domain.service.KeywordExtractor;
import com.analyticore.analysis.domain.service.SentimentAnalyzer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Ejecuta el análisis completo de un trabajo.
 */
@Service
public class StartAnalysisUseCase {

    private final AnalysisJobStatusPort jobStatusPort;
    private final SentimentAnalyzer sentimentAnalyzer;
    private final KeywordExtractor keywordExtractor;

    public StartAnalysisUseCase(
        AnalysisJobStatusPort jobStatusPort,
        SentimentAnalyzer sentimentAnalyzer,
        KeywordExtractor keywordExtractor
    ) {
        this.jobStatusPort = jobStatusPort;
        this.sentimentAnalyzer = sentimentAnalyzer;
        this.keywordExtractor = keywordExtractor;
    }

    /**
     * Analiza sentimiento, extrae palabras clave
     * y completa el trabajo.
     *
     * @param jobId identificador recibido desde Python
     * @return estado final
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

        Sentiment sentiment =
            determineSentiment(job);

        List<String> keywords =
            keywordExtractor.extract(
                job.textContent()
            );

        jobStatusPort.completeAnalysis(
            jobId,
            sentiment,
            keywords
        );

        return new StartAnalysisResult(
            jobId,
            JobStatus.COMPLETADO
        );
    }

    private Sentiment determineSentiment(
        AnalysisJobSnapshot job
    ) {
        if (job.sentiment() != null) {
            return job.sentiment();
        }

        SentimentAnalysisResult result =
            sentimentAnalyzer.analyze(
                job.textContent()
            );

        return result.sentiment();
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