package com.brunogp.minasdossons.data

object LocalGameData {
    val targets =
        listOf(
            SoundTarget(
                "s",
                "SSSS",
                "S",
                "/s/",
                "Cobra",
                "Som da cobra",
                false,
                "🐍",
                "Floresta da Cobra",
                listOf("sapo", "sopa", "saco", "sapato", "sino", "sumo", "selo", "sala", "caça", "doce", "preço"),
            ),
            SoundTarget(
                "z",
                "ZZZZ",
                "Z",
                "/z/",
                "Abelha",
                "Som da abelha",
                true,
                "🐝",
                "Colmeia da Abelha",
                listOf("zero", "zumbido", "zelo", "azeite", "casa", "mesa", "asa", "rosa", "doze", "preso"),
            ),
            SoundTarget(
                "x",
                "CHHHH",
                "X/CH",
                "/ʃ/",
                "Chuva",
                "Som da chuva",
                false,
                "🌧️",
                "Nuvem da Chuva",
                listOf("chuva", "chá", "chave", "chapéu", "chato", "queixo", "peixe", "mochila", "lixo", "caixa"),
            ),
            SoundTarget(
                "j",
                "JJJJ",
                "J",
                "/ʒ/",
                "Joaninha",
                "Som da joaninha",
                true,
                "🐞",
                "Jardim da Joaninha",
                listOf("já", "janela", "jogo", "joaninha", "jardim", "jato", "queijo", "gelo", "girafa"),
            ),
        )

    val minimalPairs =
        listOf(
            MinimalPair("caca-casa", "caça", "casa", "s", "z", "S sem vibração, Z com vibração", true, false),
            MinimalPair("assa-asa", "assa", "asa", "s", "z", "Ouve a voz ligada no Z", true, false),
            MinimalPair("doce-doze", "doce", "doze", "s", "z", "S sem vibração, Z com vibração", true, false),
            MinimalPair("preco-preso", "preço", "preso", "s", "z", "Muda só a voz ligada no meio", true, false),
            MinimalPair("aceite-azeite", "aceite", "azeite", "s", "z", "S sem vibração, Z com vibração", true, false),
            MinimalPair("selo-zelo", "selo", "zelo", "s", "z", "Muda só a voz ligada no início", true, false),
            MinimalPair("roca-rosa", "roça", "rosa", "s", "z", "Compara S e Z entre vogais", true, false),
            MinimalPair("louca-lousa", "louça", "lousa", "s", "z", "Compara S e Z entre vogais", true, false),
            MinimalPair("cacada-casada", "caçada", "casada", "s", "z", "S sem vibração, Z com vibração", true, false),
            MinimalPair("cha-ja", "chá", "já", "x", "j", "Chuva sem vibração, J com vibração", true, false),
            MinimalPair("chato-jato", "chato", "jato", "x", "j", "Muda só o som inicial", true, false),
            MinimalPair("acho-ajo", "acho", "ajo", "x", "j", "Compara X/CH e J", true, false),
            MinimalPair("queixo-queijo", "queixo", "queijo", "x", "j", "X/CH sem vibração, J com vibração", true, false),
            MinimalPair("xis-giz", "xis", "giz", "x", "j", "X/CH sem vibração, J com vibração", true, false),
            MinimalPair("choca-joca", "choca", "Joca", "x", "j", "Joca é um nome próprio", true, false),
            MinimalPair("china-gina", "China", "Gina", "x", "j", "Gina é um nome próprio", true, false),
            MinimalPair("lixeira-ligeira", "lixeira", "ligeira", "x", "j", "Compara X/CH e J no meio da palavra", true, false),
        )

    val stickers =
        listOf(
            "Cobra construtora",
            "Abelha mineira",
            "Nuvem sorridente",
            "Joaninha exploradora",
            "Picareta dourada",
            "Baú de estrelas",
            "Cristal do S",
            "Cristal do Z",
            "Cristal do X",
            "Cristal do J",
            "Portal dos sons",
            "Castelo completo",
        )

    val phraseHints =
        mapOf(
            "caça" to "O gato foi à ____.",
            "casa" to "Vou para ____.",
            "assa" to "Ele ____ o frango.",
            "asa" to "A ave tem uma ____.",
            "doce" to "O bolo está ____.",
            "doze" to "O relógio marcou ____.",
            "preço" to "Qual é o ____?",
            "preso" to "O cinto ficou ____.",
            "aceite" to "Por favor, ____ o convite.",
            "azeite" to "Ponho ____ na salada.",
            "selo" to "A carta leva um ____.",
            "zelo" to "Ele cuida com ____.",
            "roça" to "A família trabalha na ____.",
            "rosa" to "A flor é uma ____.",
            "louça" to "Lavei a ____.",
            "lousa" to "Escrevi na ____.",
            "caçada" to "A ____ foi longa.",
            "casada" to "A minha tia é ____.",
            "chá" to "Bebi ____ quente.",
            "já" to "Eu ____ sei ler.",
            "chato" to "O filme ficou ____.",
            "jato" to "O avião a ____ é rápido.",
            "acho" to "Eu ____ que sim.",
            "ajo" to "Eu ____ com calma.",
            "queixo" to "Bati com o ____.",
            "queijo" to "Comi pão com ____.",
            "xis" to "Marquei com um ____.",
            "giz" to "A professora escreve com ____.",
            "choca" to "A galinha ____ os ovos.",
            "Joca" to "O meu amigo chama-se ____.",
            "China" to "A ____ fica longe.",
            "Gina" to "A menina chama-se ____.",
            "lixeira" to "O papel vai para a ____.",
            "ligeira" to "A mochila está ____.",
        )

    fun target(id: String): SoundTarget = targets.first { it.id == id }

    fun targetsForWorld(world: Int): List<SoundTarget> = when (world) {
        1 -> listOf(target("s"), target("z"))
        2 -> listOf(target("z"), target("s"))
        3 -> listOf(target("x"), target("j"))
        4 -> listOf(target("j"), target("x"))
        5 -> listOf(target("s"), target("z"))
        6 -> listOf(target("x"), target("j"))
        7 -> targets
        8 -> targets
        9 -> targets
        else -> targets
    }
}
