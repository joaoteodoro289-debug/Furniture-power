#!/usr/bin/env bash
set -euo pipefail
if ! command -v java >/dev/null 2>&1; then echo 'Java nao encontrado.'; exit 1; fi
if ! command -v gradle >/dev/null 2>&1 && [ ! -x ./gradlew ]; then
  echo 'Gradle/gradlew nao encontrado. Use um ambiente Forge 1.12.2 com ForgeGradle 2.3 ou gere o wrapper.'
  exit 2
fi
if [ -x ./gradlew ]; then ./gradlew clean build
else gradle clean build
fi
