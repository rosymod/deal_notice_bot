package com.kst.bot.dealnotice.util;

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
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
        Pattern pattern = Pattern.compile("[^0-9a-zA-Zㄱ-힣\\-\\+_]");
        return pattern.matcher(value).replaceAll("");
    }

    public static String listToMatchStr(List<String> list){
//        final String specWord = "[\\-\\+]";
//        list = list.stream().map(word->{
//            String result = word;
//            Matcher matcher = Pattern.compile(specWord).matcher(word);
//            while(matcher.find()){
//                result = result.substring(0,matcher.start()) + "\\"+matcher.group() + result.substring(matcher.end(),result.length());
//            }
//            return result;
//        }).collect(Collectors.toList());
        list = list.stream().map(word->{
            word = word.replaceAll("\\+","\\\\+");
            word = word.replaceAll("\\-","\\\\-");
            return word;
        }).collect(Collectors.toList());
        return ".*(?i)"+String.join(".*|.*(?i)",list)+".*"; // ".*a.*|.*b.*";
    }
}
