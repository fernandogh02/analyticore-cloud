"""Valores permitidos dentro del dominio de AnalytiCore."""

from enum import Enum


class JobStatus(str, Enum):
    """Estados posibles de un trabajo de análisis."""

    PENDING = "PENDIENTE"
    PROCESSING = "PROCESANDO"
    COMPLETED = "COMPLETADO"
    ERROR = "ERROR"


class Sentiment(str, Enum):
    """Resultados posibles del análisis de sentimiento."""

    POSITIVE = "POSITIVO"
    NEGATIVE = "NEGATIVO"
    NEUTRAL = "NEUTRAL"