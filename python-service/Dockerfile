# syntax=docker/dockerfile:1

# ------------------------------------------------
# Etapa 1: instalar y preparar las dependencias
# ------------------------------------------------
FROM python:3.13-slim AS builder

ENV PIP_DISABLE_PIP_VERSION_CHECK=1
ENV PIP_NO_CACHE_DIR=1

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        build-essential \
        libpq-dev \
    && rm -rf /var/lib/apt/lists/*

RUN python -m venv /opt/venv

ENV PATH="/opt/venv/bin:${PATH}"

COPY requirements.txt .

RUN python -m pip install --upgrade pip \
    && python -m pip install --no-cache-dir -r requirements.txt \
    && rm -rf \
        /opt/venv/lib/python3.13/site-packages/pip \
        /opt/venv/lib/python3.13/site-packages/pip-*.dist-info \
        /opt/venv/lib/python3.13/site-packages/setuptools \
        /opt/venv/lib/python3.13/site-packages/setuptools-*.dist-info \
        /opt/venv/lib/python3.13/site-packages/wheel \
        /opt/venv/lib/python3.13/site-packages/wheel-*.dist-info \
        /opt/venv/lib/python3.13/site-packages/pkg_resources \
        /opt/venv/lib/python3.13/ensurepip \
    && rm -f \
        /opt/venv/bin/pip \
        /opt/venv/bin/pip3 \
        /opt/venv/bin/pip3.13


# ------------------------------------------------
# Etapa 2: ejecutar la API FastAPI
# ------------------------------------------------
FROM python:3.13-slim AS runtime

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1
ENV PATH="/opt/venv/bin:${PATH}"

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        libpq5 \
    && rm -rf /var/lib/apt/lists/* \
    && rm -rf \
        /usr/local/lib/python3.13/site-packages/pip \
        /usr/local/lib/python3.13/site-packages/pip-*.dist-info \
        /usr/local/lib/python3.13/site-packages/setuptools \
        /usr/local/lib/python3.13/site-packages/setuptools-*.dist-info \
        /usr/local/lib/python3.13/site-packages/wheel \
        /usr/local/lib/python3.13/site-packages/wheel-*.dist-info \
        /usr/local/lib/python3.13/site-packages/pkg_resources \
        /usr/local/lib/python3.13/ensurepip \
    && rm -f \
        /usr/local/bin/pip \
        /usr/local/bin/pip3 \
        /usr/local/bin/pip3.13

RUN useradd \
    --create-home \
    --uid 10001 \
    appuser

COPY --from=builder /opt/venv /opt/venv

COPY app ./app

USER appuser

EXPOSE 8000

HEALTHCHECK \
    --interval=30s \
    --timeout=5s \
    --start-period=15s \
    --retries=3 \
    CMD python -c "import os, urllib.request; port=os.getenv('PORT', '8000'); urllib.request.urlopen(f'http://127.0.0.1:{port}/health', timeout=3)" || exit 1

CMD ["sh", "-c", "exec uvicorn app.main:app --host 0.0.0.0 --port ${PORT:-8000}"]