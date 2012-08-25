package net.garrapeta.jumplings;

import net.garrapeta.gameengine.GameView;
import net.garrapeta.gameengine.sound.SoundManager;
import net.garrapeta.gameengine.vibrator.VibratorManager;
import net.garrapeta.jumplings.ui.AdDialogFactory;
import net.garrapeta.jumplings.wave.CampaignSurvivalWave;
import net.garrapeta.jumplings.wave.TestWave;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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

    public static final int SAMPLE_ENEMY_BOING = 0;
    public static final int SAMPLE_ENEMY_THROW = 1;
    public static final int SAMPLE_ENEMY_KILLED = 2;
    public static final int SAMPLE_FAIL = 3;

    public static final int SAMPLE_SLAP = 4;
    public static final int SAMPLE_BLADE_WHIP = 5;

    public static final int SAMPLE_FUSE = 6;
    public static final int SAMPLE_BOMB_BOOM = 7;
    public static final int SAMPLE_BOMB_LAUNCH = 8;

    public static final int SAMPLE_SWORD_DRAW = 9;
    public static final int SAMPLE_GUN_CLIP = 10;

    public static final int SAMPLE_LIFE_UP = 11;

    public static final int VIBRATION_ENEMY_KILLED = 0;
    public static final int VIBRATION_FAIL = 1;

    private static final long[] VIBRATION_PATTERN_ENEMY_KILLED = { 0, 90 };
    private static final long[] VIBRATION_PATTERN_FAIL = { 0, 100, 50, 400 };

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

    private ImageButton pauseBtn;

    ViewGroup lifeCounterView;
    ProgressBar specialWeaponBar;

    boolean blinkingLifeBar = false;

    TextView scoreTextView;

    TextView localHighScoreTextView;

    // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
    // DEBUG
    public Button testBtn;
    public RadioGroup weaponsRadioGroup;
    // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
    // DEBUG

    // ------------------------------------------- Variables de configuraci�n

    public short vibrateCfgLevel;
    public short flashCfgLevel;
    public short shakeCfgLevel;

    // -------------------------------------------------- M�todos de Activity

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(JumplingsApplication.LOG_SRC, "CREATE GAME");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

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

        // Preparaci�n de la UI

        setContentView(R.layout.game);

        pauseBtn = (ImageButton) findViewById(R.id.game_pauseBtn);
        pauseBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseGame();
            }
        });

        lifeCounterView = (ViewGroup) findViewById(R.id.lifes_counter_layout);

        specialWeaponBar = (ProgressBar) findViewById(R.id.game_specialWeaponBar);

        scoreTextView = (TextView) findViewById(R.id.game_scoreTextView);
        localHighScoreTextView = (TextView) findViewById(R.id.game_localHightscoreTextView);

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

        // Preparaci�n variables de configuraci�n
        PermData pd = PermData.getInstance();
        boolean soundOn = pd.getSoundConfig();

        mWorld = new JumplingsGameWorld(this, (GameView) findViewById(R.id.game_surface));
        mWorld.setDrawDebugInfo(JumplingsApplication.DEBUG_ENABLED);
        mWorld.getSoundManager().setSoundEnabled(soundOn);

        updateLifeCounterView();
        updateScoreTextView();

        vibrateCfgLevel = pd.getVibratorConfig();
        flashCfgLevel = pd.getFlashConfig();
        shakeCfgLevel = pd.getShakeConfig();

        // Preparaci�n samples sonido
        if (soundOn) {

            SoundManager sm = mWorld.getSoundManager();

            sm.add(R.raw.boing1, SAMPLE_ENEMY_BOING, this);
            sm.add(R.raw.boing2, SAMPLE_ENEMY_BOING, this);
            sm.add(R.raw.boing3, SAMPLE_ENEMY_BOING, this);

            sm.add(R.raw.boing1, SAMPLE_ENEMY_BOING, this);
            sm.add(R.raw.boing2, SAMPLE_ENEMY_BOING, this);
            sm.add(R.raw.boing3, SAMPLE_ENEMY_BOING, this);

            sm.add(R.raw.whip, SAMPLE_ENEMY_THROW, this);

            sm.add(R.raw.crush, SAMPLE_ENEMY_KILLED, this);
            sm.add(R.raw.wrong, SAMPLE_FAIL, this);

            sm.add(R.raw.slap, SAMPLE_SLAP, this);

            sm.add(R.raw.blade, SAMPLE_BLADE_WHIP, this);

            sm.add(R.raw.fuse, SAMPLE_FUSE, this);

            sm.add(R.raw.bomb_boom, SAMPLE_BOMB_BOOM, this);
            sm.add(R.raw.bomb_launch, SAMPLE_BOMB_LAUNCH, this);

            sm.add(R.raw.sword_draw, SAMPLE_SWORD_DRAW, this);
            sm.add(R.raw.clip_in, SAMPLE_GUN_CLIP, this);

            sm.add(R.raw.life_up, SAMPLE_LIFE_UP, this);

        }

        // Preparaci�n vibraciones
        if (vibrateCfgLevel > PermData.CFG_LEVEL_NONE) {
            VibratorManager vm = VibratorManager.getInstance();
            vm.init(this);

            vm.add(VIBRATION_PATTERN_ENEMY_KILLED, VIBRATION_ENEMY_KILLED);
            vm.add(VIBRATION_PATTERN_FAIL, VIBRATION_FAIL);
        }

        // Preparaci�n del di�logo de anuncions
        AdDialogFactory.getInstance().init(this);

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
        mWorld.stopRunning();
        // If the user presses the on / off button of the phone and the activity
        // is destroyed, we
        // want to show the menu activity when going to the task again.
        finish();

        // destroyGame();

        // mWorld.getSoundManager().clearAll();
        // if (vibrateCfgLevel > PermData.CFG_LEVEL_NONE) {
        // VibratorManager.getInstance().clearAll();
        // }
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            pauseGame();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Pausa el juego
     */
    void pauseGame() {
        if (!mWorld.isPaused()) {
            if (!gameOver) {
                // If the game is over the game over dialog will be active
                showDialog(DIALOG_PAUSE_ID);
                pauseBtn.setVisibility(View.GONE);
            }
            mWorld.pause();
        }
    }

    /**
     * Contin�a el juego
     */
    void resumeGame() {
        pauseBtn.setVisibility(View.VISIBLE);
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
        mWorld.scenario.onGameOver();

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
                ViewGroup lifes = (ViewGroup) lifeCounterView;
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
        specialWeaponBar.setProgress(progress);
    }

    public void activateSpecialWeaponBar(final boolean active) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (active) {
                    specialWeaponBar.setVisibility(View.VISIBLE);
                    specialWeaponBar.setMax(mWorld.mWeapon.getMaxTime());
                    updateSpecialWeaponBar();
                } else {
                    specialWeaponBar.setVisibility(View.INVISIBLE);
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
                scoreTextView.setText(String.valueOf(mWorld.getPlayer().getScore()));
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
                                int state = (lifeCounterView.getVisibility() == View.INVISIBLE) ? View.VISIBLE : View.INVISIBLE;
                                lifeCounterView.setVisibility(state);
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
                lifeCounterView.setVisibility(View.VISIBLE);
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