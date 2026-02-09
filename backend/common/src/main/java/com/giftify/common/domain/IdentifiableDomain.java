package com.giftify.common.domain;

// 제네릭 타입 파라미터 지정
public abstract class IdentifiableDomain<ID> extends BaseDomain{

    protected ID id;

    protected IdentifiableDomain() {
        this.id = null;
    }

    protected IdentifiableDomain(ID id) {
        validate(id != null, new IllegalArgumentException("ID는 null일 수 없습니다"));
        this.id = id;
    }

    public ID getId() {
        return id;
    }
}
