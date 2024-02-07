package com.ssafy.websockettest.common.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BaseSuccessResponse<T> {

    private final int code;
    private final String message;
    private final T result;

    @Builder
    private BaseSuccessResponse(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public static <T> BaseSuccessResponse<T> of(BaseResponseStatus baseResponseStatus, T result) {
        return BaseSuccessResponse.<T>builder()
                .code(baseResponseStatus.getStatus().value())
                .message(baseResponseStatus.getMessage())
                .result(result)
                .build();
    }

}
