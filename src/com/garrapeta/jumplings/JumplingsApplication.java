package com.garrapeta.jumplings;

import android.app.Application;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.util.Log;

import com.garrapeta.jumplings.flurry.FlurryHelper;

/**
 * Encapsula algunas variables y estado global de la aplicaci�n,
 * 
 * @author GaRRaPeTa
 */
public class JumplingsApplication extends Application {

    // ---------------------------------------------------------- Constantes

    /** Source trazas de log */
    public static final String LOG_SRC_JUMPLINGS = "jumplings";
    public static final String LOG_SRC = LOG_SRC_JUMPLINGS + ".misc";

    // ------------------------------------------ Constantes de configuraci�n

    // Enabled features

    public static boolean DRAW_ACTOR_SHAPES;
    public static boolean DRAW_ACTOR_BITMAPS;
    public static boolean DRAW_SCENARIO;

    public static boolean DEBUG_FUNCTIONS_ENABLED;
    public static boolean DEBUG_THREAD_BARS_ENABLED;
    public static boolean DEBUG_AUTOPLAY;
    
    public static boolean ADS_ENABLED;
    public static boolean ADS_BUY_DIALOG_BUTTON_ENABLED;

    public static String SCORE_SERVICES_URL;
    

    private static String GAME_FONT_PATH = "fonts/AnuDaw.ttf";

    // ---------------------------------------------- Variables est�ticas

    // Instancia singleton
    private static JumplingsApplication instance;

    public static Typeface game_font;



    // ---------------------------------------------------- M�todos est�ticos

    public static JumplingsApplication getInstance() {
        return instance;
    }

    // --------------------------------------------------- M�todos heredados

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_SRC, "onCreate " + this);

        // Preparaci�n de la instancia del singleton
        instance = this;

    	// Flurry initialization
    	final boolean flurryEnabled = getInstance().getResources().getBoolean(R.bool.config_flurry_enabled);
    	final String flurryApiKey = getInstance().getResources().getString(R.string.config_flurry_api_key);
    	FlurryHelper.initialize(flurryEnabled, flurryApiKey, false);
    	
        game_font = Typeface.createFromAsset(getAssets(), GAME_FONT_PATH);
        
    	DRAW_ACTOR_SHAPES = getInstance().getResources().getBoolean(R.bool.config_draw_actor_shapes);
    	DRAW_ACTOR_BITMAPS = getInstance().getResources().getBoolean(R.bool.config_draw_actor_bitmaps);
    	DRAW_SCENARIO = getInstance().getResources().getBoolean(R.bool.config_draw_scenario);
    	
    	DEBUG_FUNCTIONS_ENABLED = getInstance().getResources().getBoolean(R.bool.config_debug_functions_enabled);
    	DEBUG_THREAD_BARS_ENABLED = getInstance().getResources().getBoolean(R.bool.config_debug_thread_bars_enabled);
    	DEBUG_AUTOPLAY = getInstance().getResources().getBoolean(R.bool.config_debug_autoplay);
    	
    	ADS_ENABLED = getInstance().getResources().getBoolean(R.bool.config_ads_enabled);
    	ADS_BUY_DIALOG_BUTTON_ENABLED = getInstance().getResources().getBoolean(R.bool.config_ads_buy_dialog_button_enabled);
    	
    	SCORE_SERVICES_URL = getInstance().getResources().getString(R.string.config_score_server_url);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(LOG_SRC, "onConfigurationChanged " + this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(LOG_SRC, "onLowMemory " + this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(LOG_SRC, "onTerminate " + this);
    }

}
