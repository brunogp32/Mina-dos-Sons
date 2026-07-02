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

## Criar release assinada

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
