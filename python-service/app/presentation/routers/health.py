"""Endpoint para comprobar el estado del servicio Python."""

from typing import Final

from fastapi import APIRouter

router = APIRouter(tags=["Health"])

SERVICE_NAME: Final[str] = "python-service"


@router.get(
    "/health",
    summary="Comprobar el estado del servicio",
    response_description="Estado actual del servicio Python",
)
def health_check() -> dict[str, str]:
    """Devuelve un estado sencillo para comprobar que la API está activa."""

    return {
        "status": "UP",
        "service": SERVICE_NAME,
    }