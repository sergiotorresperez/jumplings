package net.garrapeta.jumplings.comms;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import net.garrapeta.gameengine.utils.IOUtils;
import net.garrapeta.jumplings.JumplingsApplication;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Helper class to deal with the communication with the backend
 */
public class BackendConnector {

	private final static String PARAM_DATA 		 = "data";
	private final static String PARAM_AUTH_TOKEN = "authToken";
	
	// TODO: obfuscate
	private final static String SECRET_MD5_PREFIX = "pl40ap_sw113ja_w140jx";
	
	// used to parse and format the requests and responses to Json
	private final static Gson sGson = new Gson();
	
	/**
	 * Sends a request to the request using POST synchronously
	 * @param request
	 * @throws BackendConnectionException
	 */
	public static ResponseModel postRequestSync(final RequestModel request) throws BackendConnectionException {
		try {
			HttpPost httpPost = new HttpPost(JumplingsApplication.SCORE_SERVICES_URL);
			String data = sGson.toJson(request);
			
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
	        nvps.add(new BasicNameValuePair(PARAM_DATA, data));
	        nvps.add(new BasicNameValuePair(PARAM_AUTH_TOKEN, computeAuthToken(data)));
	        
	        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF_8"));
			
	        DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(httpPost);
			return manageResponse(response);
		} catch (Exception e) {
			throw new BackendConnectionException(BackendConnectionException.ErrorType.CLIENT_ERROR, "Could not send request", e);
		}
	}

	/**
	 * Sends a request to the backend using POST asynchronously
	 * @param request
	 */
	public static void postRequestAsync(final RequestModel request, BackendConnectorCallback callback) {
		new ServerRequestAsyncTask(callback).execute(request);
	}

	private static ResponseModel manageResponse(HttpResponse response) throws BackendConnectionException{
		try {
			int code = response.getStatusLine().getStatusCode();

			HttpEntity er = response.getEntity();
			InputStream is;
			is = er.getContent();
			String responseString = IOUtils.getStringFromInputStream(is);

			Log.i(JumplingsApplication.LOG_SRC, "Response received = " + code + ". Response: " + responseString);

			if (code == 200) {
				try {
					return sGson.fromJson(responseString, ResponseModel.class);
				} catch (JsonSyntaxException je) {
					throw new BackendConnectionException(BackendConnectionException.ErrorType.PARSING_ERROR, "Could not parse response", je);
				}
			} else {
				throw new BackendConnectionException(BackendConnectionException.ErrorType.HTTP_ERROR, "Server reports error: HTTP code = " + code + ". Response: " + responseString);
			}
		} catch (IOException ioe) {
			throw new BackendConnectionException(BackendConnectionException.ErrorType.IO_ERROR, "IO error when communicating with the backend", ioe);
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
		return md5(getSecretMD5Prefix() + data);
	}

	/**
	 * @return the secret string used as a prefix to calculate the auth token
	 */
	private static String getSecretMD5Prefix() {
		// TODO: obfuscate
		return SECRET_MD5_PREFIX;
	}

	/**
	 * Computes the MD5 hash of one string
	 * {@link http://stackoverflow.com/questions/4846484/md5-or-other-hashing-in-android}
	 * 
	 * @param string
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private static final String md5(final String string) throws NoSuchAlgorithmException {
        // Create MD5 Hash
        MessageDigest digest = java.security.MessageDigest .getInstance("MD5");
        digest.update(string.getBytes());
        byte messageDigest[] = digest.digest();

        // Create Hex String
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++) {
            String h = Integer.toHexString(0xFF & messageDigest[i]);
            while (h.length() < 2)
                h = "0" + h;
            hexString.append(h);
        }
        return hexString.toString();
	}
	
	/**
	 * Task for contacting the server.
	 * </p>
	 * First parameter is the post string to send.
	 */
	private static class ServerRequestAsyncTask extends AsyncTask<RequestModel, Void, ResponseModel> {
		
		private final BackendConnectorCallback mCallback;
		private BackendConnectionException mError;

		/**
		 * @param callback
		 */
		public ServerRequestAsyncTask(BackendConnectorCallback callback) {
			super();
			mCallback = callback;
		}

		@Override
		protected ResponseModel doInBackground(RequestModel... args) {
			try {
				return BackendConnector.postRequestSync(args[0]);
			} catch (BackendConnectionException e) {
				Log.e(JumplingsApplication.LOG_SRC, "Error preparing http request: " + e.toString(), e);
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
