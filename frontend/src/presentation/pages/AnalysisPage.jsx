import { useState } from 'react'

import {
  MAX_TEXT_LENGTH,
  MIN_TEXT_LENGTH,
} from '../../domain/constants/analysis'
import { AnalysisForm } from '../components/AnalysisForm'
import { AnalysisPreview } from '../components/AnalysisPreview'
import { AppHeader } from '../components/AppHeader'

export function AnalysisPage() {
  const [text, setText] = useState('')
  const [submittedText, setSubmittedText] = useState('')
  const [error, setError] = useState('')

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
    setError('')
  }

  return (
    <div className="app-shell">
      <AppHeader />

      <main className="main-content">
        <section className="hero">
          <p className="eyebrow">Plataforma distribuida</p>

          <h2>
            Comprende mejor los comentarios de tus usuarios
          </h2>

          <p>
            AnalytiCore identifica el sentimiento de un texto y extrae
            sus palabras clave mediante servicios independientes.
          </p>
        </section>

        <div className="analysis-grid">
          <AnalysisForm
            text={text}
            error={error}
            onTextChange={handleTextChange}
            onSubmit={handleSubmit}
          />

          <AnalysisPreview submittedText={submittedText} />
        </div>
      </main>

      <footer className="app-footer">
        <p>AnalytiCore · React, Python, Java y PostgreSQL</p>
      </footer>
    </div>
  )
}