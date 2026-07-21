package com.analyticore.analysis.infrastructure.database.adapter;

import com.analyticore.analysis.application.port.out.AnalysisJobStatusPort;
import com.analyticore.analysis.domain.exception.AnalysisJobNotFoundException;
import com.analyticore.analysis.domain.model.AnalysisJobSnapshot;
import com.analyticore.analysis.domain.model.Sentiment;
import com.analyticore.analysis.infrastructure.database.entity.AnalysisJobJpaEntity;
import com.analyticore.analysis.infrastructure.database.repository.AnalysisJobJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementación JPA del acceso a trabajos.
 */
@Component
public class AnalysisJobStatusJpaAdapter
    implements AnalysisJobStatusPort {

    private final AnalysisJobJpaRepository repository;

    public AnalysisJobStatusJpaAdapter(
        AnalysisJobJpaRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public Optional<AnalysisJobSnapshot> findById(
        UUID jobId
    ) {
        return repository
            .findById(jobId)
            .map(entity -> new AnalysisJobSnapshot(
                entity.getId(),
                entity.getTextContent(),
                entity.getStatus(),
                entity.getSentiment()
            ));
    }

    @Override
    public void markAsProcessing(UUID jobId) {
        AnalysisJobJpaEntity entity =
            findEntity(jobId);

        entity.startProcessing();

        repository.saveAndFlush(entity);
    }

    @Override
    public void saveSentiment(
        UUID jobId,
        Sentiment sentiment
    ) {
        AnalysisJobJpaEntity entity =
            findEntity(jobId);

        entity.applySentiment(sentiment);

        repository.saveAndFlush(entity);
    }

    private AnalysisJobJpaEntity findEntity(
        UUID jobId
    ) {
        return repository
            .findById(jobId)
            .orElseThrow(
                () -> new AnalysisJobNotFoundException(
                    jobId
                )
            );
    }
}