package net.garrapeta.jumplings.actor;

import net.garrapeta.gameengine.Actor;
import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.JumplingsGameWorld;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;

public abstract class TextActor extends Actor {

    // ----------------------------------------------------- Constantes

    /**
     * Z-Index del actor
     */
    public final static int Z_INDEX = 20;

    // ----------------------------------------- Variables de instancia

    private JumplingsGameWorld cWorld;

    protected String text;
    protected Paint paint;

    PointF worldPos;

    float longevity;

    float lifeTime = longevity;

    protected float yVel;

    // -------------------------------------------------- Constructores

    public TextActor(JumplingsGameWorld cWorld, PointF worldPos) {
        super(cWorld, Z_INDEX);
        this.cWorld = cWorld;
        this.worldPos = worldPos;

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextAlign(Align.CENTER);
        paint.setTypeface(JumplingsApplication.game_font);
    }

    // -------------------------------------------------------- M�todos

    @Override
    public void processFrame(float gameTimeStep) {
        worldPos.y += yVel * (gameTimeStep / 1000);

        lifeTime = Math.max(0, lifeTime - gameTimeStep);
        if (lifeTime <= 0) {
            mGameWorld.removeActor(this);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        int a = (int) ((lifeTime / longevity) * 255);
        paint.setAlpha(a);
        PointF screenPos = cWorld.viewport.worldToScreen(worldPos);
        canvas.drawText(text, screenPos.x, screenPos.y, paint);
    }

    // --------------------------------------------------- IAtomicActor

}
