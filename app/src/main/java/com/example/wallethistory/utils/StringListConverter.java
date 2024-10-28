package com.example.wallethistory.utils;

import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

public class StringListConverter {
    @TypeConverter
    public String fromTagsList(List<String> tags) {
        return new Gson().toJson(tags);  // Converts List<String> to a JSON String
    }

    @TypeConverter
    public List<String> toTagsList(String tagsString) {
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        return new Gson().fromJson(tagsString, listType);  // Converts JSON String back to List<String>
    }
}
