package com.gat.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by mryit on 4/22/2017.
 */

public class DateTimeUtil {

    public static final String FORMAT_TYPE_1 = "yyyy-MM-dd HH:mm:ss";


    public static String calculateDayAgo (String formatType, String timeString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatType);
            Date past = format.parse(timeString);
            Date now = new Date();
            return TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " ngày";
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "hôm nay";
    }

    public static String calculateTimeAgo (String formatType, String timeString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatType);
            Date past = format.parse(timeString);
            Date now = new Date();

            long hoursAgo = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());

            if (hoursAgo <=1) {
                return TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + " phút";
            } else if (hoursAgo >=24){
                return TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " ngày";
            } else {
                return hoursAgo + " giờ";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "vừa nãy";
    }


    public static String transformDate (String timeString) {

        if (timeString == null || timeString.isEmpty()) {
            return "";
        }

        String[] split = timeString.split(" ");
        String[] date = split[0].split("-");
        String result = date[2] + " tháng " + date[1] + " năm " + date[0];

        return result;
    }


}
