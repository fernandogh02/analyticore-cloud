# Modelo de datos de AnalytiCore

## 1. Entidad principal

La entidad principal del sistema será `AnalysisJob`.

Representa una solicitud de análisis creada por un usuario.

## 2. Propiedades

| Propiedad      | Tipo         | Obligatoria | Descripción                     |
| -------------- | ------------ | ----------: | ------------------------------- |
| `id`           | UUID         |          Sí | Identificador único del trabajo |
| `textContent`  | Texto        |          Sí | Texto enviado por el usuario    |
| `status`       | Cadena       |          Sí | Estado actual del trabajo       |
| `sentiment`    | Cadena       |          No | Sentimiento obtenido            |
| `keywords`     | JSON         |          No | Lista de palabras clave         |
| `errorMessage` | Texto        |          No | Mensaje de error                |
| `createdAt`    | Fecha y hora |          Sí | Fecha de creación               |
| `updatedAt`    | Fecha y hora |          Sí | Fecha de última actualización   |

## 3. Estados permitidos

* `PENDIENTE`
* `PROCESANDO`
* `COMPLETADO`
* `ERROR`

## 4. Sentimientos permitidos

* `POSITIVO`
* `NEGATIVO`
* `NEUTRAL`

## 5. Reglas del modelo

1. Todo trabajo debe tener un identificador UUID.
2. Todo trabajo debe contener un texto.
3. Un trabajo nuevo debe comenzar como `PENDIENTE`.
4. Un trabajo debe pasar a `PROCESANDO` antes de finalizar.
5. Un trabajo completado debe tener un sentimiento.
6. Las palabras clave se almacenarán como una lista.
7. Un trabajo con estado `ERROR` puede contener un mensaje de error.
8. Las fechas de creación y actualización deben almacenarse en PostgreSQL.
9. El texto tendrá un máximo de 5000 caracteres.
10. Los resultados no se almacenarán permanentemente en la memoria de los servicios.

## 6. Transiciones de estado

Transición correcta:

```text
PENDIENTE → PROCESANDO → COMPLETADO
```

Transición cuando ocurre una falla:

```text
PENDIENTE → ERROR
```

o:

```text
PENDIENTE → PROCESANDO → ERROR
```

No se permitirán transiciones desde `COMPLETADO` hacia otro estado.

## 7. Tabla prevista

La tabla de PostgreSQL se llamará:

```text
analysis_jobs
```

Su estructura SQL se implementará en el paso de diseño y configuración de la base de datos.
