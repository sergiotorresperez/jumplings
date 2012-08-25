package net.garrapeta.jumplings.ui;

import net.garrapeta.jumplings.GameActivity;
import net.garrapeta.jumplings.JumplingsApplication;
import net.garrapeta.jumplings.R;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mobclix.android.sdk.MobclixAdView;
import com.mobclix.android.sdk.MobclixAdViewListener;

public class AdDialogFactory implements MobclixAdViewListener, Runnable {
	
	// ---------------------------------------------------------- Constantes
	
	private int NEGATIVE_BUTTON_DISABLED_TIME = 5;
	
	private static final String NEGATIVE_BUTTON_STR = "Play";

	// ------------------------------------------------- Variables est�ticas
	
	private static AdDialogFactory instance;
	
	// --------------------------------------------------- Variables privadas

	private GameActivity cActivity; 
	
	private View adDialogView;
	
	private boolean available = false;
	
	private Dialog adDialog;
	
	private MobclixAdView adView;
	
	private Button buyBtn;
	private Button continueBtn; 
		
	
	// ---------------------------------------------------- M�todos est�ticos
	
	public static AdDialogFactory getInstance() {
		if (instance == null) {
			instance = new AdDialogFactory();
		}
		return instance;
	}
	
	// ---------------------------------------------------------- Constructor
	
	/**
	 * Constructor privado
	 */
	private AdDialogFactory() {
		
	}
	
	// ------------------------------------------------- M�todos de instancia
	
	public void init(final GameActivity cActivity) {
		this.cActivity = cActivity;
		
		LayoutInflater inflater = (LayoutInflater) cActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		adDialogView = inflater.inflate(R.layout.dialog_ad,  null);
		
		buyBtn = (Button) adDialogView.findViewById(R.id.addialog_buy);
		continueBtn = (Button) adDialogView.findViewById(R.id.addialog_continue);

		adView = (MobclixAdView) adDialogView.findViewById(R.id.adddialog_advertising_rectangle_view);
		adView.addMobclixAdViewListener(AdDialogFactory.this);
		adView.setAllowAutoplay(false);
		adView.pause();
		adView.getAd();
		
	}
	
	public Dialog createAdDialogView() {
		 if (available) {
			if (adDialog == null) {
				adDialog = new Dialog(cActivity, R.style.CustomDialog);
				adDialog.setContentView(adDialogView);
				
				adDialog.setCancelable(false);

				if (JumplingsApplication.MOBCLIX_BUY_DIALOG_BUTTON_ENABLED) {
					buyBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							cActivity.dismissDialog(GameActivity.DIALOG_AD_ID);
							Toast toast = Toast.makeText(cActivity, "TODO: buy app", Toast.LENGTH_SHORT);
							toast.show();
							cActivity.getWorld().resume();
						}
					});
				}
				
				continueBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
					    cActivity.getWorld().resume();
						cActivity.dismissDialog(GameActivity.DIALOG_AD_ID);
						adView.getAd();
					}
				});
				
				adDialog.setOnShowListener(new OnShowListener() {
					@Override
					public void onShow(DialogInterface dialog) {
						if (cActivity.getWorld().isStarted()) {
						    cActivity.getWorld().pause();
						}
						continueBtn.setEnabled(false);
						new Thread(AdDialogFactory.this).start();
					}
					
				});
			}
			
			return adDialog; 
			
		 } else {
			 return null;
		 }
	}

	// ------------------------------------------- M�todos de listaners de di�logos
	

	
	// ---------------------------------------------------------- M�todos de Runnable
	
	@Override
	public void run() {
		for (int i = NEGATIVE_BUTTON_DISABLED_TIME; i > 0; i--) {
			try {
				Thread.sleep(1000);
				final int n = i;
				cActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						continueBtn.setText(NEGATIVE_BUTTON_STR + " (" + n + ")"); 
					}
				});							
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		cActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				continueBtn.setText(NEGATIVE_BUTTON_STR); 
				continueBtn.setEnabled(true); 
			}
		});		

	}
			
	// --------------------------------------------------- M�todos de MobclixAdViewListener
	
	@Override
	public void onSuccessfulLoad(MobclixAdView ad) {
		available = true;
	}
	
	@Override
	public void onFailedLoad(MobclixAdView ad, int error) {
		available = false;
	}
	
	
	@Override
	public String keywords() {
		return null;
	}

	@Override
	public void onAdClick(MobclixAdView arg0) {
	}

	@Override
	public void onCustomAdTouchThrough(MobclixAdView arg0, String arg1) {
	}

	@Override
	public boolean onOpenAllocationLoad(MobclixAdView arg0, int arg1) {
		return false;
	}

	@Override
	public String query() {
		return null;
	}



}
