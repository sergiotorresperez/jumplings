package com.garrapeta.jumplings;

import com.garrapeta.gameengine.GameView;
import com.garrapeta.jumplings.flurry.FlurryHelper;
import com.garrapeta.jumplings.util.Utils;
import com.garrapeta.jumplings.wave.CampaignSurvivalWave;
import com.garrapeta.jumplings.wave.MenuWave;
import com.garrapeta.jumplings.wave.TestWave;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Activity implementing the menu screen
 */
public class MenuActivity extends FragmentActivity {

    private View mTitle;
    
    private Button mStartBtn;
    private ImageButton mPreferencesBtn;
    private ImageButton mHighScoresBtn;
    private ImageButton mAboutBtn;
    private ImageButton mShareButton;
    
    private View mMobClixView;
    private View mDebugGroup;

    
    /** World */
    JumplingsWorld mWorld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Preparación de la UI
        setContentView(R.layout.activity_menu);

        mTitle = findViewById(R.id.menu_title);
 
        mStartBtn = (Button) findViewById(R.id.menu_playBtn);
        mStartBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });

        mDebugGroup = findViewById(R.id.menu_debug_view_group);
        
        Button testBtn = (Button) findViewById(R.id.menu_testBtn);
        testBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startTest();
            }
        });

        Button exitBtn = (Button) findViewById(R.id.menu_exitBtn);
        exitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mHighScoresBtn = (ImageButton) findViewById(R.id.menu_highScoresBtn);
        mHighScoresBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showHighScores();
            }
        });


        mShareButton = (ImageButton) findViewById(R.id.menu_shareBtn);
        mShareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	FlurryHelper.logShareButtonClicked();
            	// TODO: externalize share string
                Utils.share(MenuActivity.this, "I'm playing Jumplings!");
            }
        });

        mPreferencesBtn = (ImageButton) findViewById(R.id.menu_preferencesBtn);
        mPreferencesBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreferences();
            }
        });

        mAboutBtn = (ImageButton) findViewById(R.id.menu_aboutBtn);
        mAboutBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showAbout();
            }
        });
        

        // Ads
        mMobClixView = findViewById(R.id.menu_advertising_banner_view);
 
        onStartAnimationPhaseOne();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(JumplingsApplication.LOG_SRC, "onStart " + this);
        
        FlurryHelper.onStartSession(this);

        mWorld = new JumplingsWorld(this, (GameView) findViewById(R.id.menu_gamesurface), this);
        mWorld.setDrawDebugInfo(JumplingsApplication.DEBUG_FUNCTIONS_ENABLED);

        // Preparaci�n de la wave

        mWorld.mWave = new MenuWave(mWorld, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(JumplingsApplication.LOG_SRC, "onPause " + this);
        if (mWorld.isRunning()) {
        	mWorld.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(JumplingsApplication.LOG_SRC, "onResume " + this);
        mWorld.resume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(JumplingsApplication.LOG_SRC, "onStop " + this);
        FlurryHelper.onEndSession(this);
        
        if (mWorld.isRunning()) {
        	mWorld.finish();
        	mWorld = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(JumplingsApplication.LOG_SRC, "onDestroy " + this);
    }

    private void onStartAnimationPhaseOne() {
        
        mTitle.setVisibility(View.INVISIBLE);
        mStartBtn.setVisibility(View.INVISIBLE);
        mPreferencesBtn.setVisibility(View.INVISIBLE);
        mHighScoresBtn.setVisibility(View.INVISIBLE);
        mAboutBtn.setVisibility(View.INVISIBLE);
        mDebugGroup.setVisibility(JumplingsApplication.DEBUG_FUNCTIONS_ENABLED ? View.INVISIBLE : View.GONE);
        mMobClixView.setVisibility(JumplingsApplication.ADS_ENABLED ? View.INVISIBLE : View.GONE);
        mShareButton.setVisibility(View.INVISIBLE);

        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.menu_screen_scale_in);
        fadeInAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mTitle.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onStartAnimationPhaseTwo(); 
            }
        });

        mTitle.startAnimation(fadeInAnimation);
    }

    private void onStartAnimationPhaseTwo() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeInAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mStartBtn.setVisibility(View.VISIBLE);
                mStartBtn.setVisibility(View.VISIBLE);
                mPreferencesBtn.setVisibility(View.VISIBLE);
                mHighScoresBtn.setVisibility(View.VISIBLE);
                mAboutBtn.setVisibility(View.VISIBLE);
                mDebugGroup.setVisibility(JumplingsApplication.DEBUG_FUNCTIONS_ENABLED ? View.VISIBLE : View.GONE);
                mMobClixView.setVisibility(JumplingsApplication.ADS_ENABLED ? View.VISIBLE : View.GONE);
                mShareButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });

        mStartBtn.startAnimation(fadeInAnimation);
        mPreferencesBtn.startAnimation(fadeInAnimation);
        mHighScoresBtn.startAnimation(fadeInAnimation);
        mAboutBtn.startAnimation(fadeInAnimation);
        mDebugGroup.startAnimation(fadeInAnimation);
        mMobClixView.startAnimation(fadeInAnimation);
        mShareButton.startAnimation(fadeInAnimation);
    }

    private void startNewGame() {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(GameActivity.WAVE_BUNDLE_KEY, CampaignSurvivalWave.WAVE_KEY);
        startActivity(i);
    }

    private void startTest() {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(GameActivity.WAVE_BUNDLE_KEY, TestWave.WAVE_KEY);
        startActivity(i);
        // Intent i = new Intent(this, GameOverActivity.class);
        // startActivity(i);
    }

    private void showHighScores() {
        Intent i = new Intent(this, HighScoreListingActivity.class);
        startActivity(i);
    }

    private void showPreferences() {
        Intent i = new Intent(this, PreferencesActivity.class);
        startActivity(i);
    }

    private void showAbout() {
        Toast.makeText(this, "TODO: about/info Activity", Toast.LENGTH_LONG).show();
    }

}