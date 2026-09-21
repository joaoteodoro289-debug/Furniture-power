@echo off
if exist gradlew.bat (
  call gradlew.bat clean build
) else (
  echo Gradle/gradlew nao encontrado. Use um ambiente Forge 1.12.2 com ForgeGradle 2.3.
  exit /b 2
)
