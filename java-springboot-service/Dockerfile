# Java Spring Boot Dockerfile
FROM amazoncorretto:21.0.3-al2023
VOLUME /tmp
COPY build/libs/java-springboot-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080
