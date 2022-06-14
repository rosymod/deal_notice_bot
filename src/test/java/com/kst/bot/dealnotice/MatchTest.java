package com.kst.bot.dealnotice;

import com.kst.bot.dealnotice.util.StringUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class MatchTest {

    @Test
    public void matchWordTest(){
        String value = "맥북에어 M1_기본-+형(8G/256G) 판매합니다";
        System.out.println(StringUtil.removeSpecWord(value));
        System.out.println(StringUtil.removeEmpty(value));
    }

    @Test
    public void matchListTest(){
        String regx = StringUtil.listToMatchStr(new ArrayList(){{add("920+123-_-");}});
        String value = "synology920+123-_-345";
        System.out.println(value.matches(regx));
    }
}
