package com.example.wallethistory.data.database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.wallethistory.data.model.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert
    void insertTransaction(Transaction transaction);

    //delete transaction by id
    @Query("DELETE FROM transactions_table WHERE id = :id")
    void deleteTransactionById(int id);

    @Query("SELECT * FROM transactions_table")
    LiveData<List<Transaction>> getAllTransactions();

}
