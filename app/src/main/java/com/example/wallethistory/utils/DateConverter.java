package com.example.wallethistory.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateConverter {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());

    public static long parseDateToTimestamp(String dateString) {
        try {
            return DATE_FORMAT.parse(dateString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // Default value for failed parsing
        }
    }
}
