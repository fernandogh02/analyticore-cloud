"""Prueba integral de la API con PostgreSQL real."""

from collections.abc import Generator
from uuid import UUID

import pytest
from fastapi.testclient import TestClient

from app.infrastructure.database.models.analysis_job_model import (
    AnalysisJobModel,
)
from app.infrastructure.database.session import SessionLocal
from app.main import app
from app.presentation.dependencies import (
    get_analysis_service_client,
)
from tests.fakes import FakeAnalysisServiceClient


@pytest.fixture
def database_client() -> Generator[TestClient, None, None]:
    """Utiliza PostgreSQL real y simula únicamente Java."""

    analysis_service = FakeAnalysisServiceClient()

    app.dependency_overrides[
        get_analysis_service_client
    ] = lambda: analysis_service

    try:
        with TestClient(app) as test_client:
            yield test_client
    finally:
        app.dependency_overrides = {}


@pytest.mark.integration
def test_api_creates_and_reads_job_in_postgresql(
    database_client: TestClient,
) -> None:
    """La API debe persistir y recuperar el trabajo."""

    job_id: UUID | None = None

    try:
        create_response = database_client.post(
            "/api/jobs",
            json={
                "text": (
                    "Prueba integral del servicio Python."
                ),
            },
        )

        assert create_response.status_code == 202

        create_body = create_response.json()
        job_id = UUID(create_body["jobId"])

        assert create_body["status"] == "PENDIENTE"

        get_response = database_client.get(
            f"/api/jobs/{job_id}"
        )

        assert get_response.status_code == 200

        get_body = get_response.json()

        assert get_body["jobId"] == str(job_id)
        assert get_body["text"] == (
            "Prueba integral del servicio Python."
        )
        assert get_body["status"] == "PENDIENTE"
        assert get_body["sentiment"] is None
        assert get_body["keywords"] == []
        assert get_body["errorMessage"] is None

        with SessionLocal() as session:
            model = session.get(
                AnalysisJobModel,
                job_id,
            )

            assert model is not None
            assert model.status == "PENDIENTE"
    finally:
        if job_id is not None:
            with SessionLocal() as session:
                model = session.get(
                    AnalysisJobModel,
                    job_id,
                )

                if model is not None:
                    session.delete(model)
                    session.commit()