package net.garrapeta.jumplings.comms;

import java.io.IOException;
import java.io.InputStream;

import net.garrapeta.gameengine.utils.IOUtils;
import net.garrapeta.jumplings.JumplingsApplication;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Helper class to deal with the communication with the backend
 */
public class BackendConnector {

	/**
	 * Sends a request to the backend using POST synchronously
	 * @param message
	 * @throws BackendConnectionException
	 */
	public static JSONObject postRequestSync(final String message) throws BackendConnectionException {
		try {
			HttpPost request = new HttpPost(JumplingsApplication.SCORE_SERVICES_URL);
			StringEntity se = new StringEntity(message);
			request.setEntity(se);

			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(request);
			return manageResponse(response);
		} catch (Exception e) {
			throw new BackendConnectionException(BackendConnectionException.ErrorType.CLIENT_ERROR, "Could not send request", e);
		}
	}

	/**
	 * Sends a request to the backend using POST asynchronously
	 * @param message
	 */
	public static void postRequestAsync(final String message, BackendConnectorCallback callback) {
		new ServerRequestAsyncTask(callback).execute(message);
	}

	private static JSONObject manageResponse(HttpResponse response) throws BackendConnectionException{
		try {
			int code = response.getStatusLine().getStatusCode();

			HttpEntity er = response.getEntity();
			InputStream is;
			is = er.getContent();
			String responseString = IOUtils.getStringFromInputStream(is);

			Log.i(JumplingsApplication.LOG_SRC, "Response received = " + code + ". Response: " + responseString);

			if (code == 200) {
				try {
					JSONObject responseObj = new JSONObject(responseString);
					return responseObj;
				} catch (JSONException je) {
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
	 * Task for contacting the server.
	 * </p>
	 * First parameter is the post string to send.
	 */
	private static class ServerRequestAsyncTask extends AsyncTask<String, Void, JSONObject> {
		
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
		protected JSONObject doInBackground(String... args) {
			try {
				return BackendConnector.postRequestSync(args[0]);
			} catch (BackendConnectionException e) {
				Log.e(JumplingsApplication.LOG_SRC, "Error preparing http request: " + e.toString(), e);
				mError = e;
				return null;
			}
		}

		@Override
		protected void onPostExecute(JSONObject response) {
			super.onPostExecute(response);

			if (mCallback != null) {
				if (mError != null) {
					mCallback.onError(mError);
				} else {
					mCallback.onSuccess(response);
				}
			}
		}
	}
}
