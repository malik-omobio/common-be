param(
    [Parameter(Mandatory = $true)]
    [string]$Root
)

$mainSrc = Join-Path $Root "src/main/java/com/omobio/springbase"
$testSrc = Join-Path $Root "src/test/java/com/omobio/springbase"
$mainDest = Join-Path $Root "src/main/java/__BASE_PACKAGE_PATH__"
$testDest = Join-Path $Root "src/test/java/__BASE_PACKAGE_PATH__"

if (Test-Path $mainSrc) {
    New-Item -ItemType Directory -Force -Path (Split-Path $mainDest) | Out-Null
    Move-Item $mainSrc $mainDest
    Remove-Item -Recurse -Force (Join-Path $Root "src/main/java/com") -ErrorAction SilentlyContinue
}

if (Test-Path $testSrc) {
    New-Item -ItemType Directory -Force -Path (Split-Path $testDest) | Out-Null
    Move-Item $testSrc $testDest
    Remove-Item -Recurse -Force (Join-Path $Root "src/test/java/com") -ErrorAction SilentlyContinue
}

$appFile = Join-Path $mainDest "SpringBaseApplication.java"
if (Test-Path $appFile) {
    Move-Item $appFile (Join-Path $mainDest "__APPLICATION_CLASS__.java")
}

$testFile = Join-Path $testDest "SpringBaseApplicationTests.java"
if (Test-Path $testFile) {
    Move-Item $testFile (Join-Path $testDest "__APPLICATION_CLASS__Tests.java")
}

$starterPrefixes = @(
    "com.omobio.springbase.common.response",
    "com.omobio.springbase.util.abst",
    "com.omobio.springbase.util.constants",
    "com.omobio.springbase.database.DataSeeder",
    "com.omobio.springbase.database.PermissionCatalog"
)

$files = Get-ChildItem -Path $Root -Recurse -File | Where-Object {
    $_.Extension -in ".java", ".xml", ".properties", ".json", ".yml", ".yaml", ".md"
}

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw -Encoding UTF8
    $original = $content

    $content = $content -replace "com\.omobio\.springbase", "__BASE_PACKAGE__"
    foreach ($prefix in $starterPrefixes) {
        $placeholder = $prefix -replace "com\.omobio\.springbase", "__BASE_PACKAGE__"
        $content = $content -replace [regex]::Escape($placeholder), $prefix
    }

    $content = $content -replace "SpringBaseApplication", "__APPLICATION_CLASS__"
    $content = $content -replace "spring-base", "__APP_NAME__"
    $content = $content -replace "spring_base_db", "__APP_NAME___db"
    $content = $content -replace "<artifactId>service-template</artifactId>", "<artifactId>__ARTIFACT_ID__</artifactId>"
    $content = $content -replace "spring-base-postgres", "__APP_NAME__-postgres"
    $content = $content -replace "spring-base-redis", "__APP_NAME__-redis"

    if ($content -ne $original) {
        [System.IO.File]::WriteAllText($file.FullName, $content)
    }
}
