# Servicio Python de AnalytiCore

Servicio de submisión desarrollado con FastAPI.

## Responsabilidades

- Recibir las solicitudes del frontend.
- Validar los textos.
- Crear los trabajos de análisis.
- Guardar el estado inicial en PostgreSQL.
- Solicitar el procesamiento al servicio Java.
- Permitir la consulta de estados y resultados.

## Entorno virtual

Crear:

```powershell
python -m venv .venv

## PostgreSQL

La conexión utiliza SQLAlchemy y Psycopg 3.

La dirección se configura mediante:

```env
DATABASE_URL=postgresql+psycopg://usuario:contraseña@localhost:5432/analyticore