# Publish spring-base-starter to GitHub Packages.
# Requires a GitHub PAT with write:packages (and read:packages).
#
# Usage:
#   $env:GITHUB_ACTOR = "your-github-username"
#   $env:GITHUB_TOKEN = "ghp_xxxxxxxx"
#   .\scripts\deploy-starter.ps1

$ErrorActionPreference = "Stop"

if (-not $env:GITHUB_ACTOR -or -not $env:GITHUB_TOKEN) {
    Write-Host "Set GitHub credentials first:" -ForegroundColor Yellow
    Write-Host '  $env:GITHUB_ACTOR = "your-github-username"'
    Write-Host '  $env:GITHUB_TOKEN = "ghp_your_personal_access_token"'
    exit 1
}

if (-not $env:JAVA_HOME) {
    $machineJavaHome = [Environment]::GetEnvironmentVariable("JAVA_HOME", "Machine")
    if ($machineJavaHome) {
        $env:JAVA_HOME = $machineJavaHome
        $env:Path = "$env:JAVA_HOME\bin;" + $env:Path
    }
}

$repoRoot = Split-Path $PSScriptRoot -Parent
Push-Location $repoRoot
try {
    mvn -pl spring-base-starter clean deploy
    Write-Host "`nPublished com.omobio:spring-base-starter:1.0.0 to GitHub Packages." -ForegroundColor Green
}
finally {
    Pop-Location
}
