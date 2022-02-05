package com.kst.bot.dealnotice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeHistoryDto {

    private Integer memberIdx;
    private String content;
    private OffsetDateTime insertDatetime;

    private List<DealInfo> includes;

}
