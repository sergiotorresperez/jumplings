package net.garrapeta.jumplings.wave;

import net.garrapeta.gameengine.GameWorld;
import net.garrapeta.gameengine.SyncGameMessage;
import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.Wave;
import net.garrapeta.jumplings.actor.LifePowerUpActor;
import net.garrapeta.jumplings.actor.MainActor;
import net.garrapeta.jumplings.actor.SplitterEnemyActor;
import net.garrapeta.jumplings.scenario.IScenario;
import net.garrapeta.jumplings.scenario.ScenarioFactory;
import android.graphics.PointF;
import android.view.View;
import android.widget.Button;

import com.badlogic.gdx.math.Vector2;

public class TestWave extends Wave {

    // ----------------------------------------------------- Constantes

    // Clave para referirse a esta wave
    public final static String WAVE_KEY = TestWave.class.getCanonicalName();

    // --------------------------------------------------- Variables

    JumplingsGameWorld jgWorld;

    IScenario mScenario;

    // ------------------------------------------------------------- Constructor

    /**
     * @param jgWorld
     * @param level
     * @param listener
     */
    public TestWave(JumplingsGameWorld jgWorld, IWaveEndListener listener) {
        super(jgWorld, listener, 0);
        this.jgWorld = jgWorld;
        if (JumplingsApplication.DEBUG_ENABLED) {
            this.jgWorld.mGameActivity.testBtn.setVisibility(View.VISIBLE);
            this.jgWorld.mGameActivity.weaponsRadioGroup.setVisibility(View.VISIBLE);
            this.jgWorld.setGravityY(-1);
        }

    }

    // ------------------------------------------------------ M�todos de
    // BaseWave

    @Override
    public void onProcessFrame(float gameTimeStep) {
        if (mScenario == null) {
            mScenario = ScenarioFactory.getScenario(jWorld, ScenarioFactory.ScenariosIds.JUNGLE);
            jgWorld.setScenario(mScenario);
            mScenario.init();
        }
    }

    int progess = 0;

    @Override
    public void onTestButtonClicked(Button showAdBtn) {
        // this.world.cActivity.runOnUiThread(new Runnable() {
        //
        // @Override
        // public void run() {
        // world.cActivity.showDialog(DemoActivity.DIALOG_AD_ID);
        //
        // }
        // });
        //
        jgWorld.post(new SyncGameMessage() {

            @Override
            public void doInGameLoop(GameWorld world) {
                if (progess == 100) {
                    progess = 0;
                    mScenario.onGameOver();
                } else {
                    progess = Math.min(progess + 10, 100);
                    mScenario.setProgress(progess);
                }
            }
        });

    }

    public boolean onFail() {
        return true;
    }

    // ---------------------------------------------------- M�todos propios

    public void createEnemy() {

        boolean debug = false;

        float worldXPos;
        float worldYPos;

        if (debug) {
            // world.setGravityY(0);
            worldXPos = (jgWorld.viewport.getWorldBoundaries().right - jgWorld.viewport.getWorldBoundaries().left) / 2;
            // worldYPos = (world.worldBoundaries.top - ENEMY_OFFSET);
            worldYPos = 2;
        } else {
            worldXPos = getRandomPosX();
            worldYPos = getBottomPos();
        }

        PointF initPos = new PointF(worldXPos, worldYPos);
        Vector2 initVel = getInitialVelocity(initPos);

        // RoundEnemyActor mainActor = new RoundEnemyActor(jgWorld, initPos);

        // ShapeActor sa =
        // world.cActivity.CreateShapeEnemy(ShapelingsGameActivity.SHAPE_CIRCLE,
        // initPos, bodyWorldRadius);
        // JumperActor mainActor = new SplitterEnemyActor(world, initPos,
        // color2, 2);

        // EnemyActor enemy = new SplitterEnemyActor(world,
        // SplitterEnemyActor.DEFAULT_BASE_RADIUS,
        // initPos, (short) 2, 2);

        // DoubleEnemyActor ba = new DoubleEnemyActor(world, bodyWorldRadius,
        // worldPos, color1, color2);
        // SquareEnemyActor ba = new SquareEnemyActor(world, bodyWorldRadius,
        // worldPos, color1);
        MainActor mainActor = new SplitterEnemyActor(jgWorld, initPos, 1);

        if (!debug) {
            mainActor.setLinearVelocity(initVel.x, initVel.y);
        } else {
            mainActor.setLinearVelocity(initVel.x, initVel.y);

        }
        jgWorld.addActor(mainActor);

    }

    public void createPowerUp() {

        boolean debug = false;

        float worldXPos;
        float worldYPos;

        if (debug) {
            jgWorld.setGravityY(0);
            worldXPos = (jgWorld.viewport.getWorldBoundaries().right - jgWorld.viewport.getWorldBoundaries().left) / 2;
            worldYPos = (jgWorld.viewport.getWorldBoundaries().top - jgWorld.viewport.getWorldBoundaries().bottom) / 2;
        } else {
            worldXPos = getRandomPosX();
            worldYPos = getBottomPos();
        }

        PointF initPos = new PointF(worldXPos, worldYPos);
        Vector2 initVel = getInitialVelocity(initPos);

        // RoundEnemyActor ba = new RoundEnemyActor(world, bodyWorldRadius,
        // worldPos, color1);

        // ShapeActor sa =
        // world.cActivity.CreateShapeEnemy(ShapelingsGameActivity.SHAPE_CIRCLE,
        // initPos, bodyWorldRadius);
        MainActor mainActor = new LifePowerUpActor(jgWorld, initPos);

        // EnemyActor enemy = new SplitterEnemyActor(world,
        // SplitterEnemyActor.DEFAULT_BASE_RADIUS,
        // initPos, (short) 2, 2);

        // DoubleEnemyActor ba = new DoubleEnemyActor(world, bodyWorldRadius,
        // worldPos, color1, color2);
        // SquareEnemyActor ba = new SquareEnemyActor(world, bodyWorldRadius,
        // worldPos, color1);

        if (!debug) {
            mainActor.setLinearVelocity(initVel.x, initVel.y);
        }
        jgWorld.addActor(mainActor);

    }

}
