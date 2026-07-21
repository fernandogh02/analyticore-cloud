# Servicio Java de AnalytiCore

Servicio encargado de realizar el análisis de sentimiento y la extracción de palabras clave.

## Tecnologías

- Java 21.
- Spring Boot.
- Maven.
- API REST.

## Responsabilidades previstas

- Recibir un `jobId` desde Python.
- Consultar el trabajo en PostgreSQL.
- Cambiar el estado a `PROCESANDO`.
- Analizar el sentimiento.
- Extraer palabras clave.
- Guardar los resultados.
- Cambiar el estado a `COMPLETADO` o `ERROR`.

## Compilar

```powershell
mvn clean compile

## PostgreSQL

El servicio utiliza la misma tabla `analysis_jobs` compartida con el servicio Python.

Variables requeridas:

- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `DATABASE_POOL_SIZE`

Ejemplo local:

```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/analyticore"
$env:DATABASE_USERNAME="analyticore_user"
$env:DATABASE_PASSWORD="TU_CLAVE_LOCAL"