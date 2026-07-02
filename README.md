# Mina dos Sons

Aplicação Android offline, em português de Portugal, para apoiar crianças no treino dos contrastes:

- S e Z;
- X/CH e J.

## Descarregar APK

**[Descarregar Mina dos Sons para Android](releases/latest/download/Mina%20dos%20Sons-v1.0.0.apk)**

Se o Android avisar que a instalação veio de uma fonte desconhecida:

1. Toca em **Definições** quando o aviso aparecer.
2. Ativa **Permitir desta fonte** para o navegador ou gestor de ficheiros que abriu o APK.
3. Volta ao APK e toca em **Instalar**.
4. Depois de instalar, podes voltar a desativar essa permissão.

O ficheiro não tem compras reais, anúncios, conta ou ligação à internet.

> Treina os sons e completa a tua coleção de cartas.

A aplicação junta exercícios curtos, áudio modelo, gravação da própria voz, treino da vibração da garganta, progresso, diamantes, baús e cartas colecionáveis. As cartas são uma consequência do treino, não o objetivo principal.

Autor: Bruno Pedroso.

Esta aplicação é uma ferramenta educativa de apoio. Não substitui a avaliação, orientação ou intervenção de um terapeuta da fala.

## Funcionalidades

- Exercícios educativos para S/Z e X/CH/J.
- Áudios reais para SSSS, ZZZZ, CHHHH e JJJJ em `app/src/main/res/raw`.
- Gravações da criança guardadas localmente.
- Treino “Sente a garganta” para distinguir sons com e sem vibração.
- Mundos, níveis, estrelas e diamantes.
- Baús de Madeira, Ferro, Ouro e Cristal.
- Coleção “Cartas dos Sons” com 500 cartas geradas por Compose.
- Loja de cartas com compras por diamantes, sem dinheiro real.
- Modo Pais com multiplicação aleatória, pares personalizados e consulta de gravações.

## Privacidade

Não há conta, anúncios, analytics, compras reais nem permissão `INTERNET`. As gravações ficam no dispositivo e não são enviadas. Ver [PRIVACY.md](PRIVACY.md).

## Compilar

Requisitos:

- JDK 17;
- Android Gradle Plugin compatível com o projeto.

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat lintDebug
.\gradlew.bat assembleDebug
```

APK debug:

```text
app/build/outputs/apk/debug/Mina dos Sons.apk
```

Também pode ser copiado para distribuição local com:

```powershell
.\gradlew.bat copyDebugApkToDist
```

Resultado:

```text
dist/Mina dos Sons-v1.0.0.apk
```

## Assets

O APK usa apenas os frames selecionados do `Pixel Chest Pack` e os quatro áudios em `sounds/`.

Ver [ASSET_LICENSES.md](ASSET_LICENSES.md) e [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

## Testes

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat lintDebug
.\gradlew.bat assembleDebug
.\gradlew.bat connectedDebugAndroidTest
python tools/audit_pt_pt.py
```

Checklist manual: [docs/CHECKLIST_TESTE_MANUAL.md](docs/CHECKLIST_TESTE_MANUAL.md).

## Licença

O código original e a documentação estão licenciados sob MIT. Assets de terceiros mantêm as respetivas licenças ou autorizações.
