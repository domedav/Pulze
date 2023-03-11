package com.domedav.navbarnavigation;

import java.util.ArrayList;

public class NavbarBackHelper {
    /**
     * Helps with navbar back navigation
     */
    public interface BackNavigation{
        void Back();
    }

    /**
     * Resets the whole navbar navigation (must be called when activity change happens)
     */
    public static void Reset(){ m_actions.clear(); }

    /**
     * Stores the undoable actions
     */
    private static final ArrayList<BackNavigation> m_actions = new ArrayList<>();

    /**
     * Register an action, which can be undone by the user
     * @param backAction The opposite of the executed action
     */
    public static void RegisterAction(BackNavigation backAction){ m_actions.add(backAction); }

    /**
     * Undoes the actions, based on execution order
     * @return If anything could be undone
     */
    public static boolean NavigateBack(){
        // If theres nothing to undo
        if(m_actions.size() <= 0){
            // Return false to caller
            return false;
        }
        // Undo latest action
        m_actions.get(m_actions.size() - 1).Back();
        m_actions.remove(m_actions.size() - 1);
        return true;
    }
}