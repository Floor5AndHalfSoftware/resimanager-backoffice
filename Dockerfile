FROM eclipse-temurin:21-jdk-alpine
COPY target/resimanager-backoffice-1.0-SNAPSHOT.jar app.jar
EXPOSE 5000
ENTRYPOINT ["java","-jar","/app.jar"]