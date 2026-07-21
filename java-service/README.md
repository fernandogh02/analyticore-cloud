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