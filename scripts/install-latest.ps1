$ErrorActionPreference = "Stop"

if (Get-Variable PSNativeCommandUseErrorActionPreference -ErrorAction SilentlyContinue) {
    $PSNativeCommandUseErrorActionPreference = $false
}

$RepoUrl = "https://github.com/Santi-PdR/Siege.git"
$Branch = "main"
$ModsDir = Join-Path $env:APPDATA ".sklauncher\instances\test-1\mods"
$WorkDir = Join-Path $env:TEMP ("Siege-deploy-" + [guid]::NewGuid().ToString("N"))
$OriginalLocation = Get-Location

try {
    git clone --depth 1 --branch $Branch $RepoUrl $WorkDir
    if ($LASTEXITCODE -ne 0) {
        throw "Git no pudo descargar $RepoUrl."
    }

    Set-Location $WorkDir
    & powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\scripts\build-and-deploy.ps1"
    if ($LASTEXITCODE -ne 0) {
        throw "Fallo la instalacion del JAR compilado por GitHub."
    }

    $Installed = Get-ChildItem $ModsDir -Filter "siege-menu-*.jar" |
        Where-Object { $_.Name -notmatch '(sources|javadoc)' } |
        Select-Object -First 1

    if (-not $Installed) {
        throw "No se encontro el JAR instalado en $ModsDir."
    }

    Write-Host "SIEGE instalado correctamente: $($Installed.FullName)" -ForegroundColor Green
} finally {
    Set-Location $OriginalLocation
    if (Test-Path $WorkDir) {
        Remove-Item $WorkDir -Recurse -Force -ErrorAction SilentlyContinue
    }
}
