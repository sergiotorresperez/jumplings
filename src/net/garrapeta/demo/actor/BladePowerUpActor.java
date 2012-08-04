package net.garrapeta.demo.actor;

import net.garrapeta.demo.JumplingsApplication;
import net.garrapeta.demo.JumplingsGameWorld;
import net.garrapeta.demo.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

public class BladePowerUpActor extends PowerUpActor {

	// ----------------------------------------------------------- Constantes
	
	public final static short JUMPER_CODE_POWER_UP_BLADE = 8;
	
	public  final static float  DEFAULT_RADIUS = BASE_RADIUS * 1.05f;
	
	// ------------------------------------------------- Variables estáticas
	
	// vivo
	protected final static Bitmap BMP_SWORD;
	
	// debris
	protected final static Bitmap BMP_DEBRIS_SWORD;	
	
	// --------------------------------------------------- Métodos estáticos
	
	// --------------------------------------------------- Constructor
	
	public BladePowerUpActor(JumplingsGameWorld jgWorld, PointF worldPos) {
		super(jgWorld, worldPos);
		
		this.code = BladePowerUpActor.JUMPER_CODE_POWER_UP_BLADE;
		
		// vivo
		bmpIcon 	  = BMP_SWORD;
		
		// debris
		bmpDebrisIcon = BMP_DEBRIS_SWORD;
	}

	// ----------------------------------------------- Inicialización estática
	
	static {
		Resources r = JumplingsApplication.getInstance().getResources();

		// vivo
		BMP_SWORD		= BitmapFactory.decodeResource(r, R.drawable.powerup_sword);
		
		// muerto
		BMP_DEBRIS_SWORD	= BitmapFactory.decodeResource(r, R.drawable.powerup_debris_sword);
	}
	
	// ------------------------------------------------- Métodos estáticos
	
	static double getBladePowerUpHitCount() {
		// Se le pone un valor negativo, para incentivar la aparición de enemigos
		return -1f;
	}
	
	// --------------------------------------------- Métodos heredados
	
	@Override
	public void doLogic(float gameTimeStep) {
	}

	@Override
	public void onHitted() {
		jgWorld.onBladePowerUp(this);
		super.onHitted();
	}


}
