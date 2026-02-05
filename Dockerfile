# =========================
# 1. Build stage
# =========================
FROM gradle:8.5-jdk21 AS builder

WORKDIR /build

# Gradle 설정 파일 먼저 복사 (캐시 최적화)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# 백엔드 멀티모듈 전체 복사
COPY backend/app app
COPY backend/common common
COPY backend/core core
COPY backend/funding funding
COPY backend/infra infra
COPY backend/payment payment
COPY backend/search search
COPY backend/settlement settlement

# 빌드
RUN ./gradlew :app:build -x test --no-daemon


# =========================
# 2. Runtime stage
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /build/app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]