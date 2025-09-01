# Stage 1: Build jar
FROM gradle:8.4.1-jdk17 AS build
WORKDIR /home/gradle/project
COPY . .
RUN gradle clean build -x test

# Stage 2: Run jar
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/noteShield-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
