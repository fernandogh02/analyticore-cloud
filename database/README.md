# Base de datos de AnalytiCore

## Tecnología

PostgreSQL.

## Base de datos

`analyticore`

## Usuario local recomendado

`analyticore_user`

## Tabla principal

`analysis_jobs`

## Archivos

- `init.sql`: crea la tabla, restricciones, índices y trigger.
- `test-data.sql`: prueba el flujo de estados y los resultados.

## Estados permitidos

- `PENDIENTE`
- `PROCESANDO`
- `COMPLETADO`
- `ERROR`

## Sentimientos permitidos

- `POSITIVO`
- `NEGATIVO`
- `NEUTRAL`

## Ejecutar el script principal

```powershell
psql -h localhost -p 5432 -U analyticore_user -d analyticore -f database\init.sql