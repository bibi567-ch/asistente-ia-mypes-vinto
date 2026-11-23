<#
  IA Vinto - Instalar la app en el celular con un solo comando.

  Uso (PowerShell, dentro de la carpeta mobile):
    .\instalar_en_celular.ps1                         # por cable USB
    .\instalar_en_celular.ps1 -ConBackend             # ademas conecta el backend de la PC (puerto 3000)
    .\instalar_en_celular.ps1 -ConGateway -SoloAbrir  # conecta el API Gateway de la PC (8080) y la IA de prueba (5000)
    .\instalar_en_celular.ps1 -Emparejar 192.168.1.50:37123 -Codigo 123456   # wifi, solo la primera vez
    .\instalar_en_celular.ps1 -Wifi 192.168.1.50:41235                       # wifi, las siguientes veces
    .\instalar_en_celular.ps1 -SoloAbrir              # abre la app ya instalada, sin compilar
    .\instalar_en_celular.ps1 -Logs                   # muestra los errores de la app en vivo

  O doble clic en instalar_en_celular.bat
#>
param(
    [switch]$ConBackend,
    [switch]$ConGateway,
    [string]$Wifi,
    [string]$Emparejar,
    [string]$Codigo,
    [string]$Serial,
    [switch]$SoloAbrir,
    [switch]$Logs
)

# Continue: Gradle y adb escriben avisos en stderr; los errores se revisan con $LASTEXITCODE.
$ErrorActionPreference = 'Continue'
$Paquete = 'com.mypes.vinto'
$Actividad = "$Paquete/.MainActivity"
$Raiz = $PSScriptRoot
Set-Location $Raiz

function Paso($texto)  { Write-Host "`n==> $texto" -ForegroundColor Green }
function Info($texto)  { Write-Host "    $texto" -ForegroundColor Gray }
function Alerta($texto){ Write-Host "    $texto" -ForegroundColor Yellow }
function Falla($texto) { Write-Host "`n[ERROR] $texto" -ForegroundColor Red; Read-Host "`nPresiona Enter para salir"; exit 1 }

# ---------- 1. Android SDK y adb ----------
Paso 'Buscando el Android SDK'
$sdk = $null
$candidatos = @($env:ANDROID_HOME, $env:ANDROID_SDK_ROOT)
if (Test-Path "$Raiz\local.properties") {
    $linea = Get-Content "$Raiz\local.properties" | Where-Object { $_ -match '^sdk\.dir=' } | Select-Object -First 1
    if ($linea) { $candidatos += (($linea -replace '^sdk\.dir=', '') -replace '\\:', ':' -replace '\\\\', '\') }
}
$candidatos += "$env:LOCALAPPDATA\Android\Sdk"
foreach ($c in $candidatos) {
    if ($c -and (Test-Path "$c\platform-tools\adb.exe")) { $sdk = $c; break }
}
if (-not $sdk) { Falla 'No encontre el Android SDK. Abre Android Studio una vez (More Actions > SDK Manager) e instala "Android SDK Platform-Tools".' }
$adb = "$sdk\platform-tools\adb.exe"
$env:ANDROID_HOME = $sdk
Info "SDK: $sdk"

# ---------- 2. Java (el que trae Android Studio) ----------
Paso 'Buscando Java'
if (-not $env:JAVA_HOME -or -not (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    $jbr = @(
        "$env:ProgramFiles\Android\Android Studio\jbr",
        "$env:LOCALAPPDATA\Programs\Android Studio\jbr",
        "$env:ProgramFiles\Android\Android Studio\jre"
    ) | Where-Object { Test-Path "$_\bin\java.exe" } | Select-Object -First 1
    if ($jbr) { $env:JAVA_HOME = $jbr }
    elseif (-not (Get-Command java -ErrorAction SilentlyContinue)) {
        Falla 'No encontre Java. Instala Android Studio o define la variable JAVA_HOME.'
    }
}
Info "JAVA_HOME: $env:JAVA_HOME"

# ---------- 3. Celular ----------
Paso 'Buscando el celular'
& $adb start-server | Out-Null
if ($Emparejar) {
    if (-not $Codigo) { Falla 'Para emparejar por wifi usa tambien -Codigo (el de 6 numeros que muestra el celular).' }
    Info "Emparejando con $Emparejar ..."
    & $adb pair $Emparejar $Codigo
    Alerta 'Ahora mira en el celular la "Direccion IP y puerto" de Depuracion inalambrica (es otro puerto) y vuelve a ejecutar con -Wifi IP:PUERTO'
    if (-not $Wifi) { Read-Host "`nPresiona Enter para salir"; exit 0 }
}
if ($Wifi) {
    Info "Conectando por wifi a $Wifi ..."
    & $adb connect $Wifi
}

$lineas = (& $adb devices) | Select-Object -Skip 1 | Where-Object { $_.Trim() -ne '' }
$listos = @($lineas | Where-Object { $_ -match "`tdevice$" } | ForEach-Object { ($_ -split "`t")[0] })
$sinPermiso = @($lineas | Where-Object { $_ -match "`tunauthorized$" })

if ($sinPermiso.Count -gt 0 -and $listos.Count -eq 0) {
    Falla 'El celular esta conectado pero no autorizado. Desbloquealo, acepta "Permitir depuracion USB" (marca "Permitir siempre") y vuelve a ejecutar.'
}
if ($listos.Count -eq 0) {
    Falla "No hay ningun celular conectado.`n  - Activa Opciones de desarrollador > Depuracion USB.`n  - Usa un cable de datos y elige 'Transferencia de archivos'.`n  - O usa wifi: -Emparejar IP:PUERTO -Codigo 123456"
}
if ($Serial) { $dispositivo = $Serial } else { $dispositivo = $listos[0] }
if ($listos.Count -gt 1 -and -not $Serial) { Alerta "Hay varios dispositivos; uso $dispositivo (elige otro con -Serial). Lista: $($listos -join ', ')" }
$env:ANDROID_SERIAL = $dispositivo
$modelo = (& $adb -s $dispositivo shell getprop ro.product.model).Trim()
$android = (& $adb -s $dispositivo shell getprop ro.build.version.release).Trim()
Info "Celular: $modelo (Android $android) [$dispositivo]"

function AbrirApp {
    Paso 'Abriendo IA Vinto en el celular'
    & $adb -s $dispositivo shell am start -n $Actividad | Out-Null
    Info 'Listo. Mira tu celular.'
}

if ($Logs) {
    Paso 'Errores de la app en vivo (Ctrl + C para salir)'
    & $adb -s $dispositivo logcat -c
    AbrirApp
    & $adb -s $dispositivo logcat '*:E' 'AndroidRuntime:E' 'MypesLocal:*' 'vosk:*'
    exit 0
}
# ---------- API Gateway de Arquitectura (opcional) ----------
if ($ConGateway) {
    Paso 'Conectando el API Gateway de la PC (8080) y el servicio de IA de prueba (5000)'
    & $adb -s $dispositivo reverse tcp:8080 tcp:8080 | Out-Null
    & $adb -s $dispositivo reverse tcp:5000 tcp:5000 | Out-Null
    Info 'En la app: Mas > Nube protegida > Conectar (direccion http://127.0.0.1:8080/).'
    Info 'Antes levanta el gateway en la PC: cd arquitectura-gateway-prototipo ; npm run auth & npm run sync & npm run ia & npm start'
}

if ($SoloAbrir) { AbrirApp; exit 0 }

# ---------- 4. Backend (opcional) ----------
$argsGradle = @('installDebug')
if ($ConBackend) {
    Paso 'Conectando el backend de la PC (puerto 3000) por el cable'
    & $adb -s $dispositivo reverse tcp:3000 tcp:3000 | Out-Null
    $argsGradle += '-PMYPES_API_BASE_URL=http://127.0.0.1:3000/'
    Info 'El celular vera http://127.0.0.1:3000 = el backend de esta PC. Levantalo con: docker compose up'
}

# ---------- 5. Compilar e instalar ----------
Paso 'Compilando e instalando (la primera vez tarda varios minutos)'
& .\gradlew.bat @argsGradle 2>&1 | ForEach-Object { "$_" } | Tee-Object -Variable registro
$codigoSalida = $LASTEXITCODE
$texto = ($registro | Out-String)

if ($codigoSalida -ne 0 -and $texto -match 'INSTALL_FAILED_UPDATE_INCOMPATIBLE|signatures do not match') {
    Alerta 'La app que tienes instalada se firmo en otra computadora y no se puede actualizar encima.'
    $r = Read-Host '    Desinstalarla y volver a instalar? Se borran los datos de esa app (s/n)'
    if ($r -match '^[sS]') {
        & $adb -s $dispositivo uninstall $Paquete | Out-Null
        & .\gradlew.bat @argsGradle
        $codigoSalida = $LASTEXITCODE
    } else { Falla 'Instalacion cancelada.' }
}
if ($codigoSalida -ne 0) {
    $texto | Out-File "$Raiz\error_compilacion.txt" -Encoding utf8
    Falla "La compilacion fallo. Guarde el detalle en mobile\error_compilacion.txt (mandaselo a Claude)."
}

# ---------- 6. Permisos y abrir ----------
Paso 'Dando permisos de microfono y llamadas'
foreach ($p in 'android.permission.RECORD_AUDIO', 'android.permission.CALL_PHONE') {
    & $adb -s $dispositivo shell pm grant $Paquete $p 2>$null | Out-Null
}
AbrirApp
Write-Host "`nIA Vinto quedo instalada en $modelo." -ForegroundColor Green
Info 'Para volver a abrirla:   .\instalar_en_celular.ps1 -SoloAbrir'
Info 'Si se cierra sola:       .\instalar_en_celular.ps1 -Logs'
