"""Pruebas adicionales de validación de solicitudes."""

from collections.abc import Generator

import pytest
from fastapi.testclient import TestClient

from app.main import app
from app.presentation.dependencies import (
    get_analysis_job_repository,
    get_analysis_service_client,
)
from tests.fakes import (
    FakeAnalysisServiceClient,
    InMemoryAnalysisJobRepository,
)


@pytest.fixture
def client() -> Generator[TestClient, None, None]:
    """Crea un cliente con dependencias simuladas."""

    repository = InMemoryAnalysisJobRepository()
    analysis_service = FakeAnalysisServiceClient()

    app.dependency_overrides[
        get_analysis_job_repository
    ] = lambda: repository

    app.dependency_overrides[
        get_analysis_service_client
    ] = lambda: analysis_service

    try:
        with TestClient(app) as test_client:
            yield test_client
    finally:
        app.dependency_overrides = {}


def test_create_job_rejects_missing_text(
    client: TestClient,
) -> None:
    """La solicitud debe contener el campo text."""

    response = client.post(
        "/api/jobs",
        json={},
    )

    assert response.status_code == 400
    assert response.json() == {
        "error": "INVALID_TEXT",
        "message": "Debe ingresar un texto válido.",
    }


def test_create_job_rejects_text_over_limit(
    client: TestClient,
) -> None:
    """El texto no debe superar los 5000 caracteres."""

    response = client.post(
        "/api/jobs",
        json={
            "text": "a" * 5001,
        },
    )

    assert response.status_code == 400
    assert response.json() == {
        "error": "INVALID_TEXT",
        "message": "Debe ingresar un texto válido.",
    }


def test_create_job_rejects_extra_fields(
    client: TestClient,
) -> None:
    """No se deben aceptar campos desconocidos."""

    response = client.post(
        "/api/jobs",
        json={
            "text": "Texto válido.",
            "unexpected": "dato no permitido",
        },
    )

    assert response.status_code == 400
    assert response.json() == {
        "error": "INVALID_REQUEST",
        "message": (
            "La solicitud contiene datos inválidos."
        ),
    }


def test_get_job_rejects_invalid_uuid(
    client: TestClient,
) -> None:
    """El identificador debe tener formato UUID."""

    response = client.get(
        "/api/jobs/no-es-un-uuid"
    )

    assert response.status_code == 400
    assert response.json() == {
        "error": "INVALID_REQUEST",
        "message": (
            "La solicitud contiene datos inválidos."
        ),
    }