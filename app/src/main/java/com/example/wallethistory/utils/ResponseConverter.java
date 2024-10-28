package com.example.wallethistory.utils;

import android.util.Log;

import com.example.wallethistory.data.model.Transaction;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ResponseConverter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());

    // Method to parse the date string to a timestamp
    private long parseDateToTimestamp(String dateString) {
        try {
            return DATE_FORMAT.parse(dateString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // Return 0 if parsing fails
        }
    }

    public Transaction getTransactions(String responseString) {
        try {
            if (responseString != null && (responseString.startsWith("{") || responseString.startsWith("["))) {
                JsonObject jsonObject = JsonParser.parseString(responseString).getAsJsonObject();

                // Convert date string to timestamp
                if (jsonObject.has("date")) {
                    String dateString = jsonObject.get("date").getAsString();
                    long timestamp = parseDateToTimestamp(dateString);
                    jsonObject.addProperty("date", timestamp);
                }

                // Deserialize the modified JSON into a Transaction object
                return new Gson().fromJson(jsonObject, Transaction.class);
            } else {
                Log.e("Invalid Response", "AI returned non-JSON text: " + responseString);
            }
        } catch (Exception e) {
            Log.e("JSON Parsing Error", "Invalid JSON response: " + e.getMessage());
        }

        return null;
    }
}
