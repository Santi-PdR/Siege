$ErrorActionPreference = "Stop"

$ProjectDir = Split-Path -Parent $PSScriptRoot
$ModsDir = Join-Path $env:APPDATA ".sklauncher\instances\test-1\mods"
$DistDir = Join-Path $ProjectDir "dist"

$Jar = Get-ChildItem $DistDir -Filter "siege-menu-*.jar" -ErrorAction SilentlyContinue |
    Where-Object { $_.Name -notmatch '(sources|javadoc)' } |
    Select-Object -First 1

if (-not $Jar) {
    throw "No se encontro el JAR validado por GitHub Actions en $DistDir. Espera a que termine el workflow de main y vuelve a ejecutar el instalador."
}

New-Item -ItemType Directory -Force -Path $ModsDir | Out-Null
Get-ChildItem $ModsDir -Filter "siege-menu-*.jar" -ErrorAction SilentlyContinue | Remove-Item -Force
Copy-Item $Jar.FullName $ModsDir -Force

Write-Host "Instalado desde compilacion de GitHub: $($Jar.Name) -> $ModsDir" -ForegroundColor Green
