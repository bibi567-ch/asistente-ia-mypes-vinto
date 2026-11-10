$ErrorActionPreference = 'Stop'

$modelName = 'vosk-model-small-es-0.42'
$assetsDirectory = Join-Path $PSScriptRoot 'app\src\main\assets'
$modelDirectory = Join-Path $assetsDirectory $modelName
$modelFile = Join-Path $modelDirectory 'am\final.mdl'

if (Test-Path $modelFile) {
    Write-Output "Vosk model is already installed at $modelDirectory"
    exit 0
}
if (Test-Path $modelDirectory) {
    throw "The model directory exists but is incomplete: $modelDirectory"
}

$temporaryDirectory = Join-Path $env:TEMP ("mypes-vosk-" + [guid]::NewGuid().ToString('N'))
$archivePath = Join-Path $temporaryDirectory "$modelName.zip"
$extractionDirectory = Join-Path $temporaryDirectory 'extracted'
New-Item -ItemType Directory -Force -Path $temporaryDirectory, $assetsDirectory | Out-Null

try {
    Invoke-WebRequest `
        -Uri "https://alphacephei.com/vosk/models/$modelName.zip" `
        -OutFile $archivePath

    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $archive = [System.IO.Compression.ZipFile]::OpenRead($archivePath)
    try {
        $containsModel = $archive.Entries | Where-Object {
            $_.FullName -eq "$modelName/am/final.mdl"
        }
        if (-not $containsModel) {
            throw 'The downloaded archive does not contain the expected Spanish Vosk model.'
        }
    } finally {
        $archive.Dispose()
    }

    Expand-Archive -LiteralPath $archivePath -DestinationPath $extractionDirectory
    $stagedModelDirectory = Join-Path $extractionDirectory $modelName
    if (-not (Test-Path (Join-Path $stagedModelDirectory 'am\final.mdl'))) {
        throw 'Model extraction completed without the expected am/final.mdl file.'
    }
    Move-Item -LiteralPath $stagedModelDirectory -Destination $assetsDirectory

    Write-Output "Installed Vosk model at $modelDirectory"
} finally {
    if (Test-Path $temporaryDirectory) {
        Remove-Item -LiteralPath $temporaryDirectory -Recurse -Force
    }
}
