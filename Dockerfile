FROM maven:3.9.5-amazoncorretto-21 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean install -DskipTests

FROM amazoncorretto:21-alpine-jdk

ARG JAR_FILE=/app/target/*.jar

COPY --from=build $JAR_FILE app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080