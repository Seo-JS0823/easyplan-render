FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean build -x test

ENTRYPOINT ["sh", "-c", "java -jar build/libs/*.jar"]