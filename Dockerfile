# ---- Build stage ----
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x gradlew

COPY src src

RUN ./gradlew bootJar --no-daemon


# ---- Runtime stage ----
FROM eclipse-temurin:25-jre

WORKDIR /app

RUN useradd \
    --system \
    --create-home \
    --uid 10001 \
    appuser

COPY --from=build /app/build/libs/*.jar app.jar

RUN chown appuser:appuser app.jar

USER appuser

EXPOSE 10000

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]