FROM gradle:7.4.2-jdk17
WORKDIR /tmp
COPY . .
RUN ./gradlew build
