package com.domedav.listenanywhere.homescreen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.core.math.MathUtils;
import com.domedav.animhelpers.QuickValueAnimator;
import com.domedav.listenanywhere.R;
import com.domedav.listenanywhere.databinding.ActivityMainBinding;
import org.jetbrains.annotations.NotNull;

public class HomeScreenBottomNaviControlPanelManager {
    private static ActivityMainBinding m_binding;
    private static LayoutInflater m_inflater;
    private static int m_currentlyActiveIndex = 0;
    private static ImageButton m_libraryMAINBTN;
    private static ImageButton m_searchMAINBTN;
    private static ImageButton m_downloadsMAINBTN;
    private static ImageButton[] m_mainButtons;
    private static HomeScreenContentManager[] m_homeScreenManagers;
    private static final float UNSELECTED_ALPHA_VALUE = .5f;
    private static final float SELECTED_ALPHA_VALUE = 1f;
    private static float m_screenWidth; // Needed for ContentManager anims

    /**
     * Initialise this class, prepare it for use
     */
    public static void InitialiseBottomNaviManager(@NotNull Activity a, @NotNull ActivityMainBinding binding){
        m_binding = binding;

        // Assign main buttons
        m_libraryMAINBTN = m_binding.bottomLayout.library;
        m_searchMAINBTN = m_binding.bottomLayout.search;
        m_downloadsMAINBTN = m_binding.bottomLayout.downloads;
        m_mainButtons = GetMainBtnsAsArray();

        // Assign Events
        for (int i = 0; i < m_mainButtons.length; i++) {
            final int finalI = i;
            m_mainButtons[i].setOnClickListener(v -> SwitchTo(finalI));
        }
        // Get inflater service
        m_inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        m_screenWidth = m_binding.getRoot().getWidth();

        // Fill array up with appropriate values
        m_homeScreenManagers = new HomeScreenContentManager[]{
                new HomeScreenContentManager(), // Library
                new HomeScreenContentManager(), // Search
                new HomeScreenContentManager()  // Downloads
        };
        
        // Populate
        for (HomeScreenContentManager m_homeScreenManager : m_homeScreenManagers) {
            for (int i = 0; i < 20; i++) {
                // This is a dumbass way to get the root ViewGroup, but android doesnt provide a way to get the inflated layout as ViewGroup
                ViewGroup vg = (ViewGroup)m_inflater.inflate(R.layout.music_item, null).findViewById(R.id.item_music_play).getParent();
                // Store the viewgroup
                m_homeScreenManager.StoreView(vg);
            }
            m_homeScreenManager.SetVisibilityState(ViewGroup.GONE);
            m_homeScreenManager.ReparentViews(binding.unusedUielementContainer);
        }

        // Handle lib views seperately
        m_homeScreenManagers[0].SetVisibilityState(ViewGroup.VISIBLE);
        m_homeScreenManagers[0].ReparentViews(binding.homefillable);
    }

    /**
     * Switches the view to the given index
     */
    private static void SwitchTo(int windowindex){
        if(m_currentlyActiveIndex == windowindex){ return; }  // Dont change, if we are already on that window

        final int l_chachedPreviousActiveIndex = m_currentlyActiveIndex;

        // Animate the change on the icons
        QuickValueAnimator.AnimateFloat(SELECTED_ALPHA_VALUE, UNSELECTED_ALPHA_VALUE, QuickValueAnimator.ANIMATION_SHORT_MS, animation -> m_mainButtons[l_chachedPreviousActiveIndex].setAlpha((float)animation.getAnimatedValue()));
        QuickValueAnimator.AnimateFloat(UNSELECTED_ALPHA_VALUE, SELECTED_ALPHA_VALUE, QuickValueAnimator.ANIMATION_SHORT_MS, animation -> m_mainButtons[windowindex].setAlpha((float)animation.getAnimatedValue()));

        // Set placeboo visible
        m_binding.homefillablePlaceboo.setVisibility(View.VISIBLE);

        // Show target layout elements
        m_homeScreenManagers[windowindex].SetVisibilityState(ViewGroup.VISIBLE);

        // Reparent the target elements to the placeboo, so it looks like its drawing on top of the currently active ones, but they arent interactable
        m_homeScreenManagers[windowindex].ReparentViews(m_binding.homefillablePlaceboo);

        // Determine the change direction, and animate based on that
        float l_changeDir = MathUtils.clamp(l_chachedPreviousActiveIndex - windowindex, -1, 1);

        // Reset contents
        m_homeScreenManagers[windowindex].Reset(m_binding.homefillablePlaceboo, l_changeDir);

        // Animation callback handling
        // Hide previous layout elements
        m_homeScreenManagers[l_chachedPreviousActiveIndex].HideAll(l_changeDir, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                // Hide ourselves
                m_homeScreenManagers[l_chachedPreviousActiveIndex].ReparentViews(m_binding.unusedUielementContainer);
                m_homeScreenManagers[l_chachedPreviousActiveIndex].SetVisibilityState(ViewGroup.GONE);
            }
        });

        m_homeScreenManagers[windowindex].ShowAll(l_changeDir, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Reparent our views to the actual scrollview, so we can use them as needed
                m_homeScreenManagers[windowindex].ReparentViews(m_binding.homefillable);
                // Set placeboo hidden
                m_binding.homefillablePlaceboo.setVisibility(View.GONE);
            }
        }, m_binding.homefillablePlaceboo);

        // Change our current layout index
        m_currentlyActiveIndex = windowindex;
    }

    //TODO: make this filled with pointers
    /**
     * Gets the main buttons as an array, for simple handling
     */
    private static ImageButton[] GetMainBtnsAsArray() { return new ImageButton[] { m_libraryMAINBTN, m_searchMAINBTN, m_downloadsMAINBTN }; }

    /**
     * Get the width of the screen (binding root)
     */
    public static float GetScreenWidth(){ return m_screenWidth; }

    /**
     * Must be called when this class is no longer used, so we dont cause a mem leak
     */
    public static void DestroyUnneeded(){
        m_binding = null;
        m_inflater = null;
        m_libraryMAINBTN = null;
        m_searchMAINBTN = null;
        m_downloadsMAINBTN = null;
        m_mainButtons = null;
        for (HomeScreenContentManager m_homeScreenManager : m_homeScreenManagers) {
            m_homeScreenManager.DestroyMe();
        }
    }
}
