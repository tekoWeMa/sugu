FROM gradle:7.4-jdk18 AS build
COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src
RUN gradle customFatJar --no-daemon

FROM openjdk:18-jdk

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/sugu-1.0-SNAPSHOT.jar /app/sugu.jar

ENTRYPOINT ["java","-jar","/app/sugu.jar"]