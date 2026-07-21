"""Esquemas HTTP de los trabajos de análisis."""

import re
from uuid import UUID

from pydantic import (
    BaseModel,
    ConfigDict,
    Field,
    field_validator,
)

from app.domain.enums import JobStatus, Sentiment


class CreateJobRequest(BaseModel):
    """Texto enviado por el usuario."""

    model_config = ConfigDict(
        extra="forbid",
    )

    text: str = Field(
        min_length=10,
        max_length=2000,
        description="Texto que se desea analizar",
        examples=[
            "La plataforma funciona muy bien."
        ],
    )

    @field_validator(
        "text",
        mode="before",
    )
    @classmethod
    def validate_text(
        cls,
        value: object,
    ) -> object:
        """Normaliza y valida el comentario recibido."""

        if not isinstance(value, str):
            return value

        normalized_text = " ".join(
            value.split()
        )

        if not normalized_text:
            raise ValueError(
                "Debe ingresar un texto válido."
            )

        if len(normalized_text) < 10:
            raise ValueError(
                "El texto debe contener al menos "
                "10 caracteres."
            )

        if len(normalized_text) > 2000:
            raise ValueError(
                "El texto no puede superar "
                "2000 caracteres."
            )

        meaningful_characters = re.findall(
            r"[^\W_]",
            normalized_text,
            flags=re.UNICODE,
        )

        if len(meaningful_characters) < 3:
            raise ValueError(
                "El texto debe contener al menos "
                "tres letras o números."
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