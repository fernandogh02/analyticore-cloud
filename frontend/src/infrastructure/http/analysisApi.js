const DEFAULT_API_BASE_URL = 'http://127.0.0.1:8000'

const API_BASE_URL = (
  import.meta.env.VITE_API_BASE_URL ||
  DEFAULT_API_BASE_URL
).replace(/\/$/, '')

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

function resolveErrorMessage(payload, status) {
  if (typeof payload?.message === 'string') {
    return payload.message
  }

  if (typeof payload?.detail === 'string') {
    return payload.detail
  }

  if (typeof payload?.error === 'string') {
    return payload.error
  }

  return `La solicitud terminó con el código ${status}.`
}

async function request(path, options = {}) {
  let response

  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
    })
  } catch (error) {
    throw new Error(
      'No fue posible conectar con el servicio Python.',
      {
        cause: error,
      },
    )
  }

  const payload = await readResponseBody(response)

  if (!response.ok) {
    throw new Error(
      resolveErrorMessage(payload, response.status),
    )
  }

  return payload
}

export function createAnalysisJob(text, signal) {
  return request('/api/jobs', {
    method: 'POST',
    signal,
    body: JSON.stringify({
      text,
    }),
  })
}

export function getAnalysisJob(jobId, signal) {
  return request(`/api/jobs/${jobId}`, {
    method: 'GET',
    signal,
  })
}