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
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas del análisis completo.
 */
class StartAnalysisUseCaseTest {

    @Test
    void completesPendingJob() {
        UUID jobId = UUID.randomUUID();

        FakeAnalysisJobStatusPort port =
            new FakeAnalysisJobStatusPort(
                new AnalysisJobSnapshot(
                    jobId,
                    "La plataforma es excelente.",
                    JobStatus.PENDIENTE,
                    null
                )
            );

        StartAnalysisUseCase useCase =
            createUseCase(
                port,
                Sentiment.POSITIVO,
                List.of(
                    "plataforma",
                    "excelente"
                )
            );

        StartAnalysisResult result =
            useCase.execute(jobId);

        assertEquals(
            JobStatus.COMPLETADO,
            result.status()
        );

        assertEquals(
            JobStatus.COMPLETADO,
            port.currentStatus
        );

        assertEquals(
            Sentiment.POSITIVO,
            port.savedSentiment
        );

        assertEquals(
            List.of(
                "plataforma",
                "excelente"
            ),
            port.savedKeywords
        );
    }

    @Test
    void completesProcessingJob() {
        UUID jobId = UUID.randomUUID();

        FakeAnalysisJobStatusPort port =
            new FakeAnalysisJobStatusPort(
                new AnalysisJobSnapshot(
                    jobId,
                    "Texto que ya estaba procesándose.",
                    JobStatus.PROCESANDO,
                    Sentiment.NEUTRAL
                )
            );

        StartAnalysisUseCase useCase =
            createUseCase(
                port,
                Sentiment.POSITIVO,
                List.of(
                    "texto",
                    "procesandose"
                )
            );

        StartAnalysisResult result =
            useCase.execute(jobId);

        assertEquals(
            JobStatus.COMPLETADO,
            result.status()
        );

        assertEquals(
            Sentiment.NEUTRAL,
            port.savedSentiment
        );
    }

    @Test
    void rejectsCompletedJob() {
        UUID jobId = UUID.randomUUID();

        FakeAnalysisJobStatusPort port =
            new FakeAnalysisJobStatusPort(
                new AnalysisJobSnapshot(
                    jobId,
                    "Texto terminado.",
                    JobStatus.COMPLETADO,
                    Sentiment.POSITIVO
                )
            );

        StartAnalysisUseCase useCase =
            createUseCase(
                port,
                Sentiment.POSITIVO,
                List.of("texto")
            );

        assertThrows(
            InvalidJobStateException.class,
            () -> useCase.execute(jobId)
        );
    }

    @Test
    void reportsMissingJob() {
        UUID jobId = UUID.randomUUID();

        FakeAnalysisJobStatusPort port =
            new FakeAnalysisJobStatusPort(null);

        StartAnalysisUseCase useCase =
            createUseCase(
                port,
                Sentiment.NEUTRAL,
                List.of()
            );

        assertThrows(
            AnalysisJobNotFoundException.class,
            () -> useCase.execute(jobId)
        );
    }

    private StartAnalysisUseCase createUseCase(
        FakeAnalysisJobStatusPort port,
        Sentiment sentiment,
        List<String> keywords
    ) {
        SentimentAnalyzer sentimentAnalyzer =
            text -> new SentimentAnalysisResult(
                sentiment,
                1,
                List.of("prueba")
            );

        KeywordExtractor keywordExtractor =
            text -> keywords;

        return new StartAnalysisUseCase(
            port,
            sentimentAnalyzer,
            keywordExtractor
        );
    }

    private static class FakeAnalysisJobStatusPort
        implements AnalysisJobStatusPort {

        private final AnalysisJobSnapshot job;
        private JobStatus currentStatus;
        private Sentiment savedSentiment;
        private List<String> savedKeywords =
            List.of();

        FakeAnalysisJobStatusPort(
            AnalysisJobSnapshot job
        ) {
            this.job = job;

            if (job != null) {
                currentStatus = job.status();
                savedSentiment = job.sentiment();
            }
        }

        @Override
        public Optional<AnalysisJobSnapshot> findById(
            UUID jobId
        ) {
            return Optional.ofNullable(job);
        }

        @Override
        public void markAsProcessing(UUID jobId) {
            currentStatus = JobStatus.PROCESANDO;
        }

        @Override
        public void completeAnalysis(
            UUID jobId,
            Sentiment sentiment,
            List<String> keywords
        ) {
            currentStatus = JobStatus.COMPLETADO;
            savedSentiment = sentiment;
            savedKeywords = List.copyOf(keywords);
        }
    }
}