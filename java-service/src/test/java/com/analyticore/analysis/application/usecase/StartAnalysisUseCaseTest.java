package com.analyticore.analysis.application.usecase;

import com.analyticore.analysis.application.port.out.AnalysisJobStatusPort;
import com.analyticore.analysis.domain.exception.AnalysisJobNotFoundException;
import com.analyticore.analysis.domain.exception.InvalidJobStateException;
import com.analyticore.analysis.domain.model.AnalysisJobSnapshot;
import com.analyticore.analysis.domain.model.JobStatus;
import com.analyticore.analysis.domain.model.Sentiment;
import com.analyticore.analysis.domain.model.SentimentAnalysisResult;
import com.analyticore.analysis.domain.service.SentimentAnalyzer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas del inicio y análisis de sentimiento.
 */
class StartAnalysisUseCaseTest {

    @Test
    void changesPendingJobAndSavesSentiment() {
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
            new StartAnalysisUseCase(
                port,
                new FakeSentimentAnalyzer(
                    Sentiment.POSITIVO
                )
            );

        StartAnalysisResult result =
            useCase.execute(jobId);

        assertEquals(
            JobStatus.PROCESANDO,
            result.status()
        );

        assertEquals(
            JobStatus.PROCESANDO,
            port.currentStatus
        );

        assertEquals(
            Sentiment.POSITIVO,
            port.savedSentiment
        );
    }

    @Test
    void doesNotRecalculateExistingSentiment() {
        UUID jobId = UUID.randomUUID();

        FakeAnalysisJobStatusPort port =
            new FakeAnalysisJobStatusPort(
                new AnalysisJobSnapshot(
                    jobId,
                    "Texto previamente analizado.",
                    JobStatus.PROCESANDO,
                    Sentiment.NEUTRAL
                )
            );

        StartAnalysisUseCase useCase =
            new StartAnalysisUseCase(
                port,
                new FakeSentimentAnalyzer(
                    Sentiment.POSITIVO
                )
            );

        StartAnalysisResult result =
            useCase.execute(jobId);

        assertEquals(
            JobStatus.PROCESANDO,
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
            new StartAnalysisUseCase(
                port,
                new FakeSentimentAnalyzer(
                    Sentiment.POSITIVO
                )
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
            new StartAnalysisUseCase(
                port,
                new FakeSentimentAnalyzer(
                    Sentiment.NEUTRAL
                )
            );

        assertThrows(
            AnalysisJobNotFoundException.class,
            () -> useCase.execute(jobId)
        );
    }

    private static class FakeAnalysisJobStatusPort
        implements AnalysisJobStatusPort {

        private final AnalysisJobSnapshot job;
        private JobStatus currentStatus;
        private Sentiment savedSentiment;

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
        public void saveSentiment(
            UUID jobId,
            Sentiment sentiment
        ) {
            savedSentiment = sentiment;
        }
    }

    private record FakeSentimentAnalyzer(
        Sentiment result
    ) implements SentimentAnalyzer {

        @Override
        public SentimentAnalysisResult analyze(
            String text
        ) {
            return new SentimentAnalysisResult(
                result,
                1,
                List.of("prueba")
            );
        }
    }
}