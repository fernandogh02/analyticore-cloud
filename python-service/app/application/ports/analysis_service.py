"""Contrato utilizado para solicitar el análisis a Java."""

from typing import Protocol
from uuid import UUID


class AnalysisServicePort(Protocol):
    """Operación requerida del servicio externo de análisis."""

    def start_analysis(self, job_id: UUID) -> None:
        """Solicita el procesamiento de un trabajo."""

        ...