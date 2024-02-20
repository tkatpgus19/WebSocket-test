package com.ssafy.websockettest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostRoomRequest {

    @NotBlank(message = "방 타입을 선택해주세요.")
    private String roomType;

    @NotBlank(message = "방 이름을 입력해주세요.")
    private String roomName;

    @NotNull(message = "비밀방 여부를 선택해주세요.")
    private Boolean hasPassword;

    private String roomPassword;

    @NotBlank(message = "문제 난이도를 입력해주세요.")
    private String problemTier;

    @NotBlank(message = "문제 정보를 입력해주세요.")
    private String problemName;

    @NotNull(message = "제한 시간을 입력해주세요.")
    private Long timeLimit;

    @NotBlank(message = "풀이 언어를 입력해주세요.")
    private String language;

    @NotNull(message = "코드리뷰 여부를 입력해주세요.")
    private Boolean codeReview;

    @NotBlank(message = "방장 닉네임을 입력해주세요.")
    private String master;
}