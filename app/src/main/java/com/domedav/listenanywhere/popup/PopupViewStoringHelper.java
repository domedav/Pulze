package com.domedav.listenanywhere.popup;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;

public class PopupViewStoringHelper {
    private static final ArrayList<PopupViewStoringHelper> m_popupHelpers = new ArrayList<>();
    private final int m_uniqueIdentifier;
    private final ArrayList<View> m_popupViews = new ArrayList<>();   // Stores the assigned views
    private static _KeyValuePairs<Integer, PopupViewStoringHelper> m_RecentValueCaching;

    /**
     * Returns the m_popupViews field
     */
    public ArrayList<View> get_PopupViews() { return m_popupViews; }

    /**
     * Helps with view pooling
     * @param uniqueIdentifier The unique ID of this object
     */
    private PopupViewStoringHelper(int uniqueIdentifier){
        m_uniqueIdentifier = uniqueIdentifier;
    }

    /**
     * Creates a new popup pool
     * @param uniqueIdentifier The unique ID which the new pool should have<br>Hash of a string is recommended<br>DONT LOSE THE KEY!
     * @return The new pool<br>Dont forget to return when you no longer need it, to prevent memory leaks
     */
    @NonNull
    public static PopupViewStoringHelper RequestNewPool(int uniqueIdentifier){
        // Create new pool
        PopupViewStoringHelper pool = new PopupViewStoringHelper(uniqueIdentifier);
        // Cache it
        m_popupHelpers.add(pool);
        // Give it to the requester
        return pool;
    }

    /**
     * Gets a pool from cache by its unique ID
     * @param uniqueIdentifier The unique ID
     * @return The pool if there was any<br>Otherwise null
     */
    public static @Nullable PopupViewStoringHelper GetPoolByID(int uniqueIdentifier){
        // If cache is null, init class
        if(m_RecentValueCaching == null){ m_RecentValueCaching = new _KeyValuePairs<>(0, null); }
        // If not, and ID matches, return the cached object
        else if(m_RecentValueCaching.get_Key() == uniqueIdentifier){ return m_RecentValueCaching.get_Value(); }
        // If neither, search for that popup mgr, and cache it
        for (PopupViewStoringHelper popupHelper : m_popupHelpers) {
            if(Objects.equals(popupHelper.m_uniqueIdentifier, uniqueIdentifier)){ m_RecentValueCaching.set_KeyValue(uniqueIdentifier, popupHelper); return popupHelper; }
        }
        // If it doesnt exist, return null
        return null;
    }

    /**
     * Stores a view in the pools list
     * @param v The view to store
     */
    public boolean StoreView(View v){ return m_popupViews.add(v); }

    /**
     * Hides all views
     */
    public boolean HideAllViews(){
        if(m_popupViews.size() == 0){return false;}

        for (View popupView : m_popupViews) {
            popupView.setVisibility(View.GONE);
        }
        return true;
    }

    /**
     * Shows all views
     */
    public boolean ShowAllViews(){
        if(m_popupViews.size() == 0){ return false; }

        for (View popupView : m_popupViews) {
            popupView.setVisibility(View.VISIBLE);
        }
        return true;
    }

    /**
     * Call if you no longer need this<br>Takes care of destroying every view attached to this, as well as the object itself
     */
    public void ReturnPool(){
        // Remove all views (android will also auto garbage collect causing error sometimes)
        try {
            for (View popupView : m_popupViews) {
                m_popupViews.remove(popupView);
            }
        }
        catch (Exception ignored){}

        // Remove ourselves from the static list
        m_popupHelpers.remove(this);
        // Destroy class
        DestroyMe();
    }

    /**
     * Checks if there is a pool with the given workingID
     */
    public static boolean PoolExists(int workingID){ return PopupViewStoringHelper.GetPoolByID(workingID) != null; }

    /**
     * Destroys this class
     */
    public void DestroyMe() {
        m_popupViews.clear();
    }

    /**
     * Should only be called, when the application is closing
     */
    public static void DestroyAll(){
        for (PopupViewStoringHelper m_popupHelper : m_popupHelpers) {
            m_popupHelper.ReturnPool();
        }
    }

    /**
     * Helper class for caching the recent PopupViewStoringHelper
     * @param <T1> Dynamic Key
     * @param <T2> Dynamic Value
     */
    //NOTE: DONT MODIFY, LOCAL HELPER
    private static final class _KeyValuePairs<T1, T2>{ private T1 m_key; private T2 m_value; public _KeyValuePairs(T1 key, T2 value){ set_KeyValue(key, value); } public T2 get_Value(){ return m_value; } public T1 get_Key(){ return m_key; } public void set_KeyValue(T1 key, T2 value){ m_key = key; m_value = value; } }
}