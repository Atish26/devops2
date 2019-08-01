# Base Image
FROM  openjdk:8

# Maintainer
MAINTAINER  Amit Vashist <amitvashist7@gmail.com>

# Add the Index File
COPY target/spring-boot-sample-atmosphere-1.4.0.BUILD-SNAPSHOT.jar /usr/src/myapp/atmosphere.jar

# Expose the Service 
EXPOSE 8080

# Working Dir
WORKDIR /usr/src/myapp

# Bring the Apache UP
CMD ["java", "-jar", "atmosphere.jar"]
