package com.giftify.member.application.service;

import com.giftify.member.api.MemberApi;
import com.giftify.member.api.dto.MemberSummary;
import com.giftify.member.application.port.in.MemberUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberApiService implements MemberApi {

    private final MemberUseCase memberUseCase;

    @Override
    public MemberSummary findById(Long id) {
        return MemberSummary.of(
                memberUseCase.findById(id)
        );
    }

    @Override
    public MemberSummary findByEmail(String email) {
        return MemberSummary.of(
                memberUseCase.findByEmail(email)
        );
    }
}
