param(
    [string]$PythonBaseUrl = "http://127.0.0.1:8000",

    [string]$Text = (
        "La plataforma es excelente. " +
        "La plataforma analiza comentarios " +
        "y genera resultados utiles."
    )
)

$ErrorActionPreference = "Stop"

Write-Host "Creando trabajo en Python..."

$createBody = @{
    text = $Text
} | ConvertTo-Json -Compress

$createdJob = Invoke-RestMethod `
    -Uri "$PythonBaseUrl/api/jobs" `
    -Method POST `
    -ContentType "application/json" `
    -Body $createBody

$jobId = $createdJob.jobId

if ([string]::IsNullOrWhiteSpace($jobId)) {
    throw "Python no devolvió un jobId."
}

Write-Host "Trabajo creado: $jobId"
Write-Host "Consultando resultado..."

$finalJob = $null

for ($attempt = 1; $attempt -le 15; $attempt++) {
    $currentJob = Invoke-RestMethod `
        -Uri "$PythonBaseUrl/api/jobs/$jobId" `
        -Method GET

    Write-Host (
        "Intento $attempt - Estado: " +
        $currentJob.status
    )

    if (
        $currentJob.status -eq "COMPLETADO" -or
        $currentJob.status -eq "ERROR"
    ) {
        $finalJob = $currentJob
        break
    }

    Start-Sleep -Seconds 1
}

if ($null -eq $finalJob) {
    throw "El trabajo no terminó dentro del tiempo esperado."
}

if ($finalJob.status -eq "ERROR") {
    throw (
        "El trabajo terminó con ERROR: " +
        $finalJob.errorMessage
    )
}

if ($finalJob.status -ne "COMPLETADO") {
    throw (
        "Estado final inesperado: " +
        $finalJob.status
    )
}

Write-Host "Flujo completado correctamente."
Write-Host "Sentimiento: $($finalJob.sentiment)"
Write-Host (
    "Palabras clave: " +
    ($finalJob.keywords -join ", ")
)

# Devuelve el objeto para poder asignarlo a una variable.
$finalJob