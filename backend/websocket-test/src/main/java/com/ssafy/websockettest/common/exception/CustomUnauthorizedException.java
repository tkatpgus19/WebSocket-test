package com.ssafy.websockettest.common.exception;

import com.ssafy.websockettest.common.response.BaseResponseStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomUnauthorizedException extends RuntimeException {

    private final BaseResponseStatus baseResponseStatus;

}