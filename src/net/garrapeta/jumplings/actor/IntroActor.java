package net.garrapeta.jumplings.actor;

import net.garrapeta.jumplings.JumplingsWorld;
import net.garrapeta.jumplings.R;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;

public class IntroActor extends JumplingActor {

    // ----------------------------------------------------------- Constantes
    public final static float DEFAULT_RADIUS = BASE_RADIUS * 3f;

    /**
     * Z-Index del actor
     */
    public final static int Z_INDEX = 0;

    // ------------------------------------------------- Variables est�ticas

    // vivo
    protected final static int BMP_INTRO_BODY_ID = R.drawable.intro_body;

    protected final static int BMP_INTRO_FOOT_RIGHT_ID = R.drawable.intro_foot_right;
    protected final static int BMP_INTRO_FOOT_LEFT_ID = R.drawable.intro_foot_left;

    protected final static int BMP_INTRO_HAND_RIGHT_ID = R.drawable.intro_hand_right;
    protected final static int BMP_INTRO_HAND_LEFT_ID = R.drawable.intro_hand_left;

    protected final static int BMP_INTRO_EYE_RIGHT_ID = R.drawable.intro_eye_right;
    protected final static int BMP_INTRO_EYE_LEFT_ID = R.drawable.intro_eye_left;

    // ----------------------------------------------- Variables de instancia

    AnthropomorphicHelper ah;

    // Bitmaps del actor vivo
    protected Bitmap bmpBody;

    protected Bitmap bmpFootRight;
    protected Bitmap bmpFootLeft;

    protected Bitmap bmpHandRight;
    protected Bitmap bmpHandLeft;

    protected Bitmap bmpEyeRight;
    protected Bitmap bmpEyeLeft;

    // ----------------------------------------------- Inicializaci�n est�tica

    // --------------------------------------------------- Constructor

    public IntroActor(JumplingsWorld jWorld, PointF worldPos) {
        super(jWorld, IntroActor.DEFAULT_RADIUS, Z_INDEX, worldPos);
    }

    // --------------------------------------------- M�todos heredados

    @Override
    protected void initFields() {
        ah = new AnthropomorphicHelper(this);
     }

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

        ah.createLimbs(worldPos, mRadius);
    }

    @Override
    protected void initBitmaps() {
        // vivo
        ah.initBitmaps(BMP_INTRO_BODY_ID, BMP_INTRO_FOOT_RIGHT_ID, BMP_INTRO_FOOT_LEFT_ID, BMP_INTRO_HAND_RIGHT_ID, BMP_INTRO_HAND_LEFT_ID,
                BMP_INTRO_EYE_RIGHT_ID, BMP_INTRO_EYE_LEFT_ID);
    }
 
    @Override
    public void processFrame(float gameTimeStep) {
    }

    @Override
    public final void drawBodiesShapes(Canvas canvas) {
        ah.drawShapes(canvas);
    }

    @Override
    protected final void drawBitmaps(Canvas canvas) {
        ah.drawBitmaps(canvas);
    }

}
