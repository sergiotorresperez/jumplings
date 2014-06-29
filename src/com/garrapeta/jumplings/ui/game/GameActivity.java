package com.garrapeta.jumplings.ui.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.garrapeta.gameengine.GameView;
import com.garrapeta.gameengine.utils.LogX;
import com.garrapeta.jumplings.JumplingsApplication;
import com.garrapeta.jumplings.R;
import com.garrapeta.jumplings.game.JumplingsGameWorld;
import com.garrapeta.jumplings.game.Player;
import com.garrapeta.jumplings.game.Score;
import com.garrapeta.jumplings.game.Tutorial.TipDialogFragment.TipDialogListener;
import com.garrapeta.jumplings.game.Wave;
import com.garrapeta.jumplings.game.wave.CampaignWave;
import com.garrapeta.jumplings.game.wave.TestWave;
import com.garrapeta.jumplings.game.weapon.SwordWeapon;
import com.garrapeta.jumplings.ui.gameover.GameOverActivity;
import com.garrapeta.jumplings.ui.menu.MenuActivity;
import com.garrapeta.jumplings.util.AdsHelper;
import com.garrapeta.jumplings.util.FlurryHelper;
import com.garrapeta.jumplings.util.InAppPurchaseHelper;
import com.garrapeta.jumplings.util.InAppPurchaseHelper.PurchaseCallback;
import com.garrapeta.jumplings.util.PermData;
import com.garrapeta.jumplings.view.JumplingsToast;
import com.garrapeta.jumplings.view.dialog.AdDialogHelper;
import com.garrapeta.jumplings.view.dialog.AdDialogHelper.AdDialogListener;
import com.garrapeta.jumplings.view.dialog.GameOverDialogFactory;
import com.garrapeta.jumplings.view.dialog.GameOverDialogFactory.GameOverDialogFragment.GameOverDialogListener;
import com.garrapeta.jumplings.view.dialog.PauseDialogFactory;
import com.garrapeta.jumplings.view.dialog.PauseDialogFactory.PauseDialogFragment.PauseDialogListener;

/**
 * Activity where the game itself happens.
 */
public class GameActivity extends FragmentActivity implements TipDialogListener, AdDialogListener, PauseDialogListener, GameOverDialogListener, OnClickListener {

    public static final String WAVE_BUNDLE_KEY = "waveKey";
    private static final int LIFEBAR_BLINKING_LAPSE = 100; // In ms
    public static final String DIALOG_FRAGMENT_TAG = "dialog_fragment_tag";
    private final int SWORD_PROGRESS_BAR_MAX = 100;

    public JumplingsGameWorld mWorld;
    private String waveKey;
    private boolean mBlinkingLifeBar = false;

    private TextView mScoreTextView;
    private TextView mLocalHighScoreTextView;
    public View mTestBtn;
    public View mSwordBtn;
    private View mPauseBtn;
    private ViewGroup mLifeCounterView;
    private ProgressBar mSpecialWeaponBar;
    private AdDialogHelper mAdDialogHelper;
    private InAppPurchaseHelper mInAppPurchaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        mPauseBtn = findViewById(R.id.game_pauseBtn);
        mPauseBtn.setOnClickListener(this);

        mLifeCounterView = (ViewGroup) findViewById(R.id.lifes_counter_layout);
        mSpecialWeaponBar = (ProgressBar) findViewById(R.id.game_specialWeaponBar);
        mSpecialWeaponBar.setMax(SWORD_PROGRESS_BAR_MAX);
        mScoreTextView = (TextView) findViewById(R.id.game_scoreTextView);
        mLocalHighScoreTextView = (TextView) findViewById(R.id.game_localHightscoreTextView);
        Score hs = PermData.getLocalHighestScore(this);
        if (hs != null) {
            long localHighScore = hs.mScore;
            if (localHighScore > 0) {
                TextView highScoreTextView = mLocalHighScoreTextView;
                final String highScoreStr = getString(R.string.game_highscore, localHighScore);
                highScoreTextView.setText(highScoreStr);
            }
        }

        Bundle b = getIntent().getExtras();
        if (b != null) {
            waveKey = b.getString(WAVE_BUNDLE_KEY);
        }

        mWorld = new JumplingsGameWorld(this, (GameView) findViewById(R.id.game_surface), this);
        mWorld.setDrawDebugInfo(PermData.areDebugFeaturesEnabled(this));

        updateLifeCounterView();
        updateScoreTextView();

        if (PermData.areDebugFeaturesEnabled(this)) {
            mTestBtn = (Button) findViewById(R.id.game_testBtn);
            mTestBtn.setOnClickListener(this);

            mSwordBtn = (Button) findViewById(R.id.game_swordBtn);
            mSwordBtn.setOnClickListener(this);
        }

        if (waveKey.equals(CampaignWave.WAVE_KEY)) {
            mWorld.setWave(new CampaignWave(mWorld));
        } else if (waveKey.equals(TestWave.WAVE_KEY)) {
            mWorld.setWave(new TestWave(mWorld));
        } else {
            throw new IllegalArgumentException("Cannot create wave: " + waveKey);
        }

        mAdDialogHelper = new AdDialogHelper(this, DIALOG_FRAGMENT_TAG);
        mInAppPurchaseHelper = new InAppPurchaseHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogX.i(JumplingsApplication.TAG, "onStart " + this);
        // FIXME: no se realiza repintado
        FlurryHelper.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogX.i(JumplingsApplication.TAG, "onStop " + this);
        FlurryHelper.onEndSession(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogX.i(JumplingsApplication.TAG, "onRestart " + this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogX.i(JumplingsApplication.TAG, "onPause " + this);
        if (!mWorld.isGameOver()) {
            pauseGame();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogX.i(JumplingsApplication.TAG, "onResume " + this);
        if (mWorld.isPaused()) {
            showPauseDialog();
        }
        // Do not resume game here: user will resume by pressing button in pause
        // dialog
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogX.i(JumplingsApplication.TAG, "onDestroy " + this);
        mWorld.finish();
        // If the user presses the on / off button of the phone and the activity
        // is destroyed, we want to show the menu activity when going to the
        // task again.
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogX.d(JumplingsApplication.TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mInAppPurchaseHelper != null && mInAppPurchaseHelper.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public JumplingsGameWorld getWorld() {
        return mWorld;
    }

    private void gotoGameOverActivity() {
        finish();
        final Intent intent = new Intent(this, GameOverActivity.class);

        final Player player = mWorld.getPlayer();
        final int level = mWorld.getWave()
                                .getLevel();
        Score highScore = new Score(this, player.getScore(), level);

        intent.putExtra(GameOverActivity.NEW_HIGHSCORE_KEY, highScore);
        intent.putExtra(GameActivity.WAVE_BUNDLE_KEY, waveKey);

        startActivity(intent);
    }

    /**
     * va a la actividad del main menu
     */
    private void gotoMenuActivity() {
        finish();
        Intent i = new Intent(this, MenuActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        pauseGame();
        showPauseDialog();
    }

    void pauseGame() {
        if (!mWorld.isPaused()) {
            mWorld.pause();
        }
    }

    private void showPauseDialog() {
        if (getSupportFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
            // the dialog was already shown
            return;
        }

        // If the game is over the game over dialog will be active
        DialogFragment dialog = PauseDialogFactory.create();
        dialog.show(getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
        mPauseBtn.setVisibility(View.INVISIBLE);
    }

    void resumeGame() {
        mPauseBtn.setVisibility(View.VISIBLE);
        mWorld.resume();
    }

    public void onGameOver() {
        mWorld.getScenario()
              .onGameOver();
        final Wave<?> wave = mWorld.getWave();
        final int score = mWorld.getPlayer()
                                .getScore();
        FlurryHelper.logGameOver(mWorld.currentGameMillis(), wave.getLevel(), score);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogFragment dialog = GameOverDialogFactory.create();
                dialog.show(getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
            }
        });
    }

    public void updateLifeCounterView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Player player = mWorld.getPlayer();
                int lifesLeft = player.getLifes();
                ViewGroup lifes = mLifeCounterView;
                int count = lifes.getChildCount();
                for (int i = 0; i < count; i++) {
                    View life = lifes.getChildAt(i);
                    if (i < lifesLeft) {
                        life.setVisibility(View.VISIBLE);
                    } else {
                        life.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    /**
     * Start of the sword lifeTime
     */
    public void onSwordStarted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSpecialWeaponBar.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Update of sword progress bar
     */
    public void updateSwordRemainingTimeUpdated(final float progress) {
        // TODO: send this via a handler
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int visualProgress = (int) (progress * SWORD_PROGRESS_BAR_MAX);
                mSpecialWeaponBar.setProgress(visualProgress);
            }
        });
    }

    /**
     * End of the sword lifeTime
     */
    public void onSwordEnded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSpecialWeaponBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void updateScoreTextView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mScoreTextView.setText(String.valueOf(mWorld.getPlayer()
                                                            .getScore()));
            }
        });

    }

    public void startBlinkingLifeBar() {
        if (!mBlinkingLifeBar) {
            mBlinkingLifeBar = true;

            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (mBlinkingLifeBar) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int state = (mLifeCounterView.getVisibility() == View.INVISIBLE) ? View.VISIBLE : View.INVISIBLE;
                                mLifeCounterView.setVisibility(state);
                            }
                        });

                        try {
                            Thread.sleep(LIFEBAR_BLINKING_LAPSE);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
        }
    }

    public void stopBlinkingLifeBar() {
        mBlinkingLifeBar = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLifeCounterView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Shows an ad dialog if there is an ad available
     * 
     * @return if the dialog has been shown.
     */
    public void showAdDialogIfAvailable() {
        if (AdsHelper.shoulShowAds(this)) {
            mAdDialogHelper.showIfAvailable();
        }
    }

    @Override
    public void onPauseDialogShown() {
    }

    @Override
    public void onPauseDialogClosed() {
    }

    @Override
    public void onResumeButtonClicked() {
        resumeGame();
    }

    @Override
    public void onMainMenuButtonClicked() {
        gotoMenuActivity();
    }

    @Override
    public void onGameOverDialogShown() {
    }

    @Override
    public void onGameOverDialogClosed() {
        gotoGameOverActivity();
    }

    @Override
    public void onTipDialogShown() {
        if (mWorld.isRunning()) {
            mWorld.pause();
        }
    }

    @Override
    public void onTipDialogClosed() {
        mWorld.resume();
    }

    @Override
    public void onAdDialogShown() {
        if (mWorld.isRunning()) {
            mWorld.pause();
        }
    }

    @Override
    public void onAdDialogClosed() {
        mWorld.resume();
    }

    @Override
    public AdDialogHelper getAdDialogFactory() {
        return mAdDialogHelper;
    }

    @Override
    public void onPurchaseBtnClicked() {
        FlurryHelper.logBuyBtnClickedFromGame();
        mInAppPurchaseHelper.purchasePremiumAsync(this, new PurchaseCallback() {
            @Override
            public void onPurchaseFinished(boolean purchased) {
                LogX.i(JumplingsApplication.TAG, "Premium purchased.");
                FlurryHelper.logPurchasedFromGame();
                mWorld.resume();
            }

            @Override
            public void onPurchaseError(String message) {
                LogX.e(JumplingsApplication.TAG, "Error querying purchase state " + message);
                mWorld.resume();
            }
        });
    }

    /**
     * Executed when the level changes
     * 
     * @param level
     */
    public void onLevelChanged(final int level) {
        mWorld.mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = getString(R.string.game_level, level);
                TextView levelTextView = (TextView) findViewById(R.id.game_levelTextView);
                levelTextView.setText(message);
                JumplingsToast.show(GameActivity.this, message, JumplingsToast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.game_pauseBtn:
            pauseGame();
            showPauseDialog();
            return;
        case R.id.game_testBtn:
            mWorld.getWave()
                  .onTestButtonClicked(mTestBtn);
            return;
        case R.id.game_swordBtn:
            mWorld.setWeapon(SwordWeapon.WEAPON_CODE_SWORD);
            return;
        }

    }

}