# AnalytiCore Cloud

Prototipo de arquitectura orientada a servicios desplegada en la nube.

## Descripción

AnalytiCore permite enviar textos para realizar un análisis simple de sentimiento y extraer palabras clave.

## Componentes

- Frontend web desarrollado con React.
- Servicio de recepción y coordinación desarrollado con Python.
- Servicio de análisis desarrollado con Java.
- Base de datos PostgreSQL.
- Contenedores Docker.
- Despliegue en Render.

## Estructura

- `frontend`: interfaz web del sistema.
- `python-service`: servicio de recepción y consulta de trabajos.
- `java-service`: servicio encargado del análisis.
- `docs`: diagramas, informe ejecutivo y evidencias.

## Estado

Proyecto en desarrollo.

## Documentación

- [Arquitectura general](docs/architecture-overview.md)
- [Contratos de las APIs](docs/api-contracts.md)
- [Modelo de datos](docs/data-model.md)
- [Base de datos](database/README.md)

## Ejecución local integrada

AnalytiCore requiere PostgreSQL y dos servicios de backend.

### 1. Servicio Java

Desde `java-service`:

```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/analyticore"
$env:DATABASE_USERNAME="analyticore_user"
$env:DATABASE_PASSWORD="TU_CLAVE_LOCAL"
mvn spring-boot:run