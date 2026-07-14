# Use the official Microsoft Playwright Java image
# This image contains Java, Maven, and all required browser binaries (Chromium, Firefox, WebKit)
FROM mcr.microsoft.com/playwright/java:v1.44.0-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml first to download dependencies (improves Docker caching)
COPY pom.xml .

# Download dependencies (this step will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy the actual test source code into the container
COPY src ./src

# Set an environment variable so the tests know they are running in CI/Docker
ENV CI=true

# Command to run when the container starts
CMD ["mvn", "clean", "test"]
