# --- build stage ---
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# --- runtime stage ---
FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /build/target/*.jar app.jar

EXPOSE 9500

ENTRYPOINT ["java", "-jar", "app.jar"]