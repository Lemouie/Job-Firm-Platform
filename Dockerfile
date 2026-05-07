# ============================================
# Job-Firm Platform - Multi-stage Docker Build
# ============================================
# Build with: docker build --build-arg MODULE_NAME=job-firm-gateway -t job-firm-gateway .
#
# Available MODULE_NAME values:
#   job-firm-gateway, job-firm-auth, job-firm-user-service,
#   job-firm-firm-service, job-firm-job-service, job-firm-order-service,
#   job-firm-payment-service, job-firm-recommend-search-service,
#   job-firm-admin-service, job-firm-archive-service
# ============================================

# ----------------------------
# Stage 1: Build
# ----------------------------
FROM maven:3.9-eclipse-temurin-17 AS builder

ARG MODULE_NAME
ARG MAVEN_OPTS="-Xmx1024m"

# Validate required build arg
RUN test -n "$MODULE_NAME" || (echo "ERROR: MODULE_NAME build arg is required" && exit 1)

WORKDIR /build

# Copy Maven wrapper and POM files first for dependency caching
COPY pom.xml ./
COPY job-firm-common/pom.xml ./job-firm-common/pom.xml
COPY job-firm-infrastructure/pom.xml ./job-firm-infrastructure/pom.xml
COPY job-firm-api/pom.xml ./job-firm-api/pom.xml
COPY job-firm-gateway/pom.xml ./job-firm-gateway/pom.xml
COPY job-firm-auth/pom.xml ./job-firm-auth/pom.xml
COPY job-firm-user-service/pom.xml ./job-firm-user-service/pom.xml
COPY job-firm-firm-service/pom.xml ./job-firm-firm-service/pom.xml
COPY job-firm-job-service/pom.xml ./job-firm-job-service/pom.xml
COPY job-firm-order-service/pom.xml ./job-firm-order-service/pom.xml
COPY job-firm-payment-service/pom.xml ./job-firm-payment-service/pom.xml
COPY job-firm-recommend-search-service/pom.xml ./job-firm-recommend-search-service/pom.xml 2>/dev/null || true
COPY job-firm-admin-service/pom.xml ./job-firm-admin-service/pom.xml 2>/dev/null || true
COPY job-firm-archive-service/pom.xml ./job-firm-archive-service/pom.xml 2>/dev/null || true

# Resolve dependencies (cached layer)
RUN mvn dependency:go-offline -pl ${MODULE_NAME} -am -B -q || true

# Copy all source code
COPY job-firm-common/src ./job-firm-common/src
COPY job-firm-infrastructure/src ./job-firm-infrastructure/src
COPY job-firm-api/src ./job-firm-api/src
COPY job-firm-gateway/src ./job-firm-gateway/src 2>/dev/null || true
COPY job-firm-auth/src ./job-firm-auth/src 2>/dev/null || true
COPY job-firm-user-service/src ./job-firm-user-service/src 2>/dev/null || true
COPY job-firm-firm-service/src ./job-firm-firm-service/src 2>/dev/null || true
COPY job-firm-job-service/src ./job-firm-job-service/src 2>/dev/null || true
COPY job-firm-order-service/src ./job-firm-order-service/src 2>/dev/null || true
COPY job-firm-payment-service/src ./job-firm-payment-service/src 2>/dev/null || true
COPY job-firm-recommend-search-service/src ./job-firm-recommend-search-service/src 2>/dev/null || true
COPY job-firm-admin-service/src ./job-firm-admin-service/src 2>/dev/null || true
COPY job-firm-archive-service/src ./job-firm-archive-service/src 2>/dev/null || true

# Build the specified module and its dependencies, skip tests
RUN mvn package -pl ${MODULE_NAME} -am -DskipTests -B -q ${MAVEN_OPTS}

# Extract the built JAR path (handle version suffix)
RUN ls -la /build/${MODULE_NAME}/target/ && \
    JAR_FILE=$(ls /build/${MODULE_NAME}/target/*.jar 2>/dev/null | head -1) && \
    echo "Built JAR: $JAR_FILE" && \
    test -f "$JAR_FILE" || (echo "ERROR: No JAR built for ${MODULE_NAME}" && exit 1)

# ----------------------------
# Stage 2: Runtime
# ----------------------------
FROM eclipse-temurin:17-jre

ARG MODULE_NAME
ARG USER=appuser
ARG UID=1000

# Create non-root user
RUN groupadd -r ${USER} && useradd -r -g ${USER} -u ${UID} ${USER}

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /build/${MODULE_NAME}/target/*.jar ./app.jar

# Copy wait-for-it script for readiness checks
COPY scripts/wait-for-it.sh /usr/local/bin/wait-for-it.sh
RUN chmod +x /usr/local/bin/wait-for-it.sh

# Set ownership
RUN chown -R ${USER}:${USER} /app

USER ${USER}

# Health check
HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=60s \
    CMD java -jar app.jar --actuator.health || exit 1

EXPOSE 8080

# Default JVM options optimized for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]
