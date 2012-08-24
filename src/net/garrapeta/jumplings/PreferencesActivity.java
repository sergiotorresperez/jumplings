package net.garrapeta.jumplings;

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
}
