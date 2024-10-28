package com.example.wallethistory.ui.adapter;

import static com.example.wallethistory.Constants.CURRENCY_PREFIX;
import static com.example.wallethistory.Constants.CURRENCY_SUFFIX;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallethistory.R;
import com.example.wallethistory.data.model.Transaction;
import com.example.wallethistory.databinding.ModelTransactionBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransactionAdapter extends ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder> {

    private static final String TAG = "TransactionAdapter";
    Context context;

    public TransactionAdapter(Context context) {
        super(DIFFER);
        this.context = context;
    }

    public static DiffUtil.ItemCallback<Transaction> DIFFER = new DiffUtil.ItemCallback<Transaction>() {
        @Override
        public boolean areItemsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public TransactionAdapter.TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ModelTransactionBinding binding = ModelTransactionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TransactionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.TransactionViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {
        ModelTransactionBinding binding;

        public TransactionViewHolder(ModelTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Transaction transaction) {
            manageAmount(transaction);
            manageCategory(transaction);
            manageDate(transaction);
        }

        void manageAmount(Transaction transaction) {
            double value = transaction.getAmount();
            String formattedValue = String.format("%.2f", value);
            if ("income".equalsIgnoreCase(transaction.getType())) {
                binding.amountTv.setText("+" + CURRENCY_PREFIX + formattedValue + CURRENCY_SUFFIX);
                binding.amountTv.setTextColor(context.getResources().getColor(R.color.green, null));
            } else {
                binding.amountTv.setText("-" + CURRENCY_PREFIX + formattedValue + CURRENCY_SUFFIX);
                binding.amountTv.setTextColor(context.getResources().getColor(R.color.red, null));
            }
        }

        void manageCategory(Transaction transaction) {
            String category = transaction.getCategory();
            binding.categoryTv.setText(category);
        }

        void manageDate(Transaction transaction) {
            long date = transaction.getDate();
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            binding.dateTv.setText(format.format(new Date(date)));
        }


    }
}
