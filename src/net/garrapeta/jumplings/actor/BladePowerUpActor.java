package net.garrapeta.jumplings.actor;

import net.garrapeta.jumplings.R;
import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.JumplingsGameWorld;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

public class BladePowerUpActor extends PowerUpActor {

	// ----------------------------------------------------------- Constantes
	
	public final static short JUMPER_CODE_POWER_UP_BLADE = 8;
	
	public  final static float  DEFAULT_RADIUS = BASE_RADIUS * 1.05f;
	
	// ------------------------------------------------- Variables est�ticas
	
	// vivo
	protected final static Bitmap BMP_SWORD;
	
	// debris
	protected final static Bitmap BMP_DEBRIS_SWORD;	
	
	// --------------------------------------------------- M�todos est�ticos
	
	// --------------------------------------------------- Constructor
	
	public BladePowerUpActor(JumplingsGameWorld jgWorld, PointF worldPos) {
		super(jgWorld, worldPos);
		
		this.code = BladePowerUpActor.JUMPER_CODE_POWER_UP_BLADE;
		
		// vivo
		bmpIcon 	  = BMP_SWORD;
		
		// debris
		bmpDebrisIcon = BMP_DEBRIS_SWORD;
	}

	// ----------------------------------------------- Inicializaci�n est�tica
	
	static {
		Resources r = JumplingsApplication.getInstance().getResources();

		// vivo
		BMP_SWORD		= BitmapFactory.decodeResource(r, R.drawable.powerup_sword);
		
		// muerto
		BMP_DEBRIS_SWORD	= BitmapFactory.decodeResource(r, R.drawable.powerup_debris_sword);
	}
	
	// ------------------------------------------------- M�todos est�ticos
	
	static double getBladePowerUpHitCount() {
		// Se le pone un valor negativo, para incentivar la aparici�n de enemigos
		return -1f;
	}
	
	// --------------------------------------------- M�todos heredados
	
	@Override
	public void doLogic(float gameTimeStep) {
	}

	@Override
	public void onHitted() {
		jgWorld.onBladePowerUp(this);
		super.onHitted();
	}


}
