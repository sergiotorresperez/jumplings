package net.garrapeta.demo;


import com.openfeint.api.OpenFeint;
import com.openfeint.api.OpenFeintDelegate;
import com.openfeint.api.resource.CurrentUser;
import com.openfeint.api.resource.User;
import com.openfeint.api.ui.Dashboard;

import net.garrapeta.demo.wave.CampaignSurvivalWave;
import net.garrapeta.demo.wave.IntroWave;
import net.garrapeta.gameengine.GameView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;



public class MenuActivity extends JumplingsActivity {
    
    // ----------------------------------------------------------------- Constantes
	
	// ----------------------------------------------------- Variables estáticas
	
	// ----------------------------------------------------- Variables de instancia
	
	ImageButton feintLeaderBoardBtn;

	// ------------------------------------------- Variables de configuración
	
	// ---------------------------------------------------- Métodos estáticos
	
	// -------------------------------------------------- Métodos de Activity
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Preparación ventana
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
        
        // Preparación de la UI
		setContentView(R.layout.menu);
		
		// OPEN FEINT
		if (JumplingsApplication.FEINT_ENABLED) {
			OpenFeint.initialize(this, JumplingsApplication.feintSettings, new OpenFeintDelegate() {
				@Override
				public void userLoggedIn(CurrentUser user) {
					super.userLoggedIn(user);
					enableFeintLeaderboardButton();
				}
	
				@Override
				public void userLoggedOut(User user) {
					super.userLoggedOut(user);
					disableFeintLeaderboardButton();
				}
	
				@Override
				public void onDashboardAppear() {
					super.onDashboardAppear();
				}
	
				@Override
				public void onDashboardDisappear() {
					super.onDashboardDisappear();
				}
	
				@Override
				public boolean showCustomApprovalFlow(Context ctx) {
					return super.showCustomApprovalFlow(ctx);
				}}
			);
		}
		
		Button startBtn = (Button) findViewById(R.id.menu_playBtn);
		startBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startNewGame();
			}
		});
		
		if (JumplingsApplication.DEBUG_ENABLED) {
			Button testBtn = (Button) findViewById(R.id.menu_testBtn);
			testBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startTest();
				}
			});
			testBtn.setVisibility(View.VISIBLE);
			
			Button exitBtn = (Button) findViewById(R.id.menu_exitBtn);
			exitBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			exitBtn.setVisibility(View.VISIBLE);
		}
		
		ImageButton highScoresBtn = (ImageButton) findViewById(R.id.menu_highScoresBtn);
		highScoresBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showHighScores();
			}
		});
		
		if (JumplingsApplication.FEINT_ENABLED) {
			feintLeaderBoardBtn = (ImageButton) findViewById(R.id.menu_feintLeaderBoardBtn);
			feintLeaderBoardBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showFeintLeaderboard();
				}
			});
		}

		ImageButton preferencesBtn = (ImageButton) findViewById(R.id.menu_preferencesBtn);
		preferencesBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPreferences();
			}
		});
		
		// Ads
		if (JumplingsApplication.MOBCLIX_ENABLED) {
			 findViewById(R.id.menu_advertising_banner_view).setVisibility(View.VISIBLE);
		}
		
    }
 	
	@Override
	protected void onStart() {
		super.onStart();
		Log.i(JumplingsApplication.LOG_SRC,"onStart " + this);
		
		// Si ahora está logeado se activa el botón de Feint
		if (JumplingsApplication.FEINT_ENABLED && OpenFeint.isUserLoggedIn()) {
			enableFeintLeaderboardButton();
		}
		
		jWorld   = new JumplingsWorld(this, (GameView) findViewById(R.id.menu_gamesurface));
		jWorld.setFPS(60);
		jWorld.setDrawDebugInfo(JumplingsApplication.DEBUG_ENABLED);
		
		// Preparación de la wave
		
		jWorld.wave = new IntroWave(jWorld, null);
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i(JumplingsApplication.LOG_SRC,"onRestart " + this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.i(JumplingsApplication.LOG_SRC,"onPause " + this);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i(JumplingsApplication.LOG_SRC,"onResume " + this);
	}

		
	@Override
	protected void onStop() {
		super.onStop();
		
		Log.i(JumplingsApplication.LOG_SRC,"onStop " + this);

		destroyGame();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(JumplingsApplication.LOG_SRC,"onDestroy " + this);
	}
	


	
	// ---------------------------------------------------- Métodos propios
	
	// ---------------------------- Métodos de componentes de interacción
	
	void startTutorial() {
//		Intent i = new Intent(this, ShapelingsGameActivity.class);
//		i.putExtra(ShapelingsGameActivity.WAVE_BUNDLE_KEY, CampaignTutorialWave.WAVE_KEY);
//		i.putExtra(ShapelingsGameActivity.BUTTONS_COUNT_BUNDLE_KEY, 2);
//		startActivity(i);
	}
    
    
	void startNewGame() {
		Intent i = new Intent(this, JumplingsGameActivity.class);
		i.putExtra(JumplingsGameActivity.WAVE_BUNDLE_KEY, CampaignSurvivalWave.WAVE_KEY);
		startActivity(i);
	}
	
	void startTest() {
//		Intent i = new Intent(this, JumplingsGameActivity.class);
//		i.putExtra(JumplingsGameActivity.WAVE_BUNDLE_KEY, TestWave.WAVE_KEY);
//		startActivity(i);
		Intent i = new Intent(this, GameOverActivity.class);
		startActivity(i);
	}
	
	void showHighScores() {
		Intent i = new Intent(this, HighScoreListingActivity.class);
		startActivity(i);
	}
		
	void showPreferences() {
		Intent i = new Intent(this, PreferencesActivity.class);
		startActivity(i);
	}
	
	void enableFeintLeaderboardButton() {
		feintLeaderBoardBtn.setVisibility(View.VISIBLE);		
	}
	
	void disableFeintLeaderboardButton() {
		feintLeaderBoardBtn.setVisibility(View.GONE);		
	}
	
	void showFeintLeaderboard() {
    	Dashboard.openLeaderboard(GameOverActivity.feintLeaderboardId);
	}
	

}