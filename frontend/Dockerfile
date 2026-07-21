# syntax=docker/dockerfile:1

# ---------------------------------------------
# Etapa 1: compilar la aplicación React
# ---------------------------------------------
FROM node:24-alpine AS builder

WORKDIR /app

# Copiar primero los archivos de dependencias
# permite reutilizar la caché de Docker.
COPY package.json package-lock.json ./

RUN npm ci

# Copiar el código fuente.
COPY . .

# Las variables de Vite se incorporan durante
# la compilación, no cuando arranca Nginx.
ARG VITE_API_BASE_URL=http://127.0.0.1:8000

ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

RUN npm run build


# ---------------------------------------------
# Etapa 2: publicar con Nginx
# ---------------------------------------------
FROM nginx:alpine AS runtime

COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=builder \
    /app/dist \
    /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK \
    --interval=30s \
    --timeout=3s \
    --start-period=5s \
    --retries=3 \
    CMD wget -q -O - http://127.0.0.1/health || exit 1

CMD ["nginx", "-g", "daemon off;"]