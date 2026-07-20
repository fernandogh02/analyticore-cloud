"""Pruebas del repositorio PostgreSQL de trabajos."""

from uuid import uuid4

import pytest

from app.domain.entities.analysis_job import AnalysisJob
from app.domain.enums import JobStatus
from app.infrastructure.database.models.analysis_job_model import (
    AnalysisJobModel,
)
from app.infrastructure.database.session import SessionLocal
from app.infrastructure.repositories.sqlalchemy_analysis_job_repository import (
    SqlAlchemyAnalysisJobRepository,
)


@pytest.mark.integration
def test_repository_creates_and_reads_job() -> None:
    """El repositorio debe guardar y recuperar el trabajo."""

    job = AnalysisJob.create(
        "Texto de prueba para PostgreSQL."
    )

    with SessionLocal() as session:
        repository = (
            SqlAlchemyAnalysisJobRepository(session)
        )

        try:
            created_job = repository.create(job)
            recovered_job = repository.get_by_id(
                created_job.id
            )

            assert recovered_job is not None
            assert recovered_job.id == created_job.id
            assert recovered_job.status == (
                JobStatus.PENDING
            )
            assert recovered_job.text_content == (
                "Texto de prueba para PostgreSQL."
            )
        finally:
            model = session.get(
                AnalysisJobModel,
                job.id,
            )

            if model is not None:
                session.delete(model)
                session.commit()


@pytest.mark.integration
def test_repository_marks_job_as_error() -> None:
    """El repositorio debe registrar errores."""

    job = AnalysisJob(
        id=uuid4(),
        text_content="Texto que producirá un error.",
        status=JobStatus.PENDING,
    )

    with SessionLocal() as session:
        repository = (
            SqlAlchemyAnalysisJobRepository(session)
        )

        try:
            repository.create(job)

            updated_job = repository.mark_as_error(
                job.id,
                "Servicio Java no disponible.",
            )

            assert updated_job.status == JobStatus.ERROR
            assert updated_job.error_message == (
                "Servicio Java no disponible."
            )
        finally:
            model = session.get(
                AnalysisJobModel,
                job.id,
            )

            if model is not None:
                session.delete(model)
                session.commit()