package com.garrapeta.jumplings.wave;

import android.graphics.PointF;
import android.view.View;
import android.widget.Button;

import com.badlogic.gdx.math.Vector2;
import com.garrapeta.gameengine.GameWorld;
import com.garrapeta.gameengine.SyncGameMessage;
import com.garrapeta.jumplings.JumplingsApplication;
import com.garrapeta.jumplings.JumplingsGameWorld;
import com.garrapeta.jumplings.Wave;
import com.garrapeta.jumplings.actor.MainActor;
import com.garrapeta.jumplings.scenario.IScenario;
import com.garrapeta.jumplings.scenario.ScenarioFactory;

@SuppressWarnings("unused")
public class TestWave extends Wave<JumplingsGameWorld> {

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
        if (JumplingsApplication.DEBUG_FUNCTIONS_ENABLED) {
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
            mScenario = ScenarioFactory.getScenario(getWorld(), ScenarioFactory.ScenariosIds.NATURE);
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
                createEnemy();
//                if (progess == 100) {
//                    progess = 0;
//                    mScenario.onGameOver();
//                } else {
//                    progess = Math.min(progess + 10, 100);
//                    mScenario.setProgress(progess);
//                }
            }
        });

    }

    public boolean onFail() {
        return true;
    }

    // ---------------------------------------------------- M�todos propios

    public void createEnemy() {
        this.jgWorld.setGravityY(0);

        PointF initPos = new PointF(5, 12);
//        Vector2 initVel = getInitialVelocity(initPos);

         MainActor mainActor;
//         mainActor = jgWorld.getFactory().getSplitterEnemyActor(initPos, 1);

//        jgWorld.addActor(mainActor);

        initPos.x += 8;
        
        mainActor = jgWorld.getFactory().getLifePowerUpActor(initPos);
        jgWorld.addActor(mainActor);

    }

    public void createPowerUp() {

        boolean debug = false;

        float worldXPos;
        float worldYPos;

        if (debug) {
            jgWorld.setGravityY(0);
            worldXPos = (jgWorld.mViewport.getWorldBoundaries().right - jgWorld.mViewport.getWorldBoundaries().left) / 2;
            worldYPos = (jgWorld.mViewport.getWorldBoundaries().top - jgWorld.mViewport.getWorldBoundaries().bottom) / 2;
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
        MainActor mainActor = getWorld().getFactory().getLifePowerUpActor(initPos);
 

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
