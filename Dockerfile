FROM openjdk:17-jdk-alpine as builder

RUN mkdir sources
COPY . /sources
WORKDIR /sources
RUN ./gradlew shadowJar

FROM eclipse-temurin:17-jre-alpine

ENV APPLICATION_USER ktor
RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY --from=builder /sources/build/libs/* /app
WORKDIR /app

CMD ["java", "-jar", "lis-polarny-proxy-popularny-all.jar"]