"""Configuración externa del servicio Python."""

from functools import lru_cache

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Variables necesarias para ejecutar el servicio."""

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore",
    )

    app_env: str = Field(
        default="development",
        validation_alias="APP_ENV",
    )

    database_url: str = Field(
        validation_alias="DATABASE_URL",
    )

    java_service_url: str = Field(
        default="http://localhost:8080",
        validation_alias="JAVA_SERVICE_URL",
    )

    java_request_timeout_seconds: float = Field(
        default=5.0,
        validation_alias="JAVA_REQUEST_TIMEOUT_SECONDS",
        gt=0,
    )

    allowed_origins: str = Field(
        default="http://localhost:5173",
        validation_alias="ALLOWED_ORIGINS",
    )


@lru_cache
def get_settings() -> Settings:
    """Devuelve una única instancia de la configuración."""

    return Settings()