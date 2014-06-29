package com.garrapeta.jumplings.ui.about;

import android.app.Activity;
import android.os.Bundle;

import com.garrapeta.jumplings.R;
import com.garrapeta.jumplings.util.FlurryHelper;

/**
 * About screen
 * 
 * @author GaRRaPeTa
 */
public class AboutActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
