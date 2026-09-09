param(
    [switch]$PublishMain
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

$RepoRoot = Split-Path -Parent $PSScriptRoot
$TargetDir = Join-Path $RepoRoot 'assets-source\music-full'
$Branch = 'codex/full-clean-masters-063'

$Tracks = @(
    [pscustomobject]@{
        Label = 'The Tale of a Cruel World.ogg'
        Target = 'tale_cruel_world.ogg'
        Sha256 = '96d66d0c0a645279764d42f787f4bb1244b25aa0dc595623a75b40c0c7573b35'
    },
    [pscustomobject]@{
        Label = '03. The Darkest Of Days - Frostpunk Original Soundtrack.ogg'
        Target = 'darkest_of_days.ogg'
        Sha256 = 'b5797d634e6a25ce58469fca11a58b0fa3e015d167ed75c0646729283d2a7060'
    },
    [pscustomobject]@{
        Label = 'DVN lobby music.ogg'
        Target = 'kaptain_music_box.ogg'
        Sha256 = '61a498de84b0052f0cf359eda84de624af91d18b0a0248cad3755006f52f2928'
    },
    [pscustomobject]@{
        Label = "Terraria Calamity Mod Music - _Heaven's Hell-Sent Gift_ - Theme of The Astral Meteor.ogg"
        Target = 'heavens_hell_sent_gift.ogg'
        Sha256 = '24ffffa0537ae9a9483bf36596ec760d8f52305e2daf9d7140c2097ab4e62412'
    }
)

Add-Type -AssemblyName System.Windows.Forms

function Select-Ogg([string]$Title) {
    $dialog = New-Object System.Windows.Forms.OpenFileDialog
    $dialog.Title = "SIEGE 0.6.3 - Selecciona: $Title"
    $dialog.Filter = 'OGG audio (*.ogg)|*.ogg|Todos los archivos (*.*)|*.*'
    $dialog.Multiselect = $false
    if ($dialog.ShowDialog() -ne [System.Windows.Forms.DialogResult]::OK) {
        throw "Cancelado al seleccionar $Title"
    }
    return $dialog.FileName
}

Write-Host ''
Write-Host '==================================================' -ForegroundColor Cyan
Write-Host ' SIEGE 0.6.3 - IMPORTAR MASTERS OGG COMPLETOS' -ForegroundColor Cyan
Write-Host '==================================================' -ForegroundColor Cyan
Write-Host ''
Write-Host 'Seleccionaras los cuatro OGG que fueron revisados en ChatGPT.' -ForegroundColor White
Write-Host 'El script verifica SHA-256 antes de tocar el repositorio.' -ForegroundColor DarkGray
Write-Host ''

New-Item -ItemType Directory -Force -Path $TargetDir | Out-Null

foreach ($track in $Tracks) {
    $source = Select-Ogg $track.Label
    $actual = (Get-FileHash -Algorithm SHA256 -LiteralPath $source).Hash.ToLowerInvariant()
    if ($actual -ne $track.Sha256) {
        throw "El archivo seleccionado para '$($track.Label)' no es el master verificado. SHA esperado: $($track.Sha256) | SHA recibido: $actual"
    }

    $destination = Join-Path $TargetDir $track.Target
    Copy-Item -LiteralPath $source -Destination $destination -Force
    Write-Host "[OK] $($track.Label) -> $($track.Target)" -ForegroundColor Green
}

Push-Location $RepoRoot
try {
    if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
        throw 'Git no esta instalado o no esta disponible en PATH.'
    }

    $current = (git branch --show-current).Trim()
    if ($current -ne $Branch) {
        throw "Este importador debe ejecutarse desde la branch $Branch. Branch actual: $current"
    }

    git status --short
    git add -- assets-source/music-full/tale_cruel_world.ogg `
              assets-source/music-full/darkest_of_days.ogg `
              assets-source/music-full/kaptain_music_box.ogg `
              assets-source/music-full/heavens_hell_sent_gift.ogg

    $changes = git diff --cached --name-only
    if (-not $changes) {
        Write-Host 'Los cuatro masters verificados ya estaban cargados.' -ForegroundColor Yellow
    } else {
        git commit -m 'audio: restore four clean owner soundtrack masters'
        if ($LASTEXITCODE -ne 0) { throw 'No se pudo crear el commit de los masters.' }
    }

    git push origin $Branch
    if ($LASTEXITCODE -ne 0) { throw "No se pudo subir $Branch a GitHub." }

    if ($PublishMain) {
        git fetch origin main
        if ($LASTEXITCODE -ne 0) { throw 'No se pudo actualizar origin/main.' }

        git merge-base --is-ancestor origin/main HEAD
        if ($LASTEXITCODE -ne 0) {
            throw 'main avanzo mientras trabajabamos. No se publicara automaticamente para evitar sobrescribir cambios.'
        }

        Write-Host ''
        Write-Host 'Publicando 0.6.3 en main. GitHub Actions validara hashes, duraciones, Vorbis y Forge.' -ForegroundColor Cyan
        git push origin HEAD:main
        if ($LASTEXITCODE -ne 0) { throw 'No se pudo publicar en main.' }
    }

    Write-Host ''
    Write-Host 'Masters completos cargados correctamente.' -ForegroundColor Green
    if ($PublishMain) {
        Write-Host 'Ahora espera a que GitHub Actions publique dist/siege-menu-0.6.3.jar.' -ForegroundColor Green
    } else {
        Write-Host "Quedaron en $Branch listos para CI/merge." -ForegroundColor Green
    }
}
finally {
    Pop-Location
}
