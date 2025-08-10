FROM openjdk:17-oracle
EXPOSE 9999
ARG JAR_FILE=build/libs/admin-ms-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} admin.jar
ENTRYPOINT ["java", "-jar","/admin.jar"]