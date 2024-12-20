# Use an OpenJDK image as the base
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle /app/
COPY src src

# Grant execute permissions to the Gradle wrapper
RUN chmod +x gradlew

# Build the application
RUN ./gradlew bootJar

# Expose the application's port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "build/libs/courier-geolocation-tracker-0.0.1-SNAPSHOT.jar"]
