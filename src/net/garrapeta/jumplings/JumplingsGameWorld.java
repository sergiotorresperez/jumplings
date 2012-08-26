package net.garrapeta.jumplings;

import java.util.ArrayList;

import net.garrapeta.gameengine.Actor;
import net.garrapeta.gameengine.GameMessage;
import net.garrapeta.gameengine.GameView;
import net.garrapeta.gameengine.GameWorld;
import net.garrapeta.gameengine.module.BitmapManager;
import net.garrapeta.gameengine.module.VibratorManager;
import net.garrapeta.gameengine.module.SoundManager;
import net.garrapeta.jumplings.actor.BladePowerUpActor;
import net.garrapeta.jumplings.actor.BombActor;
import net.garrapeta.jumplings.actor.EnemyActor;
import net.garrapeta.jumplings.actor.FlashActor;
import net.garrapeta.jumplings.actor.JumplingActor;
import net.garrapeta.jumplings.actor.LifePowerUpActor;
import net.garrapeta.jumplings.actor.MainActor;
import net.garrapeta.jumplings.scenario.Scenario;
import android.graphics.Canvas;
import android.util.Log;
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

    // ------------------------------------------------------------ Variables

    public GameActivity mGameActivity;

    /** Si el mundo ha sido creado */
    boolean isCreated;

    public ArrayList<JumplingActor> jumplingActors = new ArrayList<JumplingActor>();

    public ArrayList<MainActor> mainActors = new ArrayList<MainActor>();

    public ArrayList<EnemyActor> enemies = new ArrayList<EnemyActor>();

    // N�mero de bombar actuales
    public int bombCount = 0;

    /** Duranci�n en ms del shake actual */
    private float shakeDuration = 0;
    /** Tiempo que le queda al shake actual */
    private float shakeRemaining = 0;
    /** Intensidad, en unidades del mundo, del shake actual */
    private float shakeIntensity = 0;

    /** Jugador */
    Player mPlayer;

    /** Arma actual */
    public Weapon mWeapon;

    /** Escenario actual */
    Scenario scenario;

    /** Escenario que est� desapareciendo */
    Scenario fadingScenario;

    // ------------------------------------------- Variables de configuraci�n

    public short mVibrateCfgLevel;
    public short mFlashCfgLevel;
    public short mShakeCfgLevel;

    // ----------------------------------------------------------- Constructor

    public JumplingsGameWorld(GameActivity gameActivity, GameView gameView) {
        super(gameActivity, gameView);
        this.mGameActivity = gameActivity;
        mPlayer = new Player(this);
        mView.setOnTouchListener(this);
    }

    // ----------------------------------------------------- M�todos de World

    public Player getPlayer() {
        return mPlayer;
    }

    @Override
    public void onCreated() {
        super.onCreated();

        // Preparaci�n variables de configuraci�n
        PermData pd = PermData.getInstance();
        mVibrateCfgLevel = pd.getVibratorConfig();
        mFlashCfgLevel = pd.getFlashConfig();
        mShakeCfgLevel = pd.getShakeConfig();
        getSoundManager().setSoundEnabled(pd.getSoundConfig());

        // TODO: do this as with the sound manager
        // Preparaci�n vibraciones
        if (mVibrateCfgLevel > PermData.CFG_LEVEL_NONE) {
            VibratorManager vm = VibratorManager.getInstance();
            vm.init(mActivity);

            vm.add(VIBRATION_PATTERN_ENEMY_KILLED, VIBRATION_ENEMY_KILLED);
            vm.add(VIBRATION_PATTERN_FAIL, VIBRATION_FAIL);
        }
        // Inicializaci�n del arma
        setWeapon(Gun.WEAPON_CODE_GUN);

        nextScenario();

    }

    @Override
    protected void loadResources() {
        // Preparaci�n samples bitmaps
        BitmapManager bm = getBitmapManager();
        bm.loadBitmap(R.drawable.eye_0_right);
        bm.loadBitmap(R.drawable.eye_0_left);
        bm.loadBitmap(R.drawable.eye_2_right);
        bm.loadBitmap(R.drawable.eye_2_left);

        bm.loadBitmap(R.drawable.red_body);
        bm.loadBitmap(R.drawable.red_foot_right);
        bm.loadBitmap(R.drawable.red_foot_left);
        bm.loadBitmap(R.drawable.red_hand_right);
        bm.loadBitmap(R.drawable.red_hand_left);
        bm.loadBitmap(R.drawable.red_debris_body);
        bm.loadBitmap(R.drawable.red_debris_foot_right);
        bm.loadBitmap(R.drawable.red_debris_foot_left);
        bm.loadBitmap(R.drawable.red_debris_hand_right);
        bm.loadBitmap(R.drawable.red_debris_hand_left);

        bm.loadBitmap(R.drawable.orange_double_body);
        bm.loadBitmap(R.drawable.orange_foot_right);
        bm.loadBitmap(R.drawable.orange_foot_left);
        bm.loadBitmap(R.drawable.orange_hand_right);
        bm.loadBitmap(R.drawable.orange_hand_left);
        bm.loadBitmap(R.drawable.orange_debris_double_body);
        bm.loadBitmap(R.drawable.orange_debris_foot_right);
        bm.loadBitmap(R.drawable.orange_debris_foot_left);
        bm.loadBitmap(R.drawable.orange_debris_hand_right);
        bm.loadBitmap(R.drawable.orange_debris_hand_left);
        bm.loadBitmap(R.drawable.orange_simple_body);
        bm.loadBitmap(R.drawable.orange_debris_simple_body);

        bm.loadBitmap(R.drawable.yellow_2_body);
        bm.loadBitmap(R.drawable.yellow_1_body);
        bm.loadBitmap(R.drawable.yellow_0_body);
        bm.loadBitmap(R.drawable.yellow_2_foot_right);
        bm.loadBitmap(R.drawable.yellow_2_foot_left);
        bm.loadBitmap(R.drawable.yellow_0_foot_right);
        bm.loadBitmap(R.drawable.yellow_0_foot_left);
        bm.loadBitmap(R.drawable.yellow_2_hand_right);
        bm.loadBitmap(R.drawable.yellow_2_hand_left);
        bm.loadBitmap(R.drawable.yellow_0_hand_right);
        bm.loadBitmap(R.drawable.yellow_0_hand_left);
        bm.loadBitmap(R.drawable.yellow_debris_2_body);
        bm.loadBitmap(R.drawable.yellow_debris_1_body);
        bm.loadBitmap(R.drawable.yellow_debris_0_body);
        bm.loadBitmap(R.drawable.yellow_debris_2_foot_right);
        bm.loadBitmap(R.drawable.yellow_debris_2_foot_left);
        bm.loadBitmap(R.drawable.yellow_debris_0_foot_right);
        bm.loadBitmap(R.drawable.yellow_debris_0_foot_left);
        bm.loadBitmap(R.drawable.yellow_debris_2_hand_right);
        bm.loadBitmap(R.drawable.yellow_debris_2_hand_left);
        bm.loadBitmap(R.drawable.yellow_debris_0_hand_right);
        bm.loadBitmap(R.drawable.yellow_debris_0_hand_left);

        bm.loadBitmap(R.drawable.bomb_body);
        bm.loadBitmap(R.drawable.bomb_fuse);
        bm.loadBitmap(R.drawable.bomb_debris_body);
        bm.loadBitmap(R.drawable.bomb_debris_fuse);

        bm.loadBitmap(R.drawable.sparks_big_0);
        bm.loadBitmap(R.drawable.sparks_big_1);
        bm.loadBitmap(R.drawable.sparks_big_2);
        bm.loadBitmap(R.drawable.sparks_big_3);

        bm.loadBitmap(R.drawable.powerup_bg);
        bm.loadBitmap(R.drawable.powerup_debris_bg);
        bm.loadBitmap(R.drawable.powerup_sword);
        bm.loadBitmap(R.drawable.powerup_debris_sword);
        bm.loadBitmap(R.drawable.powerup_heart);
        bm.loadBitmap(R.drawable.powerup_debris_heart);

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

            sm.add(R.raw.slap, SAMPLE_SLAP, mActivity);

            sm.add(R.raw.blade, SAMPLE_BLADE_WHIP, mActivity);

            sm.add(R.raw.fuse, SAMPLE_FUSE, mActivity);

            sm.add(R.raw.bomb_boom, SAMPLE_BOMB_BOOM, mActivity);
            sm.add(R.raw.bomb_launch, SAMPLE_BOMB_LAUNCH, mActivity);

            sm.add(R.raw.sword_draw, SAMPLE_SWORD_DRAW, mActivity);
            sm.add(R.raw.clip_in, SAMPLE_GUN_CLIP, mActivity);

            sm.add(R.raw.life_up, SAMPLE_LIFE_UP, mActivity);

        }
    }

    @Override
    public boolean processFrame(float gameTimeStep) {
        if (mWeapon.getWeaponCode() != Gun.WEAPON_CODE_GUN) {
            if (mWeapon.getRemainingTime() <= 0) {
                setWeapon(Gun.WEAPON_CODE_GUN);
            } else {
                mGameActivity.updateSpecialWeaponBar();
            }
        }

        super.processFrame(gameTimeStep);

        if (shakeRemaining > 0) {
            shakeRemaining -= gameTimeStep;
        }

        // scenario
        scenario.processFrame(gameTimeStep);
        if (fadingScenario != null) {
            fadingScenario.processFrame(gameTimeStep);
            if (fadingScenario.fadingOutRemainigTime <= 0) {
                fadingScenario = null;
            }
        }

        return false;
    }

    @Override
    protected void drawActors(Canvas canvas) {
        if (this.shakeRemaining <= 0) {
            super.drawActors(canvas);
        } else {
            float intensity = (shakeRemaining / shakeDuration) * shakeIntensity;

            float pixels = (int) viewport.worldUnitsToPixels(intensity);

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
        if (scenario != null) {
            scenario.draw(canvas);
            if (fadingScenario != null) {
                fadingScenario.draw(canvas);
            }
        }

    }

    // -------------------------------------------------------- M�todos propios

    public void nextScenario() {
        Log.i(LOG_SRC, " Next Scenario");
        if (scenario != null) {
            fadingScenario = scenario;
            fadingScenario.fadingOut = true;
        }
        scenario = new Scenario(this);
        scenario.reset();
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

    public int getHitsCount() {
        int hits = 0;
        int s = mainActors.size();
        for (int i = 0; i < s; i++) {
            hits += MainActor.getHitCount(mainActors.get(i).getCode());
        }
        return hits;
    }

    public void onEnemyScaped(EnemyActor e) {
        if (!mGameActivity.isGameOver()) {

            if (mPlayer.isVulnerable() && !wave.onEnemyScaped(e)) {
                onPostEnemyScaped(e);
            }
        }
    }

    public void onPostEnemyScaped(EnemyActor e) {
        if (mFlashCfgLevel >= PermData.CFG_LEVEL_SOME) {
            FlashActor flash = new FlashActor(this, FlashActor.FLASH_ENEMY_SCAPED_COLOR, FlashActor.FLASH_ENEMY_SCAPED_ALPHA,
                    FlashActor.FLASH_ENEMY_SCAPED_DURATION);
            addActor(flash);
        }

        onFail();
    }

    public void onEnemyKilled(EnemyActor enemy) {
        if (!wave.onEnemyKilled(enemy)) {
            getSoundManager().play(SAMPLE_ENEMY_KILLED);
            if (mVibrateCfgLevel == PermData.CFG_LEVEL_ALL) {
                VibratorManager.getInstance().play(VIBRATION_ENEMY_KILLED);
            }

            mPlayer.onEnemyKilled(enemy);
            scenario.setProgress(wave.getProgress());

            if (mShakeCfgLevel == PermData.CFG_LEVEL_ALL) {
                createShake(100f, 0.20f);
            }

        }

    }

    public void onBombExploded(BombActor bomb) {
        if (!mGameActivity.isGameOver()) {

            if (mPlayer.isVulnerable() && !wave.onBombExploded(bomb)) {
                onPostBombExploded(bomb);
            }
        }
    }

    private void onPostBombExploded(BombActor bomb) {
        if (mFlashCfgLevel >= PermData.CFG_LEVEL_SOME) {
            FlashActor flash = new FlashActor(this, FlashActor.FLASH_BOMB_COLOR, FlashActor.FLASH_BOMB_ALPHA, FlashActor.FLASH_BOMB_DURATION);
            addActor(flash);

            FlashActor flash2 = new FlashActor(this, FlashActor.FLASH_BOMB2_COLOR, FlashActor.FLASH_BOMB2_ALPHA, FlashActor.FLASH_BOMB2_DURATION);
            addActor(flash2);

        }

        onFail();
    }

    public void onLifePowerUp(LifePowerUpActor lifePowerUpActor) {
        if (!mGameActivity.isGameOver()) {

            if (mPlayer.isVulnerable() && !wave.onLifePowerUp(lifePowerUpActor)) {
                onPostLifePowerUp(lifePowerUpActor);
            }
        }
    }

    private void onPostLifePowerUp(LifePowerUpActor lifePowerUpActor) {
        getSoundManager().play(SAMPLE_LIFE_UP);

        if (mFlashCfgLevel >= PermData.CFG_LEVEL_SOME) {
            FlashActor flash = new FlashActor(this, FlashActor.FLASH_LIFEUP_COLOR, FlashActor.FLASH_LIFEUP_ALPHA, FlashActor.FLASH_LIFEUP_DURATION);
            addActor(flash);
        }

        mPlayer.addLifes(1);
    }

    public void onBladePowerUp(BladePowerUpActor bladePowerUpActor) {
        if (!mGameActivity.isGameOver()) {

            if (mPlayer.isVulnerable() && !wave.onBladePowerUp(bladePowerUpActor)) {
                onPostBladePowerUp(bladePowerUpActor);
            }
        }
    }

    private void onPostBladePowerUp(BladePowerUpActor bladePowerUpActor) {
        setWeapon(Blade.WEAPON_CODE_BLADE);

        if (mFlashCfgLevel >= PermData.CFG_LEVEL_SOME) {
            FlashActor flash = new FlashActor(this, FlashActor.FLASH_BLADE_DRAWN_COLOR, FlashActor.FLASH_BLADE_DRAWN_ALPHA,
                    FlashActor.FLASH_BLADE_DRAWN_DURATION);
            addActor(flash);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!mGameActivity.isGameOver() && !isPaused()) {
            final double[] info = new double[] { event.getAction(), event.getX(), event.getY(), System.currentTimeMillis() };
            post(new GameMessage() {
                @Override
                public void process(GameWorld world) {
                    mWeapon.onTouchEvent(info);
                }
            });
        }
        return true;
    }

    public void setWeapon(short weaponId) {
        if (mWeapon != null) {
            mWeapon.onEnded();
        }
        boolean active = false;
        switch (weaponId) {
        case Gun.WEAPON_CODE_GUN:
            getSoundManager().play(SAMPLE_GUN_CLIP);
            mWeapon = new Gun(this);
            active = false;
            break;
        // case Shotgun.WEAPON_CODE_SHOTGUN:
        // weapon = new Shotgun(this);
        // active = true;
        // break;
        case Blade.WEAPON_CODE_BLADE:
            getSoundManager().play(SAMPLE_SWORD_DRAW);
            mWeapon = new Blade(this);
            active = true;
            break;
        }

        mGameActivity.activateSpecialWeaponBar(active);

        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
        if (JumplingsApplication.DEBUG_ENABLED) {
            mGameActivity.updateWeaponsRadioGroup(weaponId);
        }
        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
    }

    /**
     * M�todo ejecutado cuando el jugador falla
     */
    private void onFail() {
        if (!wave.onFail()) {
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
                if (!wave.onGameOver()) {
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

}
