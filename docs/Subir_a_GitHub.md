# Subir el proyecto a GitHub conservando el historial

El proyecto ya contiene un repositorio Git local y cuatro commits.

1. Crear en GitHub un repositorio **vacío** (sin README, licencia ni .gitignore).
2. Desde la carpeta del proyecto ejecutar:

```bash
git remote add origin https://github.com/USUARIO/REPOSITORIO.git
git push -u origin main
```

3. Comprobar en GitHub que aparezcan los commits.
4. Cada integrante debe realizar sus cambios finales mediante nuevos commits para que el historial refleje el trabajo del equipo.

Para revisar el historial local:

```bash
git log --oneline --decorate
```
