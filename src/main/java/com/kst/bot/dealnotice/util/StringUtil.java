package com.kst.bot.dealnotice.util;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class StringUtil {

    public static String removeEmpty(String value){
        if(value == null){
            return value;
        }

        return value.replaceAll("\\s","");
    }

    public static String removeSpecWord(String value){
        if(!StringUtils.hasText(value)){
            return value;
        }
        Pattern pattern = Pattern.compile("[^0-9a-zA-Zㄱ-힣\\-_]");
        return pattern.matcher(value).replaceAll("");
    }
}
