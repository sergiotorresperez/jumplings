package com.garrapeta.jumplings.comms;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.garrapeta.gameengine.utils.IOUtils;
import com.garrapeta.jumplings.JumplingsApplication;
import com.garrapeta.jumplings.PermData;
import com.garrapeta.jumplings.util.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Helper class to deal with the communication with the backend
 */
public class BackendConnector {

	private final static String PARAM_DATA 		 = "data";
	private final static String PARAM_AUTH_TOKEN = "authToken";
	
	private final static int STATUS_OK                = 00;
	private final static int STATUS_ERROR_CLIENT      = 10;
	private final static int STATUS_ERROR_SERVER      = 20;
	private final static int STATUS_ERROR_AUTH_ERROR  = 30;
	
	// TODO: obfuscate
	private final static String SECRET_MD5_PREFIX = "pl40ap_sw113ja_w140jx";
	
	// used to parse and format the requests and responses to Json
	private final static Gson sGson = new Gson();
	
	/**
	 * Sends a request to the request using POST synchronously
	 * @param context
	 * @param request
	 * @throws BackendConnectionException
	 */
	public static ResponseModel sendRequestSync(Context context, final RequestModel request) throws BackendConnectionException {
		final String serviceUrl = PermData.getScoresServerUrl(context);
		
        if (!Utils.isNetworkAvailable(context)) {
        	throw new BackendConnectionException(BackendConnectionException.ErrorType.NO_CONNECTION_ERROR, "Could not send request to " + serviceUrl + ": No connection available");
        }
        
        HttpResponse response;
        
		try {
			String data = sGson.toJson(request);
			List<NameValuePair> queryParams = new ArrayList<NameValuePair>();
			queryParams.add(new BasicNameValuePair(PARAM_DATA, data)) ;	
			queryParams.add(new BasicNameValuePair(PARAM_AUTH_TOKEN, computeAuthToken(data)));

			final String url = PermData.getScoresServerUrl(context) + "?" + URLEncodedUtils.format(queryParams, HTTP.UTF_8);
			HttpGet httpGet = new HttpGet(url);
			
			HttpParams httpParams = httpGet.getParams();
			httpParams.setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.TRUE);
			httpGet.setParams(httpParams);
	        
			DefaultHttpClient client = new DefaultHttpClient();
			response = client.execute(httpGet);
		} catch (IOException ioe) {
			throw new BackendConnectionException(BackendConnectionException.ErrorType.IO_ERROR, "Could not send request to " + serviceUrl + ": " + ioe.getMessage() , ioe);
		} catch (Exception e) {
			throw new BackendConnectionException(BackendConnectionException.ErrorType.CLIENT_ERROR, "Could not send request to " + serviceUrl + ": " + e.getMessage() , e);
		}
		
		return manageResponse(response);
	}

	/**
	 * Sends a request to the backend using POST asynchronously
	 * @param context
	 * @param request
	 */
	public static void postRequestAsync(Context context, final RequestModel request, BackendConnectorCallback callback) {
		new ServerRequestAsyncTask(context, callback).execute(request);
	}

	private static ResponseModel manageResponse(HttpResponse response) throws BackendConnectionException {
		try {
			int httpStatusCode = response.getStatusLine().getStatusCode();

			HttpEntity er = response.getEntity();
			InputStream is;
			is = er.getContent();
			String responseString = IOUtils.getStringFromInputStream(is);

			Log.i(JumplingsApplication.LOG_SRC, "Response received = " + httpStatusCode + ". Response: " + responseString);

			if (httpStatusCode == HttpStatus.SC_OK) {
				try {
					if (TextUtils.isEmpty(responseString)) {
						throw new BackendConnectionException(BackendConnectionException.ErrorType.SERVER_ERROR, "Server returned empty response");
					}
					ResponseModel responseObject = sGson.fromJson(responseString, ResponseModel.class);
					checkError(responseObject);
					return responseObject;
				} catch (JsonSyntaxException je) {
					throw new BackendConnectionException(BackendConnectionException.ErrorType.SERVER_ERROR, "Could not parse response", je);
				}
			} else {
				throw new BackendConnectionException(BackendConnectionException.ErrorType.HTTP_ERROR, "Server reports error: HTTP code = " + httpStatusCode + ". Response: " + responseString);
			}
		} catch (IOException ioe) {
			throw new BackendConnectionException(BackendConnectionException.ErrorType.IO_ERROR, "IO error when communicating with the backend", ioe);
		}
	}
	
	private static void checkError(ResponseModel responseObject) throws BackendConnectionException {
		switch (responseObject.status) {
			case STATUS_ERROR_CLIENT:
			case STATUS_ERROR_AUTH_ERROR:
				throw new BackendConnectionException(BackendConnectionException.ErrorType.CLIENT_ERROR, responseObject.errorMessage);
			case STATUS_ERROR_SERVER:
				throw new BackendConnectionException(BackendConnectionException.ErrorType.SERVER_ERROR, responseObject.errorMessage);
			case STATUS_OK:
			default:
				return;
		}
	}

	/**
	 * Computes the auth token. The auth token is the MD5 of the request with a secret string preffixed.
	 * 
	 * md5($_AUTH_TOKEN_MD5_SECRET.$request) == authToken
	 * 
	 * @param data
	 * @return the authToken associated to the passed string
	 * @throws NoSuchAlgorithmException 
	 */
	private static String computeAuthToken(String data) throws NoSuchAlgorithmException {
		return Utils.md5(getSecretMD5Prefix() + data);
	}

	/**
	 * @return the secret string used as a prefix to calculate the auth token
	 */
	private static String getSecretMD5Prefix() {
		// TODO: obfuscate
		return SECRET_MD5_PREFIX;
	}

	/**
	 * Task for contacting the server.
	 * </p>
	 * First parameter is the post string to send.
	 */
	private static class ServerRequestAsyncTask extends AsyncTask<RequestModel, Void, ResponseModel> {
		
		private final BackendConnectorCallback mCallback;
		private BackendConnectionException mError;
		private final Context mContext;
		
		/**
		 * @param context
		 * @param callback
		 */
		public ServerRequestAsyncTask(Context context, BackendConnectorCallback callback) {
			super();
			mCallback = callback;
			mContext = context;
		}

		@Override
		protected ResponseModel doInBackground(RequestModel... args) {
			try {
				return BackendConnector.sendRequestSync(mContext, args[0]);
			} catch (BackendConnectionException e) {
				Log.e(JumplingsApplication.LOG_SRC, "Error in request: " + e.toString(), e);
				mError = e;
				return null;
			}
		}

		@Override
		protected void onPostExecute(ResponseModel response) {
			super.onPostExecute(response);

			if (mCallback != null) {
				if (mError != null) {
					mCallback.onBackendRequestError(mError);
				} else {
					mCallback.onBackendRequestSuccess(response);
				}
			}
		}
	}
}
