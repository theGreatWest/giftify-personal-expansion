# ---------- builder ----------
FROM gradle:8.5-jdk21 AS builder
WORKDIR /build

# 1. Gradle 관련 파일 먼저 복사 (캐시 활용)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.backend.gradle.kts settings.gradle.kts

RUN chmod +x gradlew

# 2. 백엔드 멀티모듈 전체 복사 (frontend 제외)
COPY backend/app backend/app
COPY backend/common backend/common
COPY backend/core backend/core
COPY backend/funding backend/funding
COPY backend/infra backend/infra
COPY backend/payment backend/payment
COPY backend/search backend/search
COPY backend/settlement backend/settlement

# 3. backend/app 기준으로 Gradle 실행 (frontend task를 조건부로 설정)
RUN ./gradlew :backend:app:dependencies --no-daemon
RUN ./gradlew :backend:app:build -x test --no-daemon

# ---------- runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# 빌드된 backend/app JAR만 복사
COPY --from=builder /build/backend/app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]