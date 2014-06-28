package com.garrapeta.jumplings;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Player score
 * 
 * @author GaRRaPeTa
 */
public class Score implements Parcelable {

    // used to parse and format to Json
    private final static Gson sGson = new Gson();

    // ------------------------------------------------------------ Constantes

    public static final int MAX_LOCAL_HIGHSCORE_COUNT = 25;
    public static final int MAX_GLOBAL_HIGHSCORE_COUNT = 50;

    // Communication with server

    // Name of the action to perform
    public static final String JSON_ACTION_STR = "action";
    public static final String JSON_RESPONSE_OBJ_STR = "response";

    // Value of the action for downloading scores
    public static final String JSON_ACTION_DOWNLOAD_SCORES_STR = "download_scores";
    // Value of the action for submitting scores
    public static final String JSON_ACTION_SUBMIT_SCORES_STR = "submit_scores";

    public static final String JSON_LOCALSCORES_ARRAY_STR = "localScores";
    public static final String JSON_GLOBALSCORES_ARRAY_STR = "globalScores";

    public static final String JSON_ID_IN_CLIENT_OBJ_STR = "clientId";
    public static final String JSON_PLAYERNAME_OBJ_STR = "playerName";
    public static final String JSON_SCORE_OBJ_STR = "score";
    public static final String JSON_LEVEL_OBJ_STR = "level";
    public static final String JSON_GLOBALRANK_OBJ_STR = "globalRank";

    // ------------------------------------------------ Variables de instancia

    @SerializedName("score")
    public final long mScore;

    @SerializedName("level")
    public final int mLevel;

    @SerializedName("playerName")
    public String mPlayerName;

    // ----------------------------------------------------------------- M�todos
    // est�ticos

    public static Score parseFromJson(String json) {
        return sGson.fromJson(json, Score.class);
    }

    public static String formatToJson(Score score) {
        return sGson.toJson(score);
    }

    /**
     * @return la posici�n obtenida por el player dentro de los highscores
     *         localles
     */
    static int getLocalHighScoresPosition(Context context, long newScore) {
        List<Score> list = PermData.getLocalScoresList(context);

        int index = 0;
        for (index = 0; index < list.size(); index++) {
            if (newScore > list.get(index).mScore) {
                break;
            }
        }

        return index;
    }

    public Score(Activity activity, long score, int level) {
        mScore = score;
        mLevel = level;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(new Gson().toJson(this));
    }

    /** CREATOR del parcelable */
    public static final Parcelable.Creator<Score> CREATOR = new Parcelable.Creator<Score>() {

        @Override
        public Score createFromParcel(Parcel in) {
            return parseFromJson(in.readString());
        }

        @Override
        public Score[] newArray(int size) {
            return new Score[size];
        }
    };

}