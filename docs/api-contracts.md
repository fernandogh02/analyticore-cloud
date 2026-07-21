# Contratos de las APIs de AnalytiCore

## 1. Formato general

Las APIs utilizarán JSON.

Encabezado principal:

```http
Content-Type: application/json
```

La API pública será administrada por Python.

La API interna de análisis será administrada por Java.

---

# API pública de Python

## 2. Crear un trabajo de análisis

### Solicitud

```http
POST /api/jobs
```

Cuerpo:

```json
{
  "text": "La plataforma funciona muy bien y es fácil de utilizar."
}
```

### Validaciones

* El campo `text` es obligatorio.
* El texto no puede estar vacío.
* El texto no puede contener solamente espacios.
* El texto tendrá un máximo de 5000 caracteres.

### Respuesta correcta

Código:

```http
202 Accepted
```

Cuerpo:

```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PENDIENTE",
  "message": "El análisis fue registrado correctamente."
}
```

### Texto inválido

Código:

```http
400 Bad Request
```

Cuerpo:

```json
{
  "error": "INVALID_TEXT",
  "message": "Debe ingresar un texto válido."
}
```

### Servicio Java no disponible

Código:

```http
503 Service Unavailable
```

Cuerpo:

```json
{
  "error": "ANALYSIS_SERVICE_UNAVAILABLE",
  "message": "El servicio de análisis no está disponible."
}
```

---

## 3. Consultar un trabajo

### Solicitud

```http
GET /api/jobs/{jobId}
```

Ejemplo:

```http
GET /api/jobs/550e8400-e29b-41d4-a716-446655440000
```

### Trabajo pendiente

Código:

```http
200 OK
```

Cuerpo:

```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "text": "La plataforma funciona muy bien.",
  "status": "PENDIENTE",
  "sentiment": null,
  "keywords": [],
  "errorMessage": null
}
```

### Trabajo procesándose

```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "text": "La plataforma funciona muy bien.",
  "status": "PROCESANDO",
  "sentiment": null,
  "keywords": [],
  "errorMessage": null
}
```

### Trabajo completado

```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "text": "La plataforma funciona muy bien.",
  "status": "COMPLETADO",
  "sentiment": "POSITIVO",
  "keywords": [
    "plataforma",
    "funciona"
  ],
  "errorMessage": null
}
```

### Trabajo con error

```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "text": "La plataforma funciona muy bien.",
  "status": "ERROR",
  "sentiment": null,
  "keywords": [],
  "errorMessage": "No fue posible completar el análisis."
}
```

### Trabajo inexistente

Código:

```http
404 Not Found
```

Cuerpo:

```json
{
  "error": "JOB_NOT_FOUND",
  "message": "No se encontró el trabajo solicitado."
}
```

---

## 4. Verificar el servicio Python

### Solicitud

```http
GET /health
```

### Respuesta

```json
{
  "status": "UP",
  "service": "python-service"
}
```

---

# API interna de Java

## 5. Iniciar el análisis

### Solicitud

```http
POST /internal/analysis
```

Cuerpo:

```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Respuesta aceptada

Código:

```http
202 Accepted
```

Cuerpo:

```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PROCESANDO",
  "message": "El análisis fue iniciado."
}
```

El servicio Java podrá continuar el procesamiento después de aceptar la solicitud. El estado y los resultados siempre se almacenarán en PostgreSQL.

### Trabajo inexistente

Código:

```http
404 Not Found
```

Cuerpo:

```json
{
  "error": "JOB_NOT_FOUND",
  "message": "No se encontró el trabajo solicitado."
}
```

### Estado no permitido

Código:

```http
409 Conflict
```

Cuerpo:

```json
{
  "error": "INVALID_JOB_STATUS",
  "message": "El trabajo no puede procesarse en su estado actual."
}
```

---

## 6. Verificar el servicio Java

### Solicitud

```http
GET /health
```

### Respuesta

```json
{
  "status": "UP",
  "service": "java-service"
}
```

---

# 7. Valores permitidos

## Estados

```text
PENDIENTE
PROCESANDO
COMPLETADO
ERROR
```

## Sentimientos

```text
POSITIVO
NEGATIVO
NEUTRAL
```

---

# 8. Consulta periódica desde React

Después de recibir el `jobId`, React consultará:

```http
GET /api/jobs/{jobId}
```

La consulta se realizará aproximadamente cada dos segundos.

Las consultas se detendrán cuando el estado sea:

```text
COMPLETADO
```

o:

```text
ERROR
```

React también detendrá las consultas cuando el componente sea cerrado o cuando se supere el tiempo máximo establecido para esperar una respuesta.

## Servicio Java

### Iniciar y completar análisis

`POST /internal/analysis`

Solicitud:

```json
{
  "jobId": "UUID"
}