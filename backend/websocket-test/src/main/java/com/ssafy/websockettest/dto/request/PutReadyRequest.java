package com.ssafy.websockettest.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PutReadyRequest {
    @NotBlank(message = "방 아이디를 입력해주세요.")
    private String roomId;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;
}
