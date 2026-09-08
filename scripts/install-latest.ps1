$ErrorActionPreference = "Stop"

if (Get-Variable PSNativeCommandUseErrorActionPreference -ErrorAction SilentlyContinue) {
    $PSNativeCommandUseErrorActionPreference = $false
}

$RepoUrl = "https://github.com/Santi-PdR/Siege.git"
$Branch = "main"
$ModsDir = Join-Path $env:APPDATA ".sklauncher\instances\test-1\mods"
$WorkDir = Join-Path $env:TEMP ("Siege-build-" + [guid]::NewGuid().ToString("N"))
$OriginalLocation = Get-Location

try {
    $javaOutput = & java -version 2>&1 | Out-String
    if ($LASTEXITCODE -ne 0 -or $javaOutput -notmatch 'version "17') {
        throw "Siege requires Java 17. Current Java output: $javaOutput"
    }

    git clone --depth 1 --branch $Branch $RepoUrl $WorkDir
    if ($LASTEXITCODE -ne 0) { throw "Git could not clone $RepoUrl." }

    Set-Location $WorkDir
    & powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\scripts\build-and-deploy.ps1"
    if ($LASTEXITCODE -ne 0) { throw "Siege build or deployment failed." }

    $installed = Get-ChildItem $ModsDir -Filter "siege-menu-*.jar" |
        Where-Object { $_.Name -notmatch '(sources|javadoc)' } |
        Select-Object -First 1
    if (-not $installed) { throw "The deployed Siege jar was not found in $ModsDir." }

    Write-Host "Siege installed successfully: $($installed.FullName)" -ForegroundColor Green
} finally {
    Set-Location $OriginalLocation
    if (Test-Path $WorkDir) {
        Remove-Item $WorkDir -Recurse -Force -ErrorAction SilentlyContinue
    }
}
