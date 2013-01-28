package net.garrapeta.jumplings.actor;

import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.Player;
import android.graphics.PointF;

public class ScoreTextActor extends TextActor {

    // ----------------------------------------------------- Constantes

    /** Velocidad vertical, en unidades del mundo por segundo */
    public final static float DEFAULT_Y_VELOCITY = 15;

    /** Tiempo que permanece el actor en pantalla, en ms */
    public final static int DEFAULT_LONGEVITY = 250;

    /** Tiempo que permanece el actor en pantalla, en ms */
    public final static int BASE_FONTSIZE = 35;

    // ----------------------------------------- Variables de instancia

    // ----------------------------------------------------- Constructor

    public ScoreTextActor(JumplingsGameWorld cWorld, PointF worldPos, int score) {
        super(cWorld, worldPos);

        int level = (score / Player.BASE_POINTS);

        this.mText = "+" + score;

        this.mYVel = DEFAULT_Y_VELOCITY;

        this.mLongevity = DEFAULT_LONGEVITY + (level * 150);
        this.mLifeTime = mLongevity;

        float textSize = BASE_FONTSIZE + (level * 3);
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
