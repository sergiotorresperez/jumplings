package com.garrapeta.jumplings;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import com.garrapeta.jumplings.Tutorial.TipId;

public class PermData {

    // -------------------------------------------------------------- Constantes

    public static final String LAST_PLAYER_NAME_KEY = "lastPlayerName";

    public static final String LOCAL_HIGHSCORE_KEY_PREFIX = "localHighscore_";
    public static final String GLOBAL_HIGHSCORE_KEY_PREFIX = "globalHighscore_";

    public static final String LOCAL_SCORES_SUBMISSION_PENDING_KEY = "localScoresSubmissionPending";

    public static final String TUTORIAL_TIP_PREFIX = "tip_";
    
    // niveles de configuracion
    public final static short CFG_LEVEL_ALL = 0;
    public final static short CFG_LEVEL_SOME = 1;
    public final static short CFG_LEVEL_NONE = 2;
    


    /**
     * @return
     */
    public static Score getLocalGetHighScore(Context context) {
        ArrayList<Score> localScoreList = getLocalScoresList(context);
        if (localScoreList.size() > 0) {
            return localScoreList.get(0);
        } else {
            return null;
        }
    }

    /**
     * Inserta un nuevo score
     * 
     * @return
     */
    public static void addNewLocalScore(Context context, Score highScore) {
        ArrayList<Score> list = getLocalScoresList(context);

        int index = Score.getLocalHighScoresPosition(context, highScore.score);

        if (index < Score.MAX_LOCAL_HIGHSCORE_COUNT) {
            list.add(index, highScore);
        }

        saveLocalScoresList(context, list);
    }

    /**
     * @param context
     * @return lista con scores altos
     */
    public static ArrayList<Score> getLocalScoresList(Context context) {
        ArrayList<Score> localScoreList = new ArrayList<Score>();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        for (int i = 0; i < Score.MAX_LOCAL_HIGHSCORE_COUNT; i++) {
            String str = sharedPref.getString(LOCAL_HIGHSCORE_KEY_PREFIX + i, null);
            if (str != null) {
                localScoreList.add(Score.parseFromJson(str));
            } else {
                break;
            }
        }

        return localScoreList;
    }

    /**
     * Salva la lista local
     * 
     * @return
     */
    public static void saveLocalScoresList(Context context, List<Score> localScoreList) {
        // Salvado
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        Editor editor = sharedPref.edit();

        for (int i = 0; i < Score.MAX_LOCAL_HIGHSCORE_COUNT && i < localScoreList.size(); i++) {
            editor.putString(LOCAL_HIGHSCORE_KEY_PREFIX + i, Score.formatToJson(localScoreList.get(i)));
        }

        editor.commit();
    }

    /**
     * Lo borra todo
     * @param context
     */
    public static void clearAll(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();

        getLocalScoresList(context).clear();
    }

    /**
     * @param context
     * @return lista con scores altos
     */
    public static ArrayList<Score> getGlobalScoresList(Context context) {

        ArrayList<Score> globalScoreList = new ArrayList<Score>();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        for (int i = 0; i < Score.MAX_GLOBAL_HIGHSCORE_COUNT; i++) {
            String str = sharedPref.getString(GLOBAL_HIGHSCORE_KEY_PREFIX + i, null);
            if (str != null) {
                globalScoreList.add(Score.parseFromJson(str));
            } else {
                break;
            }
        }

        return globalScoreList;
    }

    /**
     * Salva la lista local
     * 
     * @return
     */
    public static void saveGlobalScoresList(Context context, List<Score> globalScoreList) {
        // Salvado
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        Editor editor = sharedPref.edit();

        for (int i = 0; i < Score.MAX_GLOBAL_HIGHSCORE_COUNT && i < globalScoreList.size(); i++) {
            editor.putString(GLOBAL_HIGHSCORE_KEY_PREFIX + i, Score.formatToJson(globalScoreList.get(i)));
        }

        editor.commit();
    }

    /**
     * @return el nombre del �ltimo jugador que introdujo un score
     */
    public static String getLastPlayerName(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(LAST_PLAYER_NAME_KEY, "");
    }

    /**
     * salva el nombre del �ltimo jugador que introdujo un score
     */
    public static void saveLastPlayerName(Context context, String name) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sharedPref.edit();
        editor.putString(LAST_PLAYER_NAME_KEY, name);
        editor.commit();
    }

    /**
     * @return if there are local scores pending to submit
     */
    public static boolean isLocalScoresSubmissionPending(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(LOCAL_SCORES_SUBMISSION_PENDING_KEY, false);
    }

    /**
     * Sets if there are local scores pending to be submitted
     * @param pending
     */
    public static void setLocalScoresSubmissionPending(Context context, boolean pending) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sharedPref.edit();
        editor.putBoolean(LOCAL_SCORES_SUBMISSION_PENDING_KEY, pending);
        editor.commit();
    }
    
    /**
     * @param tipId
     * @return if the tip with the supplied id has been shown
     */
    public static boolean isTipShown(Context context, TipId tipId) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(TUTORIAL_TIP_PREFIX + tipId.name(), false);
    }

    /**
     * Sets the tip with the supplied id as shown
     * @param pending
     */
    public static void setTipShown(Context context, TipId tipId) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sharedPref.edit();
        editor.putBoolean(TUTORIAL_TIP_PREFIX + tipId.name(), true);
        editor.commit();
    }

    public static boolean getSoundConfig(Context context) {
        return getBooleanPreference(context, R.string.config_sound_key, R.string.config_value_default_sound);
    }

    public static short getVibratorLevel(Context context) {
        return getLevelPreference(context, R.string.config_vibrator_key, R.string.config_value_default_vibrator_level);
    }

    public static short getShakeConfig(Context context) {
        return getLevelPreference(context, R.string.config_shake_key, R.string.config_value_default_shake_level);
    }

    public static short getFlashConfig(Context context) {
        return getLevelPreference(context, R.string.config_flash_key, R.string.config_value_default_flash_level);
    }

    private static boolean getBooleanPreference(Context context, int key, int defaultResId) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources r = context.getResources();
        boolean defaultValue = Boolean.parseBoolean(r.getString(defaultResId));
        String keyStr = r.getString(key);
        return sharedPref.getBoolean(keyStr, defaultValue);
    }
    
    private static short getLevelPreference(Context context, int key, int defaultResId) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources r = context.getResources();
        String defaultValue = r.getString(defaultResId, CFG_LEVEL_ALL );
        String keyStr = r.getString(key);
        String valueStr = sharedPref.getString(keyStr, defaultValue);
        return parseConfigLevel(context, valueStr);
    }
    
    private static short parseConfigLevel(Context context, String str) {
        Resources r = context.getResources();
        if (str.equals(r.getString(R.string.config_value_all))) {
            return CFG_LEVEL_ALL;
        } else if (str.equals(r.getString(R.string.config_value_some))) {
            return CFG_LEVEL_SOME;
        } else if (str.equals(r.getString(R.string.config_value_none))) {
            return CFG_LEVEL_NONE;
        } else {
            Log.w(JumplingsApplication.LOG_SRC, "Invalid configuration level string: " + str);
            throw new IllegalArgumentException("Illegal configuration value: " + str);
        }
    }

}
