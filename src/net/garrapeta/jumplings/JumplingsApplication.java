package net.garrapeta.jumplings;

import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import android.app.Application;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.util.Log;

import com.facebook.android.Facebook;
import com.openfeint.api.OpenFeintSettings;

/**
 * Encapsula algunas variables y estado global de la aplicaci�n, 
 * @author GaRRaPeTa
 */
public class JumplingsApplication extends Application {

    // ---------------------------------------------------------- Constantes
    
    /** Source trazas de log */
    public static final String LOG_SRC_JUMPLINGS = "jumplings";
    public static final String LOG_SRC = LOG_SRC_JUMPLINGS + ".misc";
    
	// ------------------------------------------ Constantes de configuraci�n
	
	// Enabled features
	
    public static boolean DRAW_ACTOR_SHAPES = false;
    public static boolean DRAW_ACTOR_BITMAPS = true;
    public static final boolean DRAW_SCENARIO = true;
    
	public static boolean DEBUG_ENABLED					 	= true;
	public static boolean DEBUG_THREAD_BARS_ENABLED		 	= false;

	public static boolean MOBCLIX_ENABLED 					= true;
	public static boolean MOBCLIX_BUY_DIALOG_BUTTON_ENABLED = true;
	
	public static boolean FACEBOOK_ENABLED				 	= true;
	public static boolean FEINT_ENABLED					 	= true;
	public static boolean TWITTER_ENABLED					= true;
	
	// ---------------------------------------------------- Otras Constantes


	
	// SCORE SERVER
	// local 
	//public static final String SCORE_SERVICES_URL = "http://192.168.0.2/JumplingsServer/index.php";
	// remote
	public static final String SCORE_SERVICES_URL = "http://garrapeta.eu.pn/jumplings/index.php";

	
	// OPEN FEINT
	static final String feintGameName 	= "testgame2";
	static final String feintGameID 	= "350982";
	static final String feintGameKey 	= "WyHj1euWAXXfx6kzxxkVAQ";
	static final String feintGameSecret = "8rleAsGDSebvTy0DR6dK91lXoEK8DQsAOCbOn2j28A";
	
	// FACEBOOK
	static final String facebokAppID 	= "221216374606645";
	static final String facebokSecret 	= "0980f742b06b9768ab0acf7a0df5a185 ";
	
	// TWITTER
	
	/*
	Consumer key		RPoGnrTCOHRALLqHILhTBA
	Consumer secret		E9ZZN3a4jhlf2Jf7fJZQwcEnh7gTMEk8MVM8m79ZU
	Request token URL	https://api.twitter.com/oauth/request_token
	Authorize URL		https://api.twitter.com/oauth/authorize
	Access token URL	https://api.twitter.com/oauth/access_token
	Callback URL		http://garrapeta.net	 
	*/
	
 	final static String twitterConsumerKey     = "RPoGnrTCOHRALLqHILhTBA"; 
	final static String twitterConsumerSecret  = "E9ZZN3a4jhlf2Jf7fJZQwcEnh7gTMEk8MVM8m79ZU";
	final static String twitterScreenName      = ""; 
	final static String twitterPassword        = "";
	final static String twitterCallbackUrl 	   = "twitter4j://authenticated";
	
	
	private final static String GAME_FONT_PATH = "fonts/AnuDaw.ttf";

	// ---------------------------------------------- Variables est�ticas
	
	// FACEBOOK
	public static Facebook facebook = new Facebook(facebokAppID);
	
	// TWITTER
	public static CommonsHttpOAuthConsumer twitterHttpOauthConsumer;
	public static OAuthProvider twitterHttpOauthprovider;
	public static Twitter twitter;
	
	// OPEN FEINT 
	public static OpenFeintSettings feintSettings;
	
	// Instancia singleton
	private static JumplingsApplication instance;
	
	public static  Typeface game_font;
	
	// ---------------------------------------------- Inicializaci�n est�tica
	
	static {
		if (FEINT_ENABLED) {
			feintSettings = new OpenFeintSettings(feintGameName, feintGameKey, feintGameSecret, feintGameID);
		}
		if (TWITTER_ENABLED) {
			twitter =  new TwitterFactory().getInstance();
		}
		
	}
	// ---------------------------------------------------- M�todos est�ticos
	
	public static JumplingsApplication getInstance() {
		return instance;
	}
	
	// --------------------------------------------------- M�todos heredados

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(LOG_SRC,"onCreate " + this);
		
		// Preparaci�n de la instancia del singleton
		instance = this;
		 
        game_font = Typeface.createFromAsset(getAssets(), GAME_FONT_PATH);        
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.i(LOG_SRC,"onConfigurationChanged " + this);
	}


	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.w(LOG_SRC,"onLowMemory " + this);
	}


	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i(LOG_SRC,"onTerminate " + this);
	}	
	
}
