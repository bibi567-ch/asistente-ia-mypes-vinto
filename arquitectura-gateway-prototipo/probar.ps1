<#
  Prueba en vivo del API Gateway + Circuit Breaker (Windows PowerShell 5.1 o PowerShell 7).
  Antes, en OTRA terminal y en esta misma carpeta:  npm run servicios
  Luego aquí:  powershell -ExecutionPolicy Bypass -File .\probar.ps1
#>
$ErrorActionPreference = 'Continue'
$GW = 'http://localhost:8080'
$IA = 'http://localhost:5000'
$ps7 = $PSVersionTable.PSVersion.Major -ge 7

function Titulo($t) { Write-Host "`n=== $t ===" -ForegroundColor Green }

function Llamar($metodo, $url, $cuerpo, $token) {
    $h = @{}
    if ($token) { $h['Authorization'] = "Bearer $token" }
    $bytes = if ($cuerpo) { [Text.Encoding]::UTF8.GetBytes($cuerpo) } else { $null }
    $sw = [Diagnostics.Stopwatch]::StartNew()
    $code = -1; $estado = '-'; $contenido = ''
    try {
        $p = @{ Method = $metodo; Uri = $url; Headers = $h; UseBasicParsing = $true }
        if ($bytes) { $p['Body'] = $bytes; $p['ContentType'] = 'application/json; charset=utf-8' }
        if ($ps7) { $p['SkipHttpErrorCheck'] = $true }
        $r = Invoke-WebRequest @p
        $code = [int]$r.StatusCode
        $estado = (@($r.Headers['x-circuit-state']) -join '')
        $contenido = $r.Content
    } catch {
        $resp = $_.Exception.Response
        if ($resp) {
            $code = [int]$resp.StatusCode
            $estado = $resp.Headers['x-circuit-state']
            $lector = New-Object IO.StreamReader($resp.GetResponseStream())
            $contenido = $lector.ReadToEnd()
        } else {
            $contenido = $_.Exception.Message
        }
    }
    $sw.Stop()
    if (-not $estado) { $estado = '-' }
    [pscustomobject]@{ HTTP = $code; Circuito = $estado; ms = $sw.ElapsedMilliseconds; Respuesta = $contenido }
}

function Transcribir($n, $token) {
    1..$n | ForEach-Object {
        $r = Llamar 'POST' "$GW/api/v1/asr/transcribe" '{"textoSimulado":"vendí dos panes","idioma":"quh"}' $token
        $r | Add-Member -NotePropertyName N -NotePropertyValue $_ -PassThru
    } | Format-Table N, HTTP, Circuito, ms, Respuesta -AutoSize -Wrap
}

Titulo '0. ¿Está corriendo el gateway?'
$salud = Llamar 'GET' "$GW/health" $null $null
if ($salud.HTTP -ne 200) {
    Write-Host 'No responde http://localhost:8080. Abre otra terminal en esta carpeta y ejecuta: npm run servicios' -ForegroundColor Red
    exit 1
}
$salud.Respuesta
Llamar 'POST' "$IA/admin/modo" '{"modo":"normal"}' $null | Out-Null
Llamar 'POST' "$IA/admin/reset" '{}' $null | Out-Null

Titulo '1. Seguridad: sin token debe dar 401'
(Llamar 'POST' "$GW/api/v1/asr/transcribe" '{}' $null) | Format-Table HTTP, Respuesta -AutoSize

Titulo '2. Login a través del gateway (comerciante)'
$login = Llamar 'POST' "$GW/api/v1/auth/login" '{"usuario":"comerciante","password":"vinto2026"}' $null
$token = ($login.Respuesta | ConvertFrom-Json).token
"HTTP $($login.HTTP) · token recibido: $($token.Substring(0,20))..."

Titulo 'Fase A. IA normal: 3 transcripciones (circuito CERRADO, ~150 ms)'
Transcribir 3 $token

Titulo 'Fase B. Se cae la IA y se envían 10 (el circuito se ABRE y baja a ~1 ms)'
Llamar 'POST' "$IA/admin/modo" '{"modo":"caido"}' $null | Out-Null
Transcribir 10 $token
$stats = (Llamar 'GET' "$IA/admin/stats" $null $null).Respuesta | ConvertFrom-Json
"Peticiones que llegaron realmente a la IA: $($stats.llamadas) (las demás las cortó el breaker)"

Titulo 'Fase C. La IA vuelve; esperamos 11 s (resetTimeout) y probamos'
Llamar 'POST' "$IA/admin/modo" '{"modo":"normal"}' $null | Out-Null
11..1 | ForEach-Object { Write-Host -NoNewline "$_ "; Start-Sleep -Seconds 1 }
Write-Host ''
Transcribir 2 $token

Titulo 'Estado de los circuitos: comerciante (403) y admin (200)'
(Llamar 'GET' "$GW/gateway/estado" $null $token) | Format-Table HTTP, Respuesta -AutoSize
$admin = ((Llamar 'POST' "$GW/api/v1/auth/login" '{"usuario":"admin","password":"admin2026"}' $null).Respuesta | ConvertFrom-Json).token
(Llamar 'GET' "$GW/gateway/estado" $null $admin).Respuesta
Write-Host "`nListo. En la otra terminal se ven los mensajes [breaker:ia] ABIERTO / SEMIABIERTO / CERRADO." -ForegroundColor Green
