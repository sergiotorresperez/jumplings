package net.garrapeta.jumplings.comms;

import org.json.JSONObject;

/**
 * Callback of the requests to the server
 */
public interface BackendConnectorCallback {
	
	public void onBackendRequestSuccess(JSONObject response);
	
	public void onBackendRequestError(BackendConnectionException error);
}
