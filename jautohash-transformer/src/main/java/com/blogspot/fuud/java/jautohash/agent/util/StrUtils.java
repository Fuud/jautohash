package com.blogspot.fuud.java.jautohash.agent.util;

import java.util.Map;

public class StrUtils {

    public static String format(String str, Map<String, Object> values) {

        StringBuilder builder = new StringBuilder(str);

        for (Map.Entry<String, Object> entry : values.entrySet()) {

            int start;
            String pattern = "%(" + entry.getKey() + ")";
            String value = entry.getValue().toString();

            // Replace every occurence of %(key) with value
            while ((start = builder.indexOf(pattern)) != -1) {
                builder.replace(start, start + pattern.length(), value);
            }
        }

        return builder.toString();
    }
}
