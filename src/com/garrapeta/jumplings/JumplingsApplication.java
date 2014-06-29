package com.garrapeta.jumplings;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;

import com.garrapeta.gameengine.utils.LogX;
import com.garrapeta.jumplings.util.FlurryHelper;

/**
 * Encapsula algunas variables y estado global de la aplicaci�n,
 * 
 * @author GaRRaPeTa
 */
public class JumplingsApplication extends Application {

    // ---------------------------------------------------------- Constantes

    /** Source trazas de log */
    public static final String TAG_JUMPLINGS = "jumplings";
    public static final String TAG = TAG_JUMPLINGS + ".misc";

    @Override
    public void onCreate() {
        super.onCreate();

        // enable or disable the logger
        LogX.setEnabled(isDebugBuild(this) && this.getResources()
                                                  .getBoolean(R.bool.config_log_enabled_in_debug_build));

        LogX.i(TAG, "onCreate " + this);

        // Flurry initialization
        final boolean flurryEnabled = getResources().getBoolean(R.bool.config_flurry_enabled);
        final String flurryApiKey = getResources().getString(R.string.config_flurry_api_key);
        FlurryHelper.initialize(flurryEnabled, flurryApiKey, false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogX.i(TAG, "onConfigurationChanged " + this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LogX.w(TAG, "onLowMemory " + this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LogX.i(TAG, "onTerminate " + this);
    }

    /**
     * @return if this a debug/development build
     */
    public static boolean isDebugBuild(Context context) {
        return (0 != (context.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE));
    }

}
