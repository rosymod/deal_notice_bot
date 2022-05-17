package com.kst.bot.dealnotice;

import com.kst.bot.dealnotice.util.StringUtil;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;


public class MatchTest {

    @Test
    public void matchWordTest(){
        String value = "맥북에어 M1_기본-형(8G/256G) 판매합니다";
        System.out.println(StringUtil.removeSpecWord(value));
        System.out.println(StringUtil.removeEmpty(value));
    }
}
