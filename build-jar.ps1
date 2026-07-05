$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$out = Join-Path $root 'out\classes'
$jar = Join-Path $root 'space_ship.jar'

Remove-Item (Join-Path $root 'out') -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path $out | Out-Null

$javaFiles = Get-ChildItem (Join-Path $root 'src') -Recurse -Filter *.java | ForEach-Object { $_.FullName }
& javac -encoding UTF-8 -d $out $javaFiles
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Copy-Item (Join-Path $root 'resources\*') $out -Recurse -Force
& jar --create --file $jar --main-class src.project.Main -C $out .
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "JAR creado: $jar"
