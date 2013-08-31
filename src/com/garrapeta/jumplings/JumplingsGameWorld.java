package com.garrapeta.jumplings;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.garrapeta.gameengine.BitmapManager;
import com.garrapeta.gameengine.GameView;
import com.garrapeta.gameengine.GameWorld;
import com.garrapeta.gameengine.SyncGameMessage;
import com.garrapeta.gameengine.module.SoundModule;
import com.garrapeta.gameengine.module.VibrationModule;
import com.garrapeta.jumplings.actor.BombActor;
import com.garrapeta.jumplings.actor.ComboTextActor;
import com.garrapeta.jumplings.actor.EnemyActor;
import com.garrapeta.jumplings.actor.JumplingActor;
import com.garrapeta.jumplings.actor.LifePowerUpActor;
import com.garrapeta.jumplings.actor.MainActor;
import com.garrapeta.jumplings.actor.ScoreTextActor;
import com.garrapeta.jumplings.module.FlashModule;
import com.garrapeta.jumplings.module.ShakeModule;
import com.garrapeta.jumplings.scenario.IScenario;
import com.garrapeta.jumplings.weapon.FingerprintWeapon;
import com.garrapeta.jumplings.weapon.Weapon;
import com.garrapeta.jumplings.weapon.WeaponFactory;
import com.garrapeta.jumplings.weapon.SwordWeapon.WeaponSwordListener;

/**
 * Mundo del juego
 * 
 * @author GaRRaPeTa
 */
public class JumplingsGameWorld extends JumplingsWorld implements OnTouchListener, GameEventsListener, WeaponSwordListener {

    // -------------------------------------------------------- Constantes

    public static final int INVULNERABILITY_TIME_ENEMY_SCAPED = 2000;
    public static final int INVULNERABILITY_TIME_BOMB_EXPLODED = 4000;

    public static final int WEAPON_FINGERPRINT = 0;
    public static final int WEAPON_SWORD = 1;

    // ------------------------------------ Consantes de sonidos y vibraciones

    public static final short SAMPLE_ENEMY_BOING = 1;
    public static final short SAMPLE_ENEMY_DROP = 2;
    public static final short SAMPLE_ENEMY_KILLED = 3;
    public static final short SAMPLE_FAIL = 4;

    public static final short SAMPLE_FINGERPRINT = 5;
    public static final short SAMPLE_SWORD_SWING = 6;

    public static final short SAMPLE_FUSE = 7;
    public static final short SAMPLE_BOMB_BOOM = 8;
    public static final short SAMPLE_BOMB_LAUNCH = 9;

    public static final short SAMPLE_SWORD_SHEATH = 10;
    public static final short SAMPLE_SWORD_UNSHEATH = 11;

    public static final short SAMPLE_LIFE_UP = 12;

    public static final short VIBRATION_ENEMY_KILLED_KEY = 0;
    public static final short VIBRATION_FAIL_KEY = 1;

    private static final long[] VIBRATION_ENEMY_KILLED_PATTERN = { 0, 90 };
    private static final long[] VIBRATION_FAIL_PATTERN = { 0, 100, 50, 400 };


    // ------------------------------------------------------------ Variables

    public GameActivity mGameActivity;

    public ArrayList<MainActor> mMainActors = new ArrayList<MainActor>();

    public ArrayList<EnemyActor> mEnemies = new ArrayList<EnemyActor>();

    public ArrayList<BombActor> mBombActors = new ArrayList<BombActor>();

    public ArrayList<ComboTextActor> mComboTextActors = new ArrayList<ComboTextActor>();
    
    public ArrayList<ScoreTextActor> mScoreTextActors = new ArrayList<ScoreTextActor>();
    
    /** Flash module used for flash effects */
    public FlashModule mFlashModule;
    /** Flash module used for flash effects */
    private ShakeModule mShakeModule;
    
    private boolean mAutoplay;
    
    /** Jugador */
    Player mPlayer;
    
    private final Tutorial mTutorial;

    // TODO: use this paint for painting the Jumplings
    protected Paint mPaint = new Paint();

    /** Arma actual */
    public Weapon mWeapon;

    /** Escenario actual */
    IScenario mScenario = null;

    
    /** If game is over */
    private boolean mIsGameOver = false;

    // ----------------------------------------------------------- Constructor

    public JumplingsGameWorld(GameActivity gameActivity, GameView gameView, Context context) {
        super(gameActivity, gameView, context);
        mGameActivity = gameActivity;
        mPlayer = new Player(this);
        mTutorial = new Tutorial(gameActivity, GameActivity.DIALOG_FRAGMENT_TAG);
        mGameView.setOnTouchListener(this);
        mShakeModule = new ShakeModule(PermData.getShakeConfig(mGameActivity), this);
        mAutoplay = PermData.isAutoplayEnabled(mGameActivity);
     }

    // ----------------------------------------------------- M�todos de World

    public Player getPlayer() {
        return mPlayer;
    }

    @Override
    public void onBeforeRunning() {
        super.onBeforeRunning();
        // TODO: if I put this in the constructor flashes are not shown
        mFlashModule = new FlashModule(PermData.getFlashConfig(mGameActivity), this);
        
        // Inicializaci�n del arma
        setWeapon(FingerprintWeapon.WEAPON_CODE_FINGERPRINT);

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
        
        bm.loadBitmap(resources, R.drawable.fingerprint);

        // Sound samples setup
        
        
        SoundModule sm = getSoundManager();
        sm.create(PermData.CFG_LEVEL_ALL, SAMPLE_ENEMY_BOING).add(R.raw.boing1).add(R.raw.boing2).add(R.raw.boing3);
        sm.create(PermData.CFG_LEVEL_ALL, SAMPLE_ENEMY_DROP).add(R.raw.drop);
        sm.create(PermData.CFG_LEVEL_ALL, SAMPLE_ENEMY_KILLED).add(R.raw.crush);
        sm.create(PermData.CFG_LEVEL_ALL, SAMPLE_FAIL).add(R.raw.wrong);
        sm.create(PermData.CFG_LEVEL_ALL, SAMPLE_FINGERPRINT).add(R.raw.whip);
        sm.create(PermData.CFG_LEVEL_ALL, SAMPLE_SWORD_SWING).add(R.raw.sword_swing);
        sm.create(PermData.CFG_LEVEL_ALL, SAMPLE_FUSE).add(R.raw.fuse);
        sm.create(PermData.CFG_LEVEL_ALL, SAMPLE_BOMB_BOOM).add(R.raw.bomb_boom);
        sm.create(PermData.CFG_LEVEL_ALL, SAMPLE_BOMB_LAUNCH).add(R.raw.bomb_launch);
        sm.create(PermData.CFG_LEVEL_ALL, SAMPLE_SWORD_SHEATH).add(R.raw.sword_sheath);
        sm.create(PermData.CFG_LEVEL_ALL, SAMPLE_SWORD_UNSHEATH).add(R.raw.sword_unsheath);
        sm.create(PermData.CFG_LEVEL_ALL, SAMPLE_LIFE_UP).add(R.raw.life_up);
        
        // Vibrations setup
        VibrationModule vm = getVibratorManager();
        vm.add(PermData.CFG_LEVEL_ALL, VIBRATION_ENEMY_KILLED_KEY, VIBRATION_ENEMY_KILLED_PATTERN);
        vm.add(PermData.CFG_LEVEL_SOME, VIBRATION_FAIL_KEY, VIBRATION_FAIL_PATTERN);
    }

    @Override
    public boolean processFrame(float gameTimeStep) {
        if (mWeapon != null) {
        	mWeapon.processFrame(gameTimeStep);
        }

        super.processFrame(gameTimeStep);

        mShakeModule.processFrame(gameTimeStep);

        // scenario
        if (mScenario != null) {
            mScenario.processFrame(gameTimeStep);
        }

        if (mAutoplay) {
            autoPlay();
        }

        return false;
    }

    @Override
    protected void drawActors(Canvas canvas) {
        mShakeModule.preDraw(canvas, mViewport);
        super.drawActors(canvas);
        mShakeModule.postDraw(canvas);
    }

    @Override
    protected void drawScenario(Canvas canvas) {
        super.drawScenario(canvas);
    	if (mScenario != null) {
            mScenario.draw(canvas, mPaint);
        }
    }

    // -------------------------------------------------------- M�todos propios

    /**
     * @return if the game is over
     */
    public boolean isGameOver() {
    	return mIsGameOver;
    }
   
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
        if (!isGameOver() && mPlayer.isVulnerable()) {            
            if (mWave.onEnemyScaped(enemy)) {
                return true;
            }
            if (mTutorial.onEnemyScaped(enemy)) {
                mPlayer.makeInvulnerable(INVULNERABILITY_TIME_ENEMY_SCAPED);
                return true;
            }
            mFlashModule.flash(FlashModule.ENEMY_SCAPED_KEY);
            onFail(INVULNERABILITY_TIME_ENEMY_SCAPED);
        }
        return true;
    }


    private boolean onGameOver() {
        mIsGameOver = true;
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

     	getVibratorManager().vibrate(VIBRATION_ENEMY_KILLED_KEY);

        if (mWave.onEnemyKilled(enemy)) {
            return true;
        }

        mTutorial.onEnemyKilled(enemy);
        
        mPlayer.onEnemyKilled(enemy);
        mShakeModule.shake(ShakeModule.ENEMY_KILLED_SHAKE);

        return true;
    }

    @Override
    public boolean onBombExploded(BombActor bomb) {
        if (!isGameOver() && mPlayer.isVulnerable() ) {
            if (mWave.onBombExploded(bomb)) {
                return true;
            }
            if (mTutorial.onBombExploded(bomb)) {
                return true;
            }
        	mFlashModule.flash(FlashModule.BOMB_EXPLODED_KEY);
            onFail(INVULNERABILITY_TIME_BOMB_EXPLODED);
        }
        return true;
    }

    @Override
    public boolean onLifePowerUp(LifePowerUpActor lifePowerUpActor) {
        if (!isGameOver()) {
            if (mWave.onLifePowerUp(lifePowerUpActor)) {
                return true;
            }
            
            mTutorial.onLifePowerUp(lifePowerUpActor);
            
            getSoundManager().play(SAMPLE_LIFE_UP);
            mFlashModule.flash(FlashModule.LIFE_UP_KEY);
            mPlayer.addLifes(1);
        }
        return true;
    }
    
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isGameOver() && !isPaused()) {
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
    	mWeapon = WeaponFactory.getWeapon(this, weaponId);
        mWeapon.onStart(currentGameMillis());
    }
    
	@Override
	public void onSwordStarted() {
	   mGameActivity.onSwordStarted();
       mFlashModule.flash(FlashModule.SWORD_DRAWN_KEY);
       
       if (!isGameOver() && mTutorial != null) {
           mTutorial.onSwordStarted();
       }
	}
	
	@Override
	public void onSwordRemainingTimeUpdated(float remaining) {
        mGameActivity.updateSwordRemainingTimeUpdated(remaining);
	}

	@Override
	public void onSwordEnded() {
		mGameActivity.onSwordEnded();
		setWeapon(FingerprintWeapon.WEAPON_CODE_FINGERPRINT);
		
        if (!isGameOver() && mTutorial != null) {
      	    mTutorial.onSwordEnded();
        }
	}

    /**
     * Executed when the player fails
     * @param invulnerabilityTime
     */
    private void onFail(int invulnerabilityTime) {
        if (!mWave.onFail()) {
            getSoundManager().play(SAMPLE_FAIL);
            getVibratorManager().vibrate(VIBRATION_FAIL_KEY);

            mPlayer.subLifes(1);
            mPlayer.makeInvulnerable(invulnerabilityTime);

            mShakeModule.shake(ShakeModule.PLAYER_FAIL_SHAKE);
            if (mPlayer.getLifes() <= 0) {
                onGameOver();
            }
        }
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
