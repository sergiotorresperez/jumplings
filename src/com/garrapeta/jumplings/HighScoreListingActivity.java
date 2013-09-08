package com.garrapeta.jumplings;

import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.garrapeta.gameengine.utils.L;
import com.garrapeta.jumplings.actor.PremiumPurchaseHelper;
import com.garrapeta.jumplings.comms.BackendConnectionException;
import com.garrapeta.jumplings.comms.BackendConnector;
import com.garrapeta.jumplings.comms.BackendConnectorCallback;
import com.garrapeta.jumplings.comms.RequestFactory;
import com.garrapeta.jumplings.comms.RequestModel;
import com.garrapeta.jumplings.comms.ResponseModel;
import com.garrapeta.jumplings.flurry.FlurryHelper;
import com.garrapeta.jumplings.ui.JumplingsToast;
import com.garrapeta.jumplings.util.Utils;

@SuppressWarnings("deprecation")
public class HighScoreListingActivity extends TabActivity implements OnTabChangeListener {

    // ----------------------------------------------------------------
    // Constantes
	
	// Log tag
	private static final String TAG = HighScoreListingActivity.class.getSimpleName();

	// tabs ids
    public static final String TAB_LOCALSCORES_ID = "tab_local_id";
    public static final String TAB_GLOBALSCORES_ID = "tab_global_id";

	// key to pass the world size through activities
	private static final String WORLD_WIDTH_EXTRA_KEY = "worldWidth";
	private static final String WORLD_HEIGHT_EXTRA_KEY = "worldHeight";
	
    // -----------------------------------------------------------------
    // Variables

    private List<Score> mLocalScoreList;
    private List<Score> mGlobalScoreList;

    private ListView mLocalHighScoresView;
    private ListView mGlobalHighScoresView;

    private Button mSubmitScoresBtn;
    private Button mClearScoresBtn;
    private TabHost mTabHost;
    private ProgressBar mProgressBar;

    // -------------------------------------------------------- Variables
    // est�ticas

    // -------------------------------------------------------- M�todos de
    // Activity

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (L.sEnabled) Log.i(JumplingsApplication.TAG, "onCreate " + this);

        // Lectura de datos persistentes
        mLocalScoreList = PermData.getLocalScoresList(this);
        mGlobalScoreList = PermData.getGlobalScoresList(this);

        setContentView(R.layout.activity_highscores);

        mProgressBar = (ProgressBar) findViewById(R.id.highscoresListing_progress_bar);
        setHttpRequestProgressBarVisible(false);
        
        // Preparaci�n de Tabs
        mTabHost = getTabHost();

        // NOTE:
        // http://ondrejcermak.info/programovani/custom-tabs-in-android-tutorial/comment-page-1/
        {
            View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
            TextView indicatorTextView = (TextView) tabIndicator.findViewById(R.id.custom_tab_indicator_text);
            indicatorTextView.setText(R.string.highscores_local_scores);

            TabSpec spec = mTabHost.newTabSpec(TAB_LOCALSCORES_ID);
            spec.setContent(R.id.highscoresListing_localScoresTabContent);
            spec.setIndicator(tabIndicator);
            mTabHost.addTab(spec);
        }

        {
            View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
            TextView indicatorTextView = (TextView) tabIndicator.findViewById(R.id.custom_tab_indicator_text);
            indicatorTextView.setText(R.string.highscores_global_scores);

            TabSpec spec = mTabHost.newTabSpec(TAB_GLOBALSCORES_ID);
            spec.setContent(R.id.highscoresListing_globalScoresTabContent);
            spec.setIndicator(tabIndicator);
            mTabHost.addTab(spec);
        }

        getTabWidget().setStripEnabled(true);

        mTabHost.setOnTabChangedListener(this);

        mTabHost.setCurrentTabByTag(TAB_LOCALSCORES_ID);

        // Preparaci�n de contenido de tab local

        mLocalHighScoresView = (ListView) findViewById(R.id.highscoresListing_localHighScoresListView);

        // Se rellenan los scores locales
        feedLocalHighScoresView();

        // Preparaci�n de contenido de tab global

        // Se elimina la columna Global Rank en header de lista global
        findViewById(R.id.highscoresListing_globalScoresTabContent).findViewById(R.id.scoreHeader_globalRank).setVisibility(View.INVISIBLE);

        // Se rellenan los scores globales
        mGlobalHighScoresView = (ListView) findViewById(R.id.highscoresListing_globalHighScoresListView);
        feedGlobalHighScoresView();

        // Preparaci�n de los botones

        Button backBtn = (Button) findViewById(R.id.highscoresListing_backBtn);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (PermData.areDebugFeaturesEnabled(this)) {
            mClearScoresBtn = (Button) findViewById(R.id.highscoresListing_clearLocalScoresBtn);
            mClearScoresBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PermData.clearAll(HighScoreListingActivity.this);
                    updateSubmitScoresBtnVisibility();
                    mLocalScoreList = PermData.getLocalScoresList(HighScoreListingActivity.this);
                    feedLocalHighScoresView();
                }
            });
            mClearScoresBtn.setVisibility(View.VISIBLE);
        }

        mSubmitScoresBtn = (Button) findViewById(R.id.highscoresListing_submitBtn);
        mSubmitScoresBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                submitScores();
            }
        });
        updateSubmitScoresBtnVisibility();

        
		// Ads
        final boolean showAds;
		if (PermData.areAdsEnabled(this)) {
			final PremiumPurchaseHelper premiumHelper  = new PremiumPurchaseHelper(this);
			showAds = (premiumHelper.isPremiumPurchaseStateKnown(this) && !premiumHelper.isPremiumPurchased(this));
			premiumHelper.dispose();
		} else {
			showAds = false;
		}
		findViewById(R.id.highscoresListing_advertising_banner_view).setVisibility(showAds ? View.VISIBLE : View.GONE);
        
		if (Utils.isNetworkAvailable(this)) {
        	downloadScores();
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        FlurryHelper.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryHelper.onEndSession(this);
    }
    
    // ---------------------------------------------- M�todos propios

    /**
     * Alimenta la lista de scores locales
     */
    private void feedLocalHighScoresView() {

        CustomAdapter adapter = new CustomAdapter(mLocalScoreList, true);
        mLocalHighScoresView.setAdapter(adapter);

        mLocalHighScoresView.setCacheColorHint(0xFFFFFFFF);
    }

    /**
     * Alimenta la lista de scores globales
     */
    private void feedGlobalHighScoresView() {

        CustomAdapter adapter = new CustomAdapter(mGlobalScoreList, false);
        mGlobalHighScoresView.setAdapter(adapter);

        mGlobalHighScoresView.setCacheColorHint(0xFFFFFFFF);
    }

    /**
     * Sube los scores locales al servidor
     */
    private void submitScores() {
		if (L.sEnabled) Log.i(TAG, "Submitting local scores");
		
		try {
			FlurryHelper.logScoresSubmitted();
	    	
	    	// get world size
	    	final float worldWidth = getWorldWidth(this);
	    	final float worldHeight = getWorldHeight(this);
	    	if (worldWidth <= 0 || worldHeight <= 0) {
	    		throw new IllegalStateException("Screen size is unavailable");
	    	}
	    	
	    	
	    	// Prepare
	    	RequestModel request = RequestFactory.createSubmitScoresRequestModel(this, mLocalScoreList, worldWidth, worldHeight);
	    	
			// Show progress bar
	    	setHttpRequestProgressBarVisible(true);
	    	
	    	// Send
	    	BackendConnector.postRequestAsync(this, request, new BackendConnectorCallback() {
	    	 	@Override
	    		public void onBackendRequestSuccess(ResponseModel response) {
	    	 		setHttpRequestProgressBarVisible(false);
	    	 		try {
	    	            manageBackendResponse(response);
	    	            onScoresSubmitted();
	    	        } catch (JSONException e) {
	    	        	manageScoresDownloadError(new BackendConnectionException(BackendConnectionException.ErrorType.CLIENT_ERROR, "Could not parse server response", e));
	    	        }
	    		}
				@Override
	    		public void onBackendRequestError(BackendConnectionException error) {
    	            setHttpRequestProgressBarVisible(false);
    	            manageScoresDownloadError(error);
	    		}});
		} catch (Exception error) {
			String srt = "Error preparing the scores submission payload";
			if (L.sEnabled) Log.e(TAG, srt, error);
			manageScoresSubmissionError(new BackendConnectionException(BackendConnectionException.ErrorType.CLIENT_ERROR, srt, error));
		}
    }

    /**
     * Ejecutado cuando los scores se han mandado correctamente al servidor
     * 
     */
    private void onScoresSubmitted() {
        // Los scores se han mandado al servidor
    	JumplingsToast.show(this, R.string.highscores_submit_score_ok, JumplingsToast.LENGTH_LONG);
        
        PermData.setLocalScoresSubmissionPending(this, false);
        updateSubmitScoresBtnVisibility();
    }

	/**
	 * Processes the response of the server
	 * @param response
	 * @throws JSONException
	 */
	private void manageBackendResponse(ResponseModel response) throws JSONException {
     	onRankingUpdated(response.localScores);
        onScoresUpdated(response.globalScores);
	}
	
    /**
     * Actualiza los scores del servidor
     */
    private void downloadScores() {
		if (L.sEnabled) Log.i(TAG, "Requesting global scores update. ");
		
    	try {
	    	// get world size
	    	final float worldWidth = getWorldWidth(this);
	    	final float worldHeight = getWorldHeight(this);
	    	if (worldWidth <= 0 || worldHeight <= 0) {
	    		throw new IllegalStateException("Screen size is unavailable");
	    	}
	    	
	    	// Prepare
	    	RequestModel request = RequestFactory.createDownloadScoresRequestModel(this, mLocalScoreList, worldWidth, worldHeight);
	    	
			// Show progress bar
	    	setHttpRequestProgressBarVisible(true);
	    	
	    	// Send
	    	BackendConnector.postRequestAsync(this, request, new BackendConnectorCallback() {
	    	 	@Override
	    		public void onBackendRequestSuccess(ResponseModel response) {
	    	 		setHttpRequestProgressBarVisible(false);
	    	 		try {
	    	            manageBackendResponse(response);
	    	        } catch (JSONException e) {
	    	        	manageScoresDownloadError(new BackendConnectionException(BackendConnectionException.ErrorType.CLIENT_ERROR, "Could not parse server response", e));
	    	        }
	    		}
	
	    		@Override
	    		public void onBackendRequestError(BackendConnectionException error) {
	    			setHttpRequestProgressBarVisible(false);
	    			manageScoresDownloadError(error);
	    		}});
		} catch (Exception error) {
			manageScoresDownloadError(new BackendConnectionException(BackendConnectionException.ErrorType.CLIENT_ERROR, "Error preparing the scores download payload", error));
		}
    }

    /**
     * Ejecutado cuando los scores se han actualizado correctamente del servidor
     * 
     * @param globalScores
     * @throws JSONException
     */
    private void onScoresUpdated(List<Score> globalScores) throws JSONException {
    	if (globalScores == null) {
    		return;
    	}
    	
        // copiamos la lista de scores goblales
        mGlobalScoreList = globalScores;
        // la salvamos
        PermData.saveGlobalScoresList(this, mGlobalScoreList);
        // rellenamos la tabla de scores globales
        feedGlobalHighScoresView();
    }

    /**
     * Ejecutado cuando recibimos una actualizacion del ranking de los scores
     * locales
     * 
     * @param localScores
     * @throws JSONException
     */
    private void onRankingUpdated(List<Score> localScores) throws JSONException {
    	if (localScores == null) {
    		return;
    	}
        // para cada elemento recibido del server..
        for (Score aux : localScores) {
        	
            // buscamos el score en la lista local
            for (Score local : mLocalScoreList) {
                if (local.clientId.equals(aux.clientId)) {
                    local.globalRank = aux.globalRank;
                    break;
                }
            }
        }

        // salvamos la lista local
        PermData.saveLocalScoresList(this, mLocalScoreList);
        // rellenamos la tabla de scores locales
        feedLocalHighScoresView();
    }

    private void updateSubmitScoresBtnVisibility() {
        if (mTabHost.getCurrentTabTag() == TAB_LOCALSCORES_ID && PermData.isLocalScoresSubmissionPending(this)) {
            mSubmitScoresBtn.setVisibility(View.VISIBLE);
        } else {
            mSubmitScoresBtn.setVisibility(View.GONE);
        }
	}

    private void setHttpRequestProgressBarVisible(boolean visible) {
        if (visible) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void manageScoresDownloadError(BackendConnectionException e) {
    	FlurryHelper.onErrorScoreDownloadError(e);
    	if (L.sEnabled) Log.e(TAG, "Error downloading scores", e);
		notifyError(R.string.highscores_error_download_score, e.getErrorType());
    }

    private void manageScoresSubmissionError(BackendConnectionException e) {
    	FlurryHelper.onErrorScoreSubmissionError(e);
     	if (L.sEnabled) Log.e(TAG, "Error submitting scores", e);
    	notifyError(R.string.highscores_error_submit_score, e.getErrorType());
    }

    private void notifyError(int errorMessageResId, BackendConnectionException.ErrorType errorType) {
    	if (errorType != null) {
    		switch(errorType) {
    		case NO_CONNECTION_ERROR:
    			notifyError(errorMessageResId, R.string.highscores_error_no_connection);
    			return;
    		case IO_ERROR:
    			notifyError(errorMessageResId, R.string.highscores_error_io_error);
    			return;
    		case SERVER_ERROR:
    			notifyError(errorMessageResId, R.string.highscores_error_server_error);
    			return;
    		case CLIENT_ERROR:
    		case HTTP_ERROR:
    		}
    	}
    	
    	notifyError(getString(errorMessageResId));
    }

    private void notifyError(int errorMessageResId, int errorCauseResId) {
    	notifyError(getString(errorMessageResId) + ". " + getString(errorCauseResId));
    }

    private void notifyError(String message) {
    	JumplingsToast.show(this, message, JumplingsToast.LENGTH_LONG);
    }
    
    // -------------------------------------------------OnTabChangeListener methods

    @Override
    public void onTabChanged(String tabId) {
        updateSubmitScoresBtnVisibility();
        
        if (TAB_LOCALSCORES_ID.equals(tabId)) {
            if (PermData.areDebugFeaturesEnabled(this)) {
                mClearScoresBtn.setVisibility(View.VISIBLE);
            }

        } else if (TAB_GLOBALSCORES_ID.equals(tabId)) {
            if (PermData.areDebugFeaturesEnabled(this)) {
                mClearScoresBtn.setVisibility(View.GONE);
            }
        }
    }

    /**
     * @param activity
     * @return the width of the game world (retrieved from the passed Bundle), or 0.0f if it is unavailable
     */
    static float getWorldWidth(Activity activity) {
    	Bundle extras = activity.getIntent().getExtras();
    	if (extras == null) {
    		return 0;
    	}
    	return extras.getFloat(WORLD_WIDTH_EXTRA_KEY);
    }
    
    /**
     * Puts in the intent extra information with the screen size, or 0.0f if it is unavailable
     * @param intent
     * @param worldWidth
     * @param worldHeight
     */
    static void putScreenSizeExtras(Intent intent, float worldWidth, float worldHeight) {
    	intent.putExtra(HighScoreListingActivity.WORLD_WIDTH_EXTRA_KEY, Math.abs(worldWidth));
    	intent.putExtra(HighScoreListingActivity.WORLD_HEIGHT_EXTRA_KEY, Math.abs(worldHeight));
    }
   
    /**
     * @param activity
     * @return the height of the game world (retrieved from the passed Bundle), or 0.0f if it is unavailable
     */
     static float getWorldHeight(Activity activity) {
    	Bundle extras = activity.getIntent().getExtras();
    	if (extras == null) {
    		return 0;
    	}
    	return extras.getFloat(WORLD_HEIGHT_EXTRA_KEY);
    }

    // -------------------------------------------------------- Internal classes

    /**
     * Highscores list adapter
     * 
     * @author GaRRaPeTa
     */
    private class CustomAdapter extends BaseAdapter {

        // --------------------------------------- Variables de instancia

        // lista que alimenta la tabla
        private List<Score> list;
        // si es para la lista local o global
        private boolean local;

        // -------------------------------------------------- Constructor

        /**
         * @param list
         */
        public CustomAdapter(List<Score> list, boolean local) {
            this.list = list;
            this.local = local;
        }

        // ---------------------------------------- M�todes de BaseAdapter
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                // convertView = inflater.inflate(R.layout.score_item, parent,
                // false);
                LayoutInflater inflater = (LayoutInflater) HighScoreListingActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item_score, parent, false);
            }

            Score hs = list.get(position);

            {
                // index
                TextView view = (TextView) convertView.findViewById(R.id.scoreItem_index);
                view.setText(String.valueOf(position + 1 + "."));
            }

            {
                // player name
                TextView view = (TextView) convertView.findViewById(R.id.scoreItem_playerName);
                view.setText(String.valueOf(hs.playerName));
            }

            {
                // global rank
                if (local) {
                    String str = (hs.globalRank == 0) ? "?" : String.valueOf(hs.globalRank);
                    TextView view = (TextView) convertView.findViewById(R.id.scoreItem_globalRank);
                    view.setText(String.valueOf(str));
                } else {
                    convertView.findViewById(R.id.scoreItem_globalRank).setVisibility(View.INVISIBLE);
                }
            }

            {
                // level
                TextView view = (TextView) convertView.findViewById(R.id.scoreItem_level);
                view.setText(String.valueOf(hs.level));
            }

            {
                // score
                TextView view = (TextView) convertView.findViewById(R.id.scoreItem_score);
                view.setText(String.valueOf(hs.score));
            }
            return convertView;
        }

    }

}
