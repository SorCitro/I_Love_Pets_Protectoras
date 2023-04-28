package com.example.ilpp.classes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Format {

    private static final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public static Date toDate(String date){
        try {
            return formatter.parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static String toDateString(Date date){
        return formatter.format(date);
    }

}
