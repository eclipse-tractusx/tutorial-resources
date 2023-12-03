# Use the official OpenJDK 17 image as the base image
FROM openjdk:17

# Set the working directory
WORKDIR /app

# Copy your JAR file from the local build context to the image
COPY build/libs/backend-service.jar /app/backend-service.jar

# Expose the port your application is running on
EXPOSE 8080

# Start your Java application
CMD java -jar backend-service.jar
