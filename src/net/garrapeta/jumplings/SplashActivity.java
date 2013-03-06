package net.garrapeta.jumplings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

/**
 * Activity implementing the Splash screen
 */
public class SplashActivity extends Activity {

    private View mTitleView;
    private View mSubtitleView;
    
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash);
        
        View root = findViewById(R.id.splash_root);
        root.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenuActivity(false);
            }
        });

        mTitleView = findViewById(R.id.splash_title);
        mSubtitleView = findViewById(R.id.splash_subtitle);

        mTitleView.setVisibility(View.INVISIBLE);
        mSubtitleView.setVisibility(View.INVISIBLE);
 
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onAnimationPhaseOne();
            }
        }, 400);
    }

    private void onAnimationPhaseOne() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mTitleView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onAnimationPhaseTwo(); 
            }
        });

        mTitleView.startAnimation(animation);
    }

    private void onAnimationPhaseTwo() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mSubtitleView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onAnimationPhaseThree(); 
            }
        });
        mSubtitleView.startAnimation(animation);
    }

    private void onAnimationPhaseThree() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTitleView.setVisibility(View.INVISIBLE);
                mSubtitleView.setVisibility(View.INVISIBLE);
                openMenuActivity(true); 
            }
        });
        mTitleView.startAnimation(animation);
        mSubtitleView.startAnimation(animation);
    }

    private void openMenuActivity(boolean disableTransition) {
        finish();
        if (disableTransition) { 
            overridePendingTransition(0, 0);
        }
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

}
