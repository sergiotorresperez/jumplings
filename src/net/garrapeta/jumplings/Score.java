package net.garrapeta.jumplings;

import java.util.List;

import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.TelephonyManager;

/**
 * Player score
 * @author GaRRaPeTa
 */
public class Score implements Parcelable {

	// used to parse and format to Json
	private final static Gson sGson = new Gson();
	
	// ------------------------------------------------------------ Constantes
	
	public final String SEPARATOR   = "\n"; 
	public final String NULL_STRING = "\t";
	
	public static final int MAX_LOCAL_HIGHSCORE_COUNT  = 25;
	public static final int MAX_GLOBAL_HIGHSCORE_COUNT = 50;
	
	
	// Communication with server
	
	// Name of the action to perform
	public static final String JSON_ACTION_STR           		= "action";
	public static final String JSON_RESPONSE_OBJ_STR          	= "response";
	
	// Value of the action for downloading scores
	public static final String JSON_ACTION_DOWNLOAD_SCORES_STR  = "download_scores";
	// Value of the action for submitting scores
	public static final String JSON_ACTION_SUBMIT_SCORES_STR 	= "submit_scores";
	
	public static final String JSON_LOCALSCORES_ARRAY_STR 		= "localScores";
	public static final String JSON_GLOBALSCORES_ARRAY_STR 		= "globalScores";
	
	public static final String JSON_ID_IN_CLIENT_OBJ_STR 		= "clientId";
	public static final String JSON_PLAYERNAME_OBJ_STR 			= "playerName";
	public static final String JSON_SCORE_OBJ_STR 				= "score";
	public static final String JSON_LEVEL_OBJ_STR 				= "level";
	public static final String JSON_GLOBALRANK_OBJ_STR 			= "globalRank";
	
	// ------------------------------------------------ Variables de instancia
	
	public String clientId;
	
	public long score;
	
	public long kills;
	
	public int level;
	
	public String playerName;
	
	public int globalRank;

	
	// ----------------------------------------------------------------- M�todos est�ticos

	public static Score parseFromJson(String json) {
		return sGson.fromJson(json, Score.class);
	}
	
	public static String formatToJson(Score score) {
		return sGson.toJson(score);
	}

    /**
	 * @return la posici�n obtenida por el player dentro de los highscores localles
	 */
	static int getLocalHighScoresPosition(long newScore) {
		List<Score> list = PermData.getInstance().getLocalScoresList();
		
		int index = 0;
		for (index = 0; index < list.size(); index++) {
			if (newScore > list.get(index).score) {
				break;
			}
		}
		
		return index;
	}
	
	
	// ------------------------------------------------------------ Constructor
	
	private Score() {
		this.clientId 	= "";
		this.score 		= 0;
		this.kills 		= 0;
		this.level 		= 0;
		this.playerName = "";
		this.globalRank = 0;
	}
	
	public Score(Activity activity) {
		this();
		this.clientId    =  getLocalId(activity);
	}
	
	// ---------------------------------------------------------- M�todos
	
	/**
	 * @return this client id
	 */
	public String getLocalId(Activity activity) {
		TelephonyManager tManager = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
		String uid = tManager.getDeviceId();
		String userId = "android_" + uid + "_" + System.currentTimeMillis() ;
		return userId;
	}
	
	// ------------------------------------------------------- M�todos de Parcelable

	@Override
    public int describeContents() {
        return 0;
    }

	@Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(new Gson().toJson(this));
    }


	/** CREATOR del parcelable */
    public static final Parcelable.Creator<Score> CREATOR
            = new Parcelable.Creator<Score>() {
    	
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