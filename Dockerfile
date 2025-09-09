FROM maven:3.9-eclipse-temurin-21-noble AS build

WORKDIR /app

ADD src/ ./src
ADD pom.xml ./

RUN mvn package

ENTRYPOINT ["mvn", "test"]
