package com.garrapeta.jumplings.game.actor;

import android.graphics.PointF;

import com.garrapeta.jumplings.R;
import com.garrapeta.jumplings.game.JumplingsGameWorld;
import com.garrapeta.jumplings.game.Player;

public class ScoreTextActor extends TextActor {

    // ----------------------------------------------------- Constantes

    /** Velocidad vertical, en unidades del mundo por segundo */
    public final static float DEFAULT_Y_VELOCITY = 15;

    /** Tiempo que permanece el actor en pantalla, en ms */
    public final static int DEFAULT_LONGEVITY = 250;

    private static float sBaseTextSize = -1;

    // ----------------------------------------- Variables de instancia

    // ----------------------------------------------------- Constructor

    public ScoreTextActor(JumplingsGameWorld jgWorld, PointF worldPos, int score) {
        super(jgWorld, worldPos);

        int level = (score / Player.BASE_POINTS);

        mText = "+" + score;

        mYVel = DEFAULT_Y_VELOCITY;

        mLongevity = DEFAULT_LONGEVITY + (level * 150);
        mLifeTime = mLongevity;

        if (sBaseTextSize < 0) {
            sBaseTextSize = jgWorld.mActivity.getResources()
                                             .getDimension(R.dimen.ingame_score_text_actor_base_text_size);
        }
        float textSize = sBaseTextSize + (sBaseTextSize * (level - 1) * 0.3f);
        mPaint.setTextSize(textSize);

    }

    @Override
    protected void onAddedToWorld() {
        super.onAddedToWorld();
        getWorld().onScoreTextActorAdded(this);
    }

    @Override
    protected void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        getWorld().onScoreTextActorRemoved(this);
    }
}
