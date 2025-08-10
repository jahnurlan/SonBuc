FROM openjdk:17-oracle
EXPOSE 8089
ARG JAR_FILE=build/libs/timer-ms-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} timer.jar
ENTRYPOINT ["java", "-jar","/timer.jar"]