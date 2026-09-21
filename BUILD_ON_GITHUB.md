# Build remoto (sem instalar Java no celular)

Este projeto inclui um workflow para compilar no GitHub Actions usando Java 8 e Gradle 4.10.3.

1. Crie um repositório no GitHub e envie os arquivos deste projeto.
2. Abra a aba **Actions**.
3. Execute **Build Forge 1.12.2 mod** com **Run workflow**.
4. Ao terminar, abra a execução e baixe o artefato **furniture-power-1.12.2**.
5. O arquivo `.jar` gerado pode ser colocado na pasta `mods` do perfil Forge 1.12.2 no PojavLauncher, junto do CFM 6.3.2.

Observação: o workflow depende dos repositórios Maven do ForgeGradle/Forge. Se a infraestrutura externa mudar, o build pode precisar de ajustes.
