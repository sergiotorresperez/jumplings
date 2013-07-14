package net.garrapeta.jumplings;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.garrapeta.gameengine.utils.IOUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.TabActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.openfeint.api.OpenFeint;
import com.openfeint.api.ui.Dashboard;

public class HighScoreListingActivity extends TabActivity implements  OnTabChangeListener {

    // ----------------------------------------------------------------
    // Constantes

    public static final String TAB_LOCALSCORES_ID = "tab_local_id";
    public static final String TAB_GLOBALSCORES_ID = "tab_global_id";

    // -----------------------------------------------------------------
    // Variables

    private List<HighScore> mLocalScoreList;
    private List<HighScore> mGlobalScoreList;

    private ListView mLocalHighScoresView;
    private ListView mGlobalHighScoresView;

    private Button mFeintLeaderBoardBtn;
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

        Log.i(JumplingsApplication.LOG_SRC, "onCreate " + this);

        // Lectura de datos persistentes
        mLocalScoreList = PermData.getInstance().getLocalScoresList();
        mGlobalScoreList = PermData.getInstance().getGlobalScoresList();

        // Preparaci�n de la UI
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.highscores_listing);

        mProgressBar = (ProgressBar) findViewById(R.id.highscoresListing_progress_bar);
        setHttpRequestProgressBarVisible(false);
        
        // Preparaci�n de Tabs
        mTabHost = getTabHost();

        // NOTE:
        // http://ondrejcermak.info/programovani/custom-tabs-in-android-tutorial/comment-page-1/
        {
            View tabIndicator = LayoutInflater.from(this).inflate(R.layout.custom_tab_indicator, getTabWidget(), false);
            TextView indicatorTextView = (TextView) tabIndicator.findViewById(R.id.custom_tab_indicator_text);
            indicatorTextView.setText("Local scores");

            TabSpec spec = mTabHost.newTabSpec(TAB_LOCALSCORES_ID);
            spec.setContent(R.id.highscoresListing_localScoresTabContent);
            spec.setIndicator(tabIndicator);
            mTabHost.addTab(spec);
        }

        {
            View tabIndicator = LayoutInflater.from(this).inflate(R.layout.custom_tab_indicator, getTabWidget(), false);
            TextView indicatorTextView = (TextView) tabIndicator.findViewById(R.id.custom_tab_indicator_text);
            indicatorTextView.setText("Global scores");

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

        if (JumplingsApplication.DEBUG_ENABLED) {
            mClearScoresBtn = (Button) findViewById(R.id.highscoresListing_clearLocalScoresBtn);
            mClearScoresBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PermData.getInstance().clearAll();
                    updateSubmitScoresBtnVisibility();
                    mLocalScoreList = PermData.getInstance().getLocalScoresList();
                    feedLocalHighScoresView();
                }
            });
            mClearScoresBtn.setVisibility(View.VISIBLE);
        }

        mSubmitScoresBtn = (Button) findViewById(R.id.highscoresListing_submitBtn);
        mSubmitScoresBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    submitScores();
                } else {
                    Toast.makeText(HighScoreListingActivity.this, "You need to be connected to the Internet to upload your scores", Toast.LENGTH_LONG).show();
                }
            }
        });
        updateSubmitScoresBtnVisibility();

        if (JumplingsApplication.FEINT_ENABLED) {
            mFeintLeaderBoardBtn = (Button) findViewById(R.id.highscoresListing_feintLeaderBoardBtn);
            mFeintLeaderBoardBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dashboard.openLeaderboard(GameOverActivity.feintLeaderboardId);
                }
            });

            if (OpenFeint.isUserLoggedIn()) {
                mFeintLeaderBoardBtn.setVisibility(View.VISIBLE);
            }
        }

        // Ads
        if (JumplingsApplication.MOBCLIX_ENABLED) {
            findViewById(R.id.highscoresListing_advertising_banner_view).setVisibility(View.VISIBLE);
        }

        if (isNetworkAvailable()) {
        	updateScores();
        }
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
		try {
	    	// Show progress bar
	    	setHttpRequestProgressBarVisible(true);

			JSONObject jsonObject = new JSONObject();
			jsonObject.put(HighScore.JSON_REQUEST_OBJ_STR, HighScore.JSON_REQUEST_OBJ_SUBMIT_VALUE);
	        jsonObject.put(HighScore.JSON_LOCALSCORES_ARRAY_STR, HighScore.formatJSON(mLocalScoreList));
	        String requestBody = jsonObject.toString();
	        
	    	Log.i(JumplingsApplication.LOG_SRC, "Submitting local score: " + requestBody);
	    	
	    	new ServerRequestAsyncTask().execute(requestBody);
	    	
		} catch (JSONException e) {
			Log.e(JumplingsApplication.LOG_SRC, "Error creating JSONs when submiting scores: " + e.toString(), e);
			notifyError("Error submitting score to server", e);
		}
    }

    /**
     * Ejecutado cuando los scores se han mandado correctamente al servidor
     * 
     * @param scores
     */
    private void onScoresSubmitted(JSONArray scores) {
        // Los scores se han mandado al servidor

        Toast toast = Toast.makeText(HighScoreListingActivity.this, "Scores submitted", Toast.LENGTH_LONG);
        toast.show();
        
        PermData.getInstance().setLocalScoresSubmissionPending(false);
        updateSubmitScoresBtnVisibility();

        // we update the global table, to see our new scores in it
        // TODO: receive the global scores in the response of the upload, so we
        // can skip this call
        if (isNetworkAvailable()) {
        	updateScores();
        }
    }

    /**
     * Actualiza los scores del servidor
     */
    private void updateScores() {
		try {
	    	// Show progress bar
	    	setHttpRequestProgressBarVisible(true);
	    	
            JSONObject jsonObject = new JSONObject();
            // action
            jsonObject.put(HighScore.JSON_REQUEST_OBJ_STR, HighScore.JSON_REQUEST_OBJ_RETRIEVE_VALUE);
            // local scores
            jsonObject.put(HighScore.JSON_LOCALSCORES_ARRAY_STR, HighScore.formatJSON(mLocalScoreList));
            String requestBody = jsonObject.toString();
	        
	    	Log.i(JumplingsApplication.LOG_SRC, "Requesting global scores update: " + requestBody);
	    	
	    	new ServerRequestAsyncTask().execute(requestBody);
	    	
		} catch (JSONException e) {
			Log.e(JumplingsApplication.LOG_SRC, "Error creating JSONs when updating scores: " + e.toString(), e);
			notifyError("Error updating scores from server", e);
		}
    }

    /**
     * Ejecutado cuando los scores se han actualizado correctamente del servidor
     * 
     * @param scores
     * @throws JSONException
     */
    private void onScoresUpdated(JSONArray scores) throws JSONException {
        // componemos la lista de scores goblales
        mGlobalScoreList = HighScore.parseJSON(scores);
        // la salvamos
        PermData.getInstance().saveGlobalScoresList(mGlobalScoreList);
        // rellenamos la tabla de scores globales
        feedGlobalHighScoresView();
    }

    /**
     * Ejecutado cuando recibimos una actualizaci�n del ranking de los scores
     * locales
     * 
     * @param scores
     * @throws JSONException
     */
    private void onRankingUpdated(JSONArray scores) throws JSONException {
        // componemos la lista de scores que nos ha dado el server
        List<HighScore> tmpScoreList = HighScore.parseJSON(scores);

        // para cada elemento recibido del server..
        for (int i = 0; i < tmpScoreList.size(); i++) {
            HighScore aux = tmpScoreList.get(i);

            // buscamos el score en la lista local
            for (int j = 0; j < mLocalScoreList.size(); j++) {
                HighScore local = mLocalScoreList.get(i);

                if (local.localId.equals(aux.localId)) {
                    local.globalRank = aux.globalRank;
                    break;
                }
            }
        }

        // salvamos la lista local
        PermData.getInstance().saveLocalScoresList(mLocalScoreList);
        // rellenamos la tabla de scores locales
        feedLocalHighScoresView();
    }

    private void updateSubmitScoresBtnVisibility() {
        if (mTabHost.getCurrentTabTag() == TAB_LOCALSCORES_ID && PermData.getInstance().isLocalScoresSubmissionPending()) {
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager  = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
 
    // FIXME: externalize and localize error message
    private void notifyError(String errorMessage, Exception error) {
    	setHttpRequestProgressBarVisible(false);
    	 Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
    
	private void manageServerResponse(HttpResponse response) throws IllegalStateException, IOException, JSONException, HttpException {
        // Dismiss progress dialog
        setHttpRequestProgressBarVisible(false);
  
		int code = response.getStatusLine().getStatusCode();

        HttpEntity er = response.getEntity();
        InputStream is;
        is = er.getContent();
        String responseString = IOUtils.getStringFromInputStream(is);

        Log.i(JumplingsApplication.LOG_SRC, "Response received = " + code + ". Response: " + responseString);

        if (code == 200) {

            
            JSONObject responseObj = new JSONObject(responseString);

            String responseAction = responseObj.get(HighScore.JSON_RESPONSE_OBJ_STR).toString();
            if (HighScore.JSON_REQUEST_OBJ_SUBMIT_VALUE.equals(responseAction)) {
                onScoresSubmitted(responseObj.getJSONArray(HighScore.JSON_LOCALSCORES_ARRAY_STR));
                onRankingUpdated(responseObj.getJSONArray(HighScore.JSON_LOCALSCORES_ARRAY_STR));
            } else if (HighScore.JSON_REQUEST_OBJ_RETRIEVE_VALUE.equals(responseAction)) {
                onScoresUpdated(responseObj.getJSONArray(HighScore.JSON_GLOBALSCORES_ARRAY_STR));
                onRankingUpdated(responseObj.getJSONArray(HighScore.JSON_LOCALSCORES_ARRAY_STR));
            } else {
                throw new HttpException("Unknown response action: " + responseAction);
            }

        } else {
            throw new HttpException("Server reports error: HTTP code = " + code + ". Response: " + responseString);
        }
	}
    // -------------------------------------------------OnTabChangeListener methods

    @Override
    public void onTabChanged(String tabId) {
        updateSubmitScoresBtnVisibility();
        
        if (TAB_LOCALSCORES_ID.equals(tabId)) {
            if (JumplingsApplication.DEBUG_ENABLED) {
                mClearScoresBtn.setVisibility(View.VISIBLE);
            }

        } else if (TAB_GLOBALSCORES_ID.equals(tabId)) {
            if (JumplingsApplication.DEBUG_ENABLED) {
                mClearScoresBtn.setVisibility(View.GONE);
            }
        }
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
        private List<HighScore> list;
        // si es para la lista local o global
        private boolean local;

        // -------------------------------------------------- Constructor

        /**
         * @param list
         */
        public CustomAdapter(List<HighScore> list, boolean local) {
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
                convertView = inflater.inflate(R.layout.score_item, parent, false);
            }

            HighScore hs = list.get(position);

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
    
    /**
     * Task for contacting the server.
     * </p>
     * First parameter is the post string to send.
     */
    private class ServerRequestAsyncTask extends AsyncTask<String, Void, HttpResponse> {
    	private Exception mError;
    	
		@Override
		protected HttpResponse doInBackground(String... args) {
			try {
	            HttpPost request = new HttpPost(JumplingsApplication.SCORE_SERVICES_URL);
	            StringEntity se = new StringEntity(args[0]);
	            request.setEntity(se);
	
	            DefaultHttpClient client = new DefaultHttpClient();
	            return client.execute(request);
			} catch (Exception e) {
				Log.e(JumplingsApplication.LOG_SRC, "Error preparing http request: " + e.toString(), e);
				mError = e;
	            return null;
			}
		}

		@Override
		protected void onPostExecute(HttpResponse response) {
			super.onPostExecute(response);
            try {
            	if (mError != null) {
            		throw mError;
            	}
	            manageServerResponse(response);
            } catch (Exception e) {
            	notifyError("Error when communicating to server", e);
            }
		}
    	
    }

}
