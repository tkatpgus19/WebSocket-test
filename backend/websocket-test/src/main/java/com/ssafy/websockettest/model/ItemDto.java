package com.ssafy.websockettest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private String roomId;
    private String attacker;
    private String victim;
    private Integer itemNo;
}
