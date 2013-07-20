package net.garrapeta.jumplings.comms;

import org.json.JSONObject;

/**
 * Callback of the requests to the server
 */
public interface BackendConnectorCallback {
	
	public void onSuccess(JSONObject response);
	
	public void onError(BackendConnectionException error);
}
