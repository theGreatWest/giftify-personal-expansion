# ================================
# 1️⃣ 빌드 스테이지
# ================================
FROM gradle:8.5-jdk21 AS builder

# 멀티 모듈 빌드를 위한 루트 디렉토리
WORKDIR /build

# Gradle 기본 설정 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# 모든 모듈 복사
COPY app app
COPY common common
COPY core core
COPY funding funding
COPY infra infra
COPY payment payment
COPY search search
COPY settlement settlement

# gradlew 실행 권한
RUN chmod +x ./gradlew

# 실행 모듈(app)만 빌드
RUN ./gradlew :app:build -x test --no-daemon


# ================================
# 2️⃣ 실행 스테이지
# ================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# app 모듈에서 생성된 실행 JAR만 복사
COPY --from=builder /build/app/build/libs/*.jar app.jar

# Spring Boot 포트
EXPOSE 8080

# prod 프로파일로 실행
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]