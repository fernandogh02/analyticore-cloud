"""Cliente REST utilizado para comunicarse con Java."""

from uuid import UUID

import httpx

from app.domain.exceptions import AnalysisServiceUnavailableError


class JavaAnalysisClient:
    """Solicita al servicio Java que procese un trabajo."""

    def __init__(
        self,
        base_url: str,
        timeout_seconds: float,
    ) -> None:
        self._base_url = base_url.rstrip("/")
        self._timeout_seconds = timeout_seconds

    def start_analysis(self, job_id: UUID) -> None:
        """Llama al endpoint interno del servicio Java."""

        url = f"{self._base_url}/internal/analysis"

        try:
            with httpx.Client(
                timeout=self._timeout_seconds,
            ) as client:
                response = client.post(
                    url,
                    json={
                        "jobId": str(job_id),
                    },
                )
                response.raise_for_status()
        except httpx.HTTPError as exc:
            raise AnalysisServiceUnavailableError(
                "El servicio de análisis no está disponible."
            ) from exc