"""Pruebas de integración con PostgreSQL."""

import pytest
from fastapi.testclient import TestClient

from app.infrastructure.database.session import (
    check_analysis_jobs_table,
    check_database_connection,
)
from app.main import app

client = TestClient(app)


@pytest.mark.integration
def test_postgresql_connection() -> None:
    """PostgreSQL debe responder a una consulta sencilla."""

    assert check_database_connection() is True


@pytest.mark.integration
def test_analysis_jobs_table_exists() -> None:
    """La tabla principal debe existir y poder consultarse."""

    assert check_analysis_jobs_table() is True


@pytest.mark.integration
def test_database_health_endpoint() -> None:
    """El endpoint debe confirmar el estado de PostgreSQL."""

    response = client.get("/health/database")

    assert response.status_code == 200
    assert response.json() == {
        "status": "UP",
        "service": "postgresql",
        "database": "analyticore",
        "table": "analysis_jobs",
    }