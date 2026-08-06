# Build Stage — limit Maven memory so Render's 512MB free tier doesn't OOM
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies first (only re-runs when pom.xml changes)
COPY pom.xml .
RUN mvn dependency:go-offline -B -q

# Build the application
COPY src ./src
RUN MAVEN_OPTS="-Xmx256m -Xms64m" mvn clean package -DskipTests -B -q

# Runtime Stage — slim JRE image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/foodwings.jar app.jar
EXPOSE 8080

# Start with memory limits appropriate for Render's free tier (512MB)
ENTRYPOINT ["java", \
  "-Xmx384m", \
  "-Xms64m", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.net.preferIPv4Stack=true", \
  "-jar", "app.jar"]
