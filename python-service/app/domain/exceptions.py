"""Excepciones controladas de la aplicación."""


class AnalytiCoreError(Exception):
    """Excepción base del sistema."""

    pass


class JobNotFoundError(AnalytiCoreError):
    """El trabajo solicitado no existe."""

    pass


class AnalysisServiceUnavailableError(AnalytiCoreError):
    """El servicio Java no está disponible."""

    pass


class PersistenceError(AnalytiCoreError):
    """Ocurrió un problema al utilizar PostgreSQL."""

    pass