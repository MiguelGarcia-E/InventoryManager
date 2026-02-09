# -------- Build (Maven + Spring Boot) --------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos pom y bajamos dependencias
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Copiamos el código fuente
COPY src ./src

# Empaquetamos el jar de Spring Boot
RUN mvn -q clean package -DskipTests

# -------- Runtime (JRE liviano, jammy) --------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Usuario no root (versión Debian/Ubuntu)
RUN useradd -r app && chown -R app:app /app
USER app

# Copiamos el jar generado en la etapa de build
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
