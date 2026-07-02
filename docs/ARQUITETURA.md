# Arquitetura

Mina dos Sons é uma aplicação Android em Kotlin e Jetpack Compose.

- `data/cards`: catálogo, modelo e compras de cartas.
- `data/rewards`: baús e transações de diamantes.
- `audio`: reprodução dos sons S/Z/X/J e gravação da voz da criança.
- `game`: geração das perguntas e progresso educativo.
- `ui/screens`: ecrãs principais.
- `ui/cards`: componentes visuais das cartas.

O DataStore preserva as chaves antigas de coleção para migrar itens antigos para cartas, mas a nova versão já não guarda equipamento visual montável.
