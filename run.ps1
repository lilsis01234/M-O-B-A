$ErrorActionPreference = "Stop"

$moduleName = (Get-ChildItem -Path $PSScriptRoot -Filter *.iml | Select-Object -First 1).BaseName
if (-not $moduleName) { throw "Could not determine module name (.iml not found)." }

$outDir = Join-Path $PSScriptRoot ("out\production\" + $moduleName)
if (-not (Test-Path $outDir)) {
  & (Join-Path $PSScriptRoot "build.ps1")
}

# Build classpath: all jars in lib + compiled classes
$libJars = (Get-ChildItem -Path (Join-Path $PSScriptRoot "lib") -Filter "*.jar" | ForEach-Object { $_.FullName }) -join ';'
$classpath = $outDir
if ($libJars) {
    $classpath = $outDir + ';' + $libJars
}

java -cp $classpath Main.Main

