package com.example.wallethistory.ui.fragments;

import static com.example.wallethistory.MainActivity.GEMINI_API_KEY;
import static com.example.wallethistory.MainActivity.GEMINI_AI_MODEL;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.wallethistory.data.model.Transaction;
import com.example.wallethistory.databinding.FragmentNewTransactionBinding;
import com.example.wallethistory.utils.ResponseConverter;
import com.example.wallethistory.viewmodel.WalletViewModel;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.BlockThreshold;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.RequestOptions;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class NewTransactionFragment extends BottomSheetDialogFragment {

    private static final String TAG = "NewTransactionFragment";
    private static final int MAX_TAGS = 5;  // Maximum number of tags allowed
    FragmentNewTransactionBinding binding;
    WalletViewModel viewModel;

    long mDateInMillis;
    Calendar mCalender;
    ActivityResultLauncher<String> imagePickerLauncher = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewTransactionBinding.inflate(inflater, container, false);

        initListeners();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        if (view != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
//            behavior.setDraggable(false);  // Disables dragging
//            behavior.setHideable(false); // Prevent it from hiding
//            behavior.setPeekHeight(0);   // No peek height to avoid collapsing
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);  // Keeps it expanded

            view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(WalletViewModel.class);

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close(); // Always close the input stream to avoid memory leaks

                    binding.scanningMcv.setVisibility(View.VISIBLE);
                    binding.scannedImageView.setImageBitmap(bitmap);
                    TransitionManager.beginDelayedTransition(binding.scanCardView, new AutoTransition());
                    binding.scannedImageView.setVisibility(View.VISIBLE);

                    getTransactionFromImage(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        setup(null);
    }

    void setup(Transaction transaction) {

        if (transaction == null) {
            setTransactionType(true);
            setTimeInMillis(System.currentTimeMillis());
            setDateInMillis(System.currentTimeMillis());
            setTimeToTextView(binding.dateTextView, mDateInMillis, false);
            setTimeToTextView(binding.timeTextView, mDateInMillis, true);
        } else {
            Log.i(TAG, "setup: " + transaction);
            setTransactionType(transaction.getType().equalsIgnoreCase("income"));
            setDateInMillis(transaction.getDate());
            setTimeToTextView(binding.dateTextView, mDateInMillis, false);
            setTimeToTextView(binding.timeTextView, mDateInMillis, true);
            setAmount(transaction.getAmount());
            setCategory(transaction.getCategory());
            setTagsFromResponse(transaction);
            setNotes(transaction.getNote());
            setPaymentType(transaction.getPaymentMethod());
            setMerchantName(transaction.getMerchantName());
            manageVisibilityOfOptionalFields(true);
        }

    }

    void addNewTransaction() {
        if (isRequiredFieldsFilled()) {
            Transaction transaction = new Transaction();
            transaction.setType(getTransactionTypeString());
            transaction.setAmount(getAmount());
            transaction.setCategory(getCategory());
            transaction.setDate(getDateInMillis());
            if (getTags() != null && !getTags().isEmpty()) transaction.setTags(getTags());

            viewModel.addTransaction(transaction);
            dismiss();
        }
    }

    void setTransactionType(boolean isIncome) {
        binding.incomeRadioButton.setChecked(isIncome);
        binding.expenseRadioButton.setChecked(!isIncome);
    }

    void setTimeInMillis(long timeInMillis) {
        if (timeInMillis != 0) mDateInMillis = timeInMillis;
        else mDateInMillis = System.currentTimeMillis();
    }

    void setDateInMillis(long dateInMillis) {
        if (dateInMillis != 0) mDateInMillis = dateInMillis;
        else mDateInMillis = System.currentTimeMillis();
    }

    void pickTime() {
        mCalender = Calendar.getInstance();
        mCalender.setTimeInMillis(mDateInMillis);
        TimePickerDialog dialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
            mCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalender.set(Calendar.MINUTE, minute);
            setTimeInMillis(mCalender.getTimeInMillis());
            setTimeToTextView(binding.timeTextView, mDateInMillis, true);
        }, mCalender.get(Calendar.HOUR_OF_DAY), mCalender.get(Calendar.MINUTE), false);
        dialog.show();
    }

    void pickDate() {
        mCalender = Calendar.getInstance();
        mCalender.setTimeInMillis(mDateInMillis);
        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            mCalender.set(Calendar.YEAR, year);
            mCalender.set(Calendar.MONTH, month);
            mCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setDateInMillis(mCalender.getTimeInMillis());
            setTimeToTextView(binding.dateTextView, mDateInMillis, false);
        }, mCalender.get(Calendar.YEAR), mCalender.get(Calendar.MONTH), mCalender.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    void setTimeToTextView(TextView view, long timeInMillis, boolean isTime) {
        Date date = new Date(timeInMillis);
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        if (isTime) format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        view.setText(format.format(date));
    }

    void setAmount(double amount) {
        binding.amountTextInputEditText.setText(String.valueOf(amount));
    }

    void setCategory(String category) {
        binding.categoryTextInputEditText.setText(category);
    }

    void setPaymentType(String paymentType) {
        binding.paymentTypeTextInputEditText.setText(paymentType);
    }

    void setMerchantName(String merchantName) {
        binding.merchantTextInputEditText.setText(merchantName);
    }

    void setNotes(String notes) {
        binding.noteEditText.setText(notes);
    }

    void initListeners() {
        binding.tagsEditText.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                addTag(textView.getText().toString().trim());
                return true;
            }
            return false;
        });
        binding.tagsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = binding.tagsEditText.getText().toString();
                if (input.contains(" ")) { // Delimiter for tag addition
                    addTag(input.trim());
                } else if (input.contains(",")) { // Delimiter for tag addition
                    addTag(input.replace(",", "").trim());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.scanCardView.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        binding.addTransactionButton.setOnClickListener(v -> addNewTransaction());
        binding.dateCardView.setOnClickListener(v -> pickDate());
        binding.timeCardView.setOnClickListener(v -> pickTime());
        binding.optionalFieldsTextLL.setOnClickListener(v -> manageVisibilityOfOptionalFields());
    }

    void manageVisibilityOfOptionalFields() {
        boolean isVisible = binding.optionalFieldsLinearLayout.getVisibility() == View.VISIBLE;
        TransitionManager.beginDelayedTransition(binding.fieldsLL, new AutoTransition());
        binding.optionalFieldsLinearLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);

        // rotate the arrow icon with animation
        if (isVisible)
            binding.optionalFieldsArrowIcon.animate().rotationBy(-180).setDuration(1000).start();
        else binding.optionalFieldsArrowIcon.animate().rotationBy(180).setDuration(1000).start();

    }

    void manageVisibilityOfOptionalFields(boolean visible) {
        TransitionManager.beginDelayedTransition(binding.fieldsLL, new AutoTransition());
        binding.optionalFieldsLinearLayout.setVisibility(visible ? View.VISIBLE : View.GONE);

        // rotate the arrow icon with animation
        if (visible)
            binding.optionalFieldsArrowIcon.animate().rotationBy(-180).setDuration(1000).start();
        else
            binding.optionalFieldsArrowIcon.animate().rotationBy(180).setDuration(1000).start();
    }

    private void setTagsFromResponse(Transaction transaction) {
        binding.chipGroup.removeAllViews();
        List<String> tags = transaction.getTags();

        if (tags != null) {
            for (String tag : tags) {
                addTag(tag);
            }
        }

    }

    private void addTag(String tagText) {

        // Check if maximum tags are already added
        if (binding.chipGroup.getChildCount() >= MAX_TAGS) {
            binding.tagsEditText.setError("Maximum " + MAX_TAGS + " tags allowed");
            return;
        }

        // Check if the tag is already added
        for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroup.getChildAt(i);
            if (chip.getText().toString().equalsIgnoreCase(tagText)) {
                binding.tagsEditText.setText("");
                return; // Tag already exists, don't add it again
            }
        }

        if (!TextUtils.isEmpty(tagText)) {

            Chip chip = new Chip(requireContext());
            chip.setText(tagText);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(view -> binding.chipGroup.removeView(chip));

            binding.chipGroup.addView(chip);
            binding.tagsEditText.setText("");
            binding.chipScrollView.post(() -> binding.chipScrollView.fullScroll(View.FOCUS_RIGHT));
        }
    }

    boolean getTransactionType() {
        return binding.incomeRadioButton.isChecked();
    }

    String getTransactionTypeString() {
        return getTransactionType() ? "Income" : "Expense";
    }

    long getTimeInMillis() {
        return mDateInMillis;
    }

    long getDateInMillis() {
        return mDateInMillis;
    }

    double getAmount() {
        if (binding.amountTextInputEditText.getText() != null && !binding.amountTextInputEditText.getText().toString().isEmpty()) {
            return Double.parseDouble(binding.amountTextInputEditText.getText().toString());
        } else {
            binding.amountTextInputEditText.setError("Amount is required");
            return 0;
        }
    }

    String getCategory() {
        if (binding.categoryTextInputEditText.getText() != null && !binding.categoryTextInputEditText.getText().toString().isEmpty()) {
            return binding.categoryTextInputEditText.getText().toString();
        } else {
            binding.categoryTextInputEditText.setError("Category is required");
            return "";
        }
    }

    List<String> getTags() {
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroup.getChildAt(i);
            tags.add(chip.getText().toString());
        }
        return tags;
    }

    boolean isRequiredFieldsFilled() {
        return getAmount() != 0 && !getCategory().isEmpty();
    }

    public void getTransactionFromImage(Bitmap imageBitmap) {


        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.responseMimeType = "application/json";

        ArrayList<SafetySetting> safetySettings = new ArrayList();
        safetySettings.add(new SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE));
        safetySettings.add(new SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE));
        safetySettings.add(new SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE));
        safetySettings.add(new SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE));

        Content content = new Content.Builder()
                .addImage(imageBitmap)
                .addText("scan this image")
                .build();

        Content.Builder systemInstruction = new Content.Builder()
                .addText(getInstructionText());


        GenerativeModel gm = new GenerativeModel(
                GEMINI_AI_MODEL,
                GEMINI_API_KEY,
                configBuilder.build(),
                safetySettings,
                new RequestOptions(),
                null,
                null,
                systemInstruction.build()
        );

        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                Log.i(TAG, "onSuccess: " + resultText);
                handler.post(() -> {
                    ResponseConverter converter = new ResponseConverter();
                    setup(converter.getTransactions(resultText));
                    binding.scanningMcv.setVisibility(View.GONE);
                });

            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                handler.post(() -> {
                    binding.scanningMcv.setVisibility(View.GONE);
                });
            }
        }, executor);
    }

    public String getInstructionText() {
        return "Analyze the receipt image provided and return a structured JSON response with the following fields:\n" +
                "\n" +
                "Transaction Type: Either \"Expense\" or \"Income\" based on whether this is a spending or earning receipt.\n" +
                "Category: Suggested category for the transaction, like \"Food\", \"Travel\", or \"Utilities\".\n" +
                "Tags: Keywords or tags to classify the transaction, such as \"groceries\", \"dining\", or \"fuel\" (limit to five tags).\n" +
                "Date: The date and time of the transaction in the format \"dd MMM yyyy hh\n" +
                "a\" (e.g., \"25 Oct 2024 04:30 PM\").\n" +
                "Amount: The total amount spent or received in the transaction.\n" +
                "Merchant Name: The name of the business where the transaction occurred.\n" +
                "Payment Method: Type of payment used, such as \"Cash\", \"Credit Card\", or \"Digital Payment\".\n" +
                "Emoji: A single emoji representing the transaction (e.g., \uD83C\uDF54 for food, \uD83D\uDE97 for transport).\n" +
                "Notes: Additional relevant information, like transaction ID or any detail inferred from the receipt.\n" +
                "Return the extracted information in this JSON format:\n" +
                "\n" +
                "{\n" +
                "  \"transaction_type\": \"Expense\",\n" +
                "  \"category\": \"Food\",\n" +
                "  \"tags\": [\"groceries\", \"dining\"],\n" +
                "  \"date\": \"25 Oct 2024 04:30 PM\",\n" +
                "  \"amount\": 56.75,\n" +
                "  \"merchant_name\": \"SuperMart\",\n" +
                "  \"payment_method\": \"Credit Card\",\n" +
                "  \"emoji\": \"\uD83C\uDF54\",\n" +
                "  \"notes\": \"Dinner with friends at SuperMart\"\n" +
                "}\n";
    }


}