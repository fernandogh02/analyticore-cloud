"""Punto de entrada del servicio de submisión de AnalytiCore."""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.presentation.exception_handlers import (
    register_exception_handlers,
)
from app.presentation.routers.database_health import (
    router as database_health_router,
)
from app.presentation.routers.health import (
    router as health_router,
)
from app.presentation.routers.jobs import (
    router as jobs_router,
)

app = FastAPI(
    title="AnalytiCore Python Service",
    description=(
        "Servicio responsable de recibir solicitudes, "
        "registrar trabajos y consultar sus resultados."
    ),
    version="0.3.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:5173",
        "http://127.0.0.1:5173",
    ],
    allow_credentials=False,
    allow_methods=[
        "GET",
        "POST",
        "OPTIONS",
    ],
    allow_headers=[
        "Content-Type",
    ],
)

register_exception_handlers(app)

app.include_router(health_router)
app.include_router(database_health_router)
app.include_router(jobs_router)


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
        "version": "0.3.0",
        "message": (
            "Servicio Python funcionando correctamente."
        ),
    }