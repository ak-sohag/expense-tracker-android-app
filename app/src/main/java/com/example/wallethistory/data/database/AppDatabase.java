package com.example.wallethistory.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.wallethistory.data.model.Transaction;


@Database(entities = {Transaction.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {


    public abstract TransactionDao transactionDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(context, AppDatabase.class, "APP_DB")
                            .build();
                }
            }
        }

        return INSTANCE;
    }

}
