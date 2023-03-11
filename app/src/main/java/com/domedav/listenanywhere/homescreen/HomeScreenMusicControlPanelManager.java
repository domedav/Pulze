package com.domedav.listenanywhere.homescreen;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.core.math.MathUtils;

import com.domedav.animhelpers.QuickValueAnimator;
import com.domedav.animhelpers.QuickViewAnimator;
import com.domedav.listenanywhere.R;
import com.domedav.listenanywhere.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;
public class HomeScreenMusicControlPanelManager {
    private static ActivityMainBinding m_binding;
    private static ViewGroup m_musiclayout;
    private static ViewGroup m_expandCollapseImg;
    private static ViewGroup m_musicnaviCollapsedLayout;
    private static ViewGroup m_musicnaviExpandedLayout;
    private static float m_targetHeight = -200f;
    public static void InitialiseMusicLayoutHelper(@NotNull ActivityMainBinding binding){
        m_binding = binding;
        m_musiclayout = m_binding.musicnaviLayout;
        m_expandCollapseImg = m_binding.expandCollapseImg;

        m_musicnaviExpandedLayout = m_musiclayout.findViewById(R.id.musicnavi_navinormal);
        m_musicnaviCollapsedLayout = m_musiclayout.findViewById(R.id.musicnavi_navicollapsed);

        m_musiclayout.post(() -> {  // Frame skip, so android has time to process the layout height properly
            m_targetHeight = -m_musicnaviCollapsedLayout.getHeight();   // Assign max expandable height
        });

        m_expandCollapseImg.setScaleX(.65f);    // Start as collapsed
    }

    private static boolean m_isExpanded = false;

    private static boolean m_interactionLocked = false;

    /**
     * Handles layoutmoving events
     */
    public static void LayoutMove(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                m_interactionLocked = false;
                m_popupOffset = -event.getY();   // Offset our movement from the touch pos, needed so the view doesnt start jumping onto the pointer
                break;
            case MotionEvent.ACTION_MOVE:
                if(m_interactionLocked){return;}
                MoveBy(m_popupOffset + event.getY());

                if(Math.abs(m_popupTranslation) > -m_targetHeight / 2f){  // If the view moved by 1/2th, clamp it accordingly
                    m_interactionLocked = true;

                    if(m_isExpanded){Collapse();}   // Handle expand-collapse based on movement
                    else{Expand();}
                }
                break;
            case MotionEvent.ACTION_UP:
                m_interactionLocked = false;
                QuickViewAnimator.AnimateScaleX(m_expandCollapseImg, m_expandCollapseImg.getScaleX(), .65f, QuickValueAnimator.ANIMATION_SHORT_MS);
                break;
        }
    }

    private static float m_popupTranslation = 0f;
    private static float m_popupOffset = 0f;

    /**
     * Moves the layout by given amount on the Y axis
     */
    private static void MoveBy(float moveby){
        m_popupTranslation += moveby;

        m_expandCollapseImg.setScaleX(MathUtils.clamp(Math.abs(m_popupTranslation) / (float)m_musiclayout.getHeight(), .65f, 1.25f));

        if(m_popupTranslation > 0f){
            m_popupTranslation = 0f;
            m_musiclayout.setTranslationY(0f);
            return;
        }
        else if(m_popupTranslation < m_targetHeight){
            m_popupTranslation = m_targetHeight;
            m_musiclayout.setTranslationY(m_targetHeight);
        }
        m_musiclayout.setTranslationY(m_popupTranslation);
    }

    /**
     * Collapses the layout
     */
    private static void Collapse(){

    }

    /**
     * Expands the layout
     */
    private static void Expand(){

    }

    /**
     * Must be called when this class is no longer used, so we dont cause a mem leak
     */
    public static void DestroyUnneeded(){
        m_binding = null;
        m_musiclayout = null;
        m_expandCollapseImg = null;
        m_musicnaviCollapsedLayout = null;
        m_musicnaviExpandedLayout = null;
    }
}