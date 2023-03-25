package com.domedav.data;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.domedav.listenanywhere.R;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * Stores data, which we only want in memory (data wont be written to disk)
 */
public class DataStorage {
    private static final ArrayList<DataDictionary<String, Object>> m_cache = new ArrayList<>();
    private static SharedPreferences m_prefs;
    private static SharedPreferences.Editor m_prefEditor;
    /**
     * Used to determine how the value shall be saved
     */
    public enum SavingAction{
        DontSave,
        AsInt,
        AsFloat,
        AsString,
        AsBool
    }

    /**
     * Saves the given data
     * @param key The key, which will be linked to the value
     * @param value The value to save
     * @param savingAction Saves the data based on the given SavingAction
     * @return True if it was saved
     */
    public static boolean SaveData(@NotNull String key, @NotNull Object value, @NotNull SavingAction savingAction){
        // Create a new entry
        return m_cache.add(new DataDictionary<>(key, value, AssignSaveingAction(savingAction)));
    }

    /**
     * Changes data in the cache, if the value exists
     * @param key The key, you saved the data with
     * @param value The value to re-save
     * @return True if the change happened, otherwise false
     */
    public static boolean ChangeData(@NotNull String key, @NotNull Object value){
        // Check if we have such thing in cache, if no, ret false
        if(m_cache.stream().noneMatch(val -> Objects.equals(val.GetKey(), key))){ return false; }
        // If yes, find it, and replace its value
        m_cache.stream()
                // Filter the values (filtering by matching values)
                .filter(val -> Objects.equals(val.GetKey(), key))
                // Find the 1st occurrence
                .findFirst()
                // If we have such entry, change the value
                .ifPresent(result -> result.SetData(value));
        return true;
    }

    /**
     * If the data exists, it changes it, if not, it saves it (automatically determines how the given data will be saved)
     * @param key The key, which will be/is linked to the value
     * @param value The value to save
     * @param save If false, the data wont be saved to the disk, if True, the data will be saved to the disk, the saving method will be determined automatically
     * @return True if it successfully completed the operation, False if it failed
     */
    public static boolean AutoSave(@NotNull String key, @NotNull Object value, boolean save){
        return ChangeData(key, value) || SaveData(key, value, save ? DetermineTypeSaving(value) : SavingAction.DontSave);
    }

    /**
     * Determines saving method, which will be used to save the value
     */
    @Nullable
    private static DataDictionary.Saving AssignSaveingAction(@NotNull SavingAction a){
        switch (a){
            case AsInt:
                return DataStorage::SaveAsInt;
            case AsFloat:
                return DataStorage::SaveAsFloat;
            case AsString:
                return DataStorage::SaveAsString;
            case AsBool:
                return DataStorage::SaveAsBoolean;
            default:
                return null;
        }
    }

    /**
     * Gets the data, using the given key
     * @param key The key, which is linked to the value
     * @return The saved object, as an Object, or null if the value doesnt exists
     */
    @Nullable
    public static Object GetData(String key){
        // Get the stream of cache
        return m_cache.stream()
                // Filter the values (filtering by matching hashes)
                .filter(val -> Objects.equals(val.GetKey(), key))
                // Find the 1st occurrence
                .findFirst()
                // If we have such thing, map it to the stored data, and return that
                .map(DataDictionary::GetData)
                // Otherwise, just return null
                .orElse(null);
    }

    /**
     * Saves all savable values to disk
     */
    public static void SaveAll(){
        m_prefEditor = m_prefs.edit();

        m_cache.stream()
                .filter(DataDictionary::FlushToDisk)
                // GetSavingAction will never return null, if it is null, FlushToDisk is false
                .forEach(val -> val.GetSavingAction().Save(val.GetKey(), val.GetData()));

        m_prefEditor.commit();
    }

    /**
     * Must be called in order to use DataStorage, this initialises core functionalities, as well as loads the saved data
     */
    public static void Init(@NonNull Context context){
        // If we have prefs initialised, dont allow re-init
        if(m_prefs != null){return;}
        m_prefs = context.getSharedPreferences(String.valueOf(R.string.savefile_name), Context.MODE_PRIVATE);
        // Load data
        Map<String, ?> all = m_prefs.getAll();
        // If theres no data in our database, dont try to load anything
        if(all == null){return;}
        for (String key : all.keySet()) {
            Object value = all.get(key);
            SaveData(key, value, DetermineTypeSaving(value));
        }
    }

    /**
     * Parse to SavingAction, based on the given value object
     */
    private static SavingAction DetermineTypeSaving(Object value){
        if(value instanceof Boolean){
            return SavingAction.AsBool;
        }
        else if(value instanceof Integer){
            return SavingAction.AsInt;
        }
        else if(value instanceof Float){
            return SavingAction.AsFloat;
        }
        else if(value instanceof String){
            return SavingAction.AsString;
        }
        return SavingAction.DontSave;
    }

    /**
     * Handles saving of the given value, as an int
     */
    private static void SaveAsInt(String key, @NotNull Object value){
        m_prefEditor.putInt(key, (int)value);
    }

    /**
     * Handles saving of the given value, as a float
     */
    private static void SaveAsFloat(String key, @NotNull Object value){
        m_prefEditor.putFloat(key, (float)value);
    }

    /**
     * Handles saving of the given value, as a bool
     */
    private static void SaveAsBoolean(String key, @NotNull Object value){
        m_prefEditor.putBoolean(key, (boolean)value);
    }

    /**
     * Handles saving of the given value, as a string
     */
    private static void SaveAsString(String key, @NotNull Object value){
        m_prefEditor.putString(key, value.toString());
    }
}