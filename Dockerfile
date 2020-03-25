FROM openjdk:11-jre-stretch

COPY target/rest-*.jar /app.jar

HEALTHCHECK --interval=30s --timeout=10s --start-period=30s \
  CMD curl -f http://localhost:8080/rest/ || exit 1

EXPOSE 8080
