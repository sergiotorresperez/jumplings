package net.garrapeta.demo;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;



public abstract class JumplingsActivity extends Activity {
    
    // ----------------------------------------------------------------- Constantes
	
	// ----------------------------------------------------- Variables estáticas

	// ----------------------------------------------------- Variables de instancia
	
	/**
	 *  Mundo
	 */
	JumplingsWorld jWorld; 
	
	/**
	 *  Si el juego ha empezado 
	 */
	boolean worldStarted;
	
	
	// ---------------------------------------------------- Métodos estáticos
			
	// -------------------------------------------------- Métodos de Activity
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.i(JumplingsApplication.LOG_SRC,"onCreate " + this);
    }
    
	// ------------------------------------------------------ Métodos propios
	
    // ------------------------------ Métodos de gestión del estado del mundo
	
	/**
	 *  Comienza el mundo
	 */
	void startWorld() {
		Log.i(JumplingsApplication.LOG_SRC,"startNewGame " + this);
		
		worldStarted = true;
		
	 	// Se crea el mundo
		jWorld.create();
		
		// Se arranca el game loop
		jWorld.startLooping();
		// Se activa la lógica
		jWorld.play();
		// Se activa la wave
		jWorld.wave.start();
		
	}
	
		
	/**
	 *  @return si el mundo ha empezado
	 */
	boolean isWorldStarted() {
		return worldStarted;
	}
	
	/**
	 * Destruye el mundo
	 */
	void destroyGame() {
		worldStarted = false;
		
		jWorld.stopLooping();
		jWorld.dispose();
	}
	

}