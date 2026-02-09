package com.giftify.member.api;

import com.giftify.member.api.dto.MemberSummary;

public interface MemberApi {
    MemberSummary findById(Long id);
    MemberSummary findByEmail(String email);
}
