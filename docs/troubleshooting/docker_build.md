# 목표: 백엔드 모듈만 포함한 Docker 이미지 빌드

## 1️⃣ 초기 Docker 빌드 문제

### 문제

Dockerfile 위치가 backend/app/Dockerfile로 되어 있어 상대 경로 기준 파일을 찾지 못함.

### 에러

failed to compute cache key: "/settings.gradle.kts": not found

### 원인
- Docker 컨텍스트 경로 문제 
- 상대 경로 기준으로 파일을 찾지 못함

### 해결
- Dockerfile을 프로젝트 루트(giftify-personal-expansion)로 이동 
- COPY 명령을 절대 경로 기준으로 수정

```dockerfile
COPY backend/app backend/app
COPY backend/common backend/common

```

--- 

## 2️⃣ 멀티모듈 Gradle 의존성 문제

### 문제

Docker 빌드 중 일부 백엔드 모듈이 없어서 Gradle 빌드 실패

### 에러

Configuring project ':common' without an existing directory is not allowed.

### 원인
- Docker 컨테이너 안에서 일부 모듈이 누락되었거나 경로가 잘못됨

### 해결
- 모든 백엔드 모듈을 명시적으로 COPY
```dockerfile
COPY backend/app backend/app
COPY backend/common backend/common
COPY backend/core backend/core
COPY backend/funding backend/funding
COPY backend/infra backend/infra
COPY backend/payment backend/payment
COPY backend/search backend/search
COPY backend/settlement backend/settlement

```
--- 
## 3️⃣ frontend 모듈로 인한 빌드 실패

### 문제

백엔드 app 모듈의 processResources가 frontend 모듈에 의존함

### 에러

Project with path ':frontend' not found

### 원인
- Docker 빌드 환경에는 frontend 모듈이 없음 
- build.gradle.kts에서 항상 :frontend:copyFrontendToApp에 의존

### 해결
- 조건부 처리 추가

```kotlin
tasks.named<ProcessResources>("processResources") {
    val frontendProject = findProject(":frontend")
    
    if (frontendProject != null) {
    dependsOn(":frontend:copyFrontendToApp")
    }
}
```
--- 

## 4️⃣ settings.gradle.kts vs settings.backend.gradle.kts

### 문제
- settings.gradle.kts는 IDE에서 rootProject.name이 보라색으로 인식  
- settings.backend.gradle.kts는 회색으로 표시됨

### 원인
- backend 전용 빌드 설정 파일에는 frontend 모듈이 없음 
- Gradle은 존재하는 프로젝트만 root에 등록

### 해결
- Docker 빌드용 settings.backend.gradle.kts 사용
- backend 모듈만 포함하여 frontend 없이 빌드 가능
- app 모듈의 조건부 frontend 의존 처리와 결합하면 로컬 개발 및 Docker 빌드 모두 지원

--- 

## 5️⃣ Dockerfile 최종 구조

```dockerfile
# ---------- builder ----------
FROM gradle:8.5-jdk21 AS builder
WORKDIR /build

# Gradle 관련 파일 먼저 복사 (캐시 활용)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.backend.gradle.kts settings.gradle.kts
RUN chmod +x gradlew

# 백엔드 멀티모듈 전체 복사
COPY backend/app backend/app
COPY backend/common backend/common
COPY backend/core backend/core
COPY backend/funding backend/funding
COPY backend/infra backend/infra
COPY backend/payment backend/payment
COPY backend/search backend/search
COPY backend/settlement backend/settlement

# 실행 모듈만 기준으로 Gradle 실행
RUN ./gradlew :backend:app:dependencies --no-daemon
RUN ./gradlew :backend:app:build -x test --no-daemon

# ---------- runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/backend/app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]

```

--- 

## 6️⃣ 요약
1.	Dockerfile 위치와 COPY 경로 문제 해결
2.	모든 백엔드 모듈 명시적 COPY
3.	frontend 모듈 의존 조건부 처리
4.	settings.backend.gradle.kts 사용으로 frontend 없이 빌드 가능
5.	Dockerfile에서 백엔드 app만 빌드하도록 구조화
6.	로컬 개발 환경과 Docker 빌드 환경 모두 지원