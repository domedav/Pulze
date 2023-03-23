package com.domedav.listenanywhere.homescreen;

import android.animation.AnimatorListenerAdapter;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.domedav.animhelpers.QuickValueAnimator;
import com.domedav.animhelpers.QuickViewAnimator;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The main mgr of the content, displayed on the homescreen
 * <br><h3>(<i>This is Multi instance</i>)</h3></br>
 */
public class HomeScreenContentManager {
    private ArrayList<ViewGroup> m_layoutObjects = new ArrayList<>();

    /**
     * Stores the given View, helping with layout rebuilding
     */
    public void StoreView(ViewGroup v){ m_layoutObjects.add(v); }

    /**
     * Hides all cached views
     * @param animDirModifier The direction of the animation (1f or -1f)
     */
    public void HideAll(float animDirModifier, AnimatorListenerAdapter callback){
        boolean first = true;
        for (ViewGroup m_layoutObject : m_layoutObjects) {
            if(!IsVisibleOnScreen(m_layoutObject)){continue;} // Skip layouts, until we find the ones, which need to be visible

            // Animate
            QuickViewAnimator.AnimateTranslationX(m_layoutObject, 0f, HomeScreenBottomNaviControlPanelManager.GetScreenWidth() * animDirModifier, QuickValueAnimator.ANIMATION_LONG_MS, first ? callback : null);
            QuickViewAnimator.AnimateScaleAll(m_layoutObject, 1f, 0f, QuickValueAnimator.ANIMATION_LONG_MS);

            // Only set callback to our 1st object
            //QuickValueAnimator.AnimateFloat(1f, 0f, QuickValueAnimator.ANIMATION_LONG_MS, animation -> m_layoutObject.setAlpha((float)animation.getAnimatedValue()));

            if(first){first = false;}
        }
    }

    /**
     * Shows all cached views
     * @param animDirModifier The direction of the animation (1f or -1f)
     */
    public void ShowAll(float animDirModifier, AnimatorListenerAdapter callback, ViewGroup layout){
        // Animate layout
        QuickViewAnimator.AnimateTranslationX(layout, HomeScreenBottomNaviControlPanelManager.GetScreenWidth() * -animDirModifier, 0f, QuickValueAnimator.ANIMATION_LONG_MS, callback);
        for (ViewGroup m_layoutObject : m_layoutObjects) {

            // Rescale all
            m_layoutObject.setScaleX(1f);
            m_layoutObject.setScaleY(1f);

            if(IsWannabeVisibleOnScreen(m_layoutObject)){continue;} // Skip layouts, until we find the ones, which needs to be visible
            // Only animate scaling of the ones, which will be visible
            QuickViewAnimator.AnimateScaleAll(m_layoutObject, 0f, 1f, QuickValueAnimator.ANIMATION_LONG_MS);

            // Only set callback to our 1st object
            //QuickValueAnimator.AnimateFloat(0f, 1f, QuickValueAnimator.ANIMATION_SHORT_MS, animation -> m_layoutObject.setAlpha((float)animation.getAnimatedValue()));
        }
    }

    public void Reset(@NonNull ViewGroup placeboo, float animDirModifier){
        // Reset layout posX
        placeboo.setTranslationX(HomeScreenBottomNaviControlPanelManager.GetScreenWidth() * animDirModifier);

        for (ViewGroup m_layoutObject : m_layoutObjects) {
            // If we dont do this, the whole layout will be fucked
            // I have no idea how the objects are being offset, nor why, but this fixes the issue
            m_layoutObject.setTranslationX(0f);
            m_layoutObject.setTranslationY(0f);
        }
    }

    /**
     * Sets the visibility of all views stored in this class
     */
    public void SetVisibilityState(int visibility){
        for (ViewGroup m_layoutObject : m_layoutObjects) {
            m_layoutObject.setVisibility(visibility);
        }
    }

    /**
     * Reparents all of the views, stored by this, to the given layout
     * @param newparent The ViewGroup we want to reparent everything to
     */
    public void ReparentViews(ViewGroup newparent){
        int l_posOffset = 1;
        for (ViewGroup m_layoutObject : m_layoutObjects) {

            // If the object has a parent, we unparent it
            if(m_layoutObject.getParent() != null){
                ((ViewGroup)m_layoutObject.getParent()).removeView(m_layoutObject);
            }

            if(newparent.getChildAt(l_posOffset) == null){
                l_posOffset = -1;
            }

            newparent.addView(m_layoutObject, l_posOffset);

            // We need to increase it, so the layout wont get flipped
            l_posOffset++;
        }
    }

    /**
     * Checks if the given view is visible on the screen or not (used for optimised hide/show)
     */
    private boolean IsVisibleOnScreen(@NonNull ViewGroup view){
        return view.getGlobalVisibleRect(new Rect());
    }

    /**
     * Similar to <b>IsVisibleOnScreen</b> but its predictive<br></br>Meaning: it will determine if the view, which is comming onto the screen, will be visible by the user, used by <i>ShowAll()</i>
     */
    private boolean IsWannabeVisibleOnScreen(@NonNull ViewGroup view){
        int[] loc = new int[2];
        view.getLocationOnScreen(loc);
        // Check if Y pos is near to the top, as the layout scroll position will be reset to 0, we can use that as our basepoint
        // If the view Y value is less than the screen size, it is 100% out of view
        return loc[1] > HomeScreenBottomNaviControlPanelManager.GetScreenHeight();
    }

    /**
     * Destroys this class
     */
    public void DestroyMe() {
        m_layoutObjects = null;
    }
}