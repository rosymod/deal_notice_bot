package com.kst.bot.dealnotice.util;

public class StringUtil {

    public static String removeEmpty(String value){
        if(value == null){
            return value;
        }

        return value.replaceAll("\\s","");
    }
}
