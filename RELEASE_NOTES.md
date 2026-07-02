# Mina dos Sons 1.1.0

Versão de endurecimento técnico, privacidade e CI/CD.

## Destaques

- Corrige o erro de GitHub Actions `exit code 126` ao usar o Gradle Wrapper oficial.
- Atualiza workflows para Actions oficiais recentes, fixadas por SHA, sem avisos de Node.js 20.
- Adiciona CI com formatação, Detekt, testes unitários, cobertura, lint e build debug.
- Para áudio antigo dos exercícios ao avançar, incluindo TTS, sons de referência e reprodução de gravações.
- Guarda gravações em área local sem backup, com migração segura das gravações antigas.
- Mantém a aplicação offline, sem `INTERNET`, conta, anúncios, analytics, backend ou compras reais.

## Instalação

Descarrega a APK anexada à release e abre-a no Android. Se o telemóvel pedir autorização para instalar de fonte desconhecida, permite apenas para o navegador ou gestor de ficheiros usado para abrir o APK.

## Assinatura

A APK final desta versão deve ser uma build `release` assinada com a chave de produção configurada nos secrets do GitHub. Não publiques uma build `debug` como release final.
