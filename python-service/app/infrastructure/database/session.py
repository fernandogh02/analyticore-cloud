"""Motor, sesiones y comprobaciones de PostgreSQL."""

from collections.abc import Generator

from sqlalchemy import create_engine, select, text
from sqlalchemy.orm import Session, sessionmaker

from app.infrastructure.config.settings import get_settings
from app.infrastructure.database.models.analysis_job_model import (
    AnalysisJobModel,
)

settings = get_settings()

engine = create_engine(
    settings.database_url,
    pool_pre_ping=True,
)

SessionLocal = sessionmaker(
    bind=engine,
    autoflush=False,
    expire_on_commit=False,
)


def get_database_session() -> Generator[Session, None, None]:
    """Abre una sesión y garantiza que se cierre al terminar."""

    with SessionLocal() as session:
        yield session


def check_database_connection() -> bool:
    """Comprueba que PostgreSQL responda correctamente."""

    with engine.connect() as connection:
        result = connection.execute(text("SELECT 1"))
        return result.scalar_one() == 1


def check_analysis_jobs_table() -> bool:
    """Comprueba que la tabla analysis_jobs pueda consultarse."""

    with SessionLocal() as session:
        statement = select(AnalysisJobModel.id).limit(1)
        session.execute(statement)

    return True