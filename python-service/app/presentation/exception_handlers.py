"""Conversión de excepciones internas en respuestas HTTP."""

from fastapi import FastAPI, Request, status
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from app.domain.exceptions import (
    AnalysisServiceUnavailableError,
    JobNotFoundError,
    PersistenceError,
)


def register_exception_handlers(app: FastAPI) -> None:
    """Registra los errores controlados de la aplicación."""

    @app.exception_handler(RequestValidationError)
    async def validation_exception_handler(
        request: Request,
        exc: RequestValidationError,
    ) -> JSONResponse:
        del request

        text_error = any(
            "text" in error.get("loc", ())
            for error in exc.errors()
        )

        if text_error:
            error_code = "INVALID_TEXT"
            message = "Debe ingresar un texto válido."
        else:
            error_code = "INVALID_REQUEST"
            message = "La solicitud contiene datos inválidos."

        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST,
            content={
                "error": error_code,
                "message": message,
            },
        )

    @app.exception_handler(JobNotFoundError)
    async def job_not_found_handler(
        request: Request,
        exc: JobNotFoundError,
    ) -> JSONResponse:
        del request, exc

        return JSONResponse(
            status_code=status.HTTP_404_NOT_FOUND,
            content={
                "error": "JOB_NOT_FOUND",
                "message": (
                    "No se encontró el trabajo solicitado."
                ),
            },
        )

    @app.exception_handler(
        AnalysisServiceUnavailableError
    )
    async def analysis_service_unavailable_handler(
        request: Request,
        exc: AnalysisServiceUnavailableError,
    ) -> JSONResponse:
        del request, exc

        return JSONResponse(
            status_code=(
                status.HTTP_503_SERVICE_UNAVAILABLE
            ),
            content={
                "error": (
                    "ANALYSIS_SERVICE_UNAVAILABLE"
                ),
                "message": (
                    "El servicio de análisis no está "
                    "disponible."
                ),
            },
        )

    @app.exception_handler(PersistenceError)
    async def persistence_error_handler(
        request: Request,
        exc: PersistenceError,
    ) -> JSONResponse:
        del request, exc

        return JSONResponse(
            status_code=(
                status.HTTP_503_SERVICE_UNAVAILABLE
            ),
            content={
                "error": "DATABASE_UNAVAILABLE",
                "message": (
                    "No fue posible acceder a la base "
                    "de datos."
                ),
            },
        )