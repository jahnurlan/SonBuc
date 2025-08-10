FROM openjdk:17-oracle
EXPOSE 9090
ARG JAR_FILE=build/libs/workspace-ms-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} workspace.jar
ENTRYPOINT ["java", "-jar","/workspace.jar"]