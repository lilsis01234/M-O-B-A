param(
  [switch]$Clean
)

$ErrorActionPreference = "Stop"

$moduleName = (Get-ChildItem -Path $PSScriptRoot -Filter *.iml | Select-Object -First 1).BaseName
if (-not $moduleName) { throw "Could not determine module name (.iml not found)." }

$outDir = Join-Path $PSScriptRoot ("out\production\" + $moduleName)

if ($Clean) {
  if (Test-Path $outDir) { Remove-Item -Recurse -Force $outDir }
  $legacyOut = Join-Path $PSScriptRoot "out\classes"
  if (Test-Path $legacyOut) { Remove-Item -Recurse -Force $legacyOut }
  # Safety: never keep build artifacts in sources
  Get-ChildItem -Path (Join-Path $PSScriptRoot "src") -Recurse -Filter "*.class" -ErrorAction SilentlyContinue |
    Remove-Item -Force -ErrorAction SilentlyContinue
}

New-Item -ItemType Directory -Force -Path $outDir | Out-Null

$sources = Get-ChildItem -Path (Join-Path $PSScriptRoot "src") -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
if (-not $sources -or $sources.Count -eq 0) { throw "No Java sources found under src/." }

# Build classpath: all jars in lib directory
$classpath = (Get-ChildItem -Path (Join-Path $PSScriptRoot "lib") -Filter "*.jar" | ForEach-Object { $_.FullName }) -join ';'

if (-not $classpath) {
    throw "No JAR files found in lib/ directory. Please ensure required libraries are present."
}

# Compile all Java sources with all jars in classpath
Write-Host "Using classpath: $classpath"
javac -encoding UTF-8 -cp $classpath -d $outDir @sources

Write-Host "Compiled to $outDir"

