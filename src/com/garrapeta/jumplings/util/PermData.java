package com.garrapeta.jumplings.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.garrapeta.gameengine.utils.LogX;
import com.garrapeta.jumplings.JumplingsApplication;
import com.garrapeta.jumplings.R;
import com.garrapeta.jumplings.game.Score;
import com.garrapeta.jumplings.game.Tutorial.TipId;

public class PermData {

    // -------------------------------------------------------------- Constantes

    private static final String LAST_PLAYER_NAME_KEY = "lastPlayerName";

    private static final String LOCAL_HIGHSCORE_KEY_PREFIX = "localHighscore_";
    private static final String GLOBAL_HIGHSCORE_KEY_PREFIX = "globalHighscore_";

    private static final String LOCAL_SCORES_SUBMISSION_PENDING_KEY = "localScoresSubmissionPending";

    private static final String HIGHEST_SCORE_SENT_TO_LEADERBOARD = "highestScoreSentToLeaderboard";

    private static final String TUTORIAL_TIP_PREFIX = "tip_";

    // Key in shared prefs to save the state of the purchase
    private static final String PREMIUM_PURCHASED_SHARED_PREF_KEY = "premiumPurchasePurchased";

    // niveles de configuracion
    public final static short CFG_LEVEL_ALL = 0;
    public final static short CFG_LEVEL_SOME = 1;
    public final static short CFG_LEVEL_NONE = 2;

    /**
     * @return the local highest score
     */
    public static Score getLocalHighestScore(Context context) {
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

        int index = Score.getLocalHighScoresPosition(context, highScore.mScore);

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
     * 
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
     * The highest score successfully sent to the google play game leaderboard
     * 
     * @param context
     * @param score
     */
    public static void saveHighestScoreSentToLeaderboard(Context context, long score) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sharedPref.edit();
        editor.putLong(HIGHEST_SCORE_SENT_TO_LEADERBOARD, score);
        editor.commit();
    }

    /**
     * @param context
     * @return the highest score successfully sent to google play
     */
    public static long getHighestScoreSentToLeaderboard(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getLong(HIGHEST_SCORE_SENT_TO_LEADERBOARD, 0);
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
     * Gets if the state of the premium upgrade is known. This method does not
     * block.
     * 
     * @param context
     * @return if the state of the premium upgrade is known.
     */
    public static boolean isPremiumPurchaseStateKnown(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final int stateUnknown = Integer.MIN_VALUE;
        final int state = sharedPref.getInt(PREMIUM_PURCHASED_SHARED_PREF_KEY, Integer.MIN_VALUE);

        return (state != stateUnknown);
    }

    /**
     * Gets if the premium upgrade is purchased. This method does not block.
     * 
     * @param context
     * @throws IllegalStateException
     *             if the state of the purchase is not known.
     */
    public static boolean isPremiumPurchased(Context context)
            throws IllegalStateException {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final int stateUnknown = Integer.MIN_VALUE;
        final int state = sharedPref.getInt(PREMIUM_PURCHASED_SHARED_PREF_KEY, Integer.MIN_VALUE);

        if (state == stateUnknown) {
            throw new IllegalStateException("Purchase state is not known");
        }
        return state > 0;
    }

    public static void setPremiumPurchased(Context context, boolean purchased) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit()
                  .putInt(PREMIUM_PURCHASED_SHARED_PREF_KEY, (purchased ? 1 : 0))
                  .commit();
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
     * 
     * @param pending
     */
    public static void setTipShown(Context context, TipId tipId) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sharedPref.edit();
        editor.putBoolean(TUTORIAL_TIP_PREFIX + tipId.name(), true);
        editor.commit();
    }

    public static boolean isSoundEnabled(Context context) {
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

    public static boolean areAdsEnabled(Context context) {
        return getBooleanPreference(context, R.string.config_ads_key, R.string.config_value_default_ads);
    }

    public static boolean paintActorBitmaps(Context context) {
        return getBooleanPreference(context, R.string.config_paint_bitmaps_key, R.string.config_value_default_paint_bitmaps);
    }

    public static boolean isWireframeMode(Context context) {
        return getBooleanPreference(context, R.string.config_wireframe_mode_key, R.string.config_value_default_wireframe_mode);
    }

    public static boolean areDebugFeaturesEnabled(Context context) {
        return getBooleanPreference(context, R.string.config_debug_features_key, R.string.config_value_default_debug_features);
    }

    public static boolean showThreadBars(Context context) {
        return getBooleanPreference(context, R.string.config_thread_bars_key, R.string.config_value_default_thread_bars);
    }

    public static boolean isAutoplayEnabled(Context context) {
        return getBooleanPreference(context, R.string.config_autoplay_key, R.string.config_value_default_autoplay);
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
        String defaultValue = r.getString(defaultResId, CFG_LEVEL_ALL);
        String keyStr = r.getString(key);
        String valueStr = sharedPref.getString(keyStr, defaultValue);
        return parseConfigLevel(context, valueStr);
    }

    private static String getStringPreference(Context context, int key, int defaultResId) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources r = context.getResources();
        String defaultValue = r.getString(defaultResId);
        String keyStr = r.getString(key);
        return sharedPref.getString(keyStr, defaultValue);
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
            LogX.w(JumplingsApplication.TAG, "Invalid configuration level string: " + str);
            throw new IllegalArgumentException("Illegal configuration value: " + str);
        }
    }

}
