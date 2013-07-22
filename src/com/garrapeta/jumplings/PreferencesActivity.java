package com.garrapeta.jumplings;

import com.garrapeta.jumplings.flurry.FlurryHelper;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Actividad de preferencias
 * @author GaRRaPeTa
 */
public class PreferencesActivity extends PreferenceActivity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);       
    	addPreferencesFromResource(R.xml.preferences);   
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
