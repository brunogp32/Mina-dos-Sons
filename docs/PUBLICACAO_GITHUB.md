# Publicação no GitHub

O projeto está preparado para publicação pública.

Antes de publicar:

```powershell
gh auth login
```

Depois, a partir da pasta do projeto:

```powershell
git init
git add -A
git commit -m "Versão 1.0.0 com Cartas dos Sons"
gh repo create Mina-dos-Sons --public --source . --remote origin --push
gh release create v1.0.0 "dist/Mina dos Sons-v1.0.0.apk" --title "Mina dos Sons 1.0.0" --notes-file RELEASE_NOTES.md
```

O APK não fica no Git porque `dist/` e `*.apk` estão no `.gitignore`. O ficheiro é publicado apenas como asset da release.
