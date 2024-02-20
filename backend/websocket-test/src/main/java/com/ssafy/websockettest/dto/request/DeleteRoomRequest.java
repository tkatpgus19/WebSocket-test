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
public class DeleteRoomRequest {
    @NotBlank(message = "방 아이디를 입력해주세요.")
    private String roomId;
}
