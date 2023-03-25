package com.domedav.animhelpers;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public class QuickValueAnimator {

    public static final int ANIMATION_SHORT_MS = 100;
    public static final int ANIMATION_LONG_MS = 200;

    /**
     * Animates between 2 int values
     * @param startValue The starting value
     * @param targetValue The target value
     * @param duration_ms The time it should take to animate it
     * @param callback Calls back every time the animation updates
     */
    public static void AnimateInt(int startValue, int targetValue, long duration_ms, @NonNull ValueAnimator.AnimatorUpdateListener callback){ AnimateInt(startValue, targetValue, duration_ms, callback, null); }

    /**
     * Animates between 2 int values
     * @param startValue The starting value
     * @param targetValue The target value
     * @param duration_ms The time it should take to animate it
     * @param callback Calls back every time the animation updates
     * @param callback2 Useful if you want to know when the animation has ended
     */
    public static void AnimateInt(int startValue, int targetValue, long duration_ms, @NonNull ValueAnimator.AnimatorUpdateListener callback, @Nullable AnimatorListenerAdapter callback2){
        // Create the ValueAnimator that animates
        ValueAnimator anim = ValueAnimator.ofInt(startValue, targetValue);
        // Set the animation duration
        anim.setDuration(duration_ms);
        // Begin animating
        anim.start();
        // Register update callback
        anim.addUpdateListener(callback);
        // Register optional callback
        if(callback2 == null) { return; }
        anim.addListener(callback2);
    }

    /**
     * Animates between 2 float values
     * @param startValue The starting value
     * @param targetValue The target value
     * @param duration_ms The time it should take to animate it
     * @param callback Calls back every time the animation updates
     */
    public static void AnimateFloat(float startValue, float targetValue, long duration_ms, @NonNull ValueAnimator.AnimatorUpdateListener callback){ AnimateFloat(startValue, targetValue, duration_ms, callback, null); }

    /**
     * Animates between 2 float values
     * @param startValue The starting value
     * @param targetValue The target value
     * @param duration_ms The time it should take to animate it
     * @param callback Calls back every time the animation updates
     * @param callback2 Useful if you want to know when the animation has ended
     */
    public static void AnimateFloat(float startValue, float targetValue, long duration_ms, @NonNull ValueAnimator.AnimatorUpdateListener callback, @Nullable AnimatorListenerAdapter callback2){
        // Create the ValueAnimator that animates
        ValueAnimator anim = ValueAnimator.ofFloat(startValue, targetValue);
        // Set the animation duration
        anim.setDuration(duration_ms);
        // Begin animating
        anim.start();
        // Register update callback
        anim.addUpdateListener(callback);
        // Register optional callback
        if(callback2 == null) { return; }
        anim.addListener(callback2);
    }

    /**
     * Used when animators want to pass back a color value to the caller
     */
    public interface ColorHandlingCallback{
        void Callback(int color_argb);
    }

    /**
     * Animates the alpha of a color (useful for fadeouts)
     * @param referenceColorInt The reference color ID (R.color.<i><b>colorID</b></i>)
     * @param startingAlpha The starting alpha value (0 to 255)
     * @param targetAlpha The target alpha value (0 to 255)
     * @param duration_ms The time it should take to animate it
     * @param callback Calls back with the value you should apply to something
     */
    public static void AnimateColorAlpha(int referenceColorInt, @IntRange(from = 0, to = 255) int startingAlpha, @IntRange(from = 0, to = 255) int targetAlpha, long duration_ms, @NonNull ColorHandlingCallback callback){ AnimateColorAlpha(referenceColorInt, startingAlpha, targetAlpha, duration_ms, callback, null); }

    /**
     * Animates the alpha of a color (useful for fadeouts)
     * @param referenceColorInt The reference color ID (R.color.<i><b>colorID</b></i>)
     * @param startingAlpha The starting alpha value (0 to 255)
     * @param targetAlpha The target alpha value (0 to 255)
     * @param duration_ms The time it should take to animate it
     * @param callback Calls back with the value you should apply to something
     * @param callback2 Useful if you want to know when the animation has ended
     */
    public static void AnimateColorAlpha(int referenceColorInt, @IntRange(from = 0, to = 255) int startingAlpha, @IntRange(from = 0, to = 255) int targetAlpha, long duration_ms, @NonNull ColorHandlingCallback callback, @Nullable AnimatorListenerAdapter callback2){
        // Get color RGB from provided color ID
        int red = Color.red(referenceColorInt);
        int green = Color.green(referenceColorInt);
        int blue = Color.blue(referenceColorInt);
        // Call helper
        AnimateInt(startingAlpha, targetAlpha, duration_ms, animation -> callback.Callback(Color.argb((int)animation.getAnimatedValue(), red, green, blue)), callback2);
    }

    /**
     * Animates color switching between 2 colors
     * @param startingColorInt The starting color ID
     * @param targetColorInt The target color ID
     * @param duration_ms The time it should take to animate it
     * @param callback Calls back with the value you should apply to something
     */
    public static void AnimateColorChange(int startingColorInt, int targetColorInt, long duration_ms, @NonNull ColorHandlingCallback callback){ AnimateColorChange(startingColorInt, targetColorInt, duration_ms, callback, null); }

    /**
     * Animates color switching between 2 colors
     * @param startingColorInt The starting color ID
     * @param targetColorInt The target color ID
     * @param duration_ms The time it should take to animate it
     * @param callback Calls back with the value you should apply to something
     * @param callback2 Useful if you want to know when the animation has ended
     */
    public static void AnimateColorChange(int startingColorInt, int targetColorInt, long duration_ms, @NonNull ColorHandlingCallback callback, @Nullable AnimatorListenerAdapter callback2){
        //RGB value animation vars
        AtomicInteger animedRed = new AtomicInteger();
        AtomicInteger animedGreen = new AtomicInteger();
        AtomicInteger animedBlue = new AtomicInteger();
        // Animate Red
        AnimateInt(Color.red(startingColorInt), Color.red(targetColorInt), duration_ms, animation -> animedRed.set((int)animation.getAnimatedValue()));
        // Animate Green
        AnimateInt(Color.green(startingColorInt), Color.green(targetColorInt), duration_ms, animation -> animedGreen.set((int) animation.getAnimatedValue()));
        // Animate Blue
        AnimateInt(Color.blue(startingColorInt), Color.blue(targetColorInt), duration_ms, animation -> animedBlue.set((int) animation.getAnimatedValue()));
        // Animate Alpha, and callback for color value apply
        AnimateInt(Color.alpha(startingColorInt), Color.alpha(targetColorInt), duration_ms, animation -> callback.Callback(Color.argb((int)animation.getAnimatedValue() /*animedAlpha*/, animedRed.intValue(), animedGreen.intValue(), animedBlue.intValue())), callback2);
    }
}