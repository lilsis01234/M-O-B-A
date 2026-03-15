$ErrorActionPreference = "Stop"

$moduleName = (Get-ChildItem -Path $PSScriptRoot -Filter *.iml | Select-Object -First 1).BaseName
if (-not $moduleName) { throw "Could not determine module name (.iml not found)." }

$outDir = Join-Path $PSScriptRoot ("out\production\" + $moduleName)
if (-not (Test-Path $outDir)) {
  & (Join-Path $PSScriptRoot "build.ps1")
}

java -cp $outDir Main.Main
