package com.kst.bot.dealnotice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    private Integer idx;
    private String chatId;
    private String keyword;
    private String lastName;
    private String firstName;
    private String langCd;
    private OffsetDateTime insertDatetime;
    private OffsetDateTime updateDatetime;
    private String useYn;

}
