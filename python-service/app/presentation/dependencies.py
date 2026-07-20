"""Dependencias utilizadas por los controladores HTTP."""

from typing import Annotated

from fastapi import Depends
from sqlalchemy.orm import Session

from app.application.ports.analysis_service import AnalysisServicePort
from app.domain.repositories.analysis_job_repository import (
    AnalysisJobRepository,
)
from app.infrastructure.clients.java_analysis_client import (
    JavaAnalysisClient,
)
from app.infrastructure.config.settings import get_settings
from app.infrastructure.database.session import (
    get_database_session,
)
from app.infrastructure.repositories.sqlalchemy_analysis_job_repository import (
    SqlAlchemyAnalysisJobRepository,
)


def get_analysis_job_repository(
    session: Annotated[
        Session,
        Depends(get_database_session),
    ],
) -> AnalysisJobRepository:
    """Construye el repositorio de PostgreSQL."""

    return SqlAlchemyAnalysisJobRepository(session)


def get_analysis_service_client() -> AnalysisServicePort:
    """Construye el cliente REST del servicio Java."""

    settings = get_settings()

    return JavaAnalysisClient(
        base_url=settings.java_service_url,
        timeout_seconds=(
            settings.java_request_timeout_seconds
        ),
    )