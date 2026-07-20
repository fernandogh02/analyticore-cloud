"""Pruebas de los endpoints públicos de trabajos."""

from collections.abc import Generator
from uuid import UUID, uuid4

import pytest
from fastapi.testclient import TestClient

from app.main import app
from app.presentation.dependencies import (
    get_analysis_job_repository,
    get_analysis_service_client,
)
from tests.fakes import (
    FakeAnalysisServiceClient,
    FailingAnalysisServiceClient,
    InMemoryAnalysisJobRepository,
)


@pytest.fixture
def repository() -> InMemoryAnalysisJobRepository:
    """Crea un repositorio nuevo para cada prueba."""

    return InMemoryAnalysisJobRepository()


@pytest.fixture
def analysis_client() -> FakeAnalysisServiceClient:
    """Crea un cliente Java simulado."""

    return FakeAnalysisServiceClient()


@pytest.fixture
def client(
    repository: InMemoryAnalysisJobRepository,
    analysis_client: FakeAnalysisServiceClient,
) -> Generator[TestClient, None, None]:
    """Sustituye PostgreSQL y Java durante la prueba."""

    app.dependency_overrides[
        get_analysis_job_repository
    ] = lambda: repository

    app.dependency_overrides[
        get_analysis_service_client
    ] = lambda: analysis_client

    with TestClient(app) as test_client:
        yield test_client

    app.dependency_overrides = {}


def test_create_job_returns_202_and_job_id(
    client: TestClient,
    analysis_client: FakeAnalysisServiceClient,
) -> None:
    """Un texto válido debe crear un trabajo."""

    response = client.post(
        "/api/jobs",
        json={
            "text": (
                "La plataforma funciona muy bien."
            ),
        },
    )

    assert response.status_code == 202

    body = response.json()

    assert UUID(body["jobId"])
    assert body["status"] == "PENDIENTE"
    assert body["message"] == (
        "El análisis fue registrado correctamente."
    )
    assert len(
        analysis_client.received_job_ids
    ) == 1


def test_create_job_rejects_empty_text(
    client: TestClient,
) -> None:
    """Un texto formado por espacios debe rechazarse."""

    response = client.post(
        "/api/jobs",
        json={
            "text": "   ",
        },
    )

    assert response.status_code == 400
    assert response.json() == {
        "error": "INVALID_TEXT",
        "message": "Debe ingresar un texto válido.",
    }


def test_created_job_can_be_consulted(
    client: TestClient,
) -> None:
    """El trabajo creado debe poder consultarse."""

    create_response = client.post(
        "/api/jobs",
        json={
            "text": "El sistema es fácil de utilizar.",
        },
    )

    job_id = create_response.json()["jobId"]

    get_response = client.get(
        f"/api/jobs/{job_id}"
    )

    assert get_response.status_code == 200

    body = get_response.json()

    assert body["jobId"] == job_id
    assert body["text"] == (
        "El sistema es fácil de utilizar."
    )
    assert body["status"] == "PENDIENTE"
    assert body["sentiment"] is None
    assert body["keywords"] == []
    assert body["errorMessage"] is None


def test_unknown_job_returns_404(
    client: TestClient,
) -> None:
    """Un identificador inexistente debe devolver 404."""

    response = client.get(
        f"/api/jobs/{uuid4()}"
    )

    assert response.status_code == 404
    assert response.json() == {
        "error": "JOB_NOT_FOUND",
        "message": (
            "No se encontró el trabajo solicitado."
        ),
    }


def test_unavailable_java_returns_503_and_marks_error(
    repository: InMemoryAnalysisJobRepository,
) -> None:
    """Una falla de Java debe producir un error controlado."""

    app.dependency_overrides[
        get_analysis_job_repository
    ] = lambda: repository

    app.dependency_overrides[
        get_analysis_service_client
    ] = lambda: FailingAnalysisServiceClient()

    try:
        with TestClient(app) as test_client:
            response = test_client.post(
                "/api/jobs",
                json={
                    "text": (
                        "Texto cuyo servicio no responde."
                    ),
                },
            )
    finally:
        app.dependency_overrides = {}

    assert response.status_code == 503
    assert response.json() == {
        "error": (
            "ANALYSIS_SERVICE_UNAVAILABLE"
        ),
        "message": (
            "El servicio de análisis no está disponible."
        ),
    }

    saved_jobs = list(repository.jobs.values())

    assert len(saved_jobs) == 1
    assert saved_jobs[0].status.value == "ERROR"