package com.garrapeta.jumplings;

import java.util.ArrayList;

import com.garrapeta.gameengine.GameView;
import com.garrapeta.gameengine.GameWorld;
import com.garrapeta.gameengine.SyncGameMessage;
import com.garrapeta.gameengine.module.BitmapManager;
import com.garrapeta.gameengine.module.SoundManager;
import com.garrapeta.gameengine.module.VibratorManager;
import com.garrapeta.jumplings.actor.BladePowerUpActor;
import com.garrapeta.jumplings.actor.BombActor;
import com.garrapeta.jumplings.actor.ComboTextActor;
import com.garrapeta.jumplings.actor.EnemyActor;
import com.garrapeta.jumplings.actor.FlashActor;
import com.garrapeta.jumplings.actor.JumplingActor;
import com.garrapeta.jumplings.actor.LifePowerUpActor;
import com.garrapeta.jumplings.actor.MainActor;
import com.garrapeta.jumplings.actor.ScoreTextActor;
import com.garrapeta.jumplings.scenario.IScenario;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Mundo del juego
 * 
 * @author GaRRaPeTa
 */
public class JumplingsGameWorld extends JumplingsWorld implements OnTouchListener, GameEventsListener {

    // -------------------------------------------------------- Constantes

    public static final float LIFE_LOSS_FACTOR = 5;

    public static final int WEAPON_GUN = 0;
    public static final int WEAPON_SHOTGUN = 1;
    public static final int WEAPON_BLADE = 2;

    // ------------------------------------ Consantes de sonidos y vibraciones

    public static final int SAMPLE_ENEMY_BOING = 1;
    public static final int SAMPLE_ENEMY_THROW = 2;
    public static final int SAMPLE_ENEMY_KILLED = 3;
    public static final int SAMPLE_FAIL = 4;

    public static final int SAMPLE_SLAP = 5;
    public static final int SAMPLE_SWORD_SWING = 6;

    public static final int SAMPLE_FUSE = 7;
    public static final int SAMPLE_BOMB_BOOM = 8;
    public static final int SAMPLE_BOMB_LAUNCH = 9;

    public static final int SAMPLE_SWORD_SHEATH = 10;
    public static final int SAMPLE_SWORD_UNSHEATH = 11;

    public static final int SAMPLE_LIFE_UP = 12;

    public static final int VIBRATION_ENEMY_KILLED = 0;
    public static final int VIBRATION_FAIL = 1;

    private static final long[] VIBRATION_PATTERN_ENEMY_KILLED = { 0, 90 };
    private static final long[] VIBRATION_PATTERN_FAIL = { 0, 100, 50, 400 };

    /** Flash actor used in flash effects */
    public FlashActor mFlashActor;

    // ------------------------------------------------------------ Variables

    public GameActivity mGameActivity;

    public ArrayList<MainActor> mMainActors = new ArrayList<MainActor>();

    public ArrayList<EnemyActor> mEnemies = new ArrayList<EnemyActor>();

    public ArrayList<BombActor> mBombActors = new ArrayList<BombActor>();

    public ArrayList<ComboTextActor> mComboTextActors = new ArrayList<ComboTextActor>();
    
    public ArrayList<ScoreTextActor> mScoreTextActors = new ArrayList<ScoreTextActor>();
    
    /** Duranci�n en ms del shake actual */
    private float shakeDuration = 0;
    /** Tiempo que le queda al shake actual */
    private float shakeRemaining = 0;
    /** Intensidad, en unidades del mundo, del shake actual */
    private float shakeIntensity = 0;

    /** Jugador */
    Player mPlayer;
    
    private final Tutorial mTutorial;

    // TODO: use this paint for painting the Jumplings
    protected Paint mPaint = new Paint();

    /** Arma actual */
    public Weapon mWeapon;

    /** Escenario actual */
    IScenario mScenario = null;


    // ------------------------------------------- Variables de configuraci�n

    public short mVibrateCfgLevel;
    public short mFlashCfgLevel;
    public short mShakeCfgLevel;

    // ----------------------------------------------------------- Constructor

    public JumplingsGameWorld(GameActivity gameActivity, GameView gameView, Context context) {
        super(gameActivity, gameView, context);
        mGameActivity = gameActivity;
        mPlayer = new Player(this);
        mTutorial = new Tutorial(gameActivity, GameActivity.DIALOG_FRAGMENT_TAG);
        mGameView.setOnTouchListener(this);
     }

    // ----------------------------------------------------- M�todos de World

    public Player getPlayer() {
        return mPlayer;
    }

    @Override
    public void onBeforeRunning() {
        super.onBeforeRunning();

        // Preparaci�n variables de configuraci�n
        PermData pd = PermData.getInstance();
        mVibrateCfgLevel = pd.getVibratorConfig();
        mFlashCfgLevel = pd.getFlashConfig();
        mShakeCfgLevel = pd.getShakeConfig();

        // TODO: do this as with the sound manager
        // Preparaci�n vibraciones
        if (mVibrateCfgLevel > PermData.CFG_LEVEL_NONE) {
            VibratorManager vm = VibratorManager.getInstance();
            vm.init((Vibrator)mActivity.getSystemService(Context.VIBRATOR_SERVICE));

            vm.add(VIBRATION_PATTERN_ENEMY_KILLED, VIBRATION_ENEMY_KILLED);
            vm.add(VIBRATION_PATTERN_FAIL, VIBRATION_FAIL);
        }
        // Inicializaci�n del arma
        setWeapon(WeaponSlap.WEAPON_CODE_GUN);

        mFlashActor = new FlashActor(this);
        mFlashActor.setInitted();
        addActor(mFlashActor);

        // inicialización del tutorial
        mTutorial.init();
        
        mGameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGameActivity.findViewById(R.id.loading).setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void loadResources() {
        loadCommonResources();

        // Preparaci�n samples bitmaps
        BitmapManager bm = getBitmapManager();
        Resources resources = mGameActivity.getResources();
        bm.loadBitmap(resources, R.drawable.eye_0_right_opened);
        bm.loadBitmap(resources, R.drawable.eye_0_left_opened);
        bm.loadBitmap(resources, R.drawable.eye_2_right_opened);
        bm.loadBitmap(resources, R.drawable.eye_2_left_opened);

        bm.loadBitmap(resources, R.drawable.eye_0_right_closed);
        bm.loadBitmap(resources, R.drawable.eye_0_left_closed);
        bm.loadBitmap(resources, R.drawable.eye_2_right_closed);
        bm.loadBitmap(resources, R.drawable.eye_2_left_closed);

        bm.loadBitmap(resources, R.drawable.red_body);
        bm.loadBitmap(resources, R.drawable.red_foot_right);
        bm.loadBitmap(resources, R.drawable.red_foot_left);
        bm.loadBitmap(resources, R.drawable.red_hand_right);
        bm.loadBitmap(resources, R.drawable.red_hand_left);
        bm.loadBitmap(resources, R.drawable.red_debris_body);
        bm.loadBitmap(resources, R.drawable.red_debris_foot_right);
        bm.loadBitmap(resources, R.drawable.red_debris_foot_left);
        bm.loadBitmap(resources, R.drawable.red_debris_hand_right);
        bm.loadBitmap(resources, R.drawable.red_debris_hand_left);

        bm.loadBitmap(resources, R.drawable.orange_double_body);
        bm.loadBitmap(resources, R.drawable.orange_foot_right);
        bm.loadBitmap(resources, R.drawable.orange_foot_left);
        bm.loadBitmap(resources, R.drawable.orange_hand_right);
        bm.loadBitmap(resources, R.drawable.orange_hand_left);
        bm.loadBitmap(resources, R.drawable.orange_debris_double_body);
        bm.loadBitmap(resources, R.drawable.orange_debris_foot_right);
        bm.loadBitmap(resources, R.drawable.orange_debris_foot_left);
        bm.loadBitmap(resources, R.drawable.orange_debris_hand_right);
        bm.loadBitmap(resources, R.drawable.orange_debris_hand_left);
        bm.loadBitmap(resources, R.drawable.orange_simple_body);
        bm.loadBitmap(resources, R.drawable.orange_debris_simple_body);

        bm.loadBitmap(resources, R.drawable.yellow_2_body);
        bm.loadBitmap(resources, R.drawable.yellow_1_body);
        bm.loadBitmap(resources, R.drawable.yellow_0_body);
        bm.loadBitmap(resources, R.drawable.yellow_2_foot_right);
        bm.loadBitmap(resources, R.drawable.yellow_2_foot_left);
        bm.loadBitmap(resources, R.drawable.yellow_0_foot_right);
        bm.loadBitmap(resources, R.drawable.yellow_0_foot_left);
        bm.loadBitmap(resources, R.drawable.yellow_2_hand_right);
        bm.loadBitmap(resources, R.drawable.yellow_2_hand_left);
        bm.loadBitmap(resources, R.drawable.yellow_0_hand_right);
        bm.loadBitmap(resources, R.drawable.yellow_0_hand_left);
        bm.loadBitmap(resources, R.drawable.yellow_debris_2_body);
        bm.loadBitmap(resources, R.drawable.yellow_debris_1_body);
        bm.loadBitmap(resources, R.drawable.yellow_debris_0_body);
        bm.loadBitmap(resources, R.drawable.yellow_debris_2_foot_right);
        bm.loadBitmap(resources, R.drawable.yellow_debris_2_foot_left);
        bm.loadBitmap(resources, R.drawable.yellow_debris_0_foot_right);
        bm.loadBitmap(resources, R.drawable.yellow_debris_0_foot_left);
        bm.loadBitmap(resources, R.drawable.yellow_debris_2_hand_right);
        bm.loadBitmap(resources, R.drawable.yellow_debris_2_hand_left);
        bm.loadBitmap(resources, R.drawable.yellow_debris_0_hand_right);
        bm.loadBitmap(resources, R.drawable.yellow_debris_0_hand_left);

        bm.loadBitmap(resources, R.drawable.bomb_body);
        bm.loadBitmap(resources, R.drawable.bomb_fuse);
        bm.loadBitmap(resources, R.drawable.bomb_debris_body);
        bm.loadBitmap(resources, R.drawable.bomb_debris_fuse);

        bm.loadBitmap(resources, R.drawable.sparks_big_0);
        bm.loadBitmap(resources, R.drawable.sparks_big_1);
        bm.loadBitmap(resources, R.drawable.sparks_big_2);
        bm.loadBitmap(resources, R.drawable.sparks_big_3);

        bm.loadBitmap(resources, R.drawable.powerup_bg);
        bm.loadBitmap(resources, R.drawable.powerup_debris_bg);
        bm.loadBitmap(resources, R.drawable.powerup_sword);
        bm.loadBitmap(resources, R.drawable.powerup_debris_sword);
        bm.loadBitmap(resources, R.drawable.powerup_heart);
        bm.loadBitmap(resources, R.drawable.powerup_debris_heart);

        // Preparación samples sonido
        SoundManager sm = getSoundManager();
        if (sm.isSoundEnabled()) {
            sm.add(R.raw.boing1, SAMPLE_ENEMY_BOING, mActivity);
            sm.add(R.raw.boing2, SAMPLE_ENEMY_BOING, mActivity);
            sm.add(R.raw.boing3, SAMPLE_ENEMY_BOING, mActivity);

            sm.add(R.raw.boing1, SAMPLE_ENEMY_BOING, mActivity);
            sm.add(R.raw.boing2, SAMPLE_ENEMY_BOING, mActivity);
            sm.add(R.raw.boing3, SAMPLE_ENEMY_BOING, mActivity);

            sm.add(R.raw.whip, SAMPLE_ENEMY_THROW, mActivity);

            sm.add(R.raw.crush, SAMPLE_ENEMY_KILLED, mActivity);
            sm.add(R.raw.wrong, SAMPLE_FAIL, mActivity);

            sm.add(R.raw.whip, SAMPLE_SLAP, mActivity);

            sm.add(R.raw.sword_swing, SAMPLE_SWORD_SWING, mActivity);

            sm.add(R.raw.fuse, SAMPLE_FUSE, mActivity);

            sm.add(R.raw.bomb_boom, SAMPLE_BOMB_BOOM, mActivity);
            sm.add(R.raw.bomb_launch, SAMPLE_BOMB_LAUNCH, mActivity);

            sm.add(R.raw.sword_sheath, SAMPLE_SWORD_SHEATH, mActivity);
            sm.add(R.raw.sword_unsheath, SAMPLE_SWORD_UNSHEATH, mActivity);

            sm.add(R.raw.life_up, SAMPLE_LIFE_UP, mActivity);

        }
    }

    @Override
    public boolean processFrame(float gameTimeStep) {
        if (mWeapon.getWeaponCode() != WeaponSlap.WEAPON_CODE_GUN) {
            if (mWeapon.getRemainingTime() <= 0) {
                setWeapon(WeaponSlap.WEAPON_CODE_GUN);
            } else {
                mGameActivity.updateSpecialWeaponBar();
            }
        }

        super.processFrame(gameTimeStep);

        if (shakeRemaining > 0) {
            shakeRemaining -= gameTimeStep;
        }

        // scenario
        if (mScenario != null) {
            mScenario.processFrame(gameTimeStep);
        }

        if (JumplingsApplication.DEBUG_AUTOPLAY) {
            autoPlay();
        }

        return false;
    }

    @Override
    protected void drawActors(Canvas canvas) {
        if (this.shakeRemaining <= 0) {
            super.drawActors(canvas);
        } else {
            float intensity = (shakeRemaining / shakeDuration) * shakeIntensity;

            float pixels = (int) mViewport.worldUnitsToPixels(intensity);

            float pixelsX = pixels;
            if (Math.random() > 0.5) {
                pixelsX *= -1;
            }

            float pixelsY = pixels;
            if (Math.random() > 0.5) {
                pixelsY *= -1;
            }

            canvas.save();
            canvas.translate(pixelsX, pixelsY);
            super.drawActors(canvas);

            canvas.restore();
        }
    }

    @Override
    protected void drawBackground(Canvas canvas) {
        super.drawBackground(canvas);

        // TODO: evitar esta comporbaci�n de nulidad
        // TODO: pasar las medidas de la pantalla al escenario en reset()
        if (mScenario != null && JumplingsApplication.DRAW_SCENARIO) {
            mScenario.draw(canvas, mPaint);
        }
    }

    // -------------------------------------------------------- M�todos propios

    public void setScenario(IScenario scenario) {
        mScenario = scenario;
    }

    // Métodos de gestión de actores

    public void onScoreTextActorAdded(ScoreTextActor actor) {
        for (ScoreTextActor other : mScoreTextActors) {
            other.forceDisappear();
        }
        mScoreTextActors.add(actor);
    }

    public void onScoreTextActorRemoved(ScoreTextActor actor) {
        mScoreTextActors.remove(actor);
    }
    
    public void onComboTextActorAdded(ComboTextActor actor) {
        for (ComboTextActor other : mComboTextActors) {
            other.forceDisappear();
        }
        mComboTextActors.add(actor);
    }

    public void onComboTextActorRemoved(ComboTextActor actor) {
        mComboTextActors.remove(actor);
    }

    public void onMainActorAdded(MainActor actor) {
        mMainActors.add(actor);
    }

    public void onMainActorRemoved(MainActor actor) {
        mMainActors.remove(actor);
    }

    public void onEnemyActorAdded(EnemyActor actor) {
        mEnemies.add(actor);
    }

    public void onEnemyActorRemoved(EnemyActor actor) {
        mEnemies.remove(actor);
    }

    public void onBombActorAdded(BombActor actor) {
        mBombActors.add(actor);
    }

    public void onBombActorRemoved(BombActor actor) {
        mBombActors.remove(actor);
    }

    public int getThread() {
        int hits = 0;
        int s = mMainActors.size();
        for (int i = 0; i < s; i++) {
            hits += MainActor.getBaseThread(mMainActors.get(i).getCode());
        }
        return hits;
    }

    // Methods from GameEventListener

    @Override
    public boolean onEnemyScaped(EnemyActor enemy) {
        if (!mGameActivity.isGameOver() && mPlayer.isVulnerable()) {            
            if (mWave.onEnemyScaped(enemy)) {
                return true;
            }
            if (mTutorial.onEnemyScaped(enemy)) {
                mPlayer.makeInvulnerable();
                return true;
            }
            onPostEnemyScaped(enemy);
        }
        return true;
    }

    @Override
    public boolean onGameOver() {
        if (mWave.onGameOver()) {
            return true;
        }
        mTutorial.onGameOver();
        mGameActivity.onGameOver();
        return true;
    }
    
    @Override
    public boolean onCombo() {
        if (mWave.onCombo()) {
            return true;
        }
        mTutorial.onCombo();
        return true;
    }
    
    @Override
    public boolean onEnemyKilled(EnemyActor enemy) {
        getSoundManager().play(SAMPLE_ENEMY_KILLED);
        getSoundManager().play(SAMPLE_ENEMY_PAIN);

        if (mVibrateCfgLevel == PermData.CFG_LEVEL_ALL) {
            VibratorManager.getInstance().play(VIBRATION_ENEMY_KILLED);
        }

        if (mWave.onEnemyKilled(enemy)) {
            return true;
        }

        mTutorial.onEnemyKilled(enemy);
        
        mPlayer.onEnemyKilled(enemy);
        if (mShakeCfgLevel == PermData.CFG_LEVEL_ALL) {
            createShake(100f, 0.20f);
        }

        return true;
    }

    @Override
    public boolean onBombExploded(BombActor bomb) {
        if (!mGameActivity.isGameOver() && mPlayer.isVulnerable() ) {
            if (mWave.onBombExploded(bomb)) {
                return true;
            }
            if (mTutorial.onBombExploded(bomb)) {
                return true;
            }
            onPostBombExploded(bomb);
        }
        return true;
    }

    @Override
    public boolean onLifePowerUp(LifePowerUpActor lifePowerUpActor) {
        if (!mGameActivity.isGameOver()) {
            if (mWave.onLifePowerUp(lifePowerUpActor)) {
                return true;
            }
            
            mTutorial.onLifePowerUp(lifePowerUpActor);
            
            onPostLifePowerUp(lifePowerUpActor);
        }
        return true;
    }
    
    @Override
    public boolean onBladePowerUpStart(BladePowerUpActor bladePowerUpActor) {
       if (!mGameActivity.isGameOver()) {

           if (mWave.onBladePowerUpStart(bladePowerUpActor)) {
               return true;
           }
           
           mTutorial.onBladePowerUpStart(bladePowerUpActor);

           onPostBladePowerUp(bladePowerUpActor);
       }
       return true;
   }
    
    @Override
    public boolean onBladePowerUpEnd() {
       if (!mGameActivity.isGameOver()) {

           if (mWave.onBladePowerUpEnd()) {
               return true;
           }
           
           mTutorial.onBladePowerUpEnd();

       }
       return true;
   }
    public void onPostEnemyScaped(EnemyActor e) {
        if (mFlashCfgLevel >= PermData.CFG_LEVEL_SOME) {
            mFlashActor
                    .init(FlashActor.FLASH_FAIL_COLOR, FlashActor.FLASH_FAIL_ALPHA, FlashActor.FLASH_FAIL_DURATION, FlashActor.FLASH_FAIL_PRIORITY);
        }

        onFail();
    }

    private void onPostBombExploded(BombActor bomb) {
        if (mFlashCfgLevel >= PermData.CFG_LEVEL_SOME) {
            // mFlashActor.init(FlashActor.FLASH_BOMB_COLOR,
            // FlashActor.FLASH_BOMB_ALPHA, FlashActor.FLASH_BOMB_DURATION);

            mFlashActor
                    .init(FlashActor.FLASH_FAIL_COLOR, FlashActor.FLASH_FAIL_ALPHA, FlashActor.FLASH_FAIL_DURATION, FlashActor.FLASH_FAIL_PRIORITY);

        }
        onFail();
    }

    private void onPostLifePowerUp(LifePowerUpActor lifePowerUpActor) {
        getSoundManager().play(SAMPLE_LIFE_UP);

        if (mFlashCfgLevel >= PermData.CFG_LEVEL_SOME) {
            mFlashActor.init(FlashActor.FLASH_LIFEUP_COLOR, FlashActor.FLASH_LIFEUP_ALPHA, FlashActor.FLASH_LIFEUP_DURATION,
                    FlashActor.FLASH_LIFEUP_PRIORITY);
        }

        mPlayer.addLifes(1);
    }
    
    private void onPostBladePowerUp(BladePowerUpActor bladePowerUpActor) {
        setWeapon(WeaponSword.WEAPON_CODE_BLADE);

        if (mFlashCfgLevel >= PermData.CFG_LEVEL_SOME) {
            mFlashActor.init(FlashActor.FLASH_BLADE_DRAWN_COLOR, FlashActor.FLASH_BLADE_DRAWN_ALPHA, FlashActor.FLASH_BLADE_DRAWN_DURATION,
                    FlashActor.FLASH_BLADE_DRAWN_PRIORITY);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!mGameActivity.isGameOver() && !isPaused()) {
            final double[] info = new double[] { event.getAction(), event.getX(), event.getY(), System.currentTimeMillis() };
            post(new SyncGameMessage() {
                @Override
                public void doInGameLoop(GameWorld world) {
                    mWeapon.onTouchEvent(info);
                }
            });
        }
        return true;
    }

    public void setWeapon(short weaponId) {
        if (mWeapon != null) {
            mWeapon.onEnd();
        }
        boolean active = false;
        switch (weaponId) {
        case WeaponSlap.WEAPON_CODE_GUN:
            mWeapon = new WeaponSlap(this);
            active = false;
            break;
        // case Shotgun.WEAPON_CODE_SHOTGUN:
        // weapon = new Shotgun(this);
        // active = true;
        // break;
        case WeaponSword.WEAPON_CODE_BLADE:
            mWeapon = new WeaponSword(this);
            active = true;
            break;
        }

        mWeapon.onStart();
        mGameActivity.activateSpecialWeaponBar(active);

        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
        if (JumplingsApplication.DEBUG_FUNCTIONS_ENABLED) {
            mGameActivity.updateWeaponsRadioGroup(weaponId);
        }
        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
    }

    /**
     * Método ejecutado cuando el jugador falla
     */
    private void onFail() {
        if (!mWave.onFail()) {
            getSoundManager().play(SAMPLE_FAIL);
            if (mVibrateCfgLevel >= PermData.CFG_LEVEL_SOME) {
                VibratorManager.getInstance().play(VIBRATION_FAIL);
            }

            mPlayer.subLifes(1);
            mPlayer.makeInvulnerable();

            if (mShakeCfgLevel >= PermData.CFG_LEVEL_SOME) {
                createShake(425f, 0.75f);
            }

            if (mPlayer.getLifes() <= 0) {
                onGameOver();
            }
        }
    }

    /**
     * Programa un temblor de pantalla
     * 
     * @param time
     * @param intensity
     */
    private void createShake(float time, float intensity) {
        this.shakeDuration = time;
        this.shakeRemaining = time;
        this.shakeIntensity = intensity;
    }

    /**
     * Plays automatically, for testing purposes
     */
    private void autoPlay() {
        for (int i = mEnemies.size() - 1; i >= 0; i--) {
            EnemyActor enemy = mEnemies.get(i);
            PointF pos = enemy.getWorldPos();
            if (pos.y < JumplingActor.BASE_RADIUS * 10) {
                enemy.onHitted();
            }
        }
    }

    @Override
    protected void dispose() {
        super.dispose();
        mGameActivity = null;
        mMainActors.clear();
        mMainActors = null;
        mEnemies.clear();
        mEnemies = null;
        mBombActors.clear();
        mBombActors = null;
        mComboTextActors.clear();
        mComboTextActors = null;
        mScoreTextActors.clear();
        mScoreTextActors = null;
    }

}