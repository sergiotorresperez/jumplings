package com.garrapeta.jumplings;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
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

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_SRC, "onCreate " + this);

    	// Flurry initialization
    	final boolean flurryEnabled = getResources().getBoolean(R.bool.config_flurry_enabled);
    	final String flurryApiKey = getResources().getString(R.string.config_flurry_api_key);
    	FlurryHelper.initialize(flurryEnabled, flurryApiKey, false);
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
    
    /**
     * @return if this a debug/development build
     */
    public static boolean isDebugBuild(Context context) { 
    	return ( 0 != ( context.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) );
    }

}
