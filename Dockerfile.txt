# Usar una imagen base de OpenJDK con Java 17
FROM openjdk:17-jdk-slim

# Configurar una variable de entorno para seleccionar el ambiente (por defecto: DEV)
ARG ENV=DEV
ENV APP_ENV=${ENV}

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar todos los archivos del proyecto al contenedor
COPY . .

# Construir el archivo .jar usando Gradle
RUN ./gradlew clean bootJar --no-daemon

# Copiar el archivo de configuración único al contenedor
COPY src/main/resources/application.properties /app/config/application.properties

# Definir la convención de nombres para los archivos .jar según el ambiente
ENV JAR_DEV=cinemafood-0.0.1-SNAPSHOT.jar
ENV JAR_QA=cinemafood-0.0.1-PRERELEASE.jar
ENV JAR_PROD=cinemafood-0.0.1.jar

# Exponer el puerto configurado en Spring Boot
EXPOSE 8082

# Usar un script de entrada para seleccionar el archivo .jar correcto según el ambiente
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]

