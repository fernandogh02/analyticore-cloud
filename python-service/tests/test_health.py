"""Pruebas del endpoint de salud."""

from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)


def test_health_check_returns_service_status() -> None:
    """El endpoint debe confirmar que el servicio está activo."""

    response = client.get("/health")

    assert response.status_code == 200
    assert response.json() == {
        "status": "UP",
        "service": "python-service",
    }


def test_root_returns_application_information() -> None:
    """La ruta principal debe mostrar información de AnalytiCore."""

    response = client.get("/")

    assert response.status_code == 200
    assert response.json()["service"] == "python-service"
    assert response.json()["application"] == "AnalytiCore"
    assert response.json()["version"] == "0.3.0"