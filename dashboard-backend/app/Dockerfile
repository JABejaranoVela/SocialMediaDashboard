# ---------- STAGE 1: Build (Maven + JDK 21) ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copiamos primero el pom para aprovechar cache de dependencias
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copiamos el código fuente
COPY src ./src

# Compilamos el jar ejecutable de Spring Boot
RUN mvn -DskipTests clean package


# ---------- STAGE 2: Runtime (JRE 21) ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el jar generado (Spring Boot repackage)
COPY --from=build /workspace/target/*.jar /app/app.jar

# Render expone el puerto vía env PORT; Spring lo usará con server.port=${PORT:8080}
EXPOSE 8080

# Arranque
ENTRYPOINT ["java","-jar","/app/app.jar"]
