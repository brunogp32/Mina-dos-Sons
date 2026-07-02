# Ciclo de vida do áudio

A reprodução de áudio dos exercícios passa por `AudioCoordinator`.

O coordenador garante que, antes de iniciar novo áudio:

- cancela a sequência anterior de sons de referência;
- para o som de referência ativo;
- para a reprodução de gravações;
- para o TTS;
- incrementa uma geração interna para impedir que callbacks ou coroutines antigos continuem a tocar.

Os ecrãs de exercício chamam `stopExerciseAudio()` ao avançar pergunta, ao responder, ao sair da composição
e ao navegar para resultados. O gravador também é parado quando o componente de gravação muda de palavra ou
sai da composição.

Os testes unitários em `AudioCoordinatorTest` cobrem cancelamento de sequência e chamadas rápidas de reprodução.
