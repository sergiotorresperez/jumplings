package com.garrapeta.jumplings;

import android.graphics.PointF;

import com.garrapeta.gameengine.GameWorld;
import com.garrapeta.gameengine.SyncGameMessage;
import com.garrapeta.jumplings.actor.ComboTextActor;
import com.garrapeta.jumplings.actor.EnemyActor;
import com.garrapeta.jumplings.actor.ScoreTextActor;

public class Player {

    // --------------------------------------------------------- Constantes

    public static final int DEFAUL_MAX_LIFES = 5;

    public static final int DEFAUL_INIT_LIFES = 3;

    public static final int DEFAUL_INIT_SCORE = 0;

    public static final int COMBO_MAX_SPACING_TIME = 350;

    public static final int BASE_POINTS = 5;

    // --------------------------------------------- Variables de instancia

    private JumplingsGameWorld mWorld;

    private int mMaxLifes = DEFAUL_MAX_LIFES;

    private int mLife = DEFAUL_INIT_LIFES;

    private int mScore = DEFAUL_INIT_SCORE;

    private boolean mIsVulnerable = true;

    private long mPrevEnemyKillTimestamp;

    private int mCurrentComboLevel;

    // ------------------------------------------------------------- M�todos

    public Player(JumplingsGameWorld world) {
        this.mWorld = world;
    }

    /**
     * @return the life
     */
    public int getLifes() {
        return mLife;
    }

    public void addLifes(int add) {
        setLife(mLife + add);
    }

    public void subLifes(int sub) {
        setLife(mLife - sub);
    }

    public void topUpLife() {
        setLife(mMaxLifes);
    }

    /**
     * @param mLife
     *            the life to set
     */
    private void setLife(int newLifes) {
        newLifes = Math.min(newLifes, mMaxLifes);
        newLifes = Math.max(0, newLifes);
        this.mLife = newLifes;
        mWorld.mGameActivity.updateLifeCounterView();
    }

    public int getMaxLifes() {
        return mMaxLifes;
    }

    /**
     * @return the score
     */
    public int getScore() {
        return mScore;
    }

    public void onEnemyKilled(EnemyActor enemy) {
        int points = BASE_POINTS;
        long timeStamp = mWorld.currentGameMillis();

        if (timeStamp - mPrevEnemyKillTimestamp < COMBO_MAX_SPACING_TIME) {
            mCurrentComboLevel++;
        } else {
            mCurrentComboLevel = 0;
        }

        mPrevEnemyKillTimestamp = timeStamp;

        points += (mCurrentComboLevel * BASE_POINTS);
        setScore(mScore + points);

        PointF worldPos = enemy.getWorldPos();
        ScoreTextActor scoreActor = new ScoreTextActor(mWorld, worldPos, points);
        scoreActor.setInitted();

        mWorld.addActor(scoreActor);
        if (mCurrentComboLevel > 0) {
            mWorld.onCombo();
            ComboTextActor comboActor = new ComboTextActor(mWorld, new PointF(worldPos.x, worldPos.y), mCurrentComboLevel);
            comboActor.setInitted();
            mWorld.addActor(comboActor);
        }

    }

    /**
     * @param score
     *            the score to set
     */
    private void setScore(int score) {
        this.mScore = score;
        mWorld.mGameActivity.updateScoreTextView();
    }

    public boolean isVulnerable() {
        return mIsVulnerable;
    }

    public void makeVulnerable() {
        mWorld.mGameActivity.stopBlinkingLifeBar();
        this.mIsVulnerable = true;
    }

    public void makeInvulnerable(int invulnerabilityTime) {
        mIsVulnerable = false;
        mWorld.mGameActivity.startBlinkingLifeBar();

        mWorld.post(new SyncGameMessage() {

            @Override
            public void doInGameLoop(GameWorld world) {
                makeVulnerable();
            }
        }, invulnerabilityTime);

    }

}
