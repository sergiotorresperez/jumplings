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
    private static final String GAME_FONT_PATH = "fonts/AnuDaw.ttf";

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
