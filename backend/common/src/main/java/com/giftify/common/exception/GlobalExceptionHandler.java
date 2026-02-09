package com.giftify.common.exception;

import com.giftify.common.dto.CommonResponse;
import com.giftify.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("BusinessException: code={}, message={}", e.getErrorCode().getCode(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage()));
    }

    // 입력값 유효성 예외 처리
    // 새로 추가, 수정된 필드별 에러를 반환
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<List<Map<String, String>>>> handleValidationException(MethodArgumentNotValidException e) {
        // 필드별 에러 생성
        List<Map<String, String>> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", error.getDefaultMessage() != null ? error.getDefaultMessage() : "유효하지 않은 값입니다."
                ))
                .toList();

        log.warn("Validation failed: errors={}", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error("VALIDATION_ERROR", "입력값이 유효하지 않습니다.", errors));
    }

    // 기타 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.error("INTERNAL_SERVER_ERROR", "서버 에러가 발생했습니다."));
    }
}