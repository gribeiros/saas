FROM eclipse-temurin:25-jdk-noble AS builder
WORKDIR /workspace/app

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

FROM eclipse-temurin:25-jre-noble
WORKDIR /app

RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

COPY --from=builder /workspace/app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

