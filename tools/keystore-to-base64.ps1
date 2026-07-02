param(
    [string] $KeystorePath
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($KeystorePath)) {
    $KeystorePath = Read-Host "Caminho completo da keystore"
}

$resolvedKeystore = Resolve-Path $KeystorePath
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
if ($resolvedKeystore.Path.StartsWith($repoRoot.Path, [StringComparison]::OrdinalIgnoreCase)) {
    throw "A keystore nao deve estar dentro do repositorio: $($repoRoot.Path)"
}

$outputPath = Join-Path ([IO.Path]::GetTempPath()) "mina-dos-sons-keystore-base64.txt"
[Convert]::ToBase64String([IO.File]::ReadAllBytes($resolvedKeystore.Path)) | Set-Content -NoNewline -Encoding ASCII $outputPath

Write-Host ""
Write-Host "Base64 temporario gerado fora do repositorio:"
Write-Host $outputPath
Write-Host ""
Write-Host "Copie manualmente o conteudo deste ficheiro para o GitHub Actions Secret:"
Write-Host "ANDROID_KEYSTORE_BASE64"
Write-Host ""
Write-Host "Depois de criar o secret, apague o ficheiro temporario:"
Write-Host "Remove-Item -LiteralPath `"$outputPath`""
