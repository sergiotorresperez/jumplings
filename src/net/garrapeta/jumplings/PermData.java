package net.garrapeta.jumplings;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

public class PermData {

    // -------------------------------------------------------------- Constantes

    private static PermData instance;

    public static final String LAST_PLAYER_NAME_KEY = "lastPlayerName";

    public static final String LOCAL_HIGHSCORE_KEY_PREFIX = "localHighscore_";
    public static final String GLOBAL_HIGHSCORE_KEY_PREFIX = "globalHighscore_";

    public static final String TWITTER_ACCESS_TOKEN_KEY = "twitterToken";
    public static final String TWITTER_ACCESS_TOKEN_SECRET_KEY = "twitterTokenSecret";

    public static final String LOCAL_SCORES_SUBMISSION_PENDING_KEY = "localScoresSubmissionPending";
    
    // niveles de configuracion
    public final static short CFG_LEVEL_NONE = 1;
    public final static short CFG_LEVEL_SOME = 2;
    public final static short CFG_LEVEL_ALL = 3;

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
    public HighScore getLocalGetHighScore() {
        ArrayList<HighScore> localScoreList = getLocalScoresList();
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
    public void addNewLocalScore(HighScore highScore) {
        ArrayList<HighScore> list = getLocalScoresList();

        int index = HighScore.getLocalHighScoresPosition(highScore.score);

        if (index < HighScore.MAX_LOCAL_HIGHSCORE_COUNT) {
            list.add(index, highScore);
        }

        saveLocalScoresList(list);
    }

    /**
     * @return lista con scores m�s altos
     */
    public ArrayList<HighScore> getLocalScoresList() {
        ArrayList<HighScore> localScoreList = new ArrayList<HighScore>();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());

        for (int i = 0; i < HighScore.MAX_LOCAL_HIGHSCORE_COUNT; i++) {
            String str = sharedPref.getString(LOCAL_HIGHSCORE_KEY_PREFIX + i, null);
            if (str != null) {
                localScoreList.add(new HighScore(str));
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
    public void saveLocalScoresList(ArrayList<HighScore> localScoreList) {
        // Salvado
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());

        Editor editor = sharedPref.edit();

        for (int i = 0; i < HighScore.MAX_LOCAL_HIGHSCORE_COUNT && i < localScoreList.size(); i++) {
            editor.putString(LOCAL_HIGHSCORE_KEY_PREFIX + i, localScoreList.get(i).formatString());
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
    public ArrayList<HighScore> getGlobalScoresList() {

        ArrayList<HighScore> globalScoreList = new ArrayList<HighScore>();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());

        for (int i = 0; i < HighScore.MAX_GLOBAL_HIGHSCORE_COUNT; i++) {
            String str = sharedPref.getString(GLOBAL_HIGHSCORE_KEY_PREFIX + i, null);
            if (str != null) {
                globalScoreList.add(new HighScore(str));
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
    public void saveGlobalScoresList(ArrayList<HighScore> globalScoreList) {
        // Salvado
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());

        Editor editor = sharedPref.edit();

        for (int i = 0; i < HighScore.MAX_GLOBAL_HIGHSCORE_COUNT && i < globalScoreList.size(); i++) {
            editor.putString(GLOBAL_HIGHSCORE_KEY_PREFIX + i, globalScoreList.get(i).formatString());
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
    
    public boolean getSoundConfig() {
        return getLevelPreference(R.string.config_sound_key);
    }

    public short getVibratorConfig() {
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

    public String getTwitterToken() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());
        return sharedPref.getString(TWITTER_ACCESS_TOKEN_KEY, null);
    }

    public void saveTwitterToken(String token) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());

        Editor editor = sharedPref.edit();
        editor.putString(TWITTER_ACCESS_TOKEN_KEY, token);
        editor.commit();
    }

    public String getTwitterTokenSecret() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());
        return sharedPref.getString(TWITTER_ACCESS_TOKEN_SECRET_KEY, null);
    }

    public void saveTwitterTokenSecret(String secret) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(JumplingsApplication.getInstance());

        Editor editor = sharedPref.edit();
        editor.putString(TWITTER_ACCESS_TOKEN_SECRET_KEY, secret);
        editor.commit();
    }

}
