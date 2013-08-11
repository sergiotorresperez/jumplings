package com.garrapeta.jumplings.actor;

import android.graphics.PointF;

import com.garrapeta.jumplings.JumplingsGameWorld;
import com.garrapeta.jumplings.R;

public class ComboTextActor extends TextActor {

    // ----------------------------------------------------- Constantes

    /** Velocidad vertical, en unidades del mundo por segundo */
    public static final float DEFAULT_Y_VELOCITY = ScoreTextActor.DEFAULT_Y_VELOCITY / 5;

    /** Tiempo que permanece el actor en pantalla, en ms */
    public final static int DEFAULT_LONGEVITY = 600;

    /** Tiempo que permanece el actor en pantalla, en ms */
    public final static int BASE_FONTSIZE = 36;

    // ----------------------------------------- Variables de instancia

    // ----------------------------------------------------- Constructor

    public ComboTextActor(JumplingsGameWorld cWorld, PointF worldPos, int comboLevel) {
        super(cWorld, worldPos);

        mText = cWorld.mActivity.getString(R.string.game_combo, comboLevel);
        mYVel = DEFAULT_Y_VELOCITY;

        mLongevity = DEFAULT_LONGEVITY + ((comboLevel - 1) * 75);
        mLifeTime = mLongevity;

        float textSize = BASE_FONTSIZE + ((comboLevel - 1) * 6);
        mPaint.setTextSize(textSize);
    }

    @Override
    protected void onAddedToWorld() {
        super.onAddedToWorld();
        getWorld().onComboTextActorAdded(this);
    }

    @Override
    protected void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        getWorld().onComboTextActorRemoved(this);
    }

}
