package com.domedav.listenanywhere.homescreen;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.math.MathUtils;
import com.domedav.animhelpers.QuickValueAnimator;
import com.domedav.animhelpers.QuickViewAnimator;
import com.domedav.listenanywhere.R;
import com.domedav.listenanywhere.databinding.ActivityMainBinding;
import org.jetbrains.annotations.NotNull;

public class HomeScreenMusicControlPanelManager {
    private static ActivityMainBinding m_binding;
    private static Context m_context;
    private static ViewGroup m_musiclayout;
    private static ViewGroup m_expandCollapseImg;
    private static ViewGroup m_musicnaviCollapsedLayout;
    private static ViewGroup m_musicnaviExpandedLayout;
    private static float m_targetHeight = -200f;
    private static float m_yOffset = 325f;
    private static ImageButton m_playpauseButton_small;
    private static ImageButton m_playpauseButton_big;
    private static ImageButton m_skipButton;
    private static ImageButton m_previousButton;
    private static TextView m_musicName_small;
    private static TextView m_musicName_big;
    private static TextView m_musicArtist;
    private static ProgressBar m_musicProgressbar_small;
    private static ProgressBar m_musicProgressbar_big;
    private static TextView m_musicTimeLeft;
    private static boolean m_playpauseState = true;
    private static Drawable m_iconPlay;
    private static Drawable m_iconPause;

    /**
     * Initialise this class, prepare it for use
     */
    public static void InitialiseMusicLayoutManager(@NotNull Activity a, @NotNull ActivityMainBinding binding){
        m_binding = binding;
        m_context = a.getApplicationContext();
        m_musiclayout = m_binding.musicnaviLayout;
        m_expandCollapseImg = m_binding.expandCollapseImg;

        m_musicnaviExpandedLayout = m_musiclayout.findViewById(R.id.musicnavi_navinormal);
        m_musicnaviCollapsedLayout = m_musiclayout.findViewById(R.id.musicnavi_navicollapsed);

        m_musicnaviExpandedLayout.setAlpha(0f);
        m_musiclayout.setTranslationY(m_yOffset);

        m_musiclayout.post(() -> {  // Frame skip, so android has time to process the layout height properly
            m_yOffset = m_musicnaviExpandedLayout.getHeight() - m_musicnaviCollapsedLayout.getHeight();
            m_targetHeight = -(m_musiclayout.getHeight() - m_yOffset);   // Assign max expandable height
            m_musiclayout.setTranslationY(m_yOffset);
        });

        // Set up buttons
        m_iconPlay = ContextCompat.getDrawable(a.getApplicationContext(), R.drawable.ic_play);
        m_iconPause = ContextCompat.getDrawable(a.getApplicationContext(), R.drawable.ic_pause);

        // Set the 2 play-pause buttons
        m_playpauseButton_small = m_musicnaviCollapsedLayout.findViewById(R.id.playpause_btn_c);
        m_playpauseButton_small.setOnClickListener(v -> {
            MusicPlayPause();
        });

        m_playpauseButton_big = m_musicnaviExpandedLayout.findViewById(R.id.playpause_btn);
        m_playpauseButton_big.setOnClickListener(v -> MusicPlayPause());

        // Set up skip-prev buttons
        m_skipButton = m_musicnaviExpandedLayout.findViewById(R.id.skip_btn);
        m_skipButton.setOnClickListener(v -> MusicSkip());

        m_previousButton = m_musicnaviExpandedLayout.findViewById(R.id.previous_btn);
        m_previousButton.setOnClickListener(v -> MusicPrevious());

        // Set up text refs
        m_musicName_small = m_musicnaviCollapsedLayout.findViewById(R.id.nowplaying_music_name_c);
        m_musicName_big = m_musicnaviExpandedLayout.findViewById(R.id.nowplaying_music_name);
        m_musicArtist = m_musicnaviExpandedLayout.findViewById(R.id.nowplaying_music_artist);

        // Set up progressbar refs
        m_musicProgressbar_small = m_musicnaviCollapsedLayout.findViewById(R.id.progressbar_c);
        m_musicProgressbar_big = m_musicnaviExpandedLayout.findViewById(R.id.progressbar);
        m_musicTimeLeft = m_musicnaviExpandedLayout.findViewById(R.id.progress_timeleft);

        m_expandCollapseImg.setScaleX(.65f);    // Start as collapsed
    }

    private static boolean m_isExpanded = false;

    private static boolean m_interactionLocked = false;

    /**
     * Handles the Musicnavigator layout events
     */
    public static void LayoutMove(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                m_interactionLocked = false;
                m_pointerOffset = -event.getY();   // Offset our movement from the touch pos, needed so the view doesnt start jumping onto the pointer
                break;
            case MotionEvent.ACTION_MOVE:
                if(m_interactionLocked){return;}
                MoveBy(m_pointerOffset + event.getY());
                break;
            case MotionEvent.ACTION_UP:
                m_interactionLocked = true;
                QuickViewAnimator.AnimateScaleX(m_expandCollapseImg, m_expandCollapseImg.getScaleX(), .65f, QuickValueAnimator.ANIMATION_SHORT_MS);
                // Clamp to the closer one
                if(Math.abs(m_layoutTranslation) > -m_targetHeight / 2f){
                    Expand();
                }
                else{
                    Collapse();
                }
                m_layoutTranslation = m_isExpanded ? m_targetHeight : 0f;
                break;
        }
    }

    private static float m_layoutTranslation = 0f;
    private static float m_pointerOffset = 0f;

    /**
     * Moves the layout by given amount on the Y axis
     */
    private static void MoveBy(float moveby){
        m_layoutTranslation += moveby;

        float transitionPercent = m_layoutTranslation / m_targetHeight;

        m_expandCollapseImg.setScaleX(MathUtils.clamp(m_isExpanded ? Math.abs(transitionPercent - 1f) : Math.abs(transitionPercent), .65f, 1.25f));

        m_musicnaviCollapsedLayout.setAlpha(MathUtils.clamp(1f - transitionPercent, 0f, 1f));
        m_musicnaviExpandedLayout.setAlpha(MathUtils.clamp(transitionPercent, 0f, 1f));

        if(m_layoutTranslation > 0f){
            m_layoutTranslation = 0f;
            m_musiclayout.setTranslationY(m_yOffset);
            return;
        }
        else if(m_layoutTranslation < m_targetHeight){
            m_layoutTranslation = m_targetHeight;
            m_musiclayout.setTranslationY(m_targetHeight + m_yOffset);
            return;
        }
        m_musiclayout.setTranslationY(m_layoutTranslation + m_yOffset);
    }

    /**
     * Collapses the layout
     */
    private static void Collapse(){
        m_isExpanded = false;
        QuickViewAnimator.AnimateTranslationY(m_musiclayout, m_musiclayout.getTranslationY(), m_yOffset, QuickValueAnimator.ANIMATION_SHORT_MS);

        QuickValueAnimator.AnimateFloat(m_musicnaviCollapsedLayout.getAlpha(), 1f, QuickValueAnimator.ANIMATION_SHORT_MS, animation -> {
            m_musicnaviCollapsedLayout.setAlpha(MathUtils.clamp((float)animation.getAnimatedValue(), 0f, 1f));
        });
        QuickValueAnimator.AnimateFloat(m_musicnaviExpandedLayout.getAlpha(), 0f, QuickValueAnimator.ANIMATION_SHORT_MS, animation -> {
            m_musicnaviExpandedLayout.setAlpha(MathUtils.clamp((float)animation.getAnimatedValue(), 0f, 1f));
        });
    }

    /**
     * Expands the layout
     */
    private static void Expand(){
        m_isExpanded = true;
        QuickViewAnimator.AnimateTranslationY(m_musiclayout, m_musiclayout.getTranslationY(), m_targetHeight + m_yOffset, QuickValueAnimator.ANIMATION_SHORT_MS);

        QuickValueAnimator.AnimateFloat(m_musicnaviCollapsedLayout.getAlpha(), 0f, QuickValueAnimator.ANIMATION_SHORT_MS, animation -> {
            m_musicnaviCollapsedLayout.setAlpha(MathUtils.clamp((float)animation.getAnimatedValue(), 0f, 1f));
        });
        QuickValueAnimator.AnimateFloat(m_musicnaviExpandedLayout.getAlpha(), 1f, QuickValueAnimator.ANIMATION_SHORT_MS, animation -> {
            m_musicnaviExpandedLayout.setAlpha(MathUtils.clamp((float)animation.getAnimatedValue(), 0f, 1f));
        });
    }

    /**
     * Handles play-pause music events from buttons
     */
    private static void MusicPlayPause(){
        m_playpauseState = !m_playpauseState;
        m_playpauseButton_small.setImageDrawable(m_playpauseState ? m_iconPlay : m_iconPause);
        m_playpauseButton_big.setImageDrawable(m_playpauseState ? m_iconPlay : m_iconPause);
    }

    /**
     * Handles skip music events from button
     */
    private static void MusicSkip(){

    }

    /**
     * Handles previous music events from button
     */
    private static void MusicPrevious(){

    }

    /**
     * Set the currently playing music attributes with this
     * @param musicname The title of the music
     * @param artist The artist of the music
     * @param length The length of the music
     */
    public static void SetMusicAttributes(String musicname, String artist, int length){
        m_musicName_small.setText(musicname);
        m_musicName_big.setText(musicname);

        m_musicProgressbar_small.setMax((int)length);
        m_musicProgressbar_big.setMax((int)length);

        m_musicArtist.setText(m_context.getString(R.string.music_artist_placeholder, artist));

        m_musicLength = length;
    }

    private static int m_musicLength;

    /**
     * Sets the progressbar progression
     * @param currentprogress The current progress amount
     */
    public static void SetProgress(int currentprogress){
        m_musicProgressbar_small.setProgress(currentprogress);
        m_musicProgressbar_big.setProgress(currentprogress);

        int curr_mins = currentprogress / 60;
        currentprogress %= 60;

        int targ_mins = m_musicLength / 60;
        int targ_secs = m_musicLength %= 60;

        m_musicTimeLeft.setText(m_context.getString(R.string.music_timeremaining_placeholder, String.format("%02d:%02d", curr_mins, currentprogress), String.format("%02d:%02d", targ_mins, targ_secs)));
    }

    /**
     * Must be called when this class is no longer used, so we dont cause a mem leak
     */
    public static void DestroyUnneeded(){
        m_binding = null;
        m_context = null;
        m_musiclayout = null;
        m_expandCollapseImg = null;
        m_musicnaviCollapsedLayout = null;
        m_musicnaviExpandedLayout = null;
        m_playpauseButton_small = null;
        m_playpauseButton_big = null;
        m_skipButton = null;
        m_previousButton = null;
        m_musicName_small = null;
        m_musicName_big = null;
        m_musicArtist = null;
        m_musicProgressbar_small = null;
        m_musicProgressbar_big = null;
        m_musicTimeLeft = null;
        m_iconPlay = null;
        m_iconPause = null;
    }
}