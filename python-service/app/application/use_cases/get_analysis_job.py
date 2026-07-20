"""Caso de uso para consultar un trabajo."""

from uuid import UUID

from app.domain.entities.analysis_job import AnalysisJob
from app.domain.exceptions import JobNotFoundError
from app.domain.repositories.analysis_job_repository import (
    AnalysisJobRepository,
)


class GetAnalysisJobUseCase:
    """Obtiene el estado y los resultados de un trabajo."""

    def __init__(
        self,
        repository: AnalysisJobRepository,
    ) -> None:
        self._repository = repository

    def execute(self, job_id: UUID) -> AnalysisJob:
        """Busca el trabajo solicitado."""

        job = self._repository.get_by_id(job_id)

        if job is None:
            raise JobNotFoundError(
                "No se encontró el trabajo solicitado."
            )

        return job