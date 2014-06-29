package com.garrapeta.jumplings.ui.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.garrapeta.gameengine.GameView;
import com.garrapeta.gameengine.utils.LogX;
import com.garrapeta.jumplings.JumplingsApplication;
import com.garrapeta.jumplings.R;
import com.garrapeta.jumplings.game.JumplingsWorld;
import com.garrapeta.jumplings.game.wave.CampaignWave;
import com.garrapeta.jumplings.game.wave.MenuWave;
import com.garrapeta.jumplings.game.wave.TestWave;
import com.garrapeta.jumplings.ui.about.AboutActivity;
import com.garrapeta.jumplings.ui.game.GameActivity;
import com.garrapeta.jumplings.ui.preferences.PreferencesActivity;
import com.garrapeta.jumplings.ui.scores.ScoresActivity;
import com.garrapeta.jumplings.util.AdsHelper;
import com.garrapeta.jumplings.util.FlurryHelper;
import com.garrapeta.jumplings.util.GooglePlayGamesLeaderboardHelper;
import com.garrapeta.jumplings.util.InAppPurchaseHelper;
import com.garrapeta.jumplings.util.InAppPurchaseHelper.PurchaseCallback;
import com.garrapeta.jumplings.util.InAppPurchaseHelper.PurchaseStateQueryCallback;
import com.garrapeta.jumplings.util.PermData;
import com.garrapeta.jumplings.util.Utils;
import com.garrapeta.jumplings.view.dialog.PurchaseDialogFactory;
import com.garrapeta.jumplings.view.dialog.PurchaseDialogFactory.PurchaseDialogFragment.PurchaseDialogListener;
import com.google.android.gms.ads.AdView;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;
import com.google.example.games.basegameutils.RemindfulGameActivity;

/**
 * Activity implementing the menu screen
 */
public class MenuActivity extends RemindfulGameActivity implements PurchaseDialogListener, OnClickListener, GameHelperListener {

    private final static String TAG = MenuActivity.class.getSimpleName();
    /**
     * Tag used to refer to the dialog fragment
     */
    static final String DIALOG_FRAGMENT_TAG = "dialog_fragment_tag";

    private View mTitle;
    private View mStartBtn;
    private View mPreferencesBtn;
    private View mHighScoresBtn;
    private View mAboutBtn;
    private View mShareButton;
    private View mPremiumBtn;
    private View mGooglePlaySignInBtn;
    private View mGooglePlayLeaderboardBtn;
    private AdView mAdView;
    private View mDebugGroup;

    private JumplingsWorld mWorld;

    private boolean mAnimationShown = false;

    // used to resolve the state of the in app billing purchases
    private InAppPurchaseHelper mInAppPurchaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        mTitle = findViewById(R.id.menu_title);

        mStartBtn = findViewById(R.id.menu_playBtn);
        mStartBtn.setOnClickListener(this);

        mDebugGroup = findViewById(R.id.menu_debug_view_group);

        mHighScoresBtn = findViewById(R.id.menu_highScoresBtn);
        mHighScoresBtn.setOnClickListener(this);

        mShareButton = findViewById(R.id.menu_shareBtn);
        mShareButton.setOnClickListener(this);

        mPremiumBtn = findViewById(R.id.menu_premiumBtn);
        mPremiumBtn.setOnClickListener(this);

        mPreferencesBtn = findViewById(R.id.menu_preferencesBtn);
        mPreferencesBtn.setOnClickListener(this);

        mAboutBtn = findViewById(R.id.menu_aboutBtn);
        mAboutBtn.setOnClickListener(this);

        mGooglePlaySignInBtn = findViewById(R.id.menu_google_play_games_sign_in);
        mGooglePlaySignInBtn.setOnClickListener(this);

        mGooglePlayLeaderboardBtn = findViewById(R.id.menu_google_play_games_leaderboard);
        mGooglePlayLeaderboardBtn.setOnClickListener(this);

        mAdView = (AdView) findViewById(R.id.menu_advertising_banner_view);

        View testBtn = findViewById(R.id.menu_testBtn);
        testBtn.setOnClickListener(this);

        View exitBtn = findViewById(R.id.menu_exitBtn);
        exitBtn.setOnClickListener(this);

        // The UI starts invisible and becomes visible with an animation
        mTitle.setVisibility(View.INVISIBLE);
        mStartBtn.setVisibility(View.INVISIBLE);
        mPreferencesBtn.setVisibility(View.INVISIBLE);
        mHighScoresBtn.setVisibility(View.INVISIBLE);
        mAboutBtn.setVisibility(View.INVISIBLE);
        mDebugGroup.setVisibility(PermData.areDebugFeaturesEnabled(this) ? View.INVISIBLE : View.GONE);
        mShareButton.setVisibility(View.INVISIBLE);
        mAdView.setVisibility(View.INVISIBLE);
        mPremiumBtn.setVisibility(View.INVISIBLE);
        mGooglePlaySignInBtn.setVisibility(View.INVISIBLE);
        mGooglePlayLeaderboardBtn.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        LogX.i(JumplingsApplication.TAG, "onStart " + this);
        FlurryHelper.onStartSession(this);

        mWorld = new JumplingsWorld(this, (GameView) findViewById(R.id.menu_gamesurface), this);
        mWorld.setDrawDebugInfo(PermData.areDebugFeaturesEnabled(this));
        mWorld.setWave(new MenuWave(mWorld));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogX.i(JumplingsApplication.TAG, "onPause " + this);
        if (mWorld.isRunning()) {
            mWorld.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogX.i(JumplingsApplication.TAG, "onResume " + this);
        mWorld.resume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogX.i(JumplingsApplication.TAG, "onStop " + this);
        FlurryHelper.onEndSession(this);

        if (mWorld.isRunning()) {
            mWorld.finish();
            mWorld = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogX.i(JumplingsApplication.TAG, "onDestroy " + this);
        if (mInAppPurchaseHelper != null) {
            mInAppPurchaseHelper.dispose();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogX.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mInAppPurchaseHelper != null && mInAppPurchaseHelper.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.menu_playBtn:
            startNewGame();
            return;
        case R.id.menu_highScoresBtn:
            showHighScores();
            return;
        case R.id.menu_shareBtn:
            FlurryHelper.logShareButtonClicked();
            Utils.share(MenuActivity.this, getString(R.string.menu_share));
            return;
        case R.id.menu_preferencesBtn:
            showPreferences();
            return;
        case R.id.menu_aboutBtn:
            showAbout();
            return;
        case R.id.menu_premiumBtn:
            DialogFragment dialog = PurchaseDialogFactory.create();
            dialog.show(getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
            return;
        case R.id.menu_google_play_games_sign_in:
            beginUserInitiatedSignIn();
            return;
        case R.id.menu_google_play_games_leaderboard:
            GooglePlayGamesLeaderboardHelper.showLeaderboard(this, getApiClient());
            return;
        case R.id.menu_testBtn:
            startTest();
            return;
        case R.id.menu_exitBtn:
            finish();
            return;
        }
    }

    @Override
    public void onPurchaseBtnClicked() {
        FlurryHelper.logBuyBtnClickedFromHome();
        mInAppPurchaseHelper.purchasePremiumAsync(this, new PurchaseCallback() {
            @Override
            public void onPurchaseFinished(boolean purchased) {
                FlurryHelper.logPurchasedFromHome();
                onPremiumStateUpdate(purchased);
            }

            @Override
            public void onPurchaseError(String message) {
                LogX.i(TAG, "Error querying purchase state " + message);
            }
        });
    }

    @Override
    protected boolean getDefaultConnectOnStart() {
        return true;
    }

    @Override
    public void onSignInSucceeded() {
        resolvePurchaseState();
        GooglePlayGamesLeaderboardHelper.submitHighestScoreIfNeeded(this, getApiClient());
    }

    @Override
    public void onSignInFailed() {
        resolvePurchaseState();
    }

    private void resolvePurchaseState() {
        // Query the state of the purchase
        mInAppPurchaseHelper = new InAppPurchaseHelper(this);
        if (PermData.isPremiumPurchaseStateKnown(this)) {
            LogX.d(TAG, "Premium purchase state known. No need to query.");
            startAnimation(PermData.isPremiumPurchased(this));
        } else {
            LogX.d(TAG, "Premium purchase state unknown. Querying for it.");
            mInAppPurchaseHelper.queryIsPremiumPurchasedAsync(this, new PurchaseStateQueryCallback() {
                @Override
                public void onPurchaseStateQueryFinished(boolean purchased) {
                    startAnimation(purchased);
                }

                @Override
                public void onPurchaseStateQueryError(String message) {
                    LogX.i(TAG, "Error querying purchase state " + message);
                    // we assume it is purchased
                    startAnimation(true);
                }
            });
        }
    }

    private void onPremiumStateUpdate(boolean purchased) {
        LogX.i(TAG, "Premium upgrade purchased: " + purchased);
        if (!AdsHelper.shoulShowAds(this)) {
            // this will prevent the animations to start, and the views will
            // never become visible
            mAdView.setVisibility(View.GONE);
            mPremiumBtn.setVisibility(View.GONE);
        }
    }

    private void startAnimation(boolean purchased) {
        onPremiumStateUpdate(purchased);
        if (!mAnimationShown) {
            mAnimationShown = true;
            onStartAnimationPhaseOne();
        }
    }

    private void onStartAnimationPhaseOne() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.menu_screen_scale_in);
        fadeInAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onStartAnimationPhaseTwo();
            }
        });

        // doing this here instead of onAnimationStart because of problems of
        // the animation not starting in old devices
        mTitle.setVisibility(View.VISIBLE);
        mTitle.startAnimation(fadeInAnimation);
    }

    private void onStartAnimationPhaseTwo() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeInAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });

        // doing this here instead of onAnimationStart because of problems of
        // the animation not starting in old devices
        mStartBtn.setVisibility(View.VISIBLE);
        mStartBtn.setVisibility(View.VISIBLE);
        mPreferencesBtn.setVisibility(View.VISIBLE);
        mHighScoresBtn.setVisibility(View.VISIBLE);
        mAboutBtn.setVisibility(View.VISIBLE);
        mDebugGroup.setVisibility(PermData.areDebugFeaturesEnabled(this) ? View.VISIBLE : View.GONE);
        mShareButton.setVisibility(View.VISIBLE);

        if (AdsHelper.shoulShowAds(this)) {
            mAdView.setVisibility(View.VISIBLE);
            mPremiumBtn.setVisibility(View.VISIBLE);
            AdsHelper.requestAd(mAdView);
            mPremiumBtn.startAnimation(fadeInAnimation);
        } else {
            mAdView.setVisibility(View.GONE);
            mPremiumBtn.setVisibility(View.GONE);
        }

        if (getGameHelper().getApiClient()
                           .isConnected()) {
            mGooglePlaySignInBtn.setVisibility(View.GONE);
            mGooglePlayLeaderboardBtn.setVisibility(View.VISIBLE);
            mGooglePlayLeaderboardBtn.startAnimation(fadeInAnimation);
        } else {
            mGooglePlaySignInBtn.setVisibility(View.VISIBLE);
            mGooglePlayLeaderboardBtn.setVisibility(View.GONE);
            mGooglePlaySignInBtn.startAnimation(fadeInAnimation);
        }

        mStartBtn.startAnimation(fadeInAnimation);
        mPreferencesBtn.startAnimation(fadeInAnimation);
        mHighScoresBtn.startAnimation(fadeInAnimation);
        mAboutBtn.startAnimation(fadeInAnimation);
        mDebugGroup.startAnimation(fadeInAnimation);
        mAdView.startAnimation(fadeInAnimation);
        mShareButton.startAnimation(fadeInAnimation);
    }

    private void startNewGame() {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(GameActivity.WAVE_BUNDLE_KEY, CampaignWave.WAVE_KEY);
        startActivity(i);
    }

    private void startTest() {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(GameActivity.WAVE_BUNDLE_KEY, TestWave.WAVE_KEY);
        startActivity(i);

    }

    private void showHighScores() {
        Intent intent = new Intent(this, ScoresActivity.class);
        startActivity(intent);
    }

    private void showPreferences() {
        Intent i = new Intent(this, PreferencesActivity.class);
        startActivity(i);
    }

    private void showAbout() {
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }

}