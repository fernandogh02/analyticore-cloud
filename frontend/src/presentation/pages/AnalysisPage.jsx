import {
  useEffect,
  useRef,
  useState,
} from 'react'

import { analyzeComment } from '../../application/analyzeComment'
import {
  MAX_TEXT_LENGTH,
  MIN_TEXT_LENGTH,
  UI_STATUS,
} from '../../domain/constants/analysis'
import { AnalysisForm } from '../components/AnalysisForm'
import { AnalysisPreview } from '../components/AnalysisPreview'
import { AppHeader } from '../components/AppHeader'

export function AnalysisPage() {
  const [text, setText] = useState('')
  const [submittedText, setSubmittedText] =
    useState('')
  const [status, setStatus] = useState(
    UI_STATUS.IDLE,
  )
  const [sentiment, setSentiment] =
    useState(null)
  const [keywords, setKeywords] = useState([])
  const [error, setError] = useState('')
  const [errorMessage, setErrorMessage] =
    useState('')

  const requestControllerRef = useRef(null)

  const isSubmitting =
    status === UI_STATUS.PENDING ||
    status === UI_STATUS.PROCESSING

  useEffect(() => {
    return () => {
      requestControllerRef.current?.abort()
    }
  }, [])

  function handleTextChange(newText) {
    setText(newText)

    if (error) {
      setError('')
    }
  }

  function applyJobUpdate(job) {
    setStatus(
      job.status || UI_STATUS.PENDING,
    )

    setSentiment(
      job.sentiment || null,
    )

    setKeywords(
      Array.isArray(job.keywords)
        ? job.keywords
        : [],
    )

    setErrorMessage(
      job.errorMessage || '',
    )
  }

  async function handleSubmit(event) {
    event.preventDefault()

    const normalizedText = text.trim()

    if (
      normalizedText.length <
      MIN_TEXT_LENGTH
    ) {
      setError(
        `El texto debe contener al menos ${MIN_TEXT_LENGTH} caracteres.`,
      )
      return
    }

    if (
      normalizedText.length >
      MAX_TEXT_LENGTH
    ) {
      setError(
        `El texto no puede superar ${MAX_TEXT_LENGTH} caracteres.`,
      )
      return
    }

    requestControllerRef.current?.abort()

    const controller =
      new AbortController()

    requestControllerRef.current =
      controller

    setSubmittedText(normalizedText)
    setStatus(UI_STATUS.PENDING)
    setSentiment(null)
    setKeywords([])
    setError('')
    setErrorMessage('')

    try {
      const finalJob =
        await analyzeComment({
          text: normalizedText,
          signal: controller.signal,
          onUpdate: applyJobUpdate,
        })

      applyJobUpdate(finalJob)
    } catch (requestError) {
      if (
        requestError.name ===
        'AbortError'
      ) {
        return
      }

      setStatus(UI_STATUS.ERROR)
      setSentiment(null)
      setKeywords([])
      setErrorMessage(
        requestError.message ||
          'No fue posible completar el análisis.',
      )
    } finally {
      if (
        requestControllerRef.current ===
        controller
      ) {
        requestControllerRef.current =
          null
      }
    }
  }

  return (
    <div className="app-shell">
      <AppHeader />

      <main className="main-content">
        <section className="hero">
          <p className="eyebrow">
            Plataforma distribuida
          </p>

          <h2>
            Comprende mejor los comentarios
            de tus usuarios
          </h2>

          <p>
            AnalytiCore identifica el
            sentimiento de un texto y extrae
            sus palabras clave mediante
            servicios independientes.
          </p>
        </section>

        <div className="analysis-grid">
          <AnalysisForm
            text={text}
            error={error}
            isSubmitting={isSubmitting}
            onTextChange={
              handleTextChange
            }
            onSubmit={handleSubmit}
          />

          <AnalysisPreview
            status={status}
            submittedText={
              submittedText
            }
            sentiment={sentiment}
            keywords={keywords}
            errorMessage={
              errorMessage
            }
          />
        </div>
      </main>

      <footer className="app-footer">
        <p>
          AnalytiCore · React, Python,
          Java y PostgreSQL
        </p>
      </footer>
    </div>
  )
}