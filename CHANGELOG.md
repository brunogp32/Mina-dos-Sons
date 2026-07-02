# Registo de Alterações

## 1.1.0

- Substituído o wrapper personalizado pelo Gradle Wrapper oficial 8.10.2, com `gradlew` executável no Git.
- Recriados os workflows de CI, release e submissão de dependências com Actions oficiais fixadas por SHA.
- Adicionadas verificações de formatação, Detekt, Kover, lint e auditoria de português de Portugal.
- Corrigida a coordenação de áudio dos exercícios para parar TTS, áudio de referência e reprodução de gravações ao avançar.
- Protegidas gravações locais em `noBackupFilesDir/recordings`, com ficheiro temporário e substituição apenas após gravação concluída.
- Excluídas gravações de backup e transferência através das regras Android suportadas.
- Atualizada a documentação de instalação, privacidade, CI e publicação assinada.

## 1.0.0

- Removido o sistema visual anterior de peças montáveis.
- Criada a coleção “Cartas dos Sons” com 500 cartas.
- Baús passam a entregar cartas e duplicados são convertidos em diamantes.
- Adicionados os áudios reais S, Z, X/CH e J em `res/raw`.
- Removida a gravação/substituição de sons modelo no Modo Pais.
- Reformulado o treino “Sente a garganta”.
- Mantidos progresso, diamantes, gravações, pares personalizados e histórico.
- Preparação pública para GitHub em português.
