package com.ddalggak.finalproject.global.dto;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SuccessResponseDto {

    private final LocalDateTime timestamp = LocalDateTime.now();

    private final int status;

    private final String message;

    public static ResponseEntity<SuccessResponseDto> toResponseEntity(SuccessCode successCode) {
        return ResponseEntity
            .status(successCode.getHttpStatus())
            .body(SuccessResponseDto.builder()
                .status(successCode.getHttpStatus().value())
                .message(successCode.getDetail())
                .build()
            );
    }

}
