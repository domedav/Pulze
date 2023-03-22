package com.domedav.animhelpers;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.ViewGroup;
import androidx.annotation.Nullable;

public class QuickViewAnimator {

    /**
     * Animates a layouts translationY
     * @param layout The layout to animate
     * @param startingPos The starting position of the layout
     * @param targetPos The target position of the layout
     * @param duration_ms The time it should take to animate it
     */
    public static void AnimateTranslationX(ViewGroup layout, float startingPos, float targetPos, long duration_ms) { AnimateTranslationX(layout, startingPos, targetPos, duration_ms, null); }

    /**
     * Animates a layouts translationY
     * @param layout The layout to animate
     * @param startingPos The starting position of the layout
     * @param targetPos The target position of the layout
     * @param duration_ms The time it should take to animate it
     * @param callback Useful if you want to do event based things with the animation
     */
    public static void AnimateTranslationX(ViewGroup layout, float startingPos, float targetPos, long duration_ms, @Nullable AnimatorListenerAdapter callback){
        // Set the starting pos of the view
        layout.setTranslationX(startingPos);
        // Create the ObjectAnimator that animates the translationY property
        ObjectAnimator anim = ObjectAnimator.ofFloat(layout, "translationX", targetPos);
        // Set the animation duration
        anim.setDuration(duration_ms);
        // Begin animating
        anim.start();
        // If we have callback, subscribe it to the appropriate sources, else: just finish exec
        if(callback == null){ return; }
        anim.addListener(callback);
    }

    /**
     * Animates a layouts translationY
     * @param layout The layout to animate
     * @param startingPos The starting position of the layout
     * @param targetPos The target position of the layout
     * @param duration_ms The time it should take to animate it
     */
    public static void AnimateTranslationY(ViewGroup layout, float startingPos, float targetPos, long duration_ms) { AnimateTranslationY(layout, startingPos, targetPos, duration_ms, null); }

    /**
     * Animates a layouts translationY
     * @param layout The layout to animate
     * @param startingPos The starting position of the layout
     * @param targetPos The target position of the layout
     * @param duration_ms The time it should take to animate it
     * @param callback Useful if you want to do event based things with the animation
     */
    public static void AnimateTranslationY(ViewGroup layout, float startingPos, float targetPos, long duration_ms, @Nullable AnimatorListenerAdapter callback){
        // Set the starting pos of the view
        layout.setTranslationY(startingPos);
        // Create the ObjectAnimator that animates the translationY property
        ObjectAnimator anim = ObjectAnimator.ofFloat(layout, "translationY", targetPos);
        // Set the animation duration
        anim.setDuration(duration_ms);
        // Begin animating
        anim.start();
        // If we have callback, subscribe it to the appropriate sources, else: just finish exec
        if(callback == null){ return; }
        anim.addListener(callback);
    }

    /**
     * Animates a layouts Scaling on all axis
     * @param layout The layout to animate
     * @param startingScale The starting scale of the layout
     * @param targetScale The target scale of the layout
     * @param duration_ms The time it should take to animate it
     */
    public static void AnimateScaleAll(ViewGroup layout, float startingScale, float targetScale, long duration_ms) { AnimateScaleAll(layout, startingScale, targetScale, duration_ms, null); }

    /**
     * Animates a layouts Scaling on all axis
     * @param layout The layout to animate
     * @param startingScale The starting scale of the layout
     * @param targetScale The target scale of the layout
     * @param duration_ms The time it should take to animate it
     * @param callback Useful if you want to do event based things with the animation
     */
    public static void AnimateScaleAll(ViewGroup layout, float startingScale, float targetScale, long duration_ms, @Nullable AnimatorListenerAdapter callback){
        // Set the starting scale of the view
        layout.setScaleX(startingScale);
        layout.setScaleY(startingScale);
        // Create the ObjectAnimator that animates the scales property
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(layout, "scaleX", targetScale);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(layout, "scaleY", targetScale);
        // Set the animations duration
        anim1.setDuration(duration_ms);
        anim2.setDuration(duration_ms);
        // Begin animating
        anim1.start();
        anim2.start();
        // If we have callback, subscribe it to the appropriate sources, else: just finish exec
        if(callback == null){ return; }
        anim1.addListener(callback);
    }

    /**
     * Animates a layouts Scaling on all axis
     * @param layout The layout to animate
     * @param startingScale The starting scale of the layout
     * @param targetScale The target scale of the layout
     * @param duration_ms The time it should take to animate it
     */
    public static void AnimateScaleX(ViewGroup layout, float startingScale, float targetScale, long duration_ms){AnimateScaleX(layout, startingScale, targetScale, duration_ms, null);}

    /**
     * Animates a layouts Scaling on all axis
     * @param layout The layout to animate
     * @param startingScale The starting scale of the layout
     * @param targetScale The target scale of the layout
     * @param duration_ms The time it should take to animate it
     * @param callback Useful if you want to do event based things with the animation
     */
    public static void AnimateScaleX(ViewGroup layout, float startingScale, float targetScale, long duration_ms, @Nullable AnimatorListenerAdapter callback){
        // Set the starting scale of the view
        layout.setScaleX(startingScale);
        // Create the ObjectAnimator that animates the scales property
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(layout, "scaleX", targetScale);
        // Set the animations duration
        anim1.setDuration(duration_ms);
        // Begin animating
        anim1.start();
        // If we have callback, subscribe it to the appropriate sources, else: just finish exec
        if(callback == null){ return; }
        anim1.addListener(callback);
    }

    /**
     * Animates a layouts Scaling on all axis
     * @param layout The layout to animate
     * @param startingScale The starting scale of the layout
     * @param targetScale The target scale of the layout
     * @param duration_ms The time it should take to animate it
     */
    public static void AnimateScaleY(ViewGroup layout, float startingScale, float targetScale, long duration_ms){AnimateScaleY(layout, startingScale, targetScale, duration_ms, null);}

    /**
     * Animates a layouts Scaling on all axis
     * @param layout The layout to animate
     * @param startingScale The starting scale of the layout
     * @param targetScale The target scale of the layout
     * @param duration_ms The time it should take to animate it
     * @param callback Useful if you want to do event based things with the animation
     */
    public static void AnimateScaleY(ViewGroup layout, float startingScale, float targetScale, long duration_ms, @Nullable AnimatorListenerAdapter callback){
        // Set the starting scale of the view
        layout.setScaleX(startingScale);
        // Create the ObjectAnimator that animates the scales property
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(layout, "scaleY", targetScale);
        // Set the animations duration
        anim1.setDuration(duration_ms);
        // Begin animating
        anim1.start();
        // If we have callback, subscribe it to the appropriate sources, else: just finish exec
        if(callback == null){ return; }
        anim1.addListener(callback);
    }
}