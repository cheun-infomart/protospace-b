FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY . .

#実行権限
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar

#実行ステージ
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar


ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]