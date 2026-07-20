"""Caso de uso para registrar un trabajo nuevo."""

from app.application.ports.analysis_service import AnalysisServicePort
from app.domain.entities.analysis_job import AnalysisJob
from app.domain.exceptions import AnalysisServiceUnavailableError
from app.domain.repositories.analysis_job_repository import (
    AnalysisJobRepository,
)


class CreateAnalysisJobUseCase:
    """Registra el trabajo y solicita el análisis a Java."""

    def __init__(
        self,
        repository: AnalysisJobRepository,
        analysis_service: AnalysisServicePort,
    ) -> None:
        self._repository = repository
        self._analysis_service = analysis_service

    def execute(self, text_content: str) -> AnalysisJob:
        """Crea el trabajo e inicia su procesamiento."""

        job = AnalysisJob.create(text_content)

        persisted_job = self._repository.create(job)

        try:
            self._analysis_service.start_analysis(
                persisted_job.id
            )
        except AnalysisServiceUnavailableError:
            self._repository.mark_as_error(
                persisted_job.id,
                "No fue posible iniciar el análisis.",
            )
            raise

        return persisted_job