package net.garrapeta.jumplings;

import net.garrapeta.gameengine.GameView;
import net.garrapeta.gameengine.module.VibratorManager;
import net.garrapeta.jumplings.ui.AdDialogFactory;
import net.garrapeta.jumplings.wave.CampaignSurvivalWave;
import net.garrapeta.jumplings.wave.TestWave;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class GameActivity extends Activity {

    // -----------------------------------------------------------------
    // Constantes

    /** ID de di�logos */
    public static final int DIALOG_PAUSE_ID = 0;
    public static final int DIALOG_GAMEOVER_ID = 1;
    public static final int DIALOG_AD_ID = 2;

    // Constantes de keys del bundle
    public static final String WAVE_BUNDLE_KEY = "waveKey";

    /** Lapso de parpadeo de la barra de vida, en ms */
    private static final int LIFEBAR_BLINKING_LAPSE = 100;



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
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.game);
        
        mPauseBtn = (ImageButton) findViewById(R.id.game_pauseBtn);
        mPauseBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseGame();
            }
        });
        mLifeCounterView = (ViewGroup) findViewById(R.id.lifes_counter_layout);
        mSpecialWeaponBar = (ProgressBar) findViewById(R.id.game_specialWeaponBar);
        mScoreTextView = (TextView) findViewById(R.id.game_scoreTextView);
        mLocalHighScoreTextView = (TextView) findViewById(R.id.game_localHightscoreTextView);
        HighScore hs = PermData.getInstance().getLocalGetHighScore();
        if (hs != null) {
            long localHighScore = hs.score;
            if (localHighScore > 0) {
                TextView highScoreTextView = mLocalHighScoreTextView;
                highScoreTextView.setText(" Highscore: " + localHighScore);
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



        mWorld = new JumplingsGameWorld(this, (GameView) findViewById(R.id.game_surface));
        mWorld.setDrawDebugInfo(JumplingsApplication.DEBUG_ENABLED);
        
        
        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
        // DEBUG

        if (JumplingsApplication.DEBUG_ENABLED) {
            testBtn = (Button) findViewById(R.id.game_testBtn);
            testBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWorld.wave.onTestButtonClicked(testBtn);
                }
            });

            // Menu de armas
            weaponsRadioGroup = (RadioGroup) findViewById(R.id.game_weaponsBtnGroup);
            weaponsRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup rg, int id) {
                    if (id == R.id.game_weaponsRadioBtnGun) {
                        mWorld.setWeapon(Gun.WEAPON_CODE_GUN);
                        // } else if (id == R.id.game_weaponsRadioBtnShotgun) {
                        // world.setWeapon(Shotgun.WEAPON_CODE_SHOTGUN);
                    } else if (id == R.id.game_weaponsRadioBtnBlade) {
                        mWorld.setWeapon(Blade.WEAPON_CODE_BLADE);
                    }

                }
            });
        }

        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
        // DEBUG

        // Preparaci�n del di�logo de anuncions
        AdDialogFactory.getInstance().init(this);


        updateLifeCounterView();
        updateScoreTextView();

        // Preparaci�n de la wave

        if (waveKey.equals(CampaignSurvivalWave.WAVE_KEY)) {
            mWorld.wave = new CampaignSurvivalWave(mWorld, null);
            // } else if (waveKey.equals(CampaignTutorialWave.WAVE_KEY)) {
            // world.wave = new CampaignTutorialWave(world, null, 1);
        } else if (waveKey.equals(TestWave.WAVE_KEY)) {
            mWorld.wave = new TestWave(mWorld, null);
            // jgWorld.wave = new CampaignSurvivalWave(jgWorld, null);
        } else {
            throw new IllegalArgumentException("Cannot create wave: " + waveKey);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(JumplingsApplication.LOG_SRC, "onStart " + this);
        // FIXME: no se realiza repintado
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(JumplingsApplication.LOG_SRC, "onStop " + this);

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
        pauseGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(JumplingsApplication.LOG_SRC, "onResume " + this);
        // Do not resume game here: user will resume by pressing button in pause
        // dialog
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(JumplingsApplication.LOG_SRC, "onDestroy " + this);
        // TODO dispose the vibratror as is done with the sound manager
        VibratorManager vm = VibratorManager.getInstance();
        vm.clearAll();
        mWorld.finish();
        // If the user presses the on / off button of the phone and the activity
        // is destroyed, we
        // want to show the menu activity when going to the task again.
        finish();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;

        switch (id) {
        case DIALOG_PAUSE_ID:

            dialog = new Dialog(GameActivity.this, R.style.CustomDialog);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_pause);

            Button resumeBtn = (Button) dialog.findViewById(R.id.pauseDialog_resumeBtn);
            resumeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    resumeGame();
                    dismissDialog(DIALOG_PAUSE_ID);
                }
            });
            Button mainMenuBtn = (Button) dialog.findViewById(R.id.pauseDialog_mainMenuBtn);
            mainMenuBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissDialog(DIALOG_PAUSE_ID);
                    gotoMenuActivity();
                }

            });
            break;

        case DIALOG_GAMEOVER_ID:

            dialog = new Dialog(GameActivity.this, R.style.CustomDialog);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_gameover);

            Button gameOverBtn = (Button) dialog.findViewById(R.id.gameoverDialog_proceedBtn);
            gameOverBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissDialog(DIALOG_GAMEOVER_ID);
                    gotoGameOverActivity();
                }

            });
            break;

        case DIALOG_AD_ID:
            dialog = AdDialogFactory.getInstance().createAdDialogView();
            break;
        }

        return dialog;
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
        Intent i = new Intent(this, GameOverActivity.class);

        HighScore highScore = new HighScore(this);
        highScore.score = mWorld.getPlayer().getScore();
        highScore.level = mWorld.wave.getLevel();

        i.putExtra(GameOverActivity.NEW_HIGHSCORE_KEY, highScore);
        i.putExtra(GameActivity.WAVE_BUNDLE_KEY, waveKey);

        startActivity(i);
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
    }

    /**
     * Pausa el juego
     */
    void pauseGame() {
        if (!mWorld.isPaused()) {
            if (!gameOver) {
                // If the game is over the game over dialog will be active
                showDialog(DIALOG_PAUSE_ID);
                mPauseBtn.setVisibility(View.GONE);
            }
            mWorld.pause();
        }
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

        mWorld.wave.pause();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDialog(DIALOG_GAMEOVER_ID);
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

    // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG

    public void updateWeaponsRadioGroup(final short weaponId) {
        if (weaponsRadioGroup.getVisibility() == View.VISIBLE) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int checked = 0;

                    switch (weaponId) {
                    case Gun.WEAPON_CODE_GUN:
                        checked = R.id.game_weaponsRadioBtnGun;
                        break;
                    // case Shotgun.WEAPON_CODE_SHOTGUN:
                    // checked = R.id.game_weaponsRadioBtnShotgun;
                    // break;
                    case Blade.WEAPON_CODE_BLADE:
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