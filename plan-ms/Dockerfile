FROM openjdk:17-oracle
EXPOSE 8083
ARG JAR_FILE=build/libs/plan-ms-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} plan.jar
ENTRYPOINT ["java", "-jar","/plan.jar"]