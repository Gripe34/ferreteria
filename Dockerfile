# 1. Fase de Build: Usamos Maven para compilar el código y generar el JAR
FROM maven:3.9.5-eclipse-temurin-21 AS build

# El WORKDIR dentro del contenedor será /app
WORKDIR /app

# Copiamos el pom.xml y lo instalamos para que descargue dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código fuente
COPY src /app/src

# Compilamos el proyecto para generar el archivo JAR ejecutable
RUN mvn clean install -DskipTests

# 2. Fase de Ejecución: Usamos una imagen ligera de Java para ejecutar solo el JAR
FROM eclipse-temurin:17-jre-alpine

# El JAR compilado en la fase anterior se encuentra en /app/target/
# Lo copiamos a la nueva imagen ligera de ejecución
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto de la aplicación (que Spring usará, definido en application.properties)
EXPOSE 8080

# Comando de inicio: Ejecutamos el JAR
ENTRYPOINT ["java", "-jar", "/app.jar"]