FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY src/ src/
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests spotless:check package \
    && cp target/grid-manager-back-*.jar /grid-manager.jar

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /grid-manager.jar ./grid-manager.jar
COPY render-start.sh ./render-start.sh
USER 10001:10001
EXPOSE 10000
ENTRYPOINT ["/bin/sh", "/app/render-start.sh"]
