"""Entidad de dominio que representa un trabajo de análisis."""

from dataclasses import dataclass, field
from datetime import datetime
from uuid import UUID, uuid4

from app.domain.enums import JobStatus, Sentiment


@dataclass(slots=True)
class AnalysisJob:
    """Trabajo enviado por el usuario para ser analizado."""

    id: UUID
    text_content: str
    status: JobStatus
    sentiment: Sentiment | None = None
    keywords: list[str] = field(default_factory=list)
    error_message: str | None = None
    created_at: datetime | None = None
    updated_at: datetime | None = None

    @classmethod
    def create(cls, text_content: str) -> "AnalysisJob":
        """Crea un trabajo nuevo con estado pendiente."""

        return cls(
            id=uuid4(),
            text_content=text_content.strip(),
            status=JobStatus.PENDING,
        )