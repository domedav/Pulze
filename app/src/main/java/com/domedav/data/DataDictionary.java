package com.domedav.data;
import androidx.annotation.Nullable;

/**
 * Key-Value pair for Datastorage
 * @param <T1> Any
 * @param <T2> Any
 */
public final class DataDictionary<T1, T2> {
    public interface Saving{ void Save(String key, Object o); }
    private final T1 m_key;
    private T2 m_value;
    private final boolean m_flushToDisk;
    private final Saving m_savingAction;
    public DataDictionary(T1 key, T2 value, @Nullable Saving saving){
        m_key = key;
        m_value = value;
        m_flushToDisk = saving != null;
        m_savingAction = saving;
    }

    public T2 GetData() { return m_value; }

    public void SetData(T2 val){ m_value = val; }

    public T1 GetKey() { return m_key; }
    public boolean FlushToDisk() { return m_flushToDisk; }
    public @Nullable Saving GetSavingAction(){ return m_savingAction; }
}