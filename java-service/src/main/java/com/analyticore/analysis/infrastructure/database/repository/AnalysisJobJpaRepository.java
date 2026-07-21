package com.analyticore.analysis.infrastructure.database.repository;

import com.analyticore.analysis.infrastructure.database.entity.AnalysisJobJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repositorio técnico para la tabla analysis_jobs.
 */
public interface AnalysisJobJpaRepository
    extends JpaRepository<AnalysisJobJpaEntity, UUID> {
}