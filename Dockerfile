FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=builder /app/target/newsaggregator_backend-jar-with-dependencies.jar .
COPY --from=builder /app/target/classes/rssdata/ ./src/main/resources/rssdata/
COPY --from=builder /app/target/classes/data.json ./src/main/resources/data.json
COPY --from=builder /app/target/classes/log4j.properties ./src/main/resources/log4j.properties
EXPOSE 8080
CMD ["java", "-jar", "newsaggregator_backend-jar-with-dependencies.jar"]
