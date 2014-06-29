package com.garrapeta.jumplings.game.wave;

import android.graphics.PointF;
import android.view.View;

import com.badlogic.gdx.math.Vector2;
import com.garrapeta.gameengine.GameWorld;
import com.garrapeta.gameengine.SyncGameMessage;
import com.garrapeta.jumplings.game.JumplingsGameWorld;
import com.garrapeta.jumplings.game.Wave;
import com.garrapeta.jumplings.game.actor.MainActor;
import com.garrapeta.jumplings.game.scenario.IScenario;
import com.garrapeta.jumplings.game.scenario.ScenarioFactory;
import com.garrapeta.jumplings.util.PermData;

public class TestWave extends Wave<JumplingsGameWorld> {

    public final static String WAVE_KEY = TestWave.class.getCanonicalName();

    JumplingsGameWorld mJgWorld;
    IScenario mScenario;

    /**
     * @param jgWorld
     * @param level
     */
    public TestWave(JumplingsGameWorld jgWorld) {
        super(jgWorld, 0);
        mJgWorld = jgWorld;
        if (PermData.areDebugFeaturesEnabled(mJgWorld.mGameActivity)) {
            mJgWorld.mGameActivity.mTestBtn.setVisibility(View.VISIBLE);
            mJgWorld.mGameActivity.mSwordBtn.setVisibility(View.VISIBLE);
            mJgWorld.setGravityY(-1);
        }

    }

    @Override
    public void onProcessFrame(float gameTimeStep) {
        if (mScenario == null) {
            mScenario = ScenarioFactory.getScenario(getWorld(), ScenarioFactory.ScenariosIds.NATURE);
            mJgWorld.setScenario(mScenario);
            mScenario.init();
        }
    }

    int progess = 0;

    @Override
    public void onTestButtonClicked(View btn) {
        mJgWorld.post(new SyncGameMessage() {

            @Override
            public void doInGameLoop(GameWorld world) {
                createEnemy();
            }
        });

    }

    @Override
    public boolean onFail() {
        return true;
    }

    public void createPowerUp() {
        this.mJgWorld.setGravityY(0);

        PointF initPos = new PointF(5, 12);
        MainActor mainActor;
        initPos.x += 8;
        mainActor = mJgWorld.getFactory()
                            .getLifePowerUpActor(initPos);
        mJgWorld.addActor(mainActor);

    }

    public void createEnemy() {

        boolean debug = false;

        float worldXPos;
        float worldYPos;

        if (debug) {
            mJgWorld.setGravityY(0);
            worldXPos = (mJgWorld.mViewport.getWorldBoundaries().right - mJgWorld.mViewport.getWorldBoundaries().left) / 2;
            worldYPos = (mJgWorld.mViewport.getWorldBoundaries().top - mJgWorld.mViewport.getWorldBoundaries().bottom) / 2;
        } else {
            worldXPos = getRandomPosX();
            worldYPos = getBottomPos();
        }

        PointF initPos = new PointF(worldXPos, worldYPos);
        Vector2 initVel = getInitialVelocity(initPos);
        MainActor mainActor = getWorld().getFactory()
                                        .getRoundEnemyActor(initPos);
        mainActor.setLinearVelocity(initVel.x, initVel.y);
        mJgWorld.addActor(mainActor);
    }

}
