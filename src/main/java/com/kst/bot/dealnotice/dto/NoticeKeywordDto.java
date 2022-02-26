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
public class NoticeKeywordDto {

    private Integer memberIdx;

    private String keyword;

    private OffsetDateTime insertDatetime;

    private OffsetDateTime updateDatetime;
}
