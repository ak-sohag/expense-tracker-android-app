package com.example.wallethistory.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.wallethistory.data.database.AppDatabase;
import com.example.wallethistory.data.database.TransactionDao;
import com.example.wallethistory.data.model.Transaction;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TransactionRepo {

    private static final String TAG = "TransactionRepo";
    private final TransactionDao expenseDao;
    private final Executor executor;


    public TransactionRepo(Application application) {
        executor = Executors.newSingleThreadExecutor();
        AppDatabase db = AppDatabase.getInstance(application);
        expenseDao = db.transactionDao();
    }

    public void insertExpense(Transaction expense) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                expenseDao.insertTransaction(expense);
            }
        });
    }

    public void deleteTransactionById(int id) {
        executor.execute(() -> expenseDao.deleteTransactionById(id));
    }

    public LiveData<List<Transaction>> getAllExpenses() {
        return expenseDao.getAllTransactions();
    }

}
