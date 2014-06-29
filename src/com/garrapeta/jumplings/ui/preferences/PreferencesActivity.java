package com.garrapeta.jumplings.ui.preferences;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.garrapeta.jumplings.JumplingsApplication;
import com.garrapeta.jumplings.R;
import com.garrapeta.jumplings.util.FlurryHelper;

/**
 * Actividad de preferencias
 * 
 * @author GaRRaPeTa
 */
public class PreferencesActivity extends PreferenceActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        if (JumplingsApplication.isDebugBuild(this)) {
            addPreferencesFromResource(R.xml.preferences_debug);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryHelper.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryHelper.onEndSession(this);
    }
}
