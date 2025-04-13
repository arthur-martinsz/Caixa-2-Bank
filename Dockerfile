FROM eclipse-temurin:17-jdk as builder
WORKDIR /workspace
COPY . .
RUN ./mvnw clean package -DskipTests  # Linha crucial!

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]