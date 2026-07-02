# Publicação no GitHub

O repositório público é `brunogp32/Mina-dos-Sons`.

## CI

Cada `push` e `pull_request` executa:

- validação do Gradle Wrapper;
- formatação;
- Detekt;
- auditoria de português de Portugal;
- testes unitários;
- cobertura Kover;
- Android Lint;
- build debug para QA.

Os relatórios são enviados como artefactos de diagnóstico. A APK debug não é uma release pública.

## Secrets obrigatórios para release

Configura estes secrets em GitHub Actions antes de criar a tag final:

- `ANDROID_KEYSTORE_BASE64`;
- `ANDROID_KEYSTORE_PASSWORD`;
- `ANDROID_KEY_ALIAS`;
- `ANDROID_KEY_PASSWORD`.

A keystore deve ser uma chave duradoura de produção, guardada fora do Git e com cópia de segurança segura.
Sem estes secrets, o workflow de release deve falhar e não publicar APK debug.

## Criar keystore de produção no Windows

Não cries a keystore dentro do repositório e não faças commit de ficheiros `.jks`, `.keystore`, `.p12`,
`.pem`, Base64 ou ficheiros com passwords.

Executa localmente:

```powershell
.\tools\create-release-keystore.ps1
```

O script:

- localiza o `keytool` do JDK;
- pede o caminho de destino fora do repositório;
- usa o alias `mina-dos-sons-release`;
- cria uma chave RSA de 4096 bits com validade de 30 anos;
- pede passwords sem as mostrar no terminal;
- mostra o comando para consultar o certificado público.

Depois converte a keystore para Base64 fora do repositório:

```powershell
.\tools\keystore-to-base64.ps1 -KeystorePath "C:\caminho\seguro\mina-dos-sons-release.jks"
```

O ficheiro temporário gerado deve ser copiado manualmente para o secret `ANDROID_KEYSTORE_BASE64` e apagado a seguir.

Valores a configurar manualmente em GitHub Actions Secrets:

- `ANDROID_KEYSTORE_BASE64`: conteúdo Base64 gerado pelo script;
- `ANDROID_KEYSTORE_PASSWORD`: password da keystore;
- `ANDROID_KEY_ALIAS`: `mina-dos-sons-release`;
- `ANDROID_KEY_PASSWORD`: password da chave.

## Criar release assinada

Nota sobre compatibilidade: a APK 1.0.0 publicada foi verificada em
[SIGNING_AUDIT_V1.0.0.md](SIGNING_AUDIT_V1.0.0.md) e parece estar assinada com chave Android Debug. Uma nova
chave de produção para 1.1.0 é a decisão correta, mas não permitirá atualização direta sobre a instalação 1.0.0.
A release final deve avisar que pode ser necessário desinstalar a versão antiga.

Depois de o PR passar no CI e ser integrado:

```powershell
git checkout main
git pull
git tag v1.1.0
git push origin v1.1.0
```

O workflow `.github/workflows/android-release.yml` valida a versão, corre os testes, constrói `release`,
assina a APK/AAB, verifica a assinatura, gera checksums e cria a GitHub Release.

Nome esperado da APK:

```text
Mina-dos-Sons-v1.1.0.apk
```

Não publiques manualmente builds debug, ficheiros locais, gravações, keystores ou tokens.
