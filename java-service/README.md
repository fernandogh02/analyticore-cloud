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

## Extracción de palabras clave

El servicio extrae hasta cinco palabras clave por cada texto.

El algoritmo:

1. Normaliza mayúsculas y tildes.
2. Elimina signos de puntuación.
3. Descarta palabras comunes.
4. Ignora palabras cortas y números.
5. Cuenta la frecuencia.
6. Ordena por frecuencia y posición.
7. Guarda las palabras en PostgreSQL como JSONB.

Cuando el sentimiento y las palabras clave se guardan correctamente, el trabajo cambia a `COMPLETADO`.