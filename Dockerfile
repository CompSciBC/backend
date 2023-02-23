FROM openjdk:17-oracle
# COPY dst/ApplicationModule-0.0.1-SNAPSHOT.jar ApplicationModule-0.0.1-SNAPSHOT.jar
# COPY dst/CoreModule-0.0.1-SNAPSHOT.jar CoreModule-0.0.1-SNAPSHOT.jar
# COPY dst/DynamoDBConnectorModule-0.0.1-SNAPSHOT.jar DynamoDBConnectorModule-0.0.1-SNAPSHOT.jar
# COPY dst/EventsPlacesModule-0.0.1-SNAPSHOT.jar EventsPlacesModule-0.0.1-SNAPSHOT.jar
# COPY dst/LocationModule-0.0.1-SNAPSHOT.jar LocationModule-0.0.1-SNAPSHOT.jar
# COPY dst/RestaurantModule-0.0.1-SNAPSHOT.jar RestaurantModule-0.0.1-SNAPSHOT.jar
# COPY dst/WeatherModule-0.0.1-SNAPSHOT.jar WeatherModule-0.0.1-SNAPSHOT.jar
ADD dst .
EXPOSE 8080
ENTRYPOINT ["java","-jar","ApplicationModule-0.0.1-SNAPSHOT.jar"]