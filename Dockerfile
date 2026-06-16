# ── Build stage ───────────────────────────────────────────
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
# Cache deps first
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN ./gradlew --no-daemon dependencies || true
COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test

# ── Runtime stage ─────────────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
# Platform injects PORT; Spring reads ${PORT:8111}
EXPOSE 8111
ENTRYPOINT ["java", "-jar", "app.jar"]
