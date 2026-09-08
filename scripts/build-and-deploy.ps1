$ErrorActionPreference = "Stop"

$ProjectDir = Split-Path -Parent $PSScriptRoot
$GradleVersion = "8.8"
$ToolsDir = Join-Path $env:LOCALAPPDATA "SiegeBuildTools"
$GradleDir = Join-Path $ToolsDir "gradle-$GradleVersion"
$Archive = Join-Path $ToolsDir "gradle-$GradleVersion-bin.zip"
$ModsDir = Join-Path $env:APPDATA ".sklauncher\instances\test-1\mods"

$javaOutput = & java -version 2>&1 | Out-String
if ($LASTEXITCODE -ne 0 -or $javaOutput -notmatch 'version "17') {
    throw "Java 17 is required. Current Java output: $javaOutput"
}

New-Item -ItemType Directory -Force -Path $ToolsDir | Out-Null
if (-not (Test-Path $GradleDir)) {
    Invoke-WebRequest "https://services.gradle.org/distributions/gradle-$GradleVersion-bin.zip" -OutFile $Archive
    Expand-Archive -Path $Archive -DestinationPath $ToolsDir -Force
}

Push-Location $ProjectDir
try {
    & (Join-Path $GradleDir "bin\gradle.bat") --no-daemon clean build
    if ($LASTEXITCODE -ne 0) { throw "Gradle build failed." }
    New-Item -ItemType Directory -Force -Path $ModsDir | Out-Null
    Get-ChildItem $ModsDir -Filter "siege-menu-*.jar" | Remove-Item -Force
    $Jar = Get-ChildItem (Join-Path $ProjectDir "build\libs") -Filter "siege-menu-*.jar" |
        Where-Object { $_.Name -notmatch '(sources|javadoc)' } | Select-Object -First 1
    if (-not $Jar) { throw "The compiled mod jar was not found." }
    Copy-Item $Jar.FullName $ModsDir -Force
    Write-Host "Installed: $($Jar.Name) -> $ModsDir" -ForegroundColor Green
} finally {
    Pop-Location
}
