package net.garrapeta.jumplings;

import java.util.ArrayList;

import net.garrapeta.gameengine.Actor;
import net.garrapeta.gameengine.GameView;
import net.garrapeta.gameengine.GameWorld;
import net.garrapeta.gameengine.SyncGameMessage;
import net.garrapeta.gameengine.module.BitmapManager;
import net.garrapeta.gameengine.module.SoundManager;
import net.garrapeta.gameengine.module.VibratorManager;
import net.garrapeta.jumplings.actor.BladePowerUpActor;
import net.garrapeta.jumplings.actor.BombActor;
import net.garrapeta.jumplings.actor.EnemyActor;
import net.garrapeta.jumplings.actor.FlashActor;
import net.garrapeta.jumplings.actor.JumplingActor;
import net.garrapeta.jumplings.actor.LifePowerUpActor;
import net.garrapeta.jumplings.actor.MainActor;
import net.garrapeta.jumplings.scenario.IScenario;
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
public class JumplingsGameWorld extends JumplingsWorld implements OnTouchListener {

    // -------------------------------------------------------- Constantes

    public static final float LIFE_LOSS_FACTOR = 5;

    public static final float INVULNERABLE_TIME = 1500;

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

    public ArrayList<JumplingActor> jumplingActors = new ArrayList<JumplingActor>();

    public ArrayList<MainActor> mainActors = new ArrayList<MainActor>();

    public ArrayList<EnemyActor> enemies = new ArrayList<EnemyActor>();

    /** Duranci�n en ms del shake actual */
    private float shakeDuration = 0;
    /** Tiempo que le queda al shake actual */
    private float shakeRemaining = 0;
    /** Intensidad, en unidades del mundo, del shake actual */
    private float shakeIntensity = 0;

    /** Jugador */
    Player mPlayer;

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
        this.mGameActivity = gameActivity;
        mPlayer = new Player(this);
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
        addActor(mFlashActor);

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

    // M�todos de gesti�n de actores

    @Override
    public void onActorAdded(Actor a) {
        super.onActorAdded(a);
        if (a instanceof JumplingActor) {
            addJumplingActor((JumplingActor) a);
        }
    }

    @Override
    public void onActorRemoved(Actor a) {
        super.onActorRemoved(a);
        if (a instanceof JumplingActor) {
            removeJumplingActor((JumplingActor) a);
        }
    }

    private void addJumplingActor(JumplingActor pa) {
        jumplingActors.add(pa);
        if (pa instanceof MainActor) {
            addMainActor((MainActor) pa);
        }
    }

    private void removeJumplingActor(JumplingActor pa) {
        jumplingActors.remove(pa);
        if (pa instanceof MainActor) {
            removeMainActor((MainActor) pa);
        }
    }

    private void addMainActor(MainActor mainActor) {
        mainActors.add(mainActor);
        if (mainActor instanceof EnemyActor) {
            addEnemy((EnemyActor) mainActor);
        }
    }

    private void removeMainActor(MainActor mainActor) {
        mainActors.remove(mainActor);
        if (mainActor instanceof EnemyActor) {
            removeEnemy((EnemyActor) mainActor);
        }
    }

    private void addEnemy(EnemyActor enemy) {
        enemies.add(enemy);
    }

    private void removeEnemy(EnemyActor enemy) {
        enemies.remove(enemy);
    }

    public int getThread() {
        int hits = 0;
        int s = mainActors.size();
        for (int i = 0; i < s; i++) {
            hits += MainActor.getBaseThread(mainActors.get(i).getCode());
        }
        return hits;
    }

    public void onEnemyScaped(EnemyActor e) {
        if (!mGameActivity.isGameOver()) {

            if (mPlayer.isVulnerable() && !mWave.onEnemyScaped(e)) {
                onPostEnemyScaped(e);
            }
        }
    }

    public void onPostEnemyScaped(EnemyActor e) {
        if (mFlashCfgLevel >= PermData.CFG_LEVEL_SOME) {
            mFlashActor
                    .init(FlashActor.FLASH_FAIL_COLOR, FlashActor.FLASH_FAIL_ALPHA, FlashActor.FLASH_FAIL_DURATION, FlashActor.FLASH_FAIL_PRIORITY);
        }

        onFail();
    }

    public void onEnemyKilled(EnemyActor enemy) {
        if (!mWave.onEnemyKilled(enemy)) {
            getSoundManager().play(SAMPLE_ENEMY_KILLED);
            getSoundManager().play(SAMPLE_ENEMY_PAIN);
            if (mVibrateCfgLevel == PermData.CFG_LEVEL_ALL) {
                VibratorManager.getInstance().play(VIBRATION_ENEMY_KILLED);
            }

            mPlayer.onEnemyKilled(enemy);
            if (mShakeCfgLevel == PermData.CFG_LEVEL_ALL) {
                createShake(100f, 0.20f);
            }
        }
    }

    public void onBombExploded(BombActor bomb) {
        if (!mGameActivity.isGameOver()) {

            if (mPlayer.isVulnerable() && !mWave.onBombExploded(bomb)) {
                onPostBombExploded(bomb);
            }
        }
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

    public void onLifePowerUp(LifePowerUpActor lifePowerUpActor) {
        if (!mGameActivity.isGameOver()) {

            if (mPlayer.isVulnerable() && !mWave.onLifePowerUp(lifePowerUpActor)) {
                onPostLifePowerUp(lifePowerUpActor);
            }
        }
    }

    private void onPostLifePowerUp(LifePowerUpActor lifePowerUpActor) {
        getSoundManager().play(SAMPLE_LIFE_UP);

        if (mFlashCfgLevel >= PermData.CFG_LEVEL_SOME) {
            mFlashActor.init(FlashActor.FLASH_LIFEUP_COLOR, FlashActor.FLASH_LIFEUP_ALPHA, FlashActor.FLASH_LIFEUP_DURATION,
                    FlashActor.FLASH_LIFEUP_PRIORITY);
        }

        mPlayer.addLifes(1);
    }

    public void onBladePowerUp(BladePowerUpActor bladePowerUpActor) {
        if (!mGameActivity.isGameOver()) {

            if (mPlayer.isVulnerable() && !mWave.onBladePowerUp(bladePowerUpActor)) {
                onPostBladePowerUp(bladePowerUpActor);
            }
        }
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
        if (JumplingsApplication.DEBUG_ENABLED) {
            mGameActivity.updateWeaponsRadioGroup(weaponId);
        }
        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
    }

    
    public int getBombCount() {
        int count = 0;
        for (Actor actor : mActors) {
            if (actor instanceof BombActor) {
                count ++;
            }
        }
        return count;
    }
 
    /**
     * M�todo ejecutado cuando el jugador falla
     */
    private void onFail() {
        if (!mWave.onFail()) {
            getSoundManager().play(SAMPLE_FAIL);
            if (mVibrateCfgLevel >= PermData.CFG_LEVEL_SOME) {
                VibratorManager.getInstance().play(VIBRATION_FAIL);
            }

            Player player = getPlayer();
            player.subLifes(1);
            player.makeInvulnerable(INVULNERABLE_TIME);

            if (mShakeCfgLevel >= PermData.CFG_LEVEL_SOME) {
                createShake(425f, 0.75f);
            }

            if (player.getLifes() <= 0) {
                if (!mWave.onGameOver()) {
                    mGameActivity.onGameOver();
                }
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
        for (int i = enemies.size() - 1; i >= 0; i--) {
            EnemyActor enemy = enemies.get(i);
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
        jumplingActors.clear();
        jumplingActors = null;
        mainActors.clear();
        mainActors = null;
        enemies.clear();
        enemies = null;
    }

}
