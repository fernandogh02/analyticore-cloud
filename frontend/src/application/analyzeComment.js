import {
  MAX_POLLING_ATTEMPTS,
  POLLING_INTERVAL_MS,
  UI_STATUS,
} from '../domain/constants/analysis'
import {
  createAnalysisJob,
  getAnalysisJob,
} from '../infrastructure/http/analysisApi'

const TERMINAL_STATUSES = new Set([
  UI_STATUS.COMPLETED,
  UI_STATUS.ERROR,
])

function wait(milliseconds, signal) {
  return new Promise((resolve, reject) => {
    const timeoutId = setTimeout(
      resolve,
      milliseconds,
    )

    function handleAbort() {
      clearTimeout(timeoutId)

      reject(
        new DOMException(
          'La solicitud fue cancelada.',
          'AbortError',
        ),
      )
    }

    if (signal?.aborted) {
      handleAbort()
      return
    }

    signal?.addEventListener(
      'abort',
      handleAbort,
      {
        once: true,
      },
    )
  })
}

function validateCreatedJob(job) {
  if (!job?.jobId) {
    throw new Error(
      'Python no devolvió un identificador de trabajo.',
    )
  }
}

export async function analyzeComment({
  text,
  signal,
  onUpdate,
}) {
  const createdJob = await createAnalysisJob(
    text,
    signal,
  )

  validateCreatedJob(createdJob)
  onUpdate(createdJob)

  if (TERMINAL_STATUSES.has(createdJob.status)) {
    return createdJob
  }

  for (
    let attempt = 1;
    attempt <= MAX_POLLING_ATTEMPTS;
    attempt += 1
  ) {
    await wait(
      POLLING_INTERVAL_MS,
      signal,
    )

    const currentJob = await getAnalysisJob(
      createdJob.jobId,
      signal,
    )

    onUpdate(currentJob)

    if (
      TERMINAL_STATUSES.has(
        currentJob.status,
      )
    ) {
      return currentJob
    }
  }

  throw new Error(
    'El análisis tardó más de lo esperado.',
  )
}