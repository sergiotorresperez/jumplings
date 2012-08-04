package net.garrapeta.demo;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.TelephonyManager;

/**
 * 
 * Clase que encapsula una puntuación notable
 * @author GaRRaPeTa
 */
class HighScore implements Parcelable {

	// ------------------------------------------------------------ Constantes
	
	public final String SEPARATOR   = "\n"; 
	public final String NULL_STRING = "\t";
	
	public static final int MAX_LOCAL_HIGHSCORE_COUNT  = 25;
	public static final int MAX_GLOBAL_HIGHSCORE_COUNT = 50;
	
	
	// Comunicación con el servidor
	
	public static final String JSON_REQUEST_OBJ_STR           	= "request";
	public static final String JSON_RESPONSE_OBJ_STR          	= "response";
	
	public static final String JSON_REQUEST_OBJ_SUBMIT_VALUE   	= "submit";
	public static final String JSON_REQUEST_OBJ_RETRIEVE_VALUE 	= "retrieve";
	
	public static final String JSON_LOCALSCORES_ARRAY_STR 		= "localScores";
	public static final String JSON_GLOBALSCORES_ARRAY_STR 		= "globalScores";
	
	public static final String JSON_ID_IN_CLIENT_OBJ_STR 		= "idInClient";
	public static final String JSON_PLAYERNAME_OBJ_STR 			= "playerName";
	public static final String JSON_SCORE_OBJ_STR 				= "score";
	public static final String JSON_LEVEL_OBJ_STR 				= "level";
	public static final String JSON_GLOBALRANK_OBJ_STR 			= "globalRank";
	
	// ------------------------------------------------ Variables de instancia
	
	String localId;
	
	long score;
	
	long kills;
	
	int level;
	
	String playerName;
	
	int globalRank;

	
	// ----------------------------------------------------------------- Métodos estáticos
	
    /**
	 * @return la posición obtenida por el player dentro de los highscores localles
	 */
	static int getLocalHighScoresPosition(long newScore) {
		ArrayList<HighScore> list = PermData.getInstance().getLocalScoresList();
		
		int index = 0;
		for (index = 0; index < list.size(); index++) {
			if (newScore > list.get(index).score) {
				break;
			}
		}
		
		return index;
	}
	
	
	// ------------------------------------------------------------ Constructor
	
	private HighScore() {
		this.localId 	= "";
		this.score 		= 0;
		this.kills 		= 0;
		this.level 		= 0;
		this.playerName = "";
		this.globalRank = 0;
	}
	
	public HighScore(Activity activity) {
		this();
		this.localId    =  getLocalId(activity);
	}
	
	public HighScore(String str) {
		this();
		parseString(str);
	}
	

	// ---------------------------------------------------------- Métodos
	
	/**
	 * @return this client id
	 */
	public String getLocalId(Activity activity) {
		TelephonyManager tManager = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
		String uid = tManager.getDeviceId();
		String userId = "android_" + uid + "_" + System.currentTimeMillis() ;
		return userId;
	}
	
	public void parseString(String str) {
		StringTokenizer st = new StringTokenizer (str, SEPARATOR);
		
		String nextToken = st.nextToken();
		this.localId 	=nextToken.replaceFirst(NULL_STRING, "");
		nextToken = st.nextToken();
		this.score 		= Long.parseLong(nextToken);
		nextToken = st.nextToken();
		this.kills 		= Long.parseLong(nextToken);
		nextToken = st.nextToken();
		this.level 		= Integer.parseInt(nextToken);
		nextToken = st.nextToken();
		this.playerName = nextToken.replaceFirst(NULL_STRING, "");
		nextToken = st.nextToken();
		this.globalRank	= Integer.parseInt(nextToken);
	}
	
	public String formatString() {
		StringBuffer b = new StringBuffer();
		
		String localIdAux = localId;
		localIdAux = localIdAux.replaceFirst(NULL_STRING, "");
		localIdAux = localIdAux.replaceFirst(SEPARATOR, "");
		if (localIdAux.length() == 0) localIdAux =  NULL_STRING;
		b.append(localIdAux);
		b.append(SEPARATOR);
		
		b.append(score);
		b.append(SEPARATOR);
		
		b.append(kills);
		b.append(SEPARATOR);
		
		b.append(level);
		b.append(SEPARATOR);
		
		String playerNameAux = playerName;
		playerNameAux = playerNameAux.replaceFirst(NULL_STRING, "");
		playerNameAux = playerNameAux.replaceFirst(SEPARATOR, "");
		
		if (playerNameAux.length() == 0) {
			playerNameAux =  NULL_STRING;		
		}
		b.append(playerNameAux);
		b.append(SEPARATOR);
		
		b.append(globalRank);
		b.append(SEPARATOR);
		
		return b.toString();
	}
	
	public JSONObject formatJSON() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put(JSON_ID_IN_CLIENT_OBJ_STR, localId);
		jsonObject.put(JSON_PLAYERNAME_OBJ_STR, playerName);
		jsonObject.put(JSON_SCORE_OBJ_STR, 		score);
		jsonObject.put(JSON_LEVEL_OBJ_STR, 		level);
		
		return jsonObject;
	}
	
	public static JSONArray formatJSON(HighScore score) throws JSONException {
		ArrayList<HighScore> scores = new ArrayList<HighScore>();
		scores.add(score);
		
		return formatJSON(scores);
	}
	
	public static JSONArray formatJSON(ArrayList<HighScore> scores) throws JSONException {
		JSONArray array = new JSONArray();
		for (int i = 0; i < scores.size(); i++) {
			array.put(scores.get(i).formatJSON());
		}
				
		return array;
	}
	
	public static HighScore parseJSON(JSONObject scoreJSON) throws JSONException {
		HighScore score = new HighScore();
		score.localId      	 = scoreJSON.getString(HighScore.JSON_ID_IN_CLIENT_OBJ_STR);
		score.playerName 	 = scoreJSON.getString(HighScore.JSON_PLAYERNAME_OBJ_STR).toString();
		score.level      	 = (int) scoreJSON.getLong(HighScore.JSON_LEVEL_OBJ_STR);
		score.score      	 = (int) scoreJSON.getInt(HighScore.JSON_SCORE_OBJ_STR);
		
		
		if (scoreJSON.has(HighScore.JSON_GLOBALRANK_OBJ_STR)) {
			score.globalRank  = (int) scoreJSON.getInt(HighScore.JSON_GLOBALRANK_OBJ_STR);
		}
		
		return score;
	}
	
	public static ArrayList<HighScore> parseJSON(JSONArray scores) throws JSONException {
		ArrayList<HighScore> list = new ArrayList<HighScore>();
		
		for (int i = 0; i < scores.length(); i++) {
			list.add(parseJSON(scores.getJSONObject(i)));
		}
		
		return list;
	}
	
	// ------------------------------------------------------- Métodos de Parcelable

	@Override
    public int describeContents() {
        return 0;
    }

	@Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.formatString());
    }


	/** CREATOR del parcelable */
    public static final Parcelable.Creator<HighScore> CREATOR
            = new Parcelable.Creator<HighScore>() {
    	
    	@Override
        public HighScore createFromParcel(Parcel in) {
            return new HighScore(in.readString());
        }

		@Override
		public HighScore[] newArray(int size) {
			return new HighScore[size];
		}
    };

}