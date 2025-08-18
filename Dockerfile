# Usar imagen base de OpenJDK 21
FROM openjdk:21-jdk-slim

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR del proyecto
COPY target/Documentacion-0.0.1-SNAPSHOT.jar app.jar

# Crear directorio para uploads
RUN mkdir -p /app/uploads

# Exponer puerto
EXPOSE 80

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=80

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"] 