#
# Build stage
#
FROM maven:3.9.8-amazoncorretto-21-al2023 AS build
COPY . .
RUN mvn clean package

#
# Package stage
#
FROM openjdk:24-slim
COPY --from=build /target/compiler-0.0.1-SNAPSHOT.jar compiler.jar
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","compiler.jar"]