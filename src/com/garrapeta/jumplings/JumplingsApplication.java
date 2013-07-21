package com.garrapeta.jumplings;

import android.app.Application;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.util.Log;

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

    public static boolean DRAW_ACTOR_SHAPES = false;
    public static boolean DRAW_ACTOR_BITMAPS = true;
    public static final boolean DRAW_SCENARIO = true;

    public static boolean DEBUG_ENABLED = true;
    public static boolean DEBUG_THREAD_BARS_ENABLED = false;
    public static boolean DEBUG_AUTOPLAY = false;

    public static boolean MOBCLIX_ENABLED = true;
    public static boolean MOBCLIX_BUY_DIALOG_BUTTON_ENABLED = false;

    // ---------------------------------------------------- Otras Constantes

    // SCORE SERVER
    // local
    public static final String SCORE_SERVICES_URL = "http://192.168.0.2/jumplings/index.php";
    // remote
    // public static final String SCORE_SERVICES_URL = "http://garrapeta.eu.pn/jumplings/index.php";


    private final static String GAME_FONT_PATH = "fonts/AnuDaw.ttf";

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
