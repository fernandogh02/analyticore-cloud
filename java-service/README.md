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

## Análisis de sentimiento

El servicio incorpora un analizador de sentimiento basado en reglas para textos en español.

Clasificaciones posibles:

- `POSITIVO`
- `NEGATIVO`
- `NEUTRAL`

El algoritmo:

1. Normaliza el texto.
2. Elimina tildes y signos.
3. Identifica palabras positivas y negativas.
4. Interpreta negaciones.
5. Aplica intensificadores.
6. Calcula una puntuación.
7. Guarda el sentimiento en PostgreSQL.

En esta etapa, el trabajo permanece en estado `PROCESANDO`, ya que la extracción de palabras clave se implementa posteriormente.