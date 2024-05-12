FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=builder /app/target/newsaggregator_backend-jar-with-dependencies.jar .
COPY --from=builder /app/target/classes/ ./src/main/resources/
RUN rm -rf ./src/main/resources/newsaggregator
EXPOSE 8000
CMD ["java", "-jar", "newsaggregator_backend-jar-with-dependencies.jar"]
