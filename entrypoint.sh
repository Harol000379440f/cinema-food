#!/bin/sh
set -e

echo "Starting application in environment: $APP_ENV"

# Seleccionar el archivo JAR basado en el ambiente
case "$APP_ENV" in
  DEV)
    JAR_FILE="/app/build/libs/$JAR_DEV"
    ;;
  QA)
    JAR_FILE="/app/build/libs/$JAR_QA"
    ;;
  PROD)
    JAR_FILE="/app/build/libs/$JAR_PROD"
    ;;
  *)
    echo "Error: APP_ENV debe ser uno de: DEV, QA, PROD"
    exit 1
    ;;
esac

# Validar que el archivo JAR exista
if [ ! -f "$JAR_FILE" ]; then
  echo "Error: No se encontró el archivo JAR para el ambiente $APP_ENV en $JAR_FILE"
  exit 1
fi

echo "Using JAR file: $JAR_FILE"

# Ejecutar la aplicación
exec java -jar "$JAR_FILE" --spring.config.additional-location=/app/config/application.properties

