"""Esquemas HTTP de los trabajos de análisis."""

from uuid import UUID

from pydantic import BaseModel, ConfigDict, Field, field_validator

from app.domain.enums import JobStatus, Sentiment


class CreateJobRequest(BaseModel):
    """Texto enviado por el usuario."""

    model_config = ConfigDict(
        extra="forbid",
    )

    text: str = Field(
        min_length=1,
        max_length=5000,
        description="Texto que se desea analizar",
        examples=[
            "La plataforma funciona muy bien."
        ],
    )

    @field_validator("text")
    @classmethod
    def validate_text(cls, value: str) -> str:
        """Rechaza textos formados solamente por espacios."""

        normalized_text = value.strip()

        if not normalized_text:
            raise ValueError(
                "Debe ingresar un texto válido."
            )

        return normalized_text


class CreateJobResponse(BaseModel):
    """Respuesta al registrar un análisis."""

    jobId: UUID
    status: JobStatus
    message: str


class AnalysisJobResponse(BaseModel):
    """Estado y resultado de un trabajo."""

    jobId: UUID
    text: str
    status: JobStatus
    sentiment: Sentiment | None
    keywords: list[str]
    errorMessage: str | None


class ErrorResponse(BaseModel):
    """Formato estándar de los errores."""

    error: str
    message: str