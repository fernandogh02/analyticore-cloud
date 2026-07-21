package com.analyticore.analysis.infrastructure.database.entity;

import com.analyticore.analysis.domain.model.JobStatus;
import com.analyticore.analysis.domain.model.Sentiment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representación JPA de la tabla analysis_jobs.
 */
@Entity
@Table(name = "analysis_jobs")
public class AnalysisJobJpaEntity {

    @Id
    private UUID id;

    @Column(
        name = "text_content",
        nullable = false,
        columnDefinition = "text"
    )
    private String textContent;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 20
    )
    private JobStatus status;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "sentiment",
        length = 20
    )
    private Sentiment sentiment;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
        name = "keywords",
        nullable = false,
        columnDefinition = "jsonb"
    )
    private List<String> keywords = new ArrayList<>();

    @Column(
        name = "error_message",
        columnDefinition = "text"
    )
    private String errorMessage;

    @Column(
        name = "created_at",
        nullable = false,
        insertable = false,
        updatable = false
    )
    private OffsetDateTime createdAt;

    @Column(
        name = "updated_at",
        nullable = false,
        insertable = false,
        updatable = false
    )
    private OffsetDateTime updatedAt;

    /**
     * Constructor requerido por JPA.
     */
    protected AnalysisJobJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public String getTextContent() {
        return textContent;
    }

    public JobStatus getStatus() {
        return status;
    }

    public Sentiment getSentiment() {
        return sentiment;
    }

    public List<String> getKeywords() {
        return List.copyOf(keywords);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}