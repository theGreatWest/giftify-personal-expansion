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
COPY backend/member backend/member
COPY backend/auth backend/auth
COPY backend/product backend/product
COPY backend/funding backend/funding
COPY backend/order backend/order
COPY backend/payment backend/payment
COPY backend/notification backend/notification
COPY backend/search backend/search

# 3. backend/app 기준으로 Gradle 실행 (frontend task를 조건부로 설정)
RUN ./gradlew :backend:app:dependencies --no-daemon
RUN ./gradlew :backend:app:build -x test --no-daemon

# ---------- runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# 4. .env 내용을 ENV로 정의 (컨테이너 안에서 yml이 읽을 수 있도록)
ENV DOCKER_DB_URL=jdbc:postgresql://43.202.50.86:15432/giftify_db
ENV DB_USERNAME=giftify
ENV DB_PASSWORD=1234

# 빌드된 backend/app JAR만 복사
COPY --from=builder /build/backend/app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]