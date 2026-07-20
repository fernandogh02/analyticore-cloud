"""Endpoints públicos de los trabajos de análisis."""

from typing import Annotated
from uuid import UUID

from fastapi import APIRouter, Depends, status

from app.application.ports.analysis_service import (
    AnalysisServicePort,
)
from app.application.use_cases.create_analysis_job import (
    CreateAnalysisJobUseCase,
)
from app.application.use_cases.get_analysis_job import (
    GetAnalysisJobUseCase,
)
from app.domain.repositories.analysis_job_repository import (
    AnalysisJobRepository,
)
from app.presentation.dependencies import (
    get_analysis_job_repository,
    get_analysis_service_client,
)
from app.presentation.schemas.analysis_job_schemas import (
    AnalysisJobResponse,
    CreateJobRequest,
    CreateJobResponse,
    ErrorResponse,
)

router = APIRouter(
    prefix="/api/jobs",
    tags=["Analysis Jobs"],
)


@router.post(
    "",
    response_model=CreateJobResponse,
    status_code=status.HTTP_202_ACCEPTED,
    summary="Registrar un trabajo de análisis",
    responses={
        400: {"model": ErrorResponse},
        503: {"model": ErrorResponse},
    },
)
def create_analysis_job(
    request: CreateJobRequest,
    repository: Annotated[
        AnalysisJobRepository,
        Depends(get_analysis_job_repository),
    ],
    analysis_service: Annotated[
        AnalysisServicePort,
        Depends(get_analysis_service_client),
    ],
) -> CreateJobResponse:
    """Registra el texto y solicita su análisis."""

    use_case = CreateAnalysisJobUseCase(
        repository=repository,
        analysis_service=analysis_service,
    )

    job = use_case.execute(request.text)

    return CreateJobResponse(
        jobId=job.id,
        status=job.status,
        message=(
            "El análisis fue registrado correctamente."
        ),
    )


@router.get(
    "/{job_id}",
    response_model=AnalysisJobResponse,
    summary="Consultar un trabajo de análisis",
    responses={
        400: {"model": ErrorResponse},
        404: {"model": ErrorResponse},
        503: {"model": ErrorResponse},
    },
)
def get_analysis_job(
    job_id: UUID,
    repository: Annotated[
        AnalysisJobRepository,
        Depends(get_analysis_job_repository),
    ],
) -> AnalysisJobResponse:
    """Devuelve el estado y resultado del trabajo."""

    use_case = GetAnalysisJobUseCase(
        repository=repository,
    )

    job = use_case.execute(job_id)

    return AnalysisJobResponse(
        jobId=job.id,
        text=job.text_content,
        status=job.status,
        sentiment=job.sentiment,
        keywords=list(job.keywords),
        errorMessage=job.error_message,
    )