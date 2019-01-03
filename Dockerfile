# make sure before this MySql is up and running 
# Start with a base image containing Java runtime
FROM openjdk:8-jdk-alpine
#FROM java:8

# Add Maintainer Info
LABEL maintainer="challah"

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8181 available to the world outside this container
#EXPOSE 8082

# The application's jar file
ARG JAR_FILE=/home/vsts/.m2/repository/com/example/demo/0.0.1-SNAPSHOT/demo-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} demo-0.0.1-SNAPSHOT.jar

# Run the jar file 
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=container","-jar","/demo-0.0.1-SNAPSHOT.jar"]
