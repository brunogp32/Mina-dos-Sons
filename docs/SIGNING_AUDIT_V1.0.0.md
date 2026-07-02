# Auditoria de assinatura da versão 1.0.0

APK analisada:

```text
Mina.dos.Sons-v1.0.0.apk
```

Origem:

```text
https://github.com/brunogp32/Mina-dos-Sons/releases/tag/v1.0.0
```

## SHA-256 da APK

```text
6400919F17F19DC43ADC9FD8B0C59C3EAE4D103F85B85557693280B7E65C307A
```

## Resultado do apksigner

```text
Verified using v1 scheme (JAR signing): false
Verified using v2 scheme (APK Signature Scheme v2): true
Verified using v3 scheme (APK Signature Scheme v3): false
Verified using v3.1 scheme (APK Signature Scheme v3.1): false
Verified using v4 scheme (APK Signature Scheme v4): false
Number of signers: 1
Signer #1 certificate DN: C=US, O=Android, CN=Android Debug
Signer #1 certificate SHA-256 digest: b660b5ee3d4b770acfa18b965008e17c779c111861a8893850ad88b9118db065
Signer #1 key algorithm: RSA
Signer #1 key size (bits): 2048
```

## Decisão

A versão 1.0.0 publicada parece estar assinada com a chave Android Debug. Essa chave não deve ser usada
como chave de produção.

Consequência: uma versão 1.1.0 assinada com uma nova chave de produção não será uma atualização direta da
APK 1.0.0 já instalada. O Android tratará a assinatura como diferente e exigirá desinstalar a versão anterior
antes de instalar a nova.

Como a aplicação não envia dados para servidor e as gravações ficam apenas no dispositivo, a desinstalação pode
remover progresso e gravações locais. Antes de publicar a 1.1.0 final, a release deve avisar claramente sobre
esta incompatibilidade e, se for implementado um mecanismo de exportação local, orientar os pais a exportar os
dados antes de desinstalar.
