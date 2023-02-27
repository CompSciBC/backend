FROM openjdk:17-oracle
ADD dst .
EXPOSE 8080
ENTRYPOINT ["java","-jar","ApplicationModule-0.0.1-SNAPSHOT.jar"]