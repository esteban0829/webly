FROM amazoncorretto:11
WORKDIR /app
EXPOSE 8080
ENV TZ=Asia/Seoul
ARG JAR_FILE=build/libs/*-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /app/app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=production", "/app/app.jar"]
