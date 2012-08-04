package net.garrapeta.demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import net.garrapeta.gameengine.R.id;
import net.garrapeta.utils.IOUtils;
import net.garrapeta.utils.AsynchronousHttpSender.AsynchronousHttpSender;
import net.garrapeta.utils.AsynchronousHttpSender.ResponseListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
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
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.openfeint.api.OpenFeint;
import com.openfeint.api.ui.Dashboard;


public class HighScoreListingActivity extends TabActivity implements ResponseListener, OnTabChangeListener {
    
	// ---------------------------------------------------------------- Constantes
	
	public static final int DIALOG_SERVER_ERROR_ID    = 0;
	public static final int SERVER_COMUNICATION_PROGRESS_DIALOG = 2;
	
	public static final String TAB_LOCALSCORES_ID  = "tab_local_id"; 
	public static final String TAB_GLOBALSCORES_ID = "tab_global_id";
	
	public boolean globalScoresUpdated = false;
	// ----------------------------------------------------------------- Variables
	
	private ArrayList<HighScore> localScoreList;
	private ArrayList<HighScore> globalScoreList;
	
	private ListView localHighScoresView;
	private ListView globalHighScoresView;
	
	private Button feintLeaderBoardBtn;
	private Button submitScoresBtn;	
	private Button clearScoresBtn;
	private Button updateBtn;	
	
	
	
	// -------------------------------------------------------- Variables estáticas

	// -------------------------------------------------------- Métodos de Activity
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.i(JumplingsApplication.LOG_SRC,"onCreate " + this);

        // Lectura de datos persistentes
        localScoreList = PermData.getInstance().getLocalScoresList();
        globalScoreList = PermData.getInstance().getGlobalScoresList();
        
        // Preparación de la UI
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.highscores_listing);
        
        // Preparación de Tabs
        TabHost mTabHost = getTabHost();
        
        // NOTE: http://ondrejcermak.info/programovani/custom-tabs-in-android-tutorial/comment-page-1/
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
        
        // Preparación de contenido de tab local
		
        localHighScoresView = (ListView) findViewById(R.id.highscoresListing_localHighScoresListView);
		
	
		// Se rellenan los scores locales
		feedLocalHighScoresView();
		
		
		// Preparación de contenido de tab global
	
		// Se elimina la columna Global Rank en header de lista global
		findViewById(id.highscoresListing_globalScoresTabContent).findViewById(id.scoreHeader_globalRank).setVisibility(View.INVISIBLE);
		
		// Se rellenan los scores globales
 		globalHighScoresView = (ListView) findViewById(R.id.highscoresListing_globalHighScoresListView);
 		feedGlobalHighScoresView();
		
 		// Preparación de los botones
 		
 		Button backBtn = (Button) findViewById(R.id.highscoresListing_backBtn);
 		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
 		
 		if (JumplingsApplication.DEBUG_ENABLED) {
			clearScoresBtn = (Button) findViewById(R.id.highscoresListing_clearLocalScoresBtn);
			clearScoresBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					PermData.getInstance().clearAll();
					localScoreList = PermData.getInstance().getLocalScoresList();
					feedLocalHighScoresView();
				}
			});
			clearScoresBtn.setVisibility(View.VISIBLE);
 		}
		

		submitScoresBtn = (Button) findViewById(R.id.highscoresListing_submitBtn);
		submitScoresBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitScores();
			}
		});
		
		if (JumplingsApplication.FEINT_ENABLED) {
			
						
			feintLeaderBoardBtn = (Button) findViewById(R.id.highscoresListing_feintLeaderBoardBtn);
			feintLeaderBoardBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Dashboard.openLeaderboard(GameOverActivity.feintLeaderboardId);
				}
			});
			
			if (OpenFeint.isUserLoggedIn()) {
				feintLeaderBoardBtn.setVisibility(View.VISIBLE);
			}
		}
		
 		updateBtn = (Button) findViewById(R.id.highscoresListing_updateBtn);
 		updateBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateScores();
			}
		});
 		updateBtn.setVisibility(View.GONE);
 		
 		
		// Ads
		if (JumplingsApplication.MOBCLIX_ENABLED) {
			 findViewById(R.id.highscoresListing_advertising_banner_view).setVisibility(View.VISIBLE);
		}
    }
    
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_SERVER_ERROR_ID:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage("Error when communicating with server")
	    	       .setCancelable(true)
	    	       .setPositiveButton("OK", null);
	    	
	    	return builder.create();
		case SERVER_COMUNICATION_PROGRESS_DIALOG:
			Dialog pd = ProgressDialog.show(this, "", "Contacting server...", true); 
			return pd;
		}
		return null;
	}
	
    
	// ---------------------------------------------- Métodos propios

	/**
	 * Alimenta la lista de scores locales
	 */
	private void feedLocalHighScoresView() {
		
		CustomAdapter adapter = new CustomAdapter(localScoreList, true);
		localHighScoresView.setAdapter(adapter);
		
		localHighScoresView.setCacheColorHint(0xFFFFFFFF);
	}
	
	/**
	 * Alimenta la lista de scores globales
	 */
	private void feedGlobalHighScoresView() {
		
		CustomAdapter adapter = new CustomAdapter(globalScoreList, false);
		globalHighScoresView.setAdapter(adapter);
		
		globalHighScoresView.setCacheColorHint(0xFFFFFFFF);
	}
	
	/**
	 * Sube los scores locales al servidor
	 */
	private void submitScores() {
		try {
			showDialog(SERVER_COMUNICATION_PROGRESS_DIALOG);
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(HighScore.JSON_REQUEST_OBJ_STR, HighScore.JSON_REQUEST_OBJ_SUBMIT_VALUE);
			
			jsonObject.put(HighScore.JSON_LOCALSCORES_ARRAY_STR, HighScore.formatJSON(localScoreList));
			
			String requestBody = jsonObject.toString();
			
			Log.i(JumplingsApplication.LOG_SRC, "Submitting local score: " + requestBody);
			
			HttpPost request = new HttpPost(JumplingsApplication.SCORE_SERVICES_URL);
			StringEntity se = new StringEntity(requestBody);
			request.setEntity(se);
			
			AsynchronousHttpSender.sendRequest(request, this);
			
		} catch (IOException ioe) {
			Log.e(JumplingsApplication.LOG_SRC, "Error setting entity when submiting scores: " + ioe.toString());
			ioe.printStackTrace();
			showDialog(DIALOG_SERVER_ERROR_ID);
		} catch (JSONException jee) {
			Log.e(JumplingsApplication.LOG_SRC, "Error creating JSONs when submiting scores: " + jee.toString());
			jee.printStackTrace();
			showDialog(DIALOG_SERVER_ERROR_ID);
		}
	}
	
	/** 
	 *  Ejecutado cuando los scores se han mandado correctamente al servidor
	 * @param scores 
	 */
	private void onScoresSubmitted(JSONArray scores) {
		// Los scores se han mandado al servidor
		
		Toast toast = Toast.makeText(HighScoreListingActivity.this, "Scores submitted", Toast.LENGTH_LONG);
		toast.show();
		submitScoresBtn.setVisibility(View.GONE);
		
		// para obligar a que se refresque
		globalScoresUpdated = false;
	}
	
	
	/**
	 * Actualiza los scores del servidor
	 */
	private void updateScores() {
		try {
			showDialog(SERVER_COMUNICATION_PROGRESS_DIALOG);
			
			JSONObject jsonObject = new JSONObject();
			// se pone la acción
			jsonObject.put(HighScore.JSON_REQUEST_OBJ_STR, HighScore.JSON_REQUEST_OBJ_RETRIEVE_VALUE);
			// se mandan scores locales, para que el servidor comunique el ranking
			jsonObject.put(HighScore.JSON_LOCALSCORES_ARRAY_STR, HighScore.formatJSON(localScoreList));
			
			String requestBody = jsonObject.toString();
			
			HttpPost request = new HttpPost(JumplingsApplication.SCORE_SERVICES_URL);
			StringEntity se = new StringEntity(requestBody);
			request.setEntity(se);
			
			AsynchronousHttpSender.sendRequest(request, this);
			
		} catch (IOException ioe) {
			Log.e(JumplingsApplication.LOG_SRC, "Error setting entity when retrieving scores: " + ioe.toString());
			ioe.printStackTrace();
			showDialog(DIALOG_SERVER_ERROR_ID);
		} catch (JSONException jee) {
			Log.e(JumplingsApplication.LOG_SRC, "Error creating JSONs when retrieving scores: " + jee.toString());
			jee.printStackTrace();
			showDialog(DIALOG_SERVER_ERROR_ID);
		}
	}
	
	/** 
	 *  Ejecutado cuando los scores se han actualizado correctamente del servidor
	 * @param scores 
	 * @throws JSONException 
	 */
	private void onScoresUpdated(JSONArray scores) throws JSONException {
		// marcamos que ya hemos actualizado
		globalScoresUpdated = true;
		// componemos la lista de scores goblales
		globalScoreList = HighScore.parseJSON(scores);
		// la salvamos
		PermData.getInstance().saveGlobalScoresList(globalScoreList);
		// rellenamos la tabla de scores globales
		feedGlobalHighScoresView();
	}
	
	/** 
	 * Ejecutado cuando recibimos una actualización del ranking de los scores locales
	 * @param scores 
	 * @throws JSONException 
	 */
	private void onRankingUpdated(JSONArray scores) throws JSONException {
		// componemos la lista de scores que nos ha dado el server
		ArrayList<HighScore> tmpScoreList = HighScore.parseJSON(scores);
		
		// para cada elemento recibido del server..
		for (int i = 0; i < tmpScoreList.size(); i++) {
			HighScore aux = tmpScoreList.get(i);
			
			// buscamos el score en la lista local
			for (int j = 0; j < localScoreList.size(); j++) {
				HighScore local = localScoreList.get(i);
				
				if (local.localId.equals(aux.localId)) {
					local.globalRank = aux.globalRank;
					break;
				}
			}
		}
		
		// salvamos la lista local
		PermData.getInstance().saveLocalScoresList(localScoreList);
		// rellenamos la tabla de scores locales
		feedLocalHighScoresView();
	}
	


	// ------------------------------------------------------ Métodos de OnTabChangeListener
	
	@Override
	public void onTabChanged(String tabId) {
		if (TAB_LOCALSCORES_ID.equals(tabId)) {
			submitScoresBtn.setVisibility(View.VISIBLE);
			updateBtn.setVisibility(View.GONE);
						
			if (!globalScoresUpdated) {
				updateScores();
			}
			if (JumplingsApplication.DEBUG_ENABLED) {
				clearScoresBtn.setVisibility(View.VISIBLE);
			}
			
		} else if (TAB_GLOBALSCORES_ID.equals(tabId)) {
			submitScoresBtn.setVisibility(View.GONE);
			updateBtn.setVisibility(View.VISIBLE);
			
			if (JumplingsApplication.DEBUG_ENABLED) {
				clearScoresBtn.setVisibility(View.GONE);
			}
		}
	}
	

	//------------------------------------------------- Métodos de ResponseListener
	

	@Override
	public void onResponseReceived(HttpResponse response) {
		try {
			// Dismiss progress dialog
			dismissDialog(SERVER_COMUNICATION_PROGRESS_DIALOG);
			
			int code = response.getStatusLine().getStatusCode();
			
			HttpEntity er = response.getEntity();
			InputStream is;
			is = er.getContent();
			String responseString = IOUtils.getStringFromInputStream(is);
			
			Log.i(JumplingsApplication.LOG_SRC, "Response received = " + code  + ". Response: " + responseString);
			
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
				throw new HttpException("Server reports error: HTTP code = " + code  + ". Response: " + responseString);
			}
		} catch (IOException ioe) {
			Log.e(JumplingsApplication.LOG_SRC, "IOException when reading server response: " + ioe.toString());
			ioe.printStackTrace();
			showDialog(DIALOG_SERVER_ERROR_ID);
		} catch (JSONException je) {
			Log.e(JumplingsApplication.LOG_SRC, "JSONException when reading server response: " + je.toString());
			je.printStackTrace();
			showDialog(DIALOG_SERVER_ERROR_ID);
		} catch (HttpException httpe) {
			Log.e(JumplingsApplication.LOG_SRC, "HttpException when reading server response: " + httpe.toString());
			httpe.printStackTrace();
			showDialog(DIALOG_SERVER_ERROR_ID);
		} 
	}


	@Override
	public void onClientProtocolExceptionWhenSending(ClientProtocolException cpe) {
		dismissDialog(SERVER_COMUNICATION_PROGRESS_DIALOG);
		Log.e(JumplingsApplication.LOG_SRC, "ClientProtocolException when submiting scores: " + cpe.toString());
		cpe.printStackTrace();

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showDialog(DIALOG_SERVER_ERROR_ID);
			}
		});
	}


	@Override
	public void onIOExceptionWhenSending(IOException ioe) {
		dismissDialog(SERVER_COMUNICATION_PROGRESS_DIALOG);
		Log.e(JumplingsApplication.LOG_SRC, "IOException when submiting scores: " + ioe.toString());
		ioe.printStackTrace();

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showDialog(DIALOG_SERVER_ERROR_ID);
			}
		});
		
	}


	
	//-------------------------------------------------------- Clases internas
	
	/**
	 * Adaptador de la lista de highscores
	 * @author GaRRaPeTa
	 */
	class CustomAdapter extends BaseAdapter {
		
		// --------------------------------------- Variables de instancia
		
		// lista que alimenta la tabla
		private ArrayList<HighScore> list;
		// si es para la lista local o global
		private boolean local;
		
		
		// -------------------------------------------------- Constructor
		
		/**
		 * @param list
		 */
		public CustomAdapter(ArrayList<HighScore> list, boolean local) {
			this.list = list;
			this.local = local;
		}
		

		// ---------------------------------------- Métodes de BaseAdapter
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
				//convertView = inflater.inflate(R.layout.score_item, parent, false);
				LayoutInflater inflater = (LayoutInflater)HighScoreListingActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
				TextView view = (TextView) convertView.findViewById(id.scoreItem_playerName);
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
				TextView view = (TextView) convertView.findViewById(id.scoreItem_level);
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
