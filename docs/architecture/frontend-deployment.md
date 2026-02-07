# Frontend Deployment Architecture

## 목차
- [개요](#개요)
- [프로젝트 구조](#프로젝트-구조)
- [Vercel 배포 설정](#vercel-배포-설정)
- [도메인 및 네트워크](#도메인-및-네트워크)
- [환경변수 설정](#환경변수-설정)
- [배포 프로세스](#배포-프로세스)
- [백엔드 연동](#백엔드-연동)

---

## 개요

Giftify 프론트엔드는 Vercel 플랫폼을 통해 배포되며, `www.giftify.yjkim.store` 도메인에서 서비스됩니다. Vercel의 자동 배포 기능을 활용하여 main 브랜치에 push하면 자동으로 배포가 진행됩니다.

### 기술 스택
- **프레임워크**: React (또는 Next.js/Vue.js 등)
- **배포 플랫폼**: Vercel
- **DNS**: DNSZi
- **CI/CD**: Vercel 자동 배포
- **백엔드 API**: `https://api.giftify.yjkim.store`

---

## 프로젝트 구조

```
giftify-personal-expansion/
├── frontend/              # 프론트엔드 (Vercel 배포)
│   ├── src/
│   ├── public/
│   ├── package.json
│   ├── vite.config.js (또는 next.config.js)
│   └── vercel.json
├── backend/               # 백엔드 (AWS EC2 배포)
└── ...
```

**특이사항:**
- 프론트엔드와 백엔드가 단일 레포지토리(Monorepo)에 존재
- 프론트엔드는 `frontend/` 디렉토리에 위치
- Vercel은 `frontend/` 디렉토리만 빌드 및 배포
- 백엔드는 별도로 AWS EC2에 배포됨

---

## Vercel 배포 설정

### 1. Vercel 프로젝트 생성

1. **Vercel 대시보드 접속**
    - https://vercel.com 로그인
    - "New Project" 클릭

2. **GitHub 레포지토리 연결**
    - GitHub 계정 연동
    - `giftify-personal-expansion` 레포지토리 선택
    - Import 클릭

3. **프로젝트 설정**
   ```
   Project Name: giftify-frontend
   Framework Preset: (자동 감지 - React/Vite/Next.js 등)
   Root Directory: frontend/
   Build Command: npm run build (또는 yarn build)
   Output Directory: dist/ (또는 .next/, build/ 등)
   Install Command: npm install (또는 yarn install)
   ```

4. **환경변수 설정**
    - Settings → Environment Variables
   ```
   VITE_API_URL=https://api.giftify.yjkim.store
   (또는 NEXT_PUBLIC_API_URL, REACT_APP_API_URL 등)
   ```

5. **Deploy** 클릭

### 2. Vercel 설정 파일

**vercel.json** (선택사항)

```json
{
  "buildCommand": "npm run build",
  "outputDirectory": "dist",
  "devCommand": "npm run dev",
  "installCommand": "npm install",
  "framework": "vite",
  "rewrites": [
    {
      "source": "/api/:path*",
      "destination": "https://api.giftify.yjkim.store/:path*"
    }
  ],
  "headers": [
    {
      "source": "/(.*)",
      "headers": [
        {
          "key": "X-Content-Type-Options",
          "value": "nosniff"
        },
        {
          "key": "X-Frame-Options",
          "value": "DENY"
        },
        {
          "key": "X-XSS-Protection",
          "value": "1; mode=block"
        }
      ]
    }
  ]
}
```

**주요 설정:**
- `buildCommand`: 빌드 명령어
- `outputDirectory`: 빌드 결과물 디렉토리
- `rewrites`: API 프록시 설정 (CORS 우회용)
- `headers`: 보안 헤더 설정

---

## 도메인 및 네트워크

### DNS 설정 (DNSZi)

**CNAME 레코드:**
```
호스트: www.giftify
목적지: cname.vercel-dns.com (Vercel에서 제공하는 CNAME)
TTL: 600
```

**또는 A 레코드 (예시):**
```
호스트: www.giftify
IP: 76.76.21.21 (Vercel IP)
TTL: 600
```

### Vercel 도메인 설정

1. **Vercel 대시보드**
    - Project Settings → Domains

2. **커스텀 도메인 추가**
   ```
   Domain: www.giftify.yjkim.store
   ```

3. **DNS 설정 확인**
    - Vercel이 제공하는 CNAME 값 확인
    - DNSZi에서 해당 값으로 설정

4. **HTTPS 자동 설정**
    - Vercel이 자동으로 Let's Encrypt SSL 인증서 발급
    - 수동 작업 불필요

### 아키텍처

```
┌─────────────────────────────────────────────────────┐
│                    Client Browser                    │
└─────────────────────────────────────────────────────┘
                         ↓
          ┌──────────────────────────────┐
          │ DNS (DNSZi)                  │
          │ www.giftify.yjkim.store      │
          │ → cname.vercel-dns.com       │
          └──────────────────────────────┘
                         ↓
          ┌──────────────────────────────┐
          │ Vercel CDN (Global)          │
          │ • HTTPS (Auto SSL)           │
          │ • Edge Functions             │
          │ • Static Assets              │
          └──────────────────────────────┘
                         ↓
          ┌──────────────────────────────┐
          │ Frontend Application         │
          │ (React/Next.js/Vue.js)       │
          └──────────────────────────────┘
                         ↓
          ┌──────────────────────────────┐
          │ Backend API                  │
          │ https://api.giftify.yjkim.store │
          │ (AWS EC2)                    │
          └──────────────────────────────┘
```

---

## 환경변수 설정

### Vercel 환경변수

Vercel 대시보드 → Settings → Environment Variables에서 설정:

| 변수명 | 값 | 환경 |
|--------|-----|------|
| `VITE_API_URL` | `https://api.giftify.yjkim.store` | Production, Preview, Development |
| `VITE_APP_NAME` | `Giftify` | All |
| `VITE_GA_ID` | `G-XXXXXXXXXX` (Google Analytics) | Production |

**주의:**
- React (Vite): `VITE_` 접두사 사용
- Next.js: `NEXT_PUBLIC_` 접두사 사용
- Create React App: `REACT_APP_` 접두사 사용

### 환경변수 사용 예시

**Vite (React):**
```javascript
// src/config/api.js
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL: API_URL,
  timeout: 10000,
  withCredentials: true
});
```

**Next.js:**
```javascript
// src/config/api.js
const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL: API_URL,
  timeout: 10000,
  withCredentials: true
});
```

---

## 배포 프로세스

### 자동 배포 (권장)

Vercel은 GitHub와 연동되어 자동 배포됩니다.

**1. 코드 변경 후 Push**
```bash
cd frontend/
git add .
git commit -m "feat: 새 기능 추가"
git push origin main
```

**2. Vercel 자동 빌드 및 배포**
- GitHub에 push하면 자동으로 트리거됨
- Vercel 대시보드에서 배포 상태 확인 가능
- 약 1~3분 소요

**3. 배포 완료**
- Production: `https://www.giftify.yjkim.store`
- Preview (브랜치별): `https://giftify-frontend-git-feature-xxx.vercel.app`

### 배포 플로우

```
┌─────────────────────────────────────────────────────┐
│ 1. Developer Push to GitHub                         │
│    git push origin main                              │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│ 2. Vercel Detects Change                            │
│    • GitHub Webhook 트리거                          │
│    • frontend/ 디렉토리 변경 감지                   │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│ 3. Build Process                                     │
│    • npm install                                     │
│    • npm run build                                   │
│    • 환경변수 주입                                   │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│ 4. Deploy to Vercel CDN                             │
│    • 전 세계 Edge 서버에 배포                       │
│    • SSL 인증서 자동 갱신                           │
│    • 이전 버전 자동 롤백 가능                       │
└─────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────┐
│ 5. Production Live                                   │
│    • https://www.giftify.yjkim.store                │
│    • 즉시 접속 가능                                  │
└─────────────────────────────────────────────────────┘
```

### 수동 배포 (Vercel CLI)

필요시 로컬에서 직접 배포 가능:

```bash
# Vercel CLI 설치
npm i -g vercel

# 프로젝트 디렉토리로 이동
cd frontend/

# 로그인
vercel login

# Production 배포
vercel --prod

# Preview 배포
vercel
```

### 브랜치별 배포 전략

| 브랜치 | 배포 환경 | URL | 용도 |
|--------|-----------|-----|------|
| `main` | Production | `www.giftify.yjkim.store` | 실 서비스 |
| `develop` | Preview | `giftify-frontend-git-develop.vercel.app` | 개발 테스트 |
| `feature/*` | Preview | `giftify-frontend-git-feature-xxx.vercel.app` | 기능 테스트 |

**특징:**
- 모든 브랜치가 자동으로 Preview 환경 생성
- PR 생성 시 미리보기 링크 자동 생성
- Production은 main 브랜치만 배포

---

## 백엔드 연동

### CORS 설정

백엔드(Spring Boot)에서 CORS 허용:

```kotlin
// backend/app/src/main/kotlin/com/giftify/app/config/WebConfig.kt
@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "https://www.giftify.yjkim.store",
                "http://localhost:5173" // 로컬 개발용
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
    }
}
```

### API 호출 예시

**Frontend (Axios):**
```javascript
// src/api/gifts.js
import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 10000,
  withCredentials: true // 쿠키 전송
});

// 선물 목록 조회
export const getGifts = async () => {
  const response = await apiClient.get('/api/gifts');
  return response.data;
};

// 선물 생성
export const createGift = async (giftData) => {
  const response = await apiClient.post('/api/gifts', giftData);
  return response.data;
};
```

### 인증 처리

**쿠키 기반 인증:**
```javascript
// src/api/auth.js
import apiClient from './client';

// 로그인
export const login = async (credentials) => {
  const response = await apiClient.post('/api/auth/login', credentials);
  // 백엔드에서 Set-Cookie 헤더로 쿠키 설정
  return response.data;
};

// 로그아웃
export const logout = async () => {
  const response = await apiClient.post('/api/auth/logout');
  return response.data;
};

// 현재 사용자 정보
export const getCurrentUser = async () => {
  const response = await apiClient.get('/api/auth/me');
  return response.data;
};
```

**백엔드 쿠키 도메인 설정:**
```yaml
# backend/app/src/main/resources/application-prod.yml
custom:
  prod:
    cookieDomain: giftify.yjkim.store
    frontUrl: "https://www.${custom.prod.cookieDomain}"
    backUrl: "https://api.${custom.prod.cookieDomain}"
```

---

## 성능 최적화

### 1. Vercel Analytics

```javascript
// src/main.jsx (또는 _app.js for Next.js)
import { Analytics } from '@vercel/analytics/react';

function App() {
  return (
    <>
      <YourApp />
      <Analytics />
    </>
  );
}
```

### 2. 이미지 최적화

```javascript
// Vite의 경우
import { defineConfig } from 'vite';
import imagemin from 'vite-plugin-imagemin';

export default defineConfig({
  plugins: [
    imagemin({
      gifsicle: { optimizationLevel: 7 },
      mozjpeg: { quality: 80 },
      pngquant: { quality: [0.8, 0.9] },
      svgo: { plugins: [{ removeViewBox: false }] }
    })
  ]
});
```

### 3. 코드 스플리팅

```javascript
// React Lazy Loading
import { lazy, Suspense } from 'react';

const GiftList = lazy(() => import('./pages/GiftList'));
const GiftDetail = lazy(() => import('./pages/GiftDetail'));

function App() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <Routes>
        <Route path="/gifts" element={<GiftList />} />
        <Route path="/gifts/:id" element={<GiftDetail />} />
      </Routes>
    </Suspense>
  );
}
```

### 4. 캐싱 전략

```javascript
// vercel.json
{
  "headers": [
    {
      "source": "/static/(.*)",
      "headers": [
        {
          "key": "Cache-Control",
          "value": "public, max-age=31536000, immutable"
        }
      ]
    },
    {
      "source": "/(.*).html",
      "headers": [
        {
          "key": "Cache-Control",
          "value": "public, max-age=0, must-revalidate"
        }
      ]
    }
  ]
}
```

---

## 모니터링

### 1. Vercel 대시보드

- **Deployments**: 배포 히스토리 및 상태
- **Analytics**: 방문자 통계, 페이지 속도
- **Logs**: 빌드 및 런타임 로그
- **Speed Insights**: Core Web Vitals 측정

### 2. 에러 트래킹 (Sentry 연동)

```bash
npm install @sentry/react @sentry/tracing
```

```javascript
// src/main.jsx
import * as Sentry from "@sentry/react";

Sentry.init({
  dsn: "YOUR_SENTRY_DSN",
  integrations: [new Sentry.BrowserTracing()],
  tracesSampleRate: 1.0,
  environment: import.meta.env.MODE
});
```

---

## 백엔드와의 연동 구조

```
┌────────────────────────────────────────────────────┐
│          Frontend (Vercel)                         │
│      https://www.giftify.yjkim.store               │
│                                                     │
│  ┌──────────────────────────────────────┐          │
│  │ React Components                     │          │
│  │  • GiftList                          │          │
│  │  • GiftDetail                        │          │
│  │  • UserProfile                       │          │
│  └──────────────────────────────────────┘          │
│                    ↓                                │
│  ┌──────────────────────────────────────┐          │
│  │ API Client (Axios)                   │          │
│  │  • baseURL: VITE_API_URL             │          │
│  │  • withCredentials: true             │          │
│  └──────────────────────────────────────┘          │
└────────────────────────────────────────────────────┘
                     ↓ HTTPS
┌────────────────────────────────────────────────────┐
│        Backend (AWS EC2)                           │
│      https://api.giftify.yjkim.store               │
│                                                     │
│  ┌──────────────────────────────────────┐          │
│  │ Nginx Proxy Manager                  │          │
│  │  • SSL Termination                   │          │
│  │  • Reverse Proxy                     │          │
│  └──────────────────────────────────────┘          │
│                    ↓                                │
│  ┌──────────────────────────────────────┐          │
│  │ Spring Boot Application              │          │
│  │  • REST API                          │          │
│  │  • CORS 설정                         │          │
│  │  • Cookie 기반 인증                  │          │
│  └──────────────────────────────────────┘          │
│                    ↓                                │
│  ┌──────────────────────────────────────┐          │
│  │ PostgreSQL                           │          │
│  │  • Database: giftify_db              │          │
│  └──────────────────────────────────────┘          │
└────────────────────────────────────────────────────┘
```

---

## 참고 자료

- [Vercel 공식 문서](https://vercel.com/docs)
- [Vercel CLI 문서](https://vercel.com/docs/cli)
- [Vercel Analytics](https://vercel.com/docs/analytics)
- [React 공식 문서](https://react.dev/)
- [Vite 공식 문서](https://vitejs.dev/)
- [Axios 문서](https://axios-http.com/)

---

**마지막 업데이트:** 2026-02-07