package com.domedav.listenanywhere;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;
import com.domedav.listenanywhere.databinding.ActivityStorageManagmentBinding;

public class StorageManagmentActivity extends AppCompatActivity {
    private ActivityStorageManagmentBinding m_binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set basic activity params
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // Our flags are the same as the mask
        int l_flags = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND |
                WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        this.getWindow().setFlags(l_flags, l_flags);
        // Show splash screen, this is the starting activity
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        // Enable vector res extension
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        // Customize workspace, to match our theming
        getWindow().setNavigationBarColor(getResources().getColor(R.color.primary_main, null));
        getWindow().setStatusBarColor(getResources().getColor(R.color.primary_main, null));

        m_binding = ActivityStorageManagmentBinding.inflate(getLayoutInflater());
        setContentView(m_binding.getRoot());
    }
}