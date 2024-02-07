package com.ssafy.websockettest.common.exception;

import com.eni.backend.common.response.BaseResponseStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomUnauthorizedException extends RuntimeException {

    private final BaseResponseStatus baseResponseStatus;

}