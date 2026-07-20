"""Contrato de persistencia de los trabajos."""

from typing import Protocol
from uuid import UUID

from app.domain.entities.analysis_job import AnalysisJob


class AnalysisJobRepository(Protocol):
    """Operaciones que debe ofrecer cualquier repositorio de trabajos."""

    def create(self, job: AnalysisJob) -> AnalysisJob:
        """Guarda un trabajo nuevo."""

        ...

    def get_by_id(self, job_id: UUID) -> AnalysisJob | None:
        """Busca un trabajo por su identificador."""

        ...

    def mark_as_error(
        self,
        job_id: UUID,
        error_message: str,
    ) -> AnalysisJob:
        """Marca un trabajo como error."""

        ...