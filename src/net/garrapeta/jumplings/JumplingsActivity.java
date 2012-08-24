package net.garrapeta.jumplings;


import net.garrapeta.jumplings.ui.AdDialogFactory;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;



public abstract class JumplingsActivity extends Activity {
    
    // ----------------------------------------------------------------- Constantes
	

	// ----------------------------------------------------- Variables est�ticas

	// ----------------------------------------------------- Variables de instancia
	
	/**
	 *  Mundo
	 */
	JumplingsWorld mWorld; 
	
	
	//TODO: declared here but used only in the game activity
	public boolean soundOn;
	
    
	// ---------------------------------------------------- M�todos est�ticos
			
	// -------------------------------------------------- M�todos de Activity
	
	/** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.i(JumplingsApplication.LOG_SRC,"onCreate " + this);
		
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // TODO: parar mundo
    }

	// ------------------------------------------------------ M�todos propios
	


    // ------------------------------ M�todos de gesti�n del estado del mundo
	
	/**
	 * Destruye el mundo
	 */
	void destroyGame() {
		mWorld.stopRunning();
		mWorld.dispose();
	}
	

}