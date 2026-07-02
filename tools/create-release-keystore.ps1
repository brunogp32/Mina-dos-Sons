param()

$ErrorActionPreference = "Stop"

function Resolve-Keytool {
    if ($env:JAVA_HOME) {
        $candidate = Join-Path $env:JAVA_HOME "bin\keytool.exe"
        if (Test-Path $candidate) {
            return $candidate
        }
    }

    $command = Get-Command keytool.exe -ErrorAction SilentlyContinue
    if ($command) {
        return $command.Source
    }

    throw "keytool.exe nao foi encontrado. Instale um JDK e defina JAVA_HOME."
}

function ConvertTo-PlainText([securestring] $secure) {
    $ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
    try {
        [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr)
    } finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr)
    }
}

function Assert-OutsideRepository([string] $path) {
    $repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
    $targetDirectory = Split-Path -Parent $path
    if (-not (Test-Path $targetDirectory)) {
        throw "A pasta de destino nao existe: $targetDirectory"
    }
    $resolvedDirectory = Resolve-Path $targetDirectory
    if ($resolvedDirectory.Path.StartsWith($repoRoot.Path, [StringComparison]::OrdinalIgnoreCase)) {
        throw "Escolha uma pasta fora do repositorio: $($repoRoot.Path)"
    }
}

$keytool = Resolve-Keytool
$destination = Read-Host "Caminho completo da keystore fora do repositorio"
if ([string]::IsNullOrWhiteSpace($destination)) {
    throw "Caminho vazio."
}

$destination = [IO.Path]::GetFullPath($destination)
Assert-OutsideRepository $destination

if (Test-Path $destination) {
    throw "O ficheiro ja existe. Escolha outro caminho para evitar substituir uma chave."
}

$storePasswordSecure = Read-Host "Password da keystore" -AsSecureString
$keyPasswordSecure = Read-Host "Password da chave" -AsSecureString
$storePassword = ConvertTo-PlainText $storePasswordSecure
$keyPassword = ConvertTo-PlainText $keyPasswordSecure

try {
    & $keytool `
        -genkeypair `
        -v `
        -keystore $destination `
        -storetype JKS `
        -alias "mina-dos-sons-release" `
        -keyalg RSA `
        -keysize 4096 `
        -validity 10950 `
        -dname "CN=Mina dos Sons, OU=Release, O=Mina dos Sons, L=Lisboa, ST=Lisboa, C=PT" `
        -storepass $storePassword `
        -keypass $keyPassword
} finally {
    $storePassword = $null
    $keyPassword = $null
    [GC]::Collect()
}

Write-Host ""
Write-Host "Keystore criada fora do repositorio:"
Write-Host $destination
Write-Host ""
Write-Host "Alias a usar no GitHub Actions Secret ANDROID_KEY_ALIAS:"
Write-Host "mina-dos-sons-release"
Write-Host ""
Write-Host "Para consultar o certificado publico:"
Write-Host "`"$keytool`" -list -v -keystore `"$destination`" -alias mina-dos-sons-release"
