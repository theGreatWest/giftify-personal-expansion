package com.giftify.common.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class CommonResponse<T> {
    private boolean success;
    private T data;
    private ErrorResponse error;

    protected CommonResponse() {}

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(true, data, null);
    }

    public static <T> CommonResponse<T> error(String code, String message) {
        return new CommonResponse<>(false, null, new ErrorResponse(code, message));
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ErrorResponse {
        private String code;
        private String message;
    }
}
