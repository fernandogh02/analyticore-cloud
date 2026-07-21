package com.analyticore.analysis.application.usecase;

import com.analyticore.analysis.application.port.out.AnalysisJobStatusPort;
import com.analyticore.analysis.domain.exception.AnalysisJobNotFoundException;
import com.analyticore.analysis.domain.exception.InvalidJobStateException;
import com.analyticore.analysis.domain.model.AnalysisJobSnapshot;
import com.analyticore.analysis.domain.model.JobStatus;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas unitarias del inicio de análisis.
 */
class StartAnalysisUseCaseTest {

    @Test
    void changesPendingJobToProcessing() {
        UUID jobId = UUID.randomUUID();

        FakeAnalysisJobStatusPort port =
            new FakeAnalysisJobStatusPort(
                new AnalysisJobSnapshot(
                    jobId,
                    JobStatus.PENDIENTE
                )
            );

        StartAnalysisUseCase useCase =
            new StartAnalysisUseCase(port);

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
    }

    @Test
    void acceptsJobAlreadyProcessing() {
        UUID jobId = UUID.randomUUID();

        FakeAnalysisJobStatusPort port =
            new FakeAnalysisJobStatusPort(
                new AnalysisJobSnapshot(
                    jobId,
                    JobStatus.PROCESANDO
                )
            );

        StartAnalysisUseCase useCase =
            new StartAnalysisUseCase(port);

        StartAnalysisResult result =
            useCase.execute(jobId);

        assertEquals(
            JobStatus.PROCESANDO,
            result.status()
        );
    }

    @Test
    void rejectsCompletedJob() {
        UUID jobId = UUID.randomUUID();

        FakeAnalysisJobStatusPort port =
            new FakeAnalysisJobStatusPort(
                new AnalysisJobSnapshot(
                    jobId,
                    JobStatus.COMPLETADO
                )
            );

        StartAnalysisUseCase useCase =
            new StartAnalysisUseCase(port);

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
            new StartAnalysisUseCase(port);

        assertThrows(
            AnalysisJobNotFoundException.class,
            () -> useCase.execute(jobId)
        );
    }

    private static class FakeAnalysisJobStatusPort
        implements AnalysisJobStatusPort {

        private final AnalysisJobSnapshot job;
        private JobStatus currentStatus;

        FakeAnalysisJobStatusPort(
            AnalysisJobSnapshot job
        ) {
            this.job = job;

            if (job != null) {
                this.currentStatus = job.status();
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
            this.currentStatus =
                JobStatus.PROCESANDO;
        }
    }
}