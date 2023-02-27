# docker build -t bmg-backend:latest .
# docker run -p8080:8080 bmg-backend:latest
# http://bmgalb-1523887164.us-west-2.elb.amazonaws.com:8080/api/restaurants?postalCode=98125
FROM openjdk:17-oracle
ADD dst .
EXPOSE 8080
ENTRYPOINT ["java","-jar","ApplicationModule-0.0.1-SNAPSHOT.jar"]
FROM openjdk:17-oracle
ADD dst .
EXPOSE 8080
ENTRYPOINT ["java","-jar","ApplicationModule-0.0.1-SNAPSHOT.jar"]