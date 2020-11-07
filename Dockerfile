FROM openjdk:11
ARG JAR_FILE=com.petdb.server/target/petdb-server-executable.jar
COPY ${JAR_FILE} petdb-server-executable.jar
ENTRYPOINT ["java","-jar","/petdb-server-executable.jar"]
