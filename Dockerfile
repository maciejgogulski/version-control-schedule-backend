# Stage 1: Build the application
FROM gradle:7.6-jdk17 AS dependencies
WORKDIR /app
COPY build.gradle settings.gradle /app/
RUN gradle --no-daemon dependencies

# Stage 2: Copy the application source code and build the application
FROM dependencies AS builder
COPY . .
RUN gradle --no-daemon clean build -x test

# Stage 3: Create a minimal image with just the JAR file
FROM amazoncorretto:17
WORKDIR /app
COPY --from=builder /app/build/libs/version-control-schedule-0.0.1-SNAPSHOT.jar app/app.jar
RUN chmod +rx app/app.jar

ENV SPRING_PROFILES_ACTIVE=docker-local

EXPOSE 8080
CMD ["java", "-jar", "app/app.jar"]
