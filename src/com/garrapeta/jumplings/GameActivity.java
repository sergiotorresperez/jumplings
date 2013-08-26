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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.garrapeta.gameengine.GameView;
import com.garrapeta.gameengine.GameWorld;
import com.garrapeta.gameengine.SyncGameMessage;
import com.garrapeta.jumplings.Tutorial.TipDialogFragment.TipDialogListener;
import com.garrapeta.jumplings.actor.PremiumPurchaseHelper;
import com.garrapeta.jumplings.actor.PremiumPurchaseHelper.PurchaseCallback;
import com.garrapeta.jumplings.flurry.FlurryHelper;
import com.garrapeta.jumplings.ui.AdDialogHelper;
import com.garrapeta.jumplings.ui.AdDialogHelper.AdDialogListener;
import com.garrapeta.jumplings.ui.GameOverDialogFactory;
import com.garrapeta.jumplings.ui.GameOverDialogFactory.GameOverDialogFragment.GameOverDialogListener;
import com.garrapeta.jumplings.ui.PauseDialogFactory;
import com.garrapeta.jumplings.ui.PauseDialogFactory.PauseDialogFragment.PauseDialogListener;
import com.garrapeta.jumplings.wave.CampaignWave;
import com.garrapeta.jumplings.wave.TestWave;

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



    // ----------------------------------------------------- Variables de
    // instancia

    /**
     * Mundo
     */
    public JumplingsGameWorld mWorld;

    /** Wave actual */
    String waveKey;

    /**
     * Si el jugador ha muerto
     */
    private boolean gameOver = false;

    private ImageButton mPauseBtn;

    ViewGroup mLifeCounterView;
    ProgressBar mSpecialWeaponBar;

    boolean blinkingLifeBar = false;

    TextView mScoreTextView;

    TextView mLocalHighScoreTextView;
    
    private AdDialogHelper mAdDialogHelper;
    
    // used to resolve the state of the in app billing purchases and to launch purchases
    private PremiumPurchaseHelper mPremiumHelper;
    

    // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
    // DEBUG
    public Button testBtn;
    public RadioGroup weaponsRadioGroup;
    // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
    // DEBUG

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
        mScoreTextView = (TextView) findViewById(R.id.game_scoreTextView);
        mLocalHighScoreTextView = (TextView) findViewById(R.id.game_localHightscoreTextView);
        Score hs = PermData.getLocalGetHighScore(this);
        if (hs != null) {
            long localHighScore = hs.score;
            if (localHighScore > 0) {
                TextView highScoreTextView = mLocalHighScoreTextView;
                final String highScoreStr =  getString(R.string.game_highscore, localHighScore);
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
        mWorld.setDrawDebugInfo(getResources().getBoolean(R.bool.config_debug_functions_enabled));
        
        
        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
        // DEBUG

        if (getResources().getBoolean(R.bool.config_debug_functions_enabled)) {
            testBtn = (Button) findViewById(R.id.game_testBtn);
            testBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWorld.mWave.onTestButtonClicked(testBtn);
                }
            });

            // Menu de armas
            weaponsRadioGroup = (RadioGroup) findViewById(R.id.game_weaponsBtnGroup);
            weaponsRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup rg, int id) {
                    if (id == R.id.game_weaponsRadioBtnGun) {
                        mWorld.setWeapon(WeaponSlap.WEAPON_CODE_GUN);
                        // } else if (id == R.id.game_weaponsRadioBtnShotgun) {
                        // world.setWeapon(Shotgun.WEAPON_CODE_SHOTGUN);
                    } else if (id == R.id.game_weaponsRadioBtnBlade) {
                        mWorld.setWeapon(WeaponSword.WEAPON_CODE_BLADE);
                    }

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
        mPremiumHelper = new PremiumPurchaseHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(JumplingsApplication.LOG_SRC, "onStart " + this);
        // FIXME: no se realiza repintado
        FlurryHelper.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(JumplingsApplication.LOG_SRC, "onStop " + this);
        FlurryHelper.onEndSession(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(JumplingsApplication.LOG_SRC, "onRestart " + this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(JumplingsApplication.LOG_SRC, "onPause " + this);
        if (!gameOver) {
            pauseGame();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(JumplingsApplication.LOG_SRC, "onResume " + this);
        if (mWorld.isPaused()) {
            showPauseDialog();
        }
        // Do not resume game here: user will resume by pressing button in pause
        // dialog
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(JumplingsApplication.LOG_SRC, "onDestroy " + this);
        mWorld.post(new SyncGameMessage() {
			@Override
			public void doInGameLoop(GameWorld world) {
		        mWorld.finish();
		        mWorld = null;
			}
		});
        // If the user presses the on / off button of the phone and the activity
        // is destroyed, we want to show the menu activity when going to the task again.
        finish();
    }

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(JumplingsApplication.LOG_SRC, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
    	if (mPremiumHelper != null && mPremiumHelper.onActivityResult(requestCode, resultCode, data)) {
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

        Score highScore = new Score(this, mWorld.getPlayer().getScore(), mWorld.mWave.getLevel());

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
        dialog.show(getSupportFragmentManager(),  DIALOG_FRAGMENT_TAG);
        mPauseBtn.setVisibility(View.GONE);
    }

    /**
     * Contin�a el juego
     */
    void resumeGame() {
        mPauseBtn.setVisibility(View.VISIBLE);
        mWorld.resume();
    }

    /**
     * @return si el jugador ha muerto
     */
    boolean isGameOver() {
        return gameOver;
    }

    /**
     * Invocado al morir el jugador
     */
    public void onGameOver() {
        gameOver = true;
        mWorld.mScenario.onGameOver();
        FlurryHelper.logGameOver(mWorld.currentGameMillis(), mWorld.mWave.getLevel(), mWorld.getPlayer().getScore());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogFragment dialog = GameOverDialogFactory.create();
                dialog.show(getSupportFragmentManager(),  DIALOG_FRAGMENT_TAG);
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
     * Actualizaci�n de la barra de arma especial
     */
    public void updateSpecialWeaponBar() {
        Weapon weapon = mWorld.mWeapon;
        int progress = weapon.getRemainingTime();
        mSpecialWeaponBar.setProgress(progress);
    }

    public void activateSpecialWeaponBar(final boolean active) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (active) {
                    mSpecialWeaponBar.setVisibility(View.VISIBLE);
                    mSpecialWeaponBar.setMax(mWorld.mWeapon.getMaxTime());
                    updateSpecialWeaponBar();
                } else {
                    mSpecialWeaponBar.setVisibility(View.INVISIBLE);
                }

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
                mScoreTextView.setText(String.valueOf(mWorld.getPlayer().getScore()));
            }
        });

    }

    /**
     * Deja la barra de vida parpadeando
     */
    public void startBlinkingLifeBar() {
        if (!blinkingLifeBar) {
            blinkingLifeBar = true;

            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (blinkingLifeBar) {

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
        blinkingLifeBar = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLifeCounterView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Shows an ad dialog if there is an ad available
     * @return if the dialog has been shown.
     */
    public void showAdDialogIfAvailable() {
    	if (getResources().getBoolean(R.bool.config_ads_enabled) && mPremiumHelper.isPremiumPurchaseStateKnown(this) && !mPremiumHelper.isPremiumPurchased(this)) {
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
		mPremiumHelper.purchasePremiumAsync(this, new PurchaseCallback() {
			@Override
			public void onPurchaseFinished(boolean purchased) {
				Log.i(JumplingsApplication.LOG_SRC, "Premium purchased.");
				FlurryHelper.logPurchasedFromGame();
				mWorld.resume();
			}
			
			@Override
			public void onPurchaseError(String message) {
				Log.e(JumplingsApplication.LOG_SRC, "Error querying purchase state " + message);
				mWorld.resume();
			}
		});
	}
    
    /**
     * Executed when the level changes
     * @param level
     */
    public void onLevelChanged(final int level) {
        mWorld.mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message =  getString(R.string.game_level, level);
                TextView levelTextView = (TextView) findViewById(R.id.game_levelTextView);
                levelTextView.setText(message);
                Toast.makeText(GameActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
 
    // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG

    public void updateWeaponsRadioGroup(final short weaponId) {
        if (weaponsRadioGroup.getVisibility() == View.VISIBLE) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int checked = 0;

                    switch (weaponId) {
                    case WeaponSlap.WEAPON_CODE_GUN:
                        checked = R.id.game_weaponsRadioBtnGun;
                        break;
                    // case Shotgun.WEAPON_CODE_SHOTGUN:
                    // checked = R.id.game_weaponsRadioBtnShotgun;
                    // break;
                    case WeaponSword.WEAPON_CODE_BLADE:
                        checked = R.id.game_weaponsRadioBtnBlade;
                        break;
                    }

                    if (weaponsRadioGroup.getCheckedRadioButtonId() != checked) {
                        weaponsRadioGroup.check(checked);
                    }
                }
            });

        }
    }
    // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG


}