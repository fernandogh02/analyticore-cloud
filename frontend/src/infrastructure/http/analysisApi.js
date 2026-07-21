const DEFAULT_API_BASE_URL =
  'http://127.0.0.1:8000'

const API_BASE_URL = (
  import.meta.env.VITE_API_BASE_URL ||
  DEFAULT_API_BASE_URL
).replace(/\/$/, '')

const HTTP_STATUS_MESSAGES = {
  400: 'La solicitud contiene datos inválidos.',
  404: 'No se encontró el trabajo solicitado.',
  409: 'El trabajo no puede procesarse desde su estado actual.',
  422: 'El comentario no cumple las reglas de validación.',
  500: 'Ocurrió un error interno en el servidor.',
  503: 'El servicio de análisis no está disponible.',
}

async function readResponseBody(response) {
  const content = await response.text()

  if (!content) {
    return null
  }

  try {
    return JSON.parse(content)
  } catch {
    return null
  }
}

function readValidationDetail(detail) {
  if (!Array.isArray(detail)) {
    return null
  }

  const messages = detail
    .map((item) => item?.msg)
    .filter(
      (message) =>
        typeof message === 'string',
    )

  if (messages.length === 0) {
    return null
  }

  return messages.join(' ')
}

function resolveErrorMessage(
  payload,
  status,
) {
  if (
    typeof payload?.message ===
    'string'
  ) {
    return payload.message
  }

  if (
    typeof payload?.detail ===
    'string'
  ) {
    return payload.detail
  }

  const validationMessage =
    readValidationDetail(
      payload?.detail,
    )

  if (validationMessage) {
    return validationMessage
  }

  if (
    typeof payload?.error ===
    'string'
  ) {
    return payload.error
  }

  return (
    HTTP_STATUS_MESSAGES[status] ||
    `La solicitud terminó con el código ${status}.`
  )
}

async function request(
  path,
  options = {},
) {
  let response

  try {
    response = await fetch(
      `${API_BASE_URL}${path}`,
      {
        ...options,
        headers: {
          'Content-Type':
            'application/json',
          ...options.headers,
        },
      },
    )
  } catch (error) {
    throw new Error(
      'No fue posible conectar con el servicio Python.',
      {
        cause: error,
      },
    )
  }

  const payload =
    await readResponseBody(response)

  if (!response.ok) {
    throw new Error(
      resolveErrorMessage(
        payload,
        response.status,
      ),
    )
  }

  return payload
}

export function createAnalysisJob(
  text,
  signal,
) {
  return request('/api/jobs', {
    method: 'POST',
    signal,
    body: JSON.stringify({
      text,
    }),
  })
}

export function getAnalysisJob(
  jobId,
  signal,
) {
  if (!jobId) {
    return Promise.reject(
      new Error(
        'No existe un identificador de trabajo.',
      ),
    )
  }

  return request(
    `/api/jobs/${encodeURIComponent(jobId)}`,
    {
      method: 'GET',
      signal,
    },
  )
}