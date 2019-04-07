FROM openjdk:jre-alpine
MAINTAINER "James Zheng"

COPY /build/libs/todo-web-service-exercise-1.0.0.jar \
     /app/todo-web-service-exercise.jar

ENTRYPOINT ["java", "-jar", "/app/todo-web-service-exercise.jar"]

HEALTHCHECK CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1
EXPOSE 8080