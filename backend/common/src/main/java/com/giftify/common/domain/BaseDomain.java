package com.giftify.common.domain;

// 도메인 객체의 핵심은 "행위와 규칙" 이다.
// 식별자 개념은 있어야 하지만, JPA식 PK 개념은 필요하지 않다.
// 직접 쓰는 객체가 아니기 때문에 추상 클래스로 선언해, 인스턴스화를 방지한다.
public abstract class BaseDomain {

    // 생성자를 protected로 막아서 상속받은 도메인만 상속받도록 설정해 의미 없는 객체 생성을 방지한다.
    protected  BaseDomain() {}

    // 규칙 검증
    // condition이 false인 경우 예외를 발생시킨다.
    protected void validate(boolean condition, RuntimeException e) {
        if (!condition) {
            throw e;
        }
    }
}
