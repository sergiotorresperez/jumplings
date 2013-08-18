package com.garrapeta.jumplings.comms;

import java.util.List;

import com.garrapeta.jumplings.Score;

/**
 * Request model of the data sent to the backend
 */
public class RequestModel {
	
	// Key with the action sent to the server
	public static final String JSON_ACTION_KEY           		= "action";
	// Value of the action for downloading scores
	public static final String JSON_ACTION_DOWNLOAD_SCORES_VALUE  = "download_scores";
	// Value of the action for submitting scores
	public static final String JSON_ACTION_SUBMIT_SCORES_VALUE 	= "submit_scores";

	
	public final String action;
	public final List<Score> localScores;
	public final float worldWidth;
	public final float worldHeight;

	RequestModel(String action, List<Score> localScores, float worldWidth, float worldHeight) {
		this.action = action;
		this.localScores = localScores;
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
	}
}
