package com.giftify.member.adapter.in.web.controller;

import com.giftify.common.dto.CommonResponse;
import com.giftify.member.adapter.in.web.dto.request.RegisterMemberRequest;
import com.giftify.member.adapter.in.web.dto.response.RegisterMemberResponse;
import com.giftify.member.application.port.in.MemberUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberUseCase memberUseCase;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<RegisterMemberResponse>> signup(
            @Valid @RequestBody RegisterMemberRequest request
    ) {
        Long memberId = memberUseCase.registerMember(request.toCommand());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.success(new RegisterMemberResponse(memberId, "회원가입이 완료되었습니다.")));
    }
}