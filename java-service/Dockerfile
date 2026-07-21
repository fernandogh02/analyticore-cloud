# syntax=docker/dockerfile:1

# ------------------------------------------------
# Etapa 1: compilar la aplicación con Maven
# ------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiar primero el archivo de Maven para
# aprovechar la caché de dependencias.
COPY pom.xml .

RUN mvn -B -DskipTests dependency:go-offline

# Copiar el código fuente.
COPY src ./src

# Crear el archivo ejecutable de Spring Boot.
RUN mvn -B clean package -DskipTests

# Localizar el JAR principal y copiarlo con
# un nombre estable.
RUN JAR_FILE="$(find target -maxdepth 1 -type f -name '*.jar' ! -name 'original-*.jar' | head -n 1)" \
    && test -n "$JAR_FILE" \
    && cp "$JAR_FILE" /app/app.jar


# ------------------------------------------------
# Etapa 2: ejecutar la aplicación
# ------------------------------------------------
FROM eclipse-temurin:21-jre-jammy AS runtime

ENV JAVA_OPTS=""

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --gid 10001 appgroup \
    && useradd \
        --uid 10001 \
        --gid appgroup \
        --create-home \
        appuser

COPY --from=builder /app/app.jar ./app.jar

USER appuser

EXPOSE 8080

HEALTHCHECK \
    --interval=30s \
    --timeout=5s \
    --start-period=20s \
    --retries=3 \
    CMD curl --fail --silent "http://127.0.0.1:${PORT:-8080}/health" > /dev/null || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=${PORT:-8080}"]