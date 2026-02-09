# Giftify 아키텍처 개선 기록

>`architecture_overview.md`에 정의된 초기 설계 대비, 실제 구현 과정에서 수행한 구조 개선 사항을 정리한 문서이고, 
> 헥사고날 아키텍처 기반 멀티 모듈 모놀리스(Modular Monolith)입니다.

---

## 1. 모듈 축소 — funding, payment, settlement 제거

### 변경 내용

초기 설계에서는 8개 모듈(app, core, funding, payment, settlement, search, infra, common)로 구성되었으나,
현재는 **5개 모듈(app, core, search, infra, common)** 로 축소하였습니다.

```
[제거된 모듈]
- funding-module    (펀딩 도메인)
- payment-module    (결제 도메인)
- settlement-module (정산 도메인)
```

### 제거 사유

| 사유 | 설명 |
|------|------|
| **구현체 부재** | 세 모듈 모두 소스 코드가 전혀 없는 빈 모듈 상태였음. 실제 비즈니스 로직이 존재하지 않는 모듈을 유지할 이유가 없음 |
| **빌드 리소스 절약** | Gradle은 빈 모듈이라도 `compileJava`, `processResources`, `jar` 태스크를 매번 실행함. 3개 모듈 제거로 빌드 시 불필요한 태스크 9개 이상이 제거되어 빌드 시간 단축 |
| **프로파일 설정 간소화** | `application.yml`의 프로파일 그룹에서 `payment-local`, `funding-local`, `settlement-local` 등 존재하지 않는 프로파일 참조가 제거되어 설정 관리가 명확해짐 |
| **의존성 그래프 단순화** | `app/build.gradle.kts`에서 3개 모듈 의존이 제거되어, 실제 사용되는 모듈만 명시적으로 드러남 |
| **YAGNI 원칙** | 아직 필요하지 않은 모듈을 미리 만들어두면 유지보수 비용만 발생함. 펀딩/결제/정산 기능이 실제로 필요한 시점에 모듈을 생성하는 것이 효율적 |

> 향후 펀딩/결제/정산 도메인을 구현할 때, `architecture_overview.md`의 설계 방향에 따라 독립 모듈로 다시 생성할 수 있습니다.

---

## 2. 헥사고날 아키텍처 원칙 적용

### 2-1. common 모듈 순수화

**문제**: common 모듈에 `GlobalExceptionHandler`, `CustomWebMvcConfig`, `AppConfig`가 위치하여 `spring-boot-starter-web` 의존이 필요했음. 이는 common이 특정 프레임워크에 종속되는 문제를 발생시킴.

**개선**: 세 클래스를 모두 app 모듈로 이동하고, common에서 `spring-boot-starter-web` 의존을 제거.

```
[Before]                              [After]
common/                               common/
├── config/                           ├── domain/
│   ├── AppConfig.java         ✕      │   ├── BaseDomain.java
│   └── CustomWebMvcConfig.java ✕     │   ├── IdentifiableDomain.java
├── domain/                           │   └── ValueObject.java
│   ├── BaseDomain.java               ├── event/
│   ├── IdentifiableDomain.java       │   └── BaseEvent.java
│   └── ValueObject.java              └── exception/
├── event/                                ├── ErrorCode.java
│   └── BaseEvent.java                    ├── BusinessException.java
└── exception/                            ├── DomainException.java
    ├── ErrorCode.java                    └── CommonErrorCode.java
    ├── BusinessException.java
    ├── DomainException.java
    ├── CommonErrorCode.java
    └── GlobalExceptionHandler.java ✕

✕ = app 모듈로 이동됨
```

**효과**: common 모듈이 순수 도메인 기반 유틸리티 모듈로 복원되어, 어떤 모듈에서든 프레임워크 걱정 없이 의존할 수 있음.

---

### 2-2. UseCase / Service / Command를 core 모듈로 이동

**문제**: `MemberUseCase`(인바운드 포트), `MemberService`(비즈니스 로직), `RegisterMemberCommand`가 app 모듈에 위치했음. 이는 헥사고날 아키텍처에서 비즈니스 로직이 외부 계층(app)에 놓이는 구조적 위반.

**개선**: 세 클래스를 core 모듈로 이동.

```
[Before — app 모듈]                    [After — core 모듈]
app/bc/member/                         core/member/
├── usecase/MemberUseCase.java   →     ├── port/in/MemberUseCase.java
├── command/RegisterMemberCommand →    ├── port/in/RegisterMemberCommand.java
└── service/MemberService.java   →     └── service/MemberService.java
```

**효과**: 비즈니스 로직이 도메인 계층(core)에 응집되어, app 모듈 변경 없이 비즈니스 규칙을 독립적으로 테스트·변경할 수 있음.

---

### 2-3. Port 계층 in/out 분리

**문제**: core의 `port/` 패키지에 인바운드 포트(UseCase)와 아웃바운드 포트(Repository, Encoder)가 구분 없이 혼재.

**개선**: `port/in/`과 `port/out/`으로 명확히 분리.

```
core/member/
├── port/
│   ├── in/                          ← 인바운드 (외부 → 도메인)
│   │   ├── MemberUseCase.java          UseCase 인터페이스
│   │   └── RegisterMemberCommand.java  커맨드 객체
│   └── out/                         ← 아웃바운드 (도메인 → 외부)
│       ├── MemberRepositoryPort.java   저장소 인터페이스
│       └── PasswordEncoderPort.java    암호화 인터페이스
```

**효과**: 코드만 보고도 "누가 호출하는가"와 "누구를 호출하는가"를 즉시 구분할 수 있어, 헥사고날 아키텍처의 의도가 패키지 구조에 그대로 반영됨.

---

### 2-4. search 모듈의 infra 직접 의존 제거

**문제**: `search/build.gradle.kts`에서 `implementation(project(":backend:infra"))`로 인프라 모듈에 직접 의존.

**개선**: infra 의존을 제거하고, 필요한 경우 core의 포트를 통해서만 협력하도록 변경.

```
[Before]                          [After]
search → common, core, infra     search → common, core
```

**효과**: 인프라 모듈 간 불필요한 결합이 해소되어, search와 infra가 독립적으로 변경 가능.

---

### 2-5. app 모듈 패키지 정리

**문제**: app 모듈의 member 관련 클래스가 `app/bc/member/` 경로에 위치하여 불필요한 중간 패키지(`bc`)가 존재.

**개선**: `bc/` 제거 후 `app/member/`로 단순화. app에는 인바운드 어댑터(Controller)와 DTO만 위치.

```
[Before]                              [After]
app/                                  app/
├── bc/member/                        ├── common/
│   ├── controller/                   │   ├── config/
│   ├── dto/                          │   │   ├── AppConfig.java
│   ├── usecase/  ✕                   │   │   └── CustomWebMvcConfig.java
│   ├── service/  ✕                   │   └── exception/
│   └── command/  ✕                   │       └── GlobalExceptionHandler.java
└── controller/                       ├── member/
                                      │   ├── controller/MemberController.java
                                      │   └── dto/
                                      │       ├── RegisterMemberRequest.java
                                      │       └── RegisterMemberResponse.java
                                      └── controller/GiftifyController.java

✕ = core 모듈로 이동됨
```

---

## 3. 개선 후 최종 모듈 구조

### 모듈 의존 방향

```
app ──→ core ←── infra
  │       ↑        ↑
  │     common   common
  │       ↑
  └─→ search
```

- **app** (인바운드 어댑터): Controller, DTO, 설정. core의 인바운드 포트를 호출
- **core** (도메인 중심): 도메인 모델, 비즈니스 규칙, 포트 인터페이스, 서비스
- **infra** (아웃바운드 어댑터): JPA 엔티티, Repository 구현체, 외부 시스템 연동
- **search** (조회 전용): Elasticsearch 기반 통합 검색
- **common** (공통 기반): 순수 도메인 베이스 클래스, 예외 체계. 프레임워크 무관

### 모듈별 패키지 구조

```
backend/
├── common/
│   └── com.giftify.common
│       ├── domain/      (BaseDomain, IdentifiableDomain, ValueObject)
│       ├── event/       (BaseEvent)
│       └── exception/   (ErrorCode, BusinessException, DomainException, CommonErrorCode)
│
├── core/
│   └── com.giftify.core
│       └── member/
│           ├── domain/          (Member, MemberRole, MemberStatus)
│           │   └── exception/   (MemberErrorCode, MemberDomainException)
│           ├── port/
│           │   ├── in/          (MemberUseCase, RegisterMemberCommand)
│           │   └── out/         (MemberRepositoryPort, PasswordEncoderPort)
│           └── service/         (MemberService)
│
├── infra/
│   └── com.giftify.infra
│       └── member/
│           ├── adapter/         (MemberRepositoryAdapter, PasswordEncoderAdapter)
│           ├── entity/          (MemberJpaEntity)
│           ├── mapper/          (MemberMapper)
│           └── repository/      (MemberJpaRepository)
│
├── search/
│   └── (Elasticsearch 통합 검색 — 추후 구현)
│
└── app/
    └── com.giftify.app
        ├── common/
        │   ├── config/          (AppConfig, CustomWebMvcConfig)
        │   └── exception/       (GlobalExceptionHandler)
        └── member/
            ├── controller/      (MemberController)
            └── dto/             (RegisterMemberRequest, RegisterMemberResponse)
```

---

## 4. 핵심 설계 원칙 준수 현황

| 원칙 | 초기 상태 | 개선 후 |
|------|----------|--------|
| **DIP (의존 역전)** | infra 어댑터가 core 포트를 구현하나, UseCase가 app에 위치 | core에 포트 + 서비스 응집. infra와 app 모두 core에 의존 |
| **common 순수성** | `spring-boot-starter-web` 의존 | 프레임워크 무관한 순수 모듈 |
| **포트 구분** | in/out 미분리 | `port/in/`, `port/out/` 명확 분리 |
| **모듈 결합도** | search → infra 직접 의존 | core 포트를 통한 간접 협력 |
| **YAGNI** | 빈 모듈 3개 유지 | 미사용 모듈 제거, 빌드 리소스 절약 |