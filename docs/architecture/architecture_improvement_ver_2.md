# Giftify 아키텍처 개선 기록 (Version 2)

>`architecture_improvement_ver_1.md`에 정의된 계층형 멀티 모듈 구조에서 도메인 중심의 **Modular Monolith** 구조로 전환하며 수행한 주요 구조 개선 사항을 정리한 문서입니다.

---

## 1. 계층 중심에서 도메인 중심으로 모듈 재구성 (Modular Monolith)

### 변경 내용
기존의 `core`, `infra`와 같이 기술적 계층으로 분리되어 있던 모듈을 비즈니스 도메인 단위의 모듈로 전면 재구성하였습니다.

```text
[이전 구조 - 계층 중심]                 [현재 구조 - 도메인 중심]
backend/                              backend/
├── app/ (진입점)                      ├── app/ (실행 및 조립)
├── core/ (전체 비즈니스 로직)   ──→   ├── member/ (회원 도메인)
├── infra/ (전체 기술 구현)            ├── auth/ (인증 도메인)
├── search/                           ├── product/ (상품 도메인)
└── common/                           ├── funding/ (펀딩 도메인)
                                      ├── order/ (주문 도메인)
                                      ├── ... (기타 도메인 모듈)
                                      └── common/ (공통 모듈)
```

### 개선 효과
- **응집도 향상**: 특정 도메인의 변경 사항이 해당 도메인 모듈 내로 국한되어 유지보수가 용이해졌습니다.
- **확장성 확보**: 새로운 기능 추가 시 독립적인 도메인 모듈을 추가함으로써 기존 코드에 미치는 영향을 최소화할 수 있습니다.
- **코드 가독성**: 프로젝트 구조가 비즈니스 기능을 직관적으로 나타내도록 변경되었습니다.

---

## 2. 도메인 모듈 내 헥사고날 아키텍처 유지

### 변경 내용
도메인 모듈(`member` 등) 내부를 `application`, `core`, `adapter` 패키지로 나누어 헥사고날 아키텍처 원칙을 고수하였습니다.

```text
backend/member/
├── application/ (Service, Port)
├── core/ (Domain Entity, Exception)
└── adapter/ (Web Controller, Persistence Adapter)
```

### 개선 효과
- **관심사 분리**: 도메인 로직(`core`)이 외부 어댑터(`adapter`)나 프레임워크 설정에 오염되지 않도록 보호합니다.
- **교체 용이성**: DB 기술이나 API 스펙 변경 시 `adapter` 계층만 수정하면 되므로 변경에 유연합니다.

---

## 3. 의존성 중앙 관리 및 도메인 모듈 경량화

### 변경 내용
- **중앙 관리**: 루트 `build.gradle.kts`에서 `spring-boot-dependencies` BOM을 사용하여 프로젝트 전체의 라이브러리 버전을 일관되게 관리합니다.
- **도메인 모듈 독립성**: 도메인 모듈에서 `spring-boot-starter-*` 의존성을 제거하고, 비즈니스 로직에 필요한 최소한의 라이브러리(`spring-context`, `jakarta.persistence-api` 등)만 의존하도록 개선하였습니다.
- **실행 모듈 집중**: `backend/app` 모듈이 실제 실행에 필요한 모든 의존성(Starter, DB Driver 등)을 가집니다.

### 개선 효과
- **빌드 속도 최적화**: 불필요한 의존성 로딩을 줄여 빌드 및 테스트 실행 속도가 개선되었습니다.
- **테스트 용이성**: 스프링 부트 환경 없이도 도메인 로직에 대한 순수한 단위 테스트를 더 쉽게 수행할 수 있습니다.

---

## 4. `common` 모듈의 공통 기능 강화 및 표준화

### 변경 내용
모든 도메인에서 공통으로 사용하는 기반 클래스와 통신 규약을 `common` 모듈에 정의하였습니다.

- **`CommonResponse`**: 모든 API 응답을 `{"success": true, "data": ..., "error": ...}` 형식으로 표준화하였습니다.
- **`BaseEntity`**: 모든 JPA 엔티티의 공통 필드(`id`, `createdAt`, `updatedAt`)와 JPA Auditing 기능을 통합 관리합니다.
- **`GlobalExceptionHandler`**: 시스템 전역에서 발생하는 예외를 `CommonResponse` 형식에 맞춰 일관되게 처리합니다.

### 개선 효과
- **일관된 API 인터페이스**: 클라이언트(Frontend)에서 일관된 방식으로 API 결과를 처리할 수 있습니다.
- **중복 코드 제거**: 엔티티 기초 설계 및 예외 처리 로직의 중복을 제거하였습니다.

---

## 5. 전역 테스트 환경 구축

### 변경 내용
루트 `build.gradle.kts`에서 `spring-boot-starter-test`를 전역적으로 설정하여, 모든 하위 모듈에서 별도의 설정 없이 즉시 테스트를 작성하고 실행할 수 있도록 하였습니다.

### 개선 효과
- **테스트 문화 정착**: 새로운 모듈을 추가할 때 테스트 설정에 드는 비용을 없애 테스트 코드 작성을 장려합니다.
- **안정성 확보**: 각 계층(Domain, Service, Controller, Persistence)에 대한 테스트 코드를 통해 기능의 올바름을 지속적으로 검증합니다.

---

## 6. 핵심 설계 원칙 준수 요약

| 원칙 | Version 1 (계층 중심) | Version 2 (도메인 중심) |
|------|--------------------|----------------------|
| **모듈화 방식** | 기술 계층별 분리 | 비즈니스 도메인별 분리 (Modular Monolith) |
| **의존성 방향** | app/infra → core | app → 도메인 모듈 (member, auth 등) |
| **SpringBoot 종속성** | 전 모듈 Starter 의존 | 도메인 모듈은 최소 의존, app에 집중 |
| **응답 규약** | 모듈별 상이할 수 있음 | `CommonResponse`로 전역 표준화 |
| **엔티티 기초** | 각자 정의 또는 부분 공통화 | `BaseEntity` (id 포함) 상속으로 일원화 |
