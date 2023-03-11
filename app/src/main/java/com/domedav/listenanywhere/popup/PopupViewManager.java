package com.domedav.listenanywhere.popup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.math.MathUtils;
import com.domedav.animhelpers.QuickViewAnimator;
import com.domedav.animhelpers.QuickValueAnimator;
import com.domedav.listenanywhere.R;
import com.domedav.listenanywhere.databinding.ActivityMainBinding;
import com.domedav.navbarnavigation.NavbarBackHelper;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PopupViewManager {
    private static ActivityMainBinding m_binding;
    private static LayoutInflater m_inflater;
    private static Context m_context;
    private static Window m_window;
    private static LinearLayout m_popupWindow;
    private static ConstraintLayout m_popupAnimable;
    private static ViewGroup m_expandCollapseImg;
    private static int m_primaryMain_Color;
    private static int m_primaryDark_Color;
    private static int m_grayed_PrimaryMain_Color;
    private static int m_semitransDark_Color;
    private static final float INACTIVE_ALPHA = .3f;
    private static int m_workingID;

    @SuppressLint("ClickableViewAccessibility")
    public static void InitialisePopup(@NotNull Activity a, @NotNull ActivityMainBinding binding){
        // Auto Cleanup
        DestroyUnneeded();

        // Cache params
        m_binding = binding;

        // Get inflater service
        m_inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get helpers
        m_context = a.getApplicationContext();
        m_window = a.getWindow();

        // Cache layouts
        m_popupWindow = binding.popupLayout.popupPopulateMe;
        m_popupAnimable = m_binding.popupLayout.getRoot();

        m_expandCollapseImg = m_popupAnimable.findViewById(R.id.expand_collapse_img);

        // Cache colors
        m_primaryMain_Color = m_context.getColor(R.color.primary_main);
        m_primaryDark_Color = m_context.getColor(R.color.primary_dark);
        m_semitransDark_Color = m_context.getColor(R.color.semitrans_dark);
        m_grayed_PrimaryMain_Color = m_context.getColor(R.color.grayed_primary_main);

        m_popupAnimable.setOnTouchListener((v, event) -> {
            if(m_isClosing){return false;}  //You cant controll the panel, when its being closed

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    m_popupOffset = -event.getY();   // Offset our movement from the touch pos, needed so the view doesnt start jumping onto the pointer
                    break;

                case MotionEvent.ACTION_MOVE:
                    PopupMoving(m_popupOffset + event.getY());

                    if(m_popupTranslation > m_popupAnimable.getHeight() / 2f){  // If the view moved by 1/2th, auto close it, aesthetic design choice
                        // Exit via navbarback
                        NavbarBackHelper.NavigateBack();
                    }

                    break;

                case MotionEvent.ACTION_UP:
                    if(m_popupTranslation > m_popupAnimable.getHeight() / 6f){  // If the view moved by 1/6th, close it
                        // Exit via navbarback
                        NavbarBackHelper.NavigateBack();
                    }
                    else{
                        // Get our pos back
                        QuickViewAnimator.AnimateTranslationY(m_popupAnimable, m_popupAnimable.getTranslationY(), 0, QuickValueAnimator.ANIMATION_SHORT_MS);
                        QuickViewAnimator.AnimateScaleX(m_expandCollapseImg, m_expandCollapseImg.getScaleX(), 1f, QuickValueAnimator.ANIMATION_SHORT_MS);
                        m_popupTranslation = 0;
                    }
                    break;
            }
            return true;
        });
    }

    public interface PopupPopulator{
        void Populate();
    }

    /**
     * Shows the popup window for the user
     * @param populator event, which handles populating
     */
    public static void ShowPopupWindow(@NonNull PopupPopulator populator){
        if(m_isClosing || m_popupShowing){return;}
        // Animated Change
        QuickValueAnimator.AnimateColorChange(m_primaryMain_Color, m_primaryDark_Color, QuickValueAnimator.ANIMATION_SHORT_MS, m_window::setNavigationBarColor);
        QuickValueAnimator.AnimateColorChange(m_primaryMain_Color, m_grayed_PrimaryMain_Color, QuickValueAnimator.ANIMATION_LONG_MS, m_window::setStatusBarColor);
        QuickValueAnimator.AnimateColorAlpha(m_semitransDark_Color, 0, Color.alpha(m_semitransDark_Color), QuickValueAnimator.ANIMATION_LONG_MS, m_binding.darkenLayer::setBackgroundColor);

        // Make our popup views visible
        m_popupAnimable.setVisibility(View.VISIBLE);
        m_binding.darkenLayer.setVisibility(View.VISIBLE);

        // Handle popup based on given config (populating)
        populator.Populate();

        // Set the layout really down, so it doesnt just pop into existence for 1 frame
        m_popupAnimable.setTranslationY(99999f);

        // Set scale, without anim
        m_popupAnimable.setScaleX(1f);
        m_popupAnimable.setScaleY(1f);
        m_expandCollapseImg.setScaleX(1f);

        // We have to wait 1 frame, so the layout height and stuff can be calculated
        m_popupAnimable.post(() -> QuickViewAnimator.AnimateTranslationY(m_popupAnimable, m_popupAnimable.getHeight(), 0f, QuickValueAnimator.ANIMATION_LONG_MS, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                m_popupShowing = true;

                // Register backpress for popup if possible
                NavbarBackHelper.RegisterAction(PopupViewManager::ExitPopupWindow);
            }
        }));
    }

    /**
     * If we did not have any workingID related pooling helper, you have to make one
     */
    private static void CreateNewPopupState(){
        // If it doesnt exist, make a new one
        if(!PopupViewStoringHelper.PoolExists(m_workingID)){
            PopupViewStoringHelper.RequestNewPool(m_workingID);
        }
    }

    /**
     * Removes all children of the popup window (makes it blank)<br>
     * Recommended to use before populating!
     * @param destroy Destroy all workingID pooled views (true)<br>Hide previously shown views (false)
     */
    public static void ClearPopup(boolean destroy){
        if(destroy){
            // Get our pool, and destroy it
            PopupViewStoringHelper pool = PopupViewStoringHelper.GetPoolByID(m_workingID);
            if(pool == null){
                return;
            }
            pool.ReturnPool();
            return;
        }
        // Get our pool, and hide views
        PopupViewStoringHelper pool = PopupViewStoringHelper.GetPoolByID(m_workingID);
        if(pool == null){
            return;
        }
        pool.HideAllViews();
    }

    /**
     * Simple way of creating a popup<br><h5>Old, complex mode compressed into 1 method, old methods have been gotten rid of
     * @param workingID The id to work with (get from <b>PopupViewWorkingIDs</b> class)
     * @param strheader_res_id The res ID of the header (R.string.<i><b>resID</b></i>)
     * @return If you should create the views or not
     */
    public static boolean SimplifiedPopupStarter(int workingID, int strheader_res_id){
        m_popupTranslation = 0;
        // Set our workingID
        m_workingID = workingID;
        // Clear any previous popup remains
        ClearPopup(false);
        // Set the main header txt
        ((TextView)m_popupAnimable.findViewById(R.id.popup_header_title)).setText(strheader_res_id);
        // Show pooled content, if there is a pool
        if(PopupViewStoringHelper.PoolExists(m_workingID)){
            // If No view could be shown, create them again, otherwise just ignore
            return !Objects.requireNonNull(PopupViewStoringHelper.GetPoolByID(m_workingID)).ShowAllViews();
        }
        // If not, create one
        CreateNewPopupState();
        return true;
    }

    /**
     * Creates a spinner, using the given string array resource ID
     * @param strheader_res_id The res ID of the header (R.string.<i><b>resID</b></i>)
     * @param strarray_res_id The res ID of the spinner content (R.array.<i><b>resID</b></i>)
     */
    public static void MakeDropdown(int strheader_res_id, int strarray_res_id, @NotNull AdapterView.OnItemSelectedListener callback, int startingSelectionIndex){
        // Make a new adapter for the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(m_context, strarray_res_id, R.layout.spinner_item);
        // Set dropdown view to default
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        // Instantiate a new spinner layout
        ConstraintLayout layout = (ConstraintLayout)m_inflater.inflate(R.layout.settings_dropdown, null);
        // Set header txt
        ((TextView)layout.findViewById(R.id.setting_header)).setText(strheader_res_id);
        // Get spinner
        AppCompatSpinner spinner = layout.findViewById(R.id.setting_interactable);
        // Apply adapter to spinner
        spinner.setAdapter(adapter);
        // Set layout as child object
        m_popupWindow.addView(layout);
        // Set our selection index
        spinner.setSelection(startingSelectionIndex);
        // Set event
        spinner.setOnItemSelectedListener(callback);
        // Store it in helper
        Objects.requireNonNull(PopupViewStoringHelper.GetPoolByID(m_workingID)).StoreView(layout);
    }

    /**
     * Creates a toggle
     * @param strheader_res_id The res ID of the header (R.string.<i><b>resID</b></i>)
     */
    public static void MakeToggle(int strheader_res_id, @NotNull CompoundButton.OnCheckedChangeListener callback, boolean startingState){
        // Instantiate a new toggle layout
        ConstraintLayout layout = (ConstraintLayout)m_inflater.inflate(R.layout.settings_toggle, null);
        // Set header txt
        ((TextView)layout.findViewById(R.id.setting_header)).setText(strheader_res_id);
        // Get toggle
        SwitchCompat toggle = layout.findViewById(R.id.setting_interactable);
        // Set layout as child object
        m_popupWindow.addView(layout);
        // Set our starting value
        toggle.setChecked(startingState);
        // Apply click event to toggle
        toggle.setOnCheckedChangeListener(callback);
        // Store it in helper
        Objects.requireNonNull(PopupViewStoringHelper.GetPoolByID(m_workingID)).StoreView(layout);
    }

    /**
     * Creates a checkbox
     */
    public static void MakeCheckbox(int strheader_res_id, @NotNull CompoundButton.OnCheckedChangeListener callback, boolean startingState){
        // Instantiate a new toggle layout
        ConstraintLayout layout = (ConstraintLayout)m_inflater.inflate(R.layout.settings_checkbox, null);
        // Set header txt
        ((TextView)layout.findViewById(R.id.setting_header)).setText(strheader_res_id);
        AppCompatCheckBox checkbox = layout.findViewById(R.id.setting_interactable);
        // Set layout as child object
        m_popupWindow.addView(layout);
        // Set our starting value
        checkbox.setChecked(startingState);
        // Apply click event to checkbox
        checkbox.setOnCheckedChangeListener(callback);
        // Store it in helper
        Objects.requireNonNull(PopupViewStoringHelper.GetPoolByID(m_workingID)).StoreView(layout);
    }

    public static void MakeMiniHeader(int strheader_res_id){
        // Instantiate a new toggle layout
        ConstraintLayout layout = (ConstraintLayout)m_inflater.inflate(R.layout.settings_miniheader, null);
        // Set header txt
        ((TextView)layout.findViewById(R.id.setting_header)).setText(strheader_res_id);
        // Set layout as child object
        m_popupWindow.addView(layout);
        // Store it in helper
        Objects.requireNonNull(PopupViewStoringHelper.GetPoolByID(m_workingID)).StoreView(layout);
    }

    /**
     * Sets a view to interactable or passive, based on the given params (graying out options)
     * @param viewIndex The index of the view, we want to modify (The creation order position matches with its index)
     * @param isInteractable The state we want to modify the view to
     */
    public static void SetInteractableViewState(int viewIndex, boolean isInteractable){
        float alpha = isInteractable ? 1f : INACTIVE_ALPHA;
        // Get layout
        View layout = Objects.requireNonNull(PopupViewStoringHelper.GetPoolByID(m_workingID)).get_PopupViews().get(viewIndex);
        // Set layout alpha
        layout.setAlpha(alpha);
        // Set clickable state accordingly
        layout.findViewById(R.id.setting_interactable).setEnabled(isInteractable);
    }

    /**
     * EXIT VIA NAVBAR BACK!!!<br>
     * Handles the exiting of the popup window
     */
    private static void ExitPopupWindow(){
        if(m_isClosing || !m_popupShowing){return;}
        m_isClosing = true;
        m_popupShowing = false;

        //Animate it from current translation pos, to an out of bounds one
        QuickViewAnimator.AnimateTranslationY(m_popupAnimable, m_popupAnimable.getTranslationY(), m_popupAnimable.getHeight(), QuickValueAnimator.ANIMATION_LONG_MS, new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation){
                super.onAnimationEnd(animation);
                // If its out of bounds, hide its view / helper views
                m_popupAnimable.setVisibility(View.GONE);
                m_binding.darkenLayer.setVisibility(View.GONE);
                // Reset debounce
                m_isClosing = false;
                m_popupShowing = false;
            }
        });

        // Animate scale
        QuickViewAnimator.AnimateScaleAll(m_popupAnimable, m_popupAnimable.getScaleX(), .5f, QuickValueAnimator.ANIMATION_SHORT_MS);
        QuickViewAnimator.AnimateScaleX(m_expandCollapseImg, m_expandCollapseImg.getScaleX(), .65f, QuickValueAnimator.ANIMATION_SHORT_MS);

        // Animated Hiding
        QuickValueAnimator.AnimateColorChange(m_primaryDark_Color, m_primaryMain_Color, QuickValueAnimator.ANIMATION_SHORT_MS, m_window::setNavigationBarColor);
        QuickValueAnimator.AnimateColorChange(m_grayed_PrimaryMain_Color, m_primaryMain_Color, QuickValueAnimator.ANIMATION_SHORT_MS, m_window::setStatusBarColor);
        QuickValueAnimator.AnimateColorAlpha(m_semitransDark_Color, Color.alpha(m_semitransDark_Color), 0, QuickValueAnimator.ANIMATION_SHORT_MS, m_binding.darkenLayer::setBackgroundColor);
    }

    /**
     * Sets the popup window maximum extending height, so it doesnt take up full screen, even if it could
     * @param percent value between 0 - 100
     */
    public static void SetMaximumExtendPercent(@FloatRange(from = 0f, to = 100f) float percent){
        // Get the current layout parameters of the button
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)m_binding.popupLayout.popupMainfill.getLayoutParams();
        // Calculate the max height based on the screen height and the maximum height percentage
        DisplayMetrics displayMetrics = m_context.getResources().getDisplayMetrics();
        float screenHeight = displayMetrics.heightPixels / displayMetrics.density;
        // Convert % to dp
        layoutParams.matchConstraintMaxHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, screenHeight * percent / 100f, displayMetrics);
        // Apply our new values to the layout
        m_binding.popupLayout.popupMainfill.setLayoutParams(layoutParams);
    }

    private static float m_popupTranslation = 0f;
    private static float m_popupOffset = 0f;

    /**
     * Moves the popup by given amount on the Y axis
     */
    private static void PopupMoving(float moveby){
        m_popupTranslation += moveby;
        m_expandCollapseImg.setScaleX(MathUtils.clamp(1f - (m_popupTranslation / m_popupAnimable.getHeight()), .65f, 1.25f));
        // Clamp values
        if(m_popupTranslation < 0f){
            m_popupTranslation = 0f;
            m_popupAnimable.setTranslationY(0f);
            return;
        }
        m_popupAnimable.setTranslationY(m_popupTranslation);
    }

    // Open-Close debounce values, needed so animation cant break
    private static boolean m_isClosing = false;
    private static boolean m_popupShowing = false;

    /**
     * Must be called when this class is no longer used, so we dont cause a mem leak
     */
    public static void DestroyUnneeded(){
        m_binding = null;
        m_inflater = null;
        m_context = null;
        m_window = null;
        m_popupWindow = null;
        m_popupAnimable = null;

        PopupViewStoringHelper.DestroyAll();
    }
}