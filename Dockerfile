FROM openjdk:8-jre-buster

COPY target/rest-*.jar /app.jar

EXPOSE 8080
