package com.giftify.common.domain;

// Email, Money와 같이 불변 객체 생성 시 상속
// 생성 시점에 규칙 보장
public abstract class ValueObject {

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
}
