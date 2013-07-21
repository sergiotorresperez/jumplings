package net.garrapeta.jumplings.comms;


/**
 * Callback of the requests to the server
 */
public interface BackendConnectorCallback {
	
	public void onBackendRequestSuccess(ResponseModel response);
	
	public void onBackendRequestError(BackendConnectionException error);
}
