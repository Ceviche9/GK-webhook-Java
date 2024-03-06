FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
COPY . .

RUN apt-get install maven -y
RUN mvn clean install
RUN mvn package

FROM openjdk:17-jdk-slim

EXPOSE 8080

COPY --from=build /target/GK-webhook-Public-0.0.1-SNAPSHOT.jar.original app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]