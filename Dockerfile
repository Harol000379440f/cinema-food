# Usar una imagen base de OpenJDK con Java 17
FROM eclipse-temurin:17-jdk-jammy

# Configurar una variable de entorno para seleccionar el ambiente (por defecto: DEV)
ARG ENV=DEV
ENV APP_ENV=${ENV}

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar todos los archivos del proyecto al contenedor
COPY . .

# Dar permisos de ejecución al archivo gradlew
RUN chmod +x /app/gradlew

# Construir el archivo .jar usando Gradle
RUN ./gradlew clean bootJar --no-daemon

# Copiar el archivo de configuración único al contenedor
COPY src/main/resources/application.properties /app/config/application.properties


# Copiar el archivo food.txt al contenedor
COPY src/main/resources/food.txt /app/data/food.txt

# Configurar la variable de entorno PATH_FILE
ENV PATH_FILE=/app/data/food.txt

# Definir la convención de nombres para los archivos .jar según el ambiente
ENV JAR_DEV=cinemafood-0.0.1-SNAPSHOT.jar
ENV JAR_QA=cinemafood-0.0.1-PRERELEASE.jar
ENV JAR_PROD=cinemafood-0.0.1.jar

# Copiar el script Python de sincronización
COPY sync_inventory_to_db.py /app/scripts/sync_inventory_to_db.py

# Instalar Python y dependencias
RUN apt-get update && apt-get install -y \
    cron \
    python3  \
    python3-pip &&  \
    pip3 install psycopg2-binary

# Copiar el script para reiniciar el inventario
COPY reset_inventory.py /app/scripts/reset_inventory.py


# Crear cron job para sincronización (cada minuto) y reinicio (cada hora)
RUN echo "* * * * * python3 /app/scripts/sync_inventory_to_db.py >> /var/log/cron_inventory.log 2>&1" > /etc/cron.d/cron_sync && \
    echo "0 * * * * python3 /app/scripts/reset_inventory.py >> /var/log/reset_inventory.log 2>&1" > /etc/cron.d/cron_reset && \
    chmod 0644 /etc/cron.d/cron_sync /etc/cron.d/cron_reset && \
    crontab -u root -l | cat /etc/cron.d/cron_sync /etc/cron.d/cron_reset | crontab -

RUN chmod 777 /var/log

# Exponer el puerto configurado en Spring Boot
EXPOSE 8082

# Usar un script de entrada para seleccionar el archivo .jar correcto según el ambiente
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# Comando de inicio del contenedor
CMD ["sh", "-c", "service cron start && /app/entrypoint.sh"]

