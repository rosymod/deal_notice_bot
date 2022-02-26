package com.kst.bot.dealnotice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DealInfo {

    private String type;

    private Integer recommandCnt;

    private String title;

    private String state;

    private String price;

    private String link;

    private String time;

    private String matchWord;

}
