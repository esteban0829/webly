FROM amazoncorretto:11
WORKDIR /app
EXPOSE 8080

ENV TZ=Asia/Seoul

# default env values
ENV DB_URL=jdbc:postgresql://localhost:5432/webly
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=password

ENV JPA_DB_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
ENV JPA_DDL_AUTO=update

ENV HIBERNATE_FORMAT_SQL=false
ENV HIBERNATE_SHOW_SQL=false

ENV S3_ACCESS_KEY=web-clipboard
ENV S3_SECRET_KEY=password
ENV S3_REGION=us-east-1

ENV S3_BUCKET=web-clipboard
ENV S3_SERVICE_ENDPOINT=http://localhost:9000

ARG JAR_FILE=build/libs/*-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /app/app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=production", "/app/app.jar"]
