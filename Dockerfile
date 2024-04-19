FROM maven:3.9.6-eclipse-temurin-21

WORKDIR /backend
CMD mvn spring-boot:run
