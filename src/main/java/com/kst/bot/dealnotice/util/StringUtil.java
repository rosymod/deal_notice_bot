package com.kst.bot.dealnotice.util;

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public static String listToMatchStr(List<String> list){
        //String[] keywords = keywordList.stream().map(i -> i.getKeyword()).collect(Collectors.toList()).stream().toArray(String[]::new);
        return ".*(?i)"+String.join(".*|.*(?i)",list)+".*";// ".*a.*|.*b.*";
    }
}
