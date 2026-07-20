"""Implementación PostgreSQL del repositorio de trabajos."""

from uuid import UUID

from sqlalchemy.exc import SQLAlchemyError
from sqlalchemy.orm import Session

from app.domain.entities.analysis_job import AnalysisJob
from app.domain.enums import JobStatus, Sentiment
from app.domain.exceptions import JobNotFoundError, PersistenceError
from app.infrastructure.database.models.analysis_job_model import (
    AnalysisJobModel,
)


class SqlAlchemyAnalysisJobRepository:
    """Repositorio implementado con SQLAlchemy."""

    def __init__(self, session: Session) -> None:
        self._session = session

    def create(self, job: AnalysisJob) -> AnalysisJob:
        """Inserta un trabajo nuevo en PostgreSQL."""

        model = AnalysisJobModel(
            id=job.id,
            text_content=job.text_content,
            status=job.status.value,
            sentiment=(
                job.sentiment.value
                if job.sentiment is not None
                else None
            ),
            keywords=list(job.keywords),
            error_message=job.error_message,
        )

        try:
            self._session.add(model)
            self._session.commit()
            self._session.refresh(model)
        except SQLAlchemyError as exc:
            self._session.rollback()
            raise PersistenceError(
                "No fue posible guardar el trabajo."
            ) from exc

        return self._to_domain(model)

    def get_by_id(self, job_id: UUID) -> AnalysisJob | None:
        """Busca un trabajo utilizando su llave primaria."""

        try:
            model = self._session.get(
                AnalysisJobModel,
                job_id,
            )
        except SQLAlchemyError as exc:
            self._session.rollback()
            raise PersistenceError(
                "No fue posible consultar el trabajo."
            ) from exc

        if model is None:
            return None

        return self._to_domain(model)

    def mark_as_error(
        self,
        job_id: UUID,
        error_message: str,
    ) -> AnalysisJob:
        """Actualiza el trabajo cuando Java no puede iniciarlo."""

        try:
            model = self._session.get(
                AnalysisJobModel,
                job_id,
            )

            if model is None:
                raise JobNotFoundError(
                    "No se encontró el trabajo solicitado."
                )

            model.status = JobStatus.ERROR.value
            model.sentiment = None
            model.keywords = []
            model.error_message = error_message

            self._session.commit()
            self._session.refresh(model)
        except JobNotFoundError:
            self._session.rollback()
            raise
        except SQLAlchemyError as exc:
            self._session.rollback()
            raise PersistenceError(
                "No fue posible actualizar el trabajo."
            ) from exc

        return self._to_domain(model)

    @staticmethod
    def _to_domain(model: AnalysisJobModel) -> AnalysisJob:
        """Convierte el modelo SQLAlchemy en entidad de dominio."""

        return AnalysisJob(
            id=model.id,
            text_content=model.text_content,
            status=JobStatus(model.status),
            sentiment=(
                Sentiment(model.sentiment)
                if model.sentiment is not None
                else None
            ),
            keywords=list(model.keywords or []),
            error_message=model.error_message,
            created_at=model.created_at,
            updated_at=model.updated_at,
        )