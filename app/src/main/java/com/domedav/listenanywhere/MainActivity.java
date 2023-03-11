package com.domedav.listenanywhere;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;
import com.domedav.listenanywhere.databinding.ActivityMainBinding;
import com.domedav.listenanywhere.homescreen.HomeScreenMusicControlPanelManager;
import com.domedav.listenanywhere.popup.PopupViewManager;
import com.domedav.listenanywhere.popup.PopupViewWorkingIDs;
import com.domedav.navbarnavigation.NavbarBackHelper;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding m_binding;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set basic activity params
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // Our flags are the same as the mask
        int l_flags = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_BLUR_BEHIND |
                        //WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        this.getWindow().setFlags(l_flags, l_flags);
        // Show splash screen, this is the starting activity
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        // Clear NavbarBackHelper registered actions (if theres any)
        NavbarBackHelper.Reset();
        // Enable vector res extension
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        // Customize workspace, to match our theming
        getWindow().setNavigationBarColor(getResources().getColor(R.color.primary_main, null));
        getWindow().setStatusBarColor(getResources().getColor(R.color.primary_main, null));

        m_binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(m_binding.getRoot());
        // Register events \/
        // Init popup mgr
        PopupViewManager.InitialisePopup(this, m_binding);
        // Set the max popup window extend height, default is 60%
        PopupViewManager.SetMaximumExtendPercent(60f);

        // Assign settings popup
        findViewById(R.id.settings_button).setOnClickListener(v -> PopupViewManager.ShowPopupWindow(() -> {
            // Init new popup
            if (!PopupViewManager.SimplifiedPopupStarter(PopupViewWorkingIDs.POPUP_SETTINGS_ID, R.string.settings)) {
                return;
            }

            PopupViewManager.MakeMiniHeader(R.string.settings_streaming_header);

            //populate settings
            PopupViewManager.MakeToggle(R.string.settings_override_kbps, (buttonView, isChecked) -> {
                // Show/Hide dropdown interactivity, based on this toggle
                PopupViewManager.SetInteractableViewState(2, isChecked);
            }, false);

            PopupViewManager.MakeDropdown(R.string.settings_kbps_target, R.array.settings_kbps_target_dropdown, new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.e("TAG", "onItemSelected: " + position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            }, 3);

            PopupViewManager.MakeCheckbox(R.string.settings, (buttonView, isChecked) -> {

            }, true);

            PopupViewManager.SetInteractableViewState(2, false);    // Set interactable default state
        }));

        m_binding.darkenLayer.setOnClickListener(v -> NavbarBackHelper.NavigateBack()); // Tapping anywhere should also close popup

        // music layout
        HomeScreenMusicControlPanelManager.InitialiseMusicLayoutHelper(m_binding);

        m_binding.musicnaviLayout.setOnTouchListener((v, event) -> {
            HomeScreenMusicControlPanelManager.LayoutMove(event);
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if(NavbarBackHelper.NavigateBack()){return;}
        // We are at homescreen, so we shall exit the app with this back
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        // Clean our trash
        PopupViewManager.DestroyUnneeded();
        HomeScreenMusicControlPanelManager.DestroyUnneeded();
        // Main cleansing
        super.onDestroy();
    }
}