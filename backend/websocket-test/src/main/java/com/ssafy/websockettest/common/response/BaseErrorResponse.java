package com.ssafy.websockettest.common.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BaseErrorResponse {

    private final int code;
    private final String status;
    private final String message;

    @Builder
    private BaseErrorResponse(int code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public static BaseErrorResponse of(BaseResponseStatus baseResponseStatus) {
        return builder()
                .code(baseResponseStatus.getStatus().value())
                .status(baseResponseStatus.getStatus().name())
                .message(baseResponseStatus.getMessage())
                .build();
    }

    public static BaseErrorResponse of(BaseResponseStatus baseResponseStatus, String message) {
        return builder()
                .code(baseResponseStatus.getStatus().value())
                .status(baseResponseStatus.getStatus().name())
                .message(message)
                .build();
    }

}
