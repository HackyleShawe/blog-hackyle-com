package com.hackyle.blog.consumer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateFormatUtils.class);

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DEFAULT_PATTERN);

    public static Date parseString(String dateStr, String pattern) {
        if(dateStr == null || "".equals(dateStr.trim())) {
            return null;
        }

        if(pattern == null || "".equals(pattern.trim())) {
            return parseString(dateStr);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date parseDate = null;
        try {
            parseDate = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            LOGGER.error("格式化时间出现异常：", e);
        }

        return parseDate;
    }

    public static Date parseString(String dateStr) {
        if(dateStr == null || "".equals(dateStr.trim())) {
            return null;
        }

        Date parseDate = null;
        try {
            parseDate = SIMPLE_DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            LOGGER.error("格式化时间出现异常：", e);
        }
        return parseDate;
    }
}
