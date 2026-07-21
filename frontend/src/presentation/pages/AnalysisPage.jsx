import { useState } from 'react'

import {
  MAX_TEXT_LENGTH,
  MIN_TEXT_LENGTH,
  SENTIMENT,
  UI_STATUS,
} from '../../domain/constants/analysis'
import { AnalysisForm } from '../components/AnalysisForm'
import { AnalysisPreview } from '../components/AnalysisPreview'
import { AppHeader } from '../components/AppHeader'
import { DemoStateControls } from '../components/DemoStateControls'

const DEMO_TEXT =
  'La plataforma es excelente, rápida y genera reportes útiles.'

const DEMO_KEYWORDS = [
  'plataforma',
  'excelente',
  'rapida',
  'reportes',
  'utiles',
]

export function AnalysisPage() {
  const [text, setText] = useState('')
  const [submittedText, setSubmittedText] = useState('')
  const [status, setStatus] = useState(UI_STATUS.IDLE)
  const [sentiment, setSentiment] = useState(null)
  const [keywords, setKeywords] = useState([])
  const [error, setError] = useState('')
  const [errorMessage, setErrorMessage] = useState('')

  const isSubmitting =
    status === UI_STATUS.PENDING ||
    status === UI_STATUS.PROCESSING

  function handleTextChange(newText) {
    setText(newText)

    if (error) {
      setError('')
    }
  }

  function handleSubmit(event) {
    event.preventDefault()

    const normalizedText = text.trim()

    if (normalizedText.length < MIN_TEXT_LENGTH) {
      setError(
        `El texto debe contener al menos ${MIN_TEXT_LENGTH} caracteres.`,
      )
      return
    }

    if (normalizedText.length > MAX_TEXT_LENGTH) {
      setError(
        `El texto no puede superar ${MAX_TEXT_LENGTH} caracteres.`,
      )
      return
    }

    setSubmittedText(normalizedText)
    setStatus(UI_STATUS.PENDING)
    setSentiment(null)
    setKeywords([])
    setError('')
    setErrorMessage('')

    window.setTimeout(() => {
      setStatus(UI_STATUS.PROCESSING)
    }, 600)

    window.setTimeout(() => {
      setStatus(UI_STATUS.COMPLETED)
      setSentiment(SENTIMENT.POSITIVE)
      setKeywords(DEMO_KEYWORDS)
    }, 1800)
  }

  function handleDemoStatusChange(newStatus) {
    setStatus(newStatus)
    setError('')

    if (!submittedText) {
      setSubmittedText(DEMO_TEXT)
    }

    if (newStatus === UI_STATUS.COMPLETED) {
      setSentiment(SENTIMENT.POSITIVE)
      setKeywords(DEMO_KEYWORDS)
      setErrorMessage('')
      return
    }

    if (newStatus === UI_STATUS.ERROR) {
      setSentiment(null)
      setKeywords([])
      setErrorMessage(
        'El servicio de análisis no está disponible.',
      )
      return
    }

    setSentiment(null)
    setKeywords([])
    setErrorMessage('')
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
            Comprende mejor los comentarios de tus usuarios
          </h2>

          <p>
            AnalytiCore identifica el sentimiento de un texto y
            extrae sus palabras clave mediante servicios
            independientes.
          </p>
        </section>

        <div className="analysis-grid">
          <AnalysisForm
            text={text}
            error={error}
            isSubmitting={isSubmitting}
            onTextChange={handleTextChange}
            onSubmit={handleSubmit}
          />

          <AnalysisPreview
            status={status}
            submittedText={submittedText}
            sentiment={sentiment}
            keywords={keywords}
            errorMessage={errorMessage}
          />
        </div>

        <DemoStateControls
          status={status}
          onStatusChange={handleDemoStatusChange}
        />
      </main>

      <footer className="app-footer">
        <p>
          AnalytiCore · React, Python, Java y PostgreSQL
        </p>
      </footer>
    </div>
  )
}