package com.example.wallethistory.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.wallethistory.data.model.Transaction;
import com.example.wallethistory.data.repository.TransactionRepo;

import java.util.List;

public class WalletViewModel extends AndroidViewModel {

    TransactionRepo repo;

    public WalletViewModel(@NonNull Application application) {
        super(application);
        repo = new TransactionRepo(application);
    }

    public void addTransaction(Transaction expense) {
        repo.insertExpense(expense);
    }

    public void deleteTransactionById(int id) {
        repo.deleteTransactionById(id);
    }

    public LiveData<List<Transaction>> getAllExpenses() {
        return repo.getAllExpenses();
    }


}
