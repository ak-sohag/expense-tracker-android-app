package com.example.wallethistory.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wallethistory.data.model.Transaction;
import com.example.wallethistory.databinding.FragmentHomeBinding;
import com.example.wallethistory.ui.adapter.TransactionAdapter;
import com.example.wallethistory.viewmodel.WalletViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    TransactionAdapter adapter;

    WalletViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(WalletViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecycler();
        observeData();

    }

    void setupRecycler() {
        adapter = new TransactionAdapter(requireContext());
        


        binding.recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewTransactions.setHasFixedSize(false);
        binding.recyclerViewTransactions.setNestedScrollingEnabled(false);
        binding.recyclerViewTransactions.setAdapter(adapter);

    }

    void observeData() {
        viewModel.getAllExpenses().observe(getViewLifecycleOwner(), expenses -> {
            if (expenses != null && !expenses.isEmpty()) {
                int maxItem = 6;
                List<Transaction> reversedExpenses = new ArrayList<>(expenses);
                Collections.reverse(reversedExpenses); // Reverse the list

                List<Transaction> limitedExpenses;
                if (reversedExpenses.size() > maxItem)
                    limitedExpenses = reversedExpenses.subList(0, maxItem);
                else limitedExpenses = reversedExpenses;

                adapter.submitList(limitedExpenses);
            }
        });

    }


}
