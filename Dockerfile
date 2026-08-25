FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copiar POMs primero para cachear dependencias
COPY pom.xml .
COPY resimanager-domain/pom.xml resimanager-domain/pom.xml
COPY resimanager-application/pom.xml resimanager-application/pom.xml
COPY resimanager-infrastructure/pom.xml resimanager-infrastructure/pom.xml
COPY resimanager-rest/pom.xml resimanager-rest/pom.xml
COPY resimanager-bootstrap/pom.xml resimanager-bootstrap/pom.xml

RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -q

# Copiar fuentes de todos los módulos
COPY resimanager-domain resimanager-domain
COPY resimanager-application resimanager-application
COPY resimanager-infrastructure resimanager-infrastructure
COPY resimanager-rest resimanager-rest
COPY resimanager-bootstrap resimanager-bootstrap

RUN --mount=type=cache,target=/root/.m2 \
    mvn package -DskipTests -q

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/resimanager-bootstrap/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
