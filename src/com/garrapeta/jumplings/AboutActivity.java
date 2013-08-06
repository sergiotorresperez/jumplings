package com.garrapeta.jumplings;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.garrapeta.jumplings.flurry.FlurryHelper;

/**
 * About screen
 * @author GaRRaPeTa
 */
public class AboutActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
    	setContentView(R.layout.activity_about);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        FlurryHelper.onStartSession(this);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }
}
