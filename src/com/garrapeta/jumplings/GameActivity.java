package com.garrapeta.jumplings;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.garrapeta.gameengine.GameView;
import com.garrapeta.gameengine.utils.L;
import com.garrapeta.jumplings.Tutorial.TipDialogFragment.TipDialogListener;
import com.garrapeta.jumplings.flurry.FlurryHelper;
import com.garrapeta.jumplings.ui.AdDialogHelper;
import com.garrapeta.jumplings.ui.AdDialogHelper.AdDialogListener;
import com.garrapeta.jumplings.ui.GameOverDialogFactory;
import com.garrapeta.jumplings.ui.GameOverDialogFactory.GameOverDialogFragment.GameOverDialogListener;
import com.garrapeta.jumplings.ui.JumplingsToast;
import com.garrapeta.jumplings.ui.PauseDialogFactory;
import com.garrapeta.jumplings.ui.PauseDialogFactory.PauseDialogFragment.PauseDialogListener;
import com.garrapeta.jumplings.util.AdsHelper;
import com.garrapeta.jumplings.util.InAppPurchaseHelper;
import com.garrapeta.jumplings.util.InAppPurchaseHelper.PurchaseCallback;
import com.garrapeta.jumplings.wave.CampaignWave;
import com.garrapeta.jumplings.wave.TestWave;
import com.garrapeta.jumplings.weapon.SwordWeapon;

public class GameActivity extends FragmentActivity implements TipDialogListener, AdDialogListener, PauseDialogListener, GameOverDialogListener {

    // -----------------------------------------------------------------
    // Constantes

    // Constantes de keys del bundle
    public static final String WAVE_BUNDLE_KEY = "waveKey";

    /** Lapso de parpadeo de la barra de vida, en ms */
    private static final int LIFEBAR_BLINKING_LAPSE = 100;

    /**
     * Tag used to refer to the dialog fragment
     */
    static final String DIALOG_FRAGMENT_TAG = "dialog_fragment_tag";

    private final int SWORD_PROGRESS_BAR_MAX = 100;

    // ----------------------------------------------------- Variables de
    // instancia

    /**
     * Mundo
     */
    public JumplingsGameWorld mWorld;

    /** Wave actual */
    String waveKey;

    private ImageButton mPauseBtn;

    ViewGroup mLifeCounterView;
    private ProgressBar mSpecialWeaponBar;

    boolean mBlinkingLifeBar = false;

    TextView mScoreTextView;

    TextView mLocalHighScoreTextView;

    private AdDialogHelper mAdDialogHelper;

    // used to launch purchases
    private InAppPurchaseHelper mInAppPurchaseHelper;

    public Button mTestBtn;
    public Button mSwordBtn;

    // -------------------------------------------------- M�todos de Activity

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialisation of views and GUI
        setContentView(R.layout.activity_game);

        mPauseBtn = (ImageButton) findViewById(R.id.game_pauseBtn);
        mPauseBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseGame();
                showPauseDialog();
            }
        });
        mLifeCounterView = (ViewGroup) findViewById(R.id.lifes_counter_layout);
        mSpecialWeaponBar = (ProgressBar) findViewById(R.id.game_specialWeaponBar);
        mSpecialWeaponBar.setMax(SWORD_PROGRESS_BAR_MAX);
        mScoreTextView = (TextView) findViewById(R.id.game_scoreTextView);
        mLocalHighScoreTextView = (TextView) findViewById(R.id.game_localHightscoreTextView);
        Score hs = PermData.getLocalGetHighScore(this);
        if (hs != null) {
            long localHighScore = hs.score;
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

        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
        // DEBUG

        if (waveKey == null) {
            waveKey = TestWave.WAVE_KEY;
        }

        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
        // DEBUG

        mWorld = new JumplingsGameWorld(this, (GameView) findViewById(R.id.game_surface), this);
        mWorld.setDrawDebugInfo(PermData.areDebugFeaturesEnabled(this));

        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
        // DEBUG

        if (PermData.areDebugFeaturesEnabled(this)) {
            mTestBtn = (Button) findViewById(R.id.game_testBtn);
            mTestBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWorld.mWave.onTestButtonClicked(mTestBtn);
                }
            });

            // Menu de armas
            mSwordBtn = (Button) findViewById(R.id.game_swordBtn);
            mSwordBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWorld.setWeapon(SwordWeapon.WEAPON_CODE_SWORD);
                }
            });
        }

        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
        // DEBUG

        updateLifeCounterView();
        updateScoreTextView();

        // Preparaci�n de la wave

        if (waveKey.equals(CampaignWave.WAVE_KEY)) {
            mWorld.mWave = new CampaignWave(mWorld);
            // } else if (waveKey.equals(CampaignTutorialWave.WAVE_KEY)) {
            // world.wave = new CampaignTutorialWave(world, null, 1);
        } else if (waveKey.equals(TestWave.WAVE_KEY)) {
            mWorld.mWave = new TestWave(mWorld);
            // jgWorld.wave = new CampaignSurvivalWave(jgWorld, null);
        } else {
            throw new IllegalArgumentException("Cannot create wave: " + waveKey);
        }

        // Preparation of ad dialog helper
        mAdDialogHelper = new AdDialogHelper(this, DIALOG_FRAGMENT_TAG);
        mInAppPurchaseHelper = new InAppPurchaseHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (L.sEnabled)
            Log.i(JumplingsApplication.TAG, "onStart " + this);
        // FIXME: no se realiza repintado
        FlurryHelper.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (L.sEnabled)
            Log.i(JumplingsApplication.TAG, "onStop " + this);
        FlurryHelper.onEndSession(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (L.sEnabled)
            Log.i(JumplingsApplication.TAG, "onRestart " + this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (L.sEnabled)
            Log.i(JumplingsApplication.TAG, "onPause " + this);
        if (!mWorld.isGameOver()) {
            pauseGame();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (L.sEnabled)
            Log.i(JumplingsApplication.TAG, "onResume " + this);
        if (mWorld.isPaused()) {
            showPauseDialog();
        }
        // Do not resume game here: user will resume by pressing button in pause
        // dialog
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (L.sEnabled)
            Log.i(JumplingsApplication.TAG, "onDestroy " + this);
        mWorld.finish();
        // If the user presses the on / off button of the phone and the activity
        // is destroyed, we want to show the menu activity when going to the
        // task again.
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (L.sEnabled)
            Log.d(JumplingsApplication.TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mInAppPurchaseHelper != null && mInAppPurchaseHelper.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // ---------------------------------------------------- M�todos propios

    public JumplingsGameWorld getWorld() {
        return mWorld;
    }

    /**
     * va a la actividad de introducci�n de nuevo highscores
     */
    private void gotoGameOverActivity() {
        finish();
        Intent intent = new Intent(this, GameOverActivity.class);

        Score highScore = new Score(this, mWorld.getPlayer()
                                                .getScore(), mWorld.mWave.getLevel());

        final RectF worldBoundaries = mWorld.mViewport.getWorldBoundaries();
        HighScoreListingActivity.putScreenSizeExtras(intent, worldBoundaries.width(), worldBoundaries.height());
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

    // ------------------------------ M�todos de gesti�n del estado del mundo

    @Override
    public void onBackPressed() {
        pauseGame();
        showPauseDialog();
    }

    /**
     * Pausa el juego
     */
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

    /**
     * Contin�a el juego
     */
    void resumeGame() {
        mPauseBtn.setVisibility(View.VISIBLE);
        mWorld.resume();
    }

    /**
     * Invocado al morir el jugador
     */
    public void onGameOver() {
        mWorld.mScenario.onGameOver();
        FlurryHelper.logGameOver(mWorld.currentGameMillis(), mWorld.mWave.getLevel(), mWorld.getPlayer()
                                                                                            .getScore());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogFragment dialog = GameOverDialogFactory.create();
                dialog.show(getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
            }
        });
    }

    // ---------------------------- M�todos de componentes de interacci�n

    /**
     * Actualizaci�n del contador de vidas
     */
    public void updateLifeCounterView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Player player = mWorld.getPlayer();
                int lifesLeft = player.getLifes();
                ViewGroup lifes = (ViewGroup) mLifeCounterView;
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

    /**
     * Actualizaci�n del texto de score
     */
    public void updateScoreTextView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mScoreTextView.setText(String.valueOf(mWorld.getPlayer()
                                                            .getScore()));
            }
        });

    }

    /**
     * Deja la barra de vida parpadeando
     */
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

    /**
     * Para el parpadero de la barra de vida
     */
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
                if (L.sEnabled)
                    Log.i(JumplingsApplication.TAG, "Premium purchased.");
                FlurryHelper.logPurchasedFromGame();
                mWorld.resume();
            }

            @Override
            public void onPurchaseError(String message) {
                if (L.sEnabled)
                    Log.e(JumplingsApplication.TAG, "Error querying purchase state " + message);
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

}