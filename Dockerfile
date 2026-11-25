
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/numeric-0.0.1.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
