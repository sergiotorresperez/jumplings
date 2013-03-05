package net.garrapeta.jumplings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        mTitleView = findViewById(R.id.splash_title);
        mSubtitleView = findViewById(R.id.splash_subtitle);

        mTitleView.setVisibility(View.INVISIBLE);
        mSubtitleView.setVisibility(View.INVISIBLE);
 
        startAnimationPhaseOne();
    }

    private void startAnimationPhaseOne() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        fadeInAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mTitleView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startAnimationPhaseTwo(); 
            }
        });

        mTitleView.startAnimation(fadeInAnimation);
    }

    private void startAnimationPhaseTwo() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        fadeInAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mSubtitleView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startAnimationPhaseThree(); 
            }
        });
        mSubtitleView.startAnimation(fadeInAnimation);
    }

    private void startAnimationPhaseThree() {
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        fadeOutAnimation.setAnimationListener(new AnimationListener() {
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
                openMenuActivity(); 
            }
        });
        mTitleView.startAnimation(fadeOutAnimation);
        mSubtitleView.startAnimation(fadeOutAnimation);
    }

    private void openMenuActivity() {
        finish();
        overridePendingTransition(0, 0);
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

}
