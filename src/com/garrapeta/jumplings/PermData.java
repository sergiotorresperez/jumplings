package com.garrapeta.jumplings;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import com.garrapeta.jumplings.Tutorial.TipId;

public class PermData {

    // -------------------------------------------------------------- Constantes

    private static PermData instance;

    public static final String LAST_PLAYER_NAME_KEY = "lastPlayerName";

    public static final String LOCAL_HIGHSCORE_KEY_PREFIX = "localHighscore_";
    public static final String GLOBAL_HIGHSCORE_KEY_PREFIX = "globalHighscore_";

    public static final String LOCAL_SCORES_SUBMISSION_PENDING_KEY = "localScoresSubmissionPending";

    public static final String TUTORIAL_TIP_PREFIX = "tip_";
    
    // niveles de configuracion
    public final static short CFG_LEVEL_ALL = 0;
    public final static short CFG_LEVEL_SOME = 1;
    public final static short CFG_LEVEL_NONE = 2;
    

    // -------------------------------------------------- Variables de instancia

    // ------------------------------------------------------- M�todos est�ticos

    /**
     * @return instancia de PermData
     * */
    public static PermData getInstance() {
        if (instance == null) {
            instance = new PermData();
        }
        return instance;
    }

    // ------------------------------------------------------------------
    // M�todos

    // ---------------------------------------------- M�todos relativos al score

    /**
     * @return
     */
    public Score getLocalGetHighScore() {
        ArrayList<Score> localScoreList = getLocalScoresList();
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
    public void addNewLocalScore(Score highScore) {
        ArrayList<Score> list = getLocalScoresList();

        int index = Score.getLocalHighScoresPosition(highScore.score);

        if (index < Score.MAX_LOCAL_HIGHSCORE_COUNT) {
            list.add(index, highScore);
        }

        saveLocalScoresList(list);
    }

    /**
     * @return lista con scores m�s altos
     */
    public ArrayList<Score> getLocalScoresList() {
        ArrayList<Score> localScoreList = new ArrayList<Score>();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());

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
    public void saveLocalScoresList(List<Score> localScoreList) {
        // Salvado
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());

        Editor editor = sharedPref.edit();

        for (int i = 0; i < Score.MAX_LOCAL_HIGHSCORE_COUNT && i < localScoreList.size(); i++) {
            editor.putString(LOCAL_HIGHSCORE_KEY_PREFIX + i, Score.formatToJson(localScoreList.get(i)));
        }

        editor.commit();
    }

    /**
     * Lo borra todo
     */
    public void clearAll() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());

        Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();

        getLocalScoresList().clear();
    }

    /**
     * @return lista con scores m�s altos
     */
    public ArrayList<Score> getGlobalScoresList() {

        ArrayList<Score> globalScoreList = new ArrayList<Score>();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());

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
    public void saveGlobalScoresList(List<Score> globalScoreList) {
        // Salvado
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());

        Editor editor = sharedPref.edit();

        for (int i = 0; i < Score.MAX_GLOBAL_HIGHSCORE_COUNT && i < globalScoreList.size(); i++) {
            editor.putString(GLOBAL_HIGHSCORE_KEY_PREFIX + i, Score.formatToJson(globalScoreList.get(i)));
        }

        editor.commit();
    }

    /**
     * @return el nombre del �ltimo jugador que introdujo un score
     */
    public String getLastPlayerName() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());
        return sharedPref.getString(LAST_PLAYER_NAME_KEY, "");
    }

    /**
     * salva el nombre del �ltimo jugador que introdujo un score
     */
    public void saveLastPlayerName(String name) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());
        Editor editor = sharedPref.edit();
        editor.putString(LAST_PLAYER_NAME_KEY, name);
        editor.commit();
    }

    /**
     * @return if there are local scores pending to submit
     */
    public boolean isLocalScoresSubmissionPending() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());
        return sharedPref.getBoolean(LOCAL_SCORES_SUBMISSION_PENDING_KEY, false);
    }

    /**
     * Sets if there are local scores pending to be submitted
     * @param pending
     */
    public void setLocalScoresSubmissionPending(boolean pending) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());
        Editor editor = sharedPref.edit();
        editor.putBoolean(LOCAL_SCORES_SUBMISSION_PENDING_KEY, pending);
        editor.commit();
    }
    
    /**
     * @param tipId
     * @return if the tip with the supplied id has been shown
     */
    public boolean isTipShown(TipId tipId) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());
        return sharedPref.getBoolean(TUTORIAL_TIP_PREFIX + tipId.name(), false);
    }

    /**
     * Sets the tip with the supplied id as shown
     * @param pending
     */
    public void setTipShown(TipId tipId) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());
        Editor editor = sharedPref.edit();
        editor.putBoolean(TUTORIAL_TIP_PREFIX + tipId.name(), true);
        editor.commit();
    }

    public boolean getSoundConfig() {
        return getLevelPreference(R.string.config_sound_key);
    }

    public short getVibratorLevel() {
        return getBooleanPreference(R.string.config_vibrator_key);
    }

    public short getShakeConfig() {
        return getBooleanPreference(R.string.config_shake_key);
    }

    public short getFlashConfig() {
        return getBooleanPreference(R.string.config_flash_key);
    }

    private short getBooleanPreference(int key) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());
        Resources r = JumplingsApplication.getInstance().getResources();
        String defaultValue = r.getString(R.string.config_value_default);
        String keyStr = r.getString(key);
        String valueStr = sharedPref.getString(keyStr, defaultValue);
        return parseConfigLevel(valueStr);
    }
    
    private boolean getLevelPreference(int key) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());
        Resources r = JumplingsApplication.getInstance().getResources();
        boolean defaultValue = Boolean.parseBoolean(r.getString(R.string.confing_sound_enabled_default));
        String keyStr = r.getString(key);
        return sharedPref.getBoolean(keyStr, defaultValue);
    }
    
    private short parseConfigLevel(String str) {
        Resources r = JumplingsApplication.getInstance().getResources();
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
