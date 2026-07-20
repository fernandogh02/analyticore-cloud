"""Implementaciones simuladas para las pruebas."""

from uuid import UUID

from app.domain.entities.analysis_job import AnalysisJob
from app.domain.enums import JobStatus
from app.domain.exceptions import (
    AnalysisServiceUnavailableError,
    JobNotFoundError,
)


class InMemoryAnalysisJobRepository:
    """Repositorio temporal almacenado en un diccionario."""

    def __init__(self) -> None:
        self.jobs: dict[UUID, AnalysisJob] = {}

    def create(self, job: AnalysisJob) -> AnalysisJob:
        self.jobs[job.id] = job
        return job

    def get_by_id(
        self,
        job_id: UUID,
    ) -> AnalysisJob | None:
        return self.jobs.get(job_id)

    def mark_as_error(
        self,
        job_id: UUID,
        error_message: str,
    ) -> AnalysisJob:
        job = self.jobs.get(job_id)

        if job is None:
            raise JobNotFoundError(
                "No se encontró el trabajo solicitado."
            )

        job.status = JobStatus.ERROR
        job.error_message = error_message
        job.sentiment = None
        job.keywords = []

        return job


class FakeAnalysisServiceClient:
    """Cliente Java simulado que acepta solicitudes."""

    def __init__(self) -> None:
        self.received_job_ids: list[UUID] = []

    def start_analysis(self, job_id: UUID) -> None:
        self.received_job_ids.append(job_id)


class FailingAnalysisServiceClient:
    """Cliente Java simulado que siempre falla."""

    def start_analysis(self, job_id: UUID) -> None:
        del job_id

        raise AnalysisServiceUnavailableError(
            "El servicio de análisis no está disponible."
        )