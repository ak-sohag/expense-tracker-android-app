package com.example.wallethistory;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.wallethistory.databinding.ActivityMainBinding;
import com.example.wallethistory.ui.fragments.NewTransactionFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.ConfigUpdate;
import com.google.firebase.remoteconfig.ConfigUpdateListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // TODO: Update your api key in firebase remote config or you can hard code hare for testing purpose only.
    public static String GEMINI_API_KEY = "null";
    public static String GEMINI_AI_MODEL = "gemini-1.5-pro-002";

    ActivityMainBinding binding;
    FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        fetchRemoteConfig();
        configNavController();
        binding.fabAddNewTransaction.setOnClickListener(v -> {
            NewTransactionFragment newTransactionFragment = new NewTransactionFragment();
            newTransactionFragment.show(getSupportFragmentManager(), "newTransactionFragment");
        });


        // Get the BottomAppBar height and set it as margin for the FragmentContainerView
        binding.bottomAppBar.post(() -> {
            int bottomAppBarHeight = binding.bottomAppBar.getHeight();  // Get the BottomAppBar height

            // Set margin for the FragmentContainerView dynamically
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.navHostFragment.getLayoutParams();
            params.bottomMargin = bottomAppBarHeight;  // Apply BottomAppBar height as bottom margin
            binding.navHostFragment.setLayoutParams(params);
        });


    }

    private void configNavController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

    private void fetchRemoteConfig() {

        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            updateGeminiSettings();
                        }
                    }
                });
        remoteConfig.addOnConfigUpdateListener(new ConfigUpdateListener() {
            @Override
            public void onUpdate(@NonNull ConfigUpdate configUpdate) {
                updateGeminiSettings();
            }

            @Override
            public void onError(@NonNull FirebaseRemoteConfigException error) {

            }
        });

    }

    void updateGeminiSettings() {
        GEMINI_API_KEY = remoteConfig.getValue("GEMINI_API_KEY").asString();
        GEMINI_AI_MODEL = remoteConfig.getValue("GEMINI_AI_MODEL").asString();
    }
}