package net.garrapeta.jumplings.wave;

import net.garrapeta.gameengine.GameWorld;
import net.garrapeta.gameengine.SyncGameMessage;
import net.garrapeta.gameengine.utils.PhysicsUtils;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.WeaponSlap;
import net.garrapeta.jumplings.actor.BladePowerUpActor;
import net.garrapeta.jumplings.actor.BombActor;
import net.garrapeta.jumplings.actor.DoubleEnemyActor;
import net.garrapeta.jumplings.actor.EnemyActor;
import net.garrapeta.jumplings.actor.LifePowerUpActor;
import net.garrapeta.jumplings.actor.MainActor;
import net.garrapeta.jumplings.actor.RoundEnemyActor;
import net.garrapeta.jumplings.actor.SplitterEnemyActor;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;

public class AllowanceShooterWave extends AllowanceWave {

    // --------------------------------------------------- Constantes

    private static float POWERUP_BASE_LAPSE = 30000;

    // ---------------------------------------- Variables de instancia

    JumplingsGameWorld mJgWorld;

    private short nextJumperCode;

    private float bombProbability;
    private float specialEnemyProbability;
    private float doubleEnemyProbability;
    private float tripleSplitterEnemyProbability;

    private float maxBombs;

    /** N�mero de enemigos que hay que matar */
    private int totalKills;
    /** N�mero de enemigos muertos */
    private int kills;

    // -------------------------------------------------------- Constructor

    public AllowanceShooterWave(JumplingsGameWorld jgWorld, IWaveEndListener listener, int level) {
        super(jgWorld, listener, level);
        this.mJgWorld = jgWorld;

        nextJumperCode = RoundEnemyActor.JUMPER_CODE_SIMPLE;

        // Inicializaci�n de probabilidades y riesgos
        setMaxThreat(2 + (level * 1.3));

        bombProbability = Math.min(0.35f, 0.025f + (level * 0.075f));

        specialEnemyProbability = Math.min(0.65f, 0.2f + (level * 0.05f));

        doubleEnemyProbability = 0.35f;

        tripleSplitterEnemyProbability = Math.min(0.5f, 0.0f + (level * 0.05f));

        maxBombs = Math.min(3, 0.49f + (level * 0.4f));

        totalKills = 30 + level * 10;

        scheduleGeneratePowerUp(getPowerUpCreationLapse());

    }

    // ------------------------------------------------- M�todos heredados

    @Override
    protected float getCurrentThreat() {
        return mJgWorld.getHitsCount();
    }

    @Override
    protected double generateThreat(double threatNeeded) {
        PointF initPos = new PointF();
        Vector2 initVel = new Vector2();

        createInitPosAndVel(initPos, initVel);

        double threat = tryToCreateJumper(threatNeeded, initPos, initVel);

        return threat;
    }

    @Override
    public void onProcessFrame(float stepTime) {
        // se comprueba que la wave no ha terminado por tiempo
        if (getProgress() >= 100) {
            // no se comunica el fin de la wave hasta que todos los enemigos
            // est�n muertos
            if (mJgWorld.jumplingActors.size() == 0 && mListener != null) {
                mListener.onWaveEnded();
                ;
            }
        } else {
            super.onProcessFrame(stepTime);
        }
    }

    @Override
    public boolean onEnemyKilled(EnemyActor enemy) {
        kills++;
        return false;
    }

    /**
     * @return progreso, del 0 - 100
     */
    public float getProgress() {
        return kills * 100 / totalKills;
    }

    @Override
    public void dispose() {
        super.dispose();
        mJgWorld = null;
    }

    // -------------------------------------------------- M�todos propios

    private short generateJumperCode() {
        short code;

        do {
            while (true) {
                if (Math.random() < bombProbability && mJgWorld.bombCount + 1 <= maxBombs
                        && mJgWorld.mWeapon.getWeaponCode() == WeaponSlap.WEAPON_CODE_GUN) {
                    code = BombActor.JUMPER_CODE_BOMB;
                    break;
                }

                if (Math.random() > specialEnemyProbability) {
                    code = RoundEnemyActor.JUMPER_CODE_SIMPLE;
                    break;
                }

                if (Math.random() < doubleEnemyProbability) {
                    code = DoubleEnemyActor.JUMPER_CODE_DOUBLE;
                    break;
                }

                if (Math.random() < tripleSplitterEnemyProbability) {
                    code = SplitterEnemyActor.JUMPER_CODE_SPLITTER_TRIPLE;
                    break;
                }

                code = SplitterEnemyActor.JUMPER_CODE_SPLITTER_DOUBLE;
                break;
            }
        } while (MainActor.getHitCount(code) > getMaxThreat());

        Log.i(LOG_SRC, "Next enemy code: " + code);
        return code;
    }

    private double tryToCreateJumper(double threatNeeded, PointF initPos, Vector2 initVel) {
        double threat = MainActor.getHitCount(nextJumperCode);
        MainActor mainActor = null;

        if (threat <= threatNeeded) {

            switch (nextJumperCode) {
            case RoundEnemyActor.JUMPER_CODE_SIMPLE:
                // simple
                mainActor = new RoundEnemyActor(mJgWorld, initPos);
                break;
            case DoubleEnemyActor.JUMPER_CODE_DOUBLE:
                // double
                mainActor = new DoubleEnemyActor(mJgWorld, initPos);
                break;
            case SplitterEnemyActor.JUMPER_CODE_SPLITTER_DOUBLE:
                mainActor = new SplitterEnemyActor(mJgWorld, initPos, 1);
                break;
            case SplitterEnemyActor.JUMPER_CODE_SPLITTER_TRIPLE:
                mainActor = new SplitterEnemyActor(mJgWorld, initPos, 2);
                break;
            case BombActor.JUMPER_CODE_BOMB:
                mainActor = new BombActor(mJgWorld, initPos);
                break;
            }
        }

        if (mainActor != null) {

            mainActor.setLinearVelocity(initVel.x, initVel.y);
            mJgWorld.addActor(mainActor);
            Log.i(LOG_SRC, "Added mainActor: " + nextJumperCode);
            nextJumperCode = generateJumperCode();
            return threat;

        } else {
            return 0;
        }
    }

    private void createInitPosAndVel(PointF pos, Vector2 vel) {
        int rand = (int) (Math.random() * 4);

        switch (rand) {
        case 0:
            // arriba
            createInitPosAndVelTop(pos, vel);
            break;
        case 1:
            // abajo
            createInitPosAndVelBottom(pos, vel);
            break;
        case 2:
            // izquierda
            createInitPosAndVelLeft(pos, vel);
            break;
        case 3:
            // derecha
            createInitPosAndVelRight(pos, vel);
            break;
        }

    }

    private void createInitPosAndVelBottom(PointF pos, Vector2 vel) {
        // posX
        pos.x = getRandomPosX();

        // posY
        pos.y = getBottomPos();

        // vel
        createInitVel(pos, vel);
    }

    private void createInitPosAndVelTop(PointF pos, Vector2 vel) {
        // posX
        pos.x = getRandomPosX();

        // posY
        pos.y = getTopPos();

        // vel
        vel.x = 0;
        vel.y = 0;
    }

    private void createInitPosAndVelLeft(PointF pos, Vector2 vel) {
        // posX
        pos.x = getLeftPos();

        // posY
        pos.y = getRandomPosY();

        // vel
        createInitVel(pos, vel);
    }

    private void createInitPosAndVelRight(PointF pos, Vector2 vel) {
        // posX
        pos.x = getRightPos();

        // posY
        pos.y = getRandomPosY();

        // vel
        createInitVel(pos, vel);
    }

    private void createInitVel(PointF pos, Vector2 vel) {
        // vel
        float g = mJgWorld.getGravityY();

        // Factor de aletoriedad (0 - 1)
        float XFACTOR = 0.9f;
        float YFACTOR = 0.75f;

        // Distancia m�xima que pueda viajar verticalmente
        RectF bounds = mJgWorld.mViewport.getWorldBoundaries();
        float boundsHeight = bounds.top - bounds.bottom;

        // Distancia vertical que va a alcanzar. Se le hace un poco aleatoria.
        float maxYDistance = (float) (YFACTOR + ((1 - YFACTOR) * Math.random())) * boundsHeight;

        float targetY = bounds.bottom + maxYDistance;

        // Distancia vertical que a subir
        float yDistance = targetY - pos.y;

        // velocidad vertical inicial
        float vy = (float) PhysicsUtils.getInitialVelocity(yDistance, 0, g);

        // Tiempo que va a estar subiendo
        float tup = (float) PhysicsUtils.getTime(vy, 0, g);
        float tdown = (float) PhysicsUtils.getTime(maxYDistance, -g);

        // Tiempo que va a estar viajando (arriba + abajo)
        float t = tup + tdown;

        float worldWidth = bounds.right - bounds.left;

        // Distancia m�xima que pueda viajar horizontalmente
        float maxXDistance;
        // Dependiendo de la posici�n se le tira a la izquierda o derecha
        if (pos.x > bounds.left + (worldWidth / 2)) {
            maxXDistance = bounds.left - pos.x;
        } else {
            maxXDistance = bounds.right - pos.x;
        }

        // Distancia que va a viajer horizontalmente. Se le hace un poco
        // aleatoria.
        float xDistance = (float) (XFACTOR + ((1 - XFACTOR) * Math.random())) * maxXDistance;

        float vx = xDistance / t;

        vel.x = vx;
        vel.y = vy;

    }

    private float getPowerUpCreationLapse() {
        int wounds = mJgWorld.getPlayer().getMaxLifes() - mJgWorld.getPlayer().getLifes();

        float l = POWERUP_BASE_LAPSE - ((POWERUP_BASE_LAPSE / mJgWorld.getPlayer().getMaxLifes() * (wounds - 1)));
        return l;
    }

    private void scheduleGeneratePowerUp(float delay) {
        mJWorld.post(new SyncGameMessage() {
            @Override
            public void doInGameLoop(GameWorld world) {
                if (!mIsGameOver) {
                    generatePowerUp();
                    scheduleGeneratePowerUp(getPowerUpCreationLapse());
                }
            }
        }, delay);
    }

    private void generatePowerUp() {
        PointF initPos = new PointF();
        Vector2 initVel = new Vector2();

        createInitPosAndVelTop(initPos, initVel);

        MainActor powerUp;

        // la probabilidad de que salga una vida crece conforme se ha perdido
        // vida
        float lifeUpBaseProbability = 0.7f;
        float woundedFactor = 1 - (mJgWorld.getPlayer().getLifes() / mJgWorld.getPlayer().getMaxLifes());
        float lifeUpProbability = lifeUpBaseProbability * woundedFactor;

        if (Math.random() < lifeUpProbability) {
            powerUp = new LifePowerUpActor(mJgWorld, initPos);
        } else {
            powerUp = new BladePowerUpActor(mJgWorld, initPos);
        }

        powerUp.setLinearVelocity(initVel.x, initVel.y);
        mJgWorld.addActor(powerUp);
    }

}
