FROM java:8-jdk-alpine
COPY /src/main/java/httpwebserver/Server.java /app/httpwebserver/
WORKDIR /app
EXPOSE 8080
CMD javac ./httpwebserver/Server.java && java httpwebserver.Server