# Privacidade

Mina dos Sons funciona offline.

- Sem conta.
- Sem anúncios.
- Sem analytics.
- Sem compras reais.
- Sem permissão `INTERNET`.
- As gravações ficam no dispositivo.
- Nenhuma gravação é enviada pela aplicação.
- As novas gravações são guardadas em `noBackupFilesDir/recordings`, uma área local excluída do Auto Backup.
- As regras `backup_rules.xml` e `data_extraction_rules.xml` excluem gravações de backup e transferência suportados pelo Android.
- Gravações antigas em `filesDir/recordings` são migradas localmente para a área sem backup quando a aplicação precisa delas.
- Os dados são removidos ao limpar os dados da aplicação ou ao desinstalar.
- A permissão de microfone é usada apenas para a criança gravar e ouvir a própria voz.

A própria aplicação não transmite dados. O sistema operativo pode oferecer mecanismos gerais de cópia,
restauro ou transferência de dispositivo, mas as gravações foram tecnicamente excluídas pelas regras da
aplicação nas versões Android suportadas.

Esta aplicação é uma ferramenta educativa de apoio. Não substitui a avaliação, orientação ou intervenção de um terapeuta da fala.
