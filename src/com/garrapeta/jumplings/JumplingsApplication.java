package com.garrapeta.jumplings;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.crashlytics.android.Crashlytics;
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

        LogX.setEnabled(isDebugBuild(this) && this.getResources()
                                                  .getBoolean(R.bool.config_log_enabled_in_debug_build));

        LogX.i(TAG, "onCreate " + this);

        setupFlurry();

        setupCrashlytics();
    }

    private void setupFlurry() {
        final boolean flurryEnabled = getResources().getBoolean(R.bool.config_flurry_enabled);
        final String flurryApiKey = getResources().getString(R.string.config_flurry_api_key);
        FlurryHelper.initialize(flurryEnabled, flurryApiKey, false);
    }

    private void setupCrashlytics() {
        final boolean crashLyticsEnabled = getResources().getBoolean(R.bool.config_crashlytics_enabled);
        if (crashLyticsEnabled) {
            Crashlytics.getInstance()
                       .setDebugMode(isDebugBuild(this));
            Crashlytics.start(this);
            Crashlytics.logException(new RuntimeException("test3"));
        }
    }

    /**
     * @return if this a debug/development build
     */
    public static boolean isDebugBuild(Context context) {
        return (0 != (context.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE));
    }

}
