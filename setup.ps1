# Setup script - downloads required dependencies
$ErrorActionPreference = "Stop"

$libDir = Join-Path $PSScriptRoot "lib"
if (-not (Test-Path $libDir)) {
    New-Item -ItemType Directory -Path $libDir | Out-Null
}

$sqliteJarUrl = "https://repo1.maven.org/maven2/org/xerial/sqlite/sqlite-jdbc/3.45.2.0/sqlite-jdbc-3.45.2.0.jar"
$sqliteJarPath = Join-Path $libDir "sqlite-jdbc.jar"

if (-not (Test-Path $sqliteJarPath)) {
    Write-Host "Downloading SQLite JDBC driver..."
    try {
        Invoke-WebRequest -Uri $sqliteJarUrl -OutFile $sqliteJarPath
        Write-Host "Downloaded SQLite JDBC driver to $sqliteJarPath"
    } catch {
        Write-Host "Failed to download SQLite JDBC driver automatically."
        Write-Host "Please download manually from: $sqliteJarUrl"
        Write-Host "And save it as: $sqliteJarPath"
    }
} else {
    Write-Host "SQLite JDBC driver already exists."
}

# Check Gson
$gsonJarPath = Join-Path $libDir "gson-2.10.1.jar"
if (-not (Test-Path $gsonJarPath)) {
    Write-Host "Gson library not found. Please download gson-2.10.1.jar and place it in $libDir"
} else {
    Write-Host "Gson library found."
}

Write-Host "`nSetup complete. You can now run .\build.ps1 and .\run.ps1"
