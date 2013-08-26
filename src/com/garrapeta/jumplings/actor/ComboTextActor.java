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

    private static float sBaseTextSize = -1;
    
    // ----------------------------------------- Variables de instancia

    // ----------------------------------------------------- Constructor

    public ComboTextActor(JumplingsGameWorld jgWorld, PointF worldPos, int level) {
        super(jgWorld, worldPos);

        mText = jgWorld.mActivity.getString(R.string.game_combo, level);
        mYVel = DEFAULT_Y_VELOCITY;

        mLongevity = DEFAULT_LONGEVITY + ((level - 1) * 75);
        mLifeTime = mLongevity;

        if (sBaseTextSize < 0) {
        	sBaseTextSize = jgWorld.mActivity.getResources().getDimension(R.dimen.ingame_combo_text_actor_base_text_size);
        }
        float textSize = sBaseTextSize +  (sBaseTextSize * (level - 1)  * 0.3f);
        
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
