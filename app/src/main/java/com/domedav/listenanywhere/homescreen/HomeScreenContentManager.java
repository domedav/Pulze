package com.domedav.listenanywhere.homescreen;

import android.animation.AnimatorListenerAdapter;
import android.view.ViewGroup;
import com.domedav.animhelpers.QuickValueAnimator;
import com.domedav.animhelpers.QuickViewAnimator;
import java.util.ArrayList;

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
        boolean first = true;
        for (ViewGroup m_layoutObject : m_layoutObjects) {
            // Animate
            QuickViewAnimator.AnimateTranslationX(layout, HomeScreenBottomNaviControlPanelManager.GetScreenWidth() * -animDirModifier, 0f, QuickValueAnimator.ANIMATION_LONG_MS, first ? callback : null);
            QuickViewAnimator.AnimateScaleAll(m_layoutObject, 0f, 1f, QuickValueAnimator.ANIMATION_LONG_MS);
            // Only set callback to our 1st object
            //QuickValueAnimator.AnimateFloat(0f, 1f, QuickValueAnimator.ANIMATION_SHORT_MS, animation -> m_layoutObject.setAlpha((float)animation.getAnimatedValue()));

            if(first){first = false;}
        }
    }

    public void Reset(ViewGroup placeboo, float animDirModifier){
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
     * Destroys this class
     */
    public void DestroyMe() {
        m_layoutObjects = null;
    }
}