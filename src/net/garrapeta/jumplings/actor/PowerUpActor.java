package net.garrapeta.jumplings.actor;

import java.util.ArrayList;

import net.garrapeta.gameengine.Viewport;
import net.garrapeta.gameengine.module.BitmapManager;
import net.garrapeta.jumplings.JumplingsGameWorld;
import net.garrapeta.jumplings.R;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

public abstract class PowerUpActor extends MainActor {

    // ----------------------------------------------------------- Constantes
    public final static float DEFAULT_RADIUS = BASE_RADIUS * 1.05f;

    // ------------------------------------------------- Variables est�ticas

    // vivo
    protected final static int BMP_POWERUP_BG = R.drawable.powerup_bg;

    // debris
    protected final static int BMP_DEBRIS_POWERUP_BG = R.drawable.powerup_debris_bg;

    // ---------------------------------------------- Variables de instancia

    protected Body iconBody;

    // Bitmaps del actor vivo
    protected Bitmap bmpBg;
    protected Bitmap bmpIcon;

    // Bitmaps del actor muerto (debris)
    protected Bitmap bmpDebrisBg;
    protected Bitmap bmpDebrisIcon;

    // --------------------------------------------------- Constructor

    public PowerUpActor(JumplingsGameWorld mJWorld, PointF worldPos) {
        super(mJWorld, worldPos, BladePowerUpActor.DEFAULT_RADIUS, Z_INDEX);

        // vivo
        BitmapManager mb = mJWorld.getBitmapManager();
        bmpBg = mb.getBitmap(BMP_POWERUP_BG);
        // debris
        bmpDebrisBg = mb.getBitmap(BMP_DEBRIS_POWERUP_BG);
    }

    // --------------------------------------------- M�todos heredados

    @Override
    protected void initBodies(PointF worldPos) {

        // Cuerpo
        {
            // Create Shape with Properties
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(mRadius);
            mainBody = mJWorld.createBody(this, worldPos, true);
            mainBody.setBullet(true);

            // Assign shape to Body
            Fixture f = mainBody.createFixture(circleShape, 1.0f);
            f.setFilterData(CONTACT_FILTER);
            circleShape.dispose();

        }

        // Icon
        {
            // Create Shape with Properties
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.setAsBox(mRadius, mRadius);
            PointF pos = new PointF(worldPos.x, worldPos.y);
            iconBody = mJWorld.createBody(this, pos, true);
            iconBody.setBullet(false);

            // Assign shape to Body
            Fixture f = iconBody.createFixture(polygonShape, 1.0f);
            f.setRestitution(AUX_BODIES_RESTITUTION);
            f.setFilterData(NO_CONTACT_FILTER);
            polygonShape.dispose();

            // Uni�n
            WeldJointDef jointDef = new WeldJointDef();

            jointDef.initialize(mainBody, iconBody, Viewport.pointFToVector2(pos));

            mJWorld.createJoint(this, jointDef);
        }
    }

    @Override
    protected ArrayList<JumplingActor> getDebrisBodies() {
        ArrayList<JumplingActor> debrisActors = new ArrayList<JumplingActor>();

        // Main Body
        {
            Body body = mainBody;
            DebrisActor debrisActor = new DebrisActor(mJWorld, body, bmpDebrisBg);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        // Icon
        {
            Body body = iconBody;
            DebrisActor debrisActor = new DebrisActor(mJWorld, body, bmpDebrisIcon);

            mGameWorld.addActor(debrisActor);
            debrisActors.add(debrisActor);
        }

        return debrisActors;
    }

    @Override
    protected void drawBitmaps(Canvas canvas) {
        mJWorld.drawBitmap(canvas, this.mainBody, bmpBg);
        mJWorld.drawBitmap(canvas, this.iconBody, bmpIcon);
    }

    // ------------------------------------------------ M�todos propios

}
