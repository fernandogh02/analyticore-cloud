"""Endpoint para comprobar PostgreSQL."""

from fastapi import APIRouter, HTTPException, status
from sqlalchemy.exc import SQLAlchemyError

from app.infrastructure.database.session import (
    check_analysis_jobs_table,
    check_database_connection,
)

router = APIRouter(
    prefix="/health",
    tags=["Health"],
)


@router.get(
    "/database",
    summary="Comprobar la conexión con PostgreSQL",
)
def database_health_check() -> dict[str, str]:
    """Comprueba la conexión y la tabla principal."""

    try:
        check_database_connection()
        check_analysis_jobs_table()
    except SQLAlchemyError as exc:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail={
                "status": "DOWN",
                "service": "postgresql",
                "message": "No fue posible conectar con la base de datos.",
            },
        ) from exc

    return {
        "status": "UP",
        "service": "postgresql",
        "database": "analyticore",
        "table": "analysis_jobs",
    }