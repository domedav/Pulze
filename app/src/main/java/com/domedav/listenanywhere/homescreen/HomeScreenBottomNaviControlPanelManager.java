package com.domedav.listenanywhere.homescreen;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.domedav.animhelpers.QuickValueAnimator;
import com.domedav.listenanywhere.databinding.ActivityMainBinding;
import org.jetbrains.annotations.NotNull;

public class HomeScreenBottomNaviControlPanelManager {
    private static ActivityMainBinding m_binding;
    private static Context m_context;
    private static LayoutInflater m_inflater;
    private static int m_currentlyActiveIndex = 0;

    /**
     * Initialise this class, prepare it for use
     */
    public static void InitialiseBottomNaviManager(@NotNull Activity a, @NotNull ActivityMainBinding binding){
        m_binding = binding;
        m_context = a.getApplicationContext();
        // Get inflater service
        m_inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Switches the view to the given index
     */
    private static void SwitchTo(int windowindex){
        if(m_currentlyActiveIndex == windowindex){return;}
        m_currentlyActiveIndex = windowindex;                     //prevent multi init call of the same layout
    }

    /**
     * Must be called when this class is no longer used, so we dont cause a mem leak
     */
    public static void DestroyUnneeded(){
        m_binding = null;
        m_context = null;
        m_inflater = null;
    }
}
