"""Punto de entrada del servicio de submisión de AnalytiCore."""

from fastapi import FastAPI

from app.presentation.routers.database_health import (
    router as database_health_router,
)
from app.presentation.routers.health import router as health_router

app = FastAPI(
    title="AnalytiCore Python Service",
    description=(
        "Servicio responsable de recibir solicitudes, "
        "registrar trabajos y consultar sus resultados."
    ),
    version="0.2.0",
)

app.include_router(health_router)
app.include_router(database_health_router)


@app.get(
    "/",
    tags=["Root"],
    summary="Mostrar información básica del servicio",
)
def read_root() -> dict[str, str]:
    """Devuelve información general de la API."""

    return {
        "service": "python-service",
        "application": "AnalytiCore",
        "version": "0.2.0",
        "message": "Servicio Python funcionando correctamente.",
    }