FROM bellsoft/liberica-openjdk-alpine:11
ARG JAR_FILE=/target/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java","-jar","/application.jar"]
EXPOSE 8084