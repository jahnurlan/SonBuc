FROM openjdk:17-oracle
EXPOSE 8088
ARG JAR_FILE=build/libs/chat-ms-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} chat.jar
ENTRYPOINT ["java", "-jar","/chat.jar"]
