# Backend Deployment Architecture

## 목차
- [개요](#개요)
- [프로젝트 구조](#프로젝트-구조)
- [인프라 아키텍처](#인프라-아키텍처)
- [데이터베이스 설정](#데이터베이스-설정)
- [Docker 설정](#docker-설정)
- [CI/CD 파이프라인](#cicd-파이프라인)
- [배포 프로세스](#배포-프로세스)
- [네트워크 및 도메인](#네트워크-및-도메인)

---

## 개요

Giftify 백엔드는 AWS EC2 기반의 Docker 컨테이너 환경에서 운영되며, GitHub Actions를 통한 자동화된 CI/CD 파이프라인으로 배포됩니다. PostgreSQL을 데이터베이스로 사용하고, Nginx Proxy Manager를 통해 HTTPS 트래픽을 처리합니다.

### 기술 스택
- **클라우드**: AWS EC2 (서울 리전)
- **컨테이너**: Docker
- **데이터베이스**: PostgreSQL 16
- **웹 서버**: Nginx Proxy Manager
- **CI/CD**: GitHub Actions
- **레지스트리**: GitHub Container Registry (GHCR)
- **DNS**: DNSZi
- **빌드 도구**: Gradle 8.5 (Kotlin DSL)
- **런타임**: Eclipse Temurin JDK 21

---

## 프로젝트 구조

```
giftify-personal-expansion/
├── frontend/              # 프론트엔드 (Vercel 배포)
│   └── ...
├── backend/               # 백엔드 멀티모듈 (AWS EC2 배포)
│   ├── app/              # 실행 모듈 (Main Application)
│   ├── common/           # 공통 모듈
│   ├── core/             # 핵심 비즈니스 로직
│   ├── funding/          # 펀딩 도메인
│   ├── infra/            # 인프라 계층
│   ├── payment/          # 결제 도메인
│   ├── search/           # 검색 기능
│   └── settlement/       # 정산 도메인
├── build.gradle.kts
├── settings.backend.gradle.kts
├── Dockerfile
└── .github/
    └── workflows/
        └── deploy.yml
```

**특이사항:**
- 프론트엔드와 백엔드가 단일 레포지토리(Monorepo)에 존재
- 백엔드는 JVM 기반 멀티모듈 구조
- `backend/app` 모듈이 전체 애플리케이션의 진입점
- 프론트엔드는 별도로 Vercel을 통해 배포되어 `www.giftify.yjkim.store`에서 서비스
- 백엔드 API는 `api.giftify.yjkim.store`에서 서비스

---

## 인프라 아키텍처

```
┌─────────────────────────────────────────────────────────────┐
│                      AWS EC2 Instance                       │
│                   (43.202.50.86 - 서울)                      │
│                                                             │
│  ┌────────────────────┐  ┌─────────────────────┐            │
│  │ Nginx Proxy        │  │ Application         │            │
│  │ Manager            │  │ Container           │            │
│  │ (Port 80/443)      │→ │ (container-giftify) │            │
│  │                    │  │ Port: 8081→8080     │            │
│  └────────────────────┘  └─────────────────────┘            │
│           ↓                        ↓                         │
│  ┌──────────────────────────────────────────┐                │
│  │ PostgreSQL Container                     │                │
│  │ (postgres-giftify)                       │                │
│  │ Port: 15432→5432                         │                │
│  │ Database: giftify_db                     │                │
│  │ User: giftify                            │                │
│  └──────────────────────────────────────────┘                │
│                                                              │
│  Volume Mounts:                                              │
│  • /dockerProjects/postgres_1/volumes/data                   │
│  • /dockerProjects/giftify/volumes/gen                       │
└───────────────────────────────────────────────────────────────┘
                           ↓
             ┌──────────────────────────┐
             │ DNS (DNSZi)              │
             │ api.giftify.yjkim.store  │
             │ → 43.202.50.86           │
             └──────────────────────────┘
                           ↓
             ┌──────────────────────────┐
             │ HTTPS (Let's Encrypt)    │
             │ SSL 자동 갱신            │
             └──────────────────────────┘
```

---

## 데이터베이스 설정

### 1. PostgreSQL 컨테이너 생성

```bash
docker run -d \
  --name postgres-giftify \
  --restart unless-stopped \
  -p 15432:5432 \
  -e TZ=Asia/Seoul \
  -e POSTGRES_PASSWORD=1234 \
  -v /dockerProjects/postgres_1/volumes/data:/var/lib/postgresql/data \
  postgres:16
```

**포트 설정 이유:**
- 호스트의 5432 포트가 이미 사용 중이므로 15432 포트로 매핑
- 컨테이너 내부에서는 기본 5432 포트 사용

### 2. 데이터베이스 및 사용자 생성

```bash
# PostgreSQL 접속
docker exec -it postgres-giftify psql -U postgres

# SQL 명령어 실행
CREATE DATABASE giftify_db;
CREATE USER giftify WITH PASSWORD '1234';
GRANT ALL PRIVILEGES ON DATABASE giftify_db TO giftify;

# giftify_db로 전환
\c giftify_db

# 스키마 권한 부여
GRANT ALL ON SCHEMA public TO giftify;

# 종료
\q
```

### 3. 연결 정보

| 항목 | 값 |
|------|-----|
| 호스트 | 43.202.50.86 |
| 포트 | 15432 |
| 데이터베이스 | giftify_db |
| 사용자명 | giftify |
| 비밀번호 | 1234 |

### 4. DBeaver 연결 설정

1. 새 연결 > PostgreSQL
2. 호스트: `43.202.50.86`
3. 포트: `15432`
4. 데이터베이스: `giftify_db`
5. 사용자: `giftify`
6. 비밀번호: `1234`
7. 테스트 연결 → 성공

---

## Docker 설정

### Dockerfile (멀티스테이지 빌드)

```dockerfile
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

# 3. backend/app 기준으로 Gradle 실행
RUN ./gradlew :backend:app:dependencies --no-daemon
RUN ./gradlew :backend:app:build -x test --no-daemon

# ---------- runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# 빌드된 backend/app JAR만 복사
COPY --from=builder /build/backend/app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

**특이사항:**
- Frontend 디렉토리는 복사하지 않음 (Vercel에서 별도 배포)
- 멀티모듈 구조에서 `backend/app` 모듈만 빌드
- 멀티스테이지 빌드로 최종 이미지 크기 최소화
- JDK 21 기반으로 빌드 및 실행

### 애플리케이션 설정 파일

**application.yml** (기본 설정)
```yaml
server:
  port: ${SERVER_PORT:8080}

spring:
  application:
    name: giftify
  
  profiles:
    group:
      prod:
        - app-prod
        - core-prod
        - payment-prod
        - funding-prod
        - settlement-prod
        - search-prod
        - infra-prod
        - common-prod

custom:
  prod:
    cookieDomain: giftify.yjkim.store
    frontUrl: "https://www.${custom.prod.cookieDomain}"
    backUrl: "https://api.${custom.prod.cookieDomain}"
```

**application-prod.yml** (운영 환경)
```yaml
spring:
  datasource:
    url: ${DOCKER_DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver

  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: none
    open-in-view: false

logging:
  level:
    root: warn
    org.springframework.transaction: info
```

**환경변수 주입:**
- `DOCKER_DB_URL`: `jdbc:postgresql://43.202.50.86:15432/giftify_db`
- `DB_USERNAME`: `giftify`
- `DB_PASSWORD`: `1234`

---

## CI/CD 파이프라인

### GitHub Actions 워크플로우

**.github/workflows/deploy.yml**

```yaml
name: 'deploy'

on:
  push:
    paths:
      - '.github/workflows/**'
      - 'backend/**'
      - 'build.gradle.kts'
      - 'settings.backend.gradle.kts'
      - 'Dockerfile'
    branches:
      - 'main'

env:
  IMAGE_NAME: giftify-img

jobs:
  makeTagAndRelease:
    runs-on: ubuntu-latest
    outputs:
      tag_name: ${{ steps.create_tag.outputs.new_tag }}
    steps:
      - uses: actions/checkout@v4
      - id: create_tag
        uses: mathieudutour/github-tag-action@v6.1
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
      - uses: actions/create-release@v1
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        with:
          tag_name: ${{ steps.create_tag.outputs.new_tag }}
          release_name: Release ${{ steps.create_tag.outputs.new_tag }}
          body: ${{ steps.create_tag.outputs.changelog }}

  buildImageAndPush:
    needs: makeTagAndRelease
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Docker Buildx 설치
        uses: docker/setup-buildx-action@v2
      - name: 레지스트리 로그인
        uses: docker/login-action@v2
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}
      - name: set lower case owner name
        run: |
          echo "OWNER_LC=${OWNER,,}" >> ${GITHUB_ENV}
        env:
          OWNER: "${{ github.repository_owner }}"
      - name: 빌드 앤 푸시
        uses: docker/build-push-action@v3
        with:
          context: .
          push: true
          tags: |
            ghcr.io/${{ env.OWNER_LC }}/${{ env.IMAGE_NAME }}:${{ needs.makeTagAndRelease.outputs.tag_name }},
            ghcr.io/${{ env.OWNER_LC }}/${{ env.IMAGE_NAME }}:latest

  deploy:
    runs-on: ubuntu-latest
    needs: [ buildImageAndPush ]
    steps:
      - name: set lower case owner name
        run: |
          echo "OWNER_LC=${OWNER,,}" >> ${GITHUB_ENV}
        env:
          OWNER: "${{ github.repository_owner }}"
      - name: AWS SSM Send-Command
        uses: peterkimzz/aws-ssm-send-command@master
        id: ssm
        with:
          aws-region: ${{ secrets.AWS_REGION }}
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          instance-ids: ${{ secrets.AWS_EC2_INSTANCE_ID }}
          working-directory: /
          comment: Deploy Giftify
          command: |
            echo "=== 배포 시작 ==="
            docker stop container-giftify || true
            docker rm container-giftify || true
            docker rmi -f $(docker images "ghcr.io/${{ env.OWNER_LC }}/${{ env.IMAGE_NAME }}" -q) || true
            docker image prune -f || true
            docker pull ghcr.io/${{ env.OWNER_LC }}/${{ env.IMAGE_NAME }}:latest
            docker run -d \
              --name container-giftify \
              --restart unless-stopped \
              -p 8081:8080 \
              -e TZ=Asia/Seoul \
              -e SPRING_PROFILES_ACTIVE=prod \
              -e DOCKER_DB_URL=jdbc:postgresql://43.202.50.86:15432/giftify_db \
              -e DB_USERNAME=giftify \
              -e DB_PASSWORD=1234 \
              -v /dockerProjects/giftify/volumes/gen:/gen \
              ghcr.io/${{ env.OWNER_LC }}/${{ env.IMAGE_NAME }}:latest
            echo "=== 배포 완료 ==="
```

### CI/CD 파이프라인 흐름

```
┌─────────────────────────────────────────────────────────┐
│ 1. Code Push to main branch                             │
│    (backend/** 또는 build.gradle.kts 변경 시 트리거)        │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ 2. makeTagAndRelease Job                                │
│    • 자동 버전 태그 생성 (Semantic Versioning)              │
│    • GitHub Release 자동 생성                             │
│    • Changelog 자동 생성                                  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ 3. buildImageAndPush Job                                │
│    • Docker 이미지 빌드 (멀티스테이지)                        │
│    • GHCR에 이미지 푸시                                    │
│    • 태그: latest, v1.2.3 (버전별)                         │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ 4. deploy Job                                           │
│    • AWS SSM을 통해 EC2에 명령 전송                         │
│    • 기존 컨테이너 중지 및 삭제                               │
│    • 최신 이미지 Pull                                      │
│    • 새 컨테이너 실행 (환경변수 포함)                          │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ 5. Application Running                                  │
│    • https://api.giftify.yjkim.store                    │
│    • 무중단 배포 (컨테이너 교체 방식)                          │
└─────────────────────────────────────────────────────────┘
```

### GitHub Secrets 설정

다음 Secret 변수들이 설정되어 있어야 합니다:

| Secret 이름 | 설명 | 예시 값 |
|------------|------|---------|
| `AWS_REGION` | AWS 리전 | `ap-northeast-2` |
| `AWS_ACCESS_KEY_ID` | AWS IAM 액세스 키 | `AKIA...` |
| `AWS_SECRET_ACCESS_KEY` | AWS IAM 시크릿 키 | `wJa...` |
| `AWS_EC2_INSTANCE_ID` | EC2 인스턴스 ID | `i-0123456789abcdef0` |

**GitHub Actions 권한 설정:**
- Repository Settings → Actions → General
- Workflow permissions: **Read and write permissions** 체크

---

## 배포 프로세스

### 자동 배포 (CI/CD)

1. **코드 변경 후 Push**
   ```bash
   git add .
   git commit -m "feat: 새 기능 추가"
   git push origin main
   ```

2. **GitHub Actions 자동 실행**
    - Actions 탭에서 실행 상태 확인
    - 약 2~3분 소요

3. **배포 완료 확인**
   ```bash
   # EC2에서 확인
   docker ps | grep giftify
   docker logs -f container-giftify
   
   # 브라우저에서 확인
   https://api.giftify.yjkim.store/actuator/health
   ```

### 수동 배포 (긴급 상황)

필요시 EC2에서 직접 배포 가능:

```bash
# EC2 접속
ssh ec2-user@43.202.50.86

# 관리자 권한 전환
sudo su

# 수동 배포 스크립트
docker stop container-giftify
docker rm container-giftify
docker pull ghcr.io/theGreatWest/giftify-img:latest
docker run -d \
  --name container-giftify \
  --restart unless-stopped \
  -p 8081:8080 \
  -e TZ=Asia/Seoul \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DOCKER_DB_URL=jdbc:postgresql://43.202.50.86:15432/giftify_db \
  -e DB_USERNAME=giftify \
  -e DB_PASSWORD=1234 \
  -v /dockerProjects/giftify/volumes/gen:/gen \
  ghcr.io/theGreatWest/giftify-img:latest
```

### 배포 스크립트 (deploy.sh)

```bash
#!/bin/bash

echo "🔄 코드 업데이트 중..."
git pull origin main

echo "🛑 기존 컨테이너 중지..."
docker stop container-giftify
docker rm container-giftify

echo "🏗️  새 이미지 빌드 중..."
docker build -t giftify-img .

echo "🚀 컨테이너 실행 중..."
docker run -d \
  --name container-giftify \
  --restart unless-stopped \
  -p 8081:8080 \
  -e TZ=Asia/Seoul \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DOCKER_DB_URL=jdbc:postgresql://43.202.50.86:15432/giftify_db \
  -e DB_USERNAME=giftify \
  -e DB_PASSWORD=1234 \
  -v /dockerProjects/giftify/volumes/gen:/gen \
  giftify-img:latest

echo "✅ 배포 완료!"
docker logs -f container-giftify
```

---

## 네트워크 및 도메인

### DNS 설정 (DNSZi)

**A 레코드:**
```
호스트: api.giftify
IP: 43.202.50.86
TTL: 600
DDNS: X
```

**최종 도메인:** `api.giftify.yjkim.store`

### Nginx Proxy Manager 설정

**Proxy Host 설정:**

| 항목 | 값 |
|------|-----|
| Domain Names | api.giftify.yjkim.store |
| Scheme | http |
| Forward Hostname/IP | localhost (또는 43.202.50.86) |
| Forward Port | 8081 |
| Block Common Exploits | ✅ |
| Websockets Support | ✅ |

**SSL 설정:**
- SSL Certificate: Request a new SSL Certificate (Let's Encrypt)
- Force SSL: ✅
- HTTP/2 Support: ✅
- HSTS Enabled: ✅
- Email: 관리자 이메일 입력

**결과:**
- HTTP 요청 → HTTPS 자동 리다이렉트
- SSL 인증서 자동 갱신
- `https://api.giftify.yjkim.store` 접속 가능

---

## 특이사항 및 주의사항

### 1. Monorepo 구조

- Frontend와 Backend가 단일 레포지토리에 공존
- GitHub Actions는 `backend/**` 경로 변경 시에만 트리거
- Frontend 변경은 Backend 배포에 영향 없음

### 2. 멀티모듈 빌드

- Gradle 멀티모듈 프로젝트에서 `backend/app` 모듈만 실행
- 다른 모듈들은 라이브러리로 포함됨
- Dockerfile에서 frontend 디렉토리 복사 제외

### 3. 포트 충돌 방지

- PostgreSQL: 15432 포트 사용 (기본 5432 회피)
- Application: 8081 포트 사용 (호스트) → 8080 (컨테이너)

### 4. 환경변수 주입

- 민감 정보는 환경변수로 관리
- Dockerfile에 하드코딩하지 않음
- 런타임 시 `-e` 옵션으로 주입

### 5. 볼륨 마운트

- `/gen` 디렉토리: 파일 업로드/생성 영구 저장
- PostgreSQL 데이터: `/var/lib/postgresql/data` 영구 저장

### 6. DNS 전파 지연

- DNSZi 사용 시 DNS 전파가 10~30분 소요될 수 있음
- 긴급 시 `/etc/hosts` 파일로 우회 가능

### 7. 보안 고려사항

- 데이터베이스 비밀번호는 환경변수로 관리
- GitHub Secrets에 민감 정보 저장
- Nginx Proxy Manager를 통한 HTTPS 강제 적용

---

## 모니터링 및 유지보수

### 로그 확인

```bash
# 실시간 로그
docker logs -f container-giftify

# 최근 100줄
docker logs --tail 100 container-giftify

# 특정 시간대 로그
docker logs --since 2024-01-01T00:00:00 container-giftify
```

### 헬스 체크

```bash
# Actuator health endpoint
curl https://api.giftify.yjkim.store/actuator/health

# HTTP 직접 접근
curl http://43.202.50.86:8081/actuator/health
```

### 디스크 용량 관리

```bash
# 사용하지 않는 이미지 정리
docker image prune -a

# 사용하지 않는 볼륨 정리
docker volume prune

# 전체 시스템 정리
docker system prune -a
```

### 데이터베이스 백업

```bash
# 백업 생성
docker exec postgres-giftify pg_dump -U giftify giftify_db > backup_$(date +%Y%m%d).sql

# 백업 복원
cat backup_20240101.sql | docker exec -i postgres-giftify psql -U giftify -d giftify_db
```

---

## 참고 자료

- [AWS EC2 문서](https://docs.aws.amazon.com/ec2/)
- [Docker 공식 문서](https://docs.docker.com/)
- [PostgreSQL 16 문서](https://www.postgresql.org/docs/16/)
- [GitHub Actions 문서](https://docs.github.com/en/actions)
- [Nginx Proxy Manager](https://nginxproxymanager.com/)
- [참고 블로그 - AWS 테라폼 수동배포, CI/CD](https://www.slog.gg/p/14565)

---

**마지막 업데이트:** 2026-02-07