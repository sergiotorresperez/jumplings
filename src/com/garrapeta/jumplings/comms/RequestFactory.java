package com.garrapeta.jumplings.comms;

import java.util.List;

import android.content.Context;

import com.garrapeta.jumplings.Score;

/**
 * Factory to create requests to the backend
 */
public class RequestFactory {
	
	public static RequestModel createDownloadScoresRequestModel(Context context, List<Score> scores, float worldWidth, float worldHeight) {
		return new RequestModel(context, RequestModel.JSON_ACTION_DOWNLOAD_SCORES_VALUE, scores, worldWidth, worldHeight);
	}
	
	public static RequestModel createSubmitScoresRequestModel(Context context, List<Score> scores, float worldWidth, float worldHeight) {
		return new RequestModel(context, RequestModel.JSON_ACTION_SUBMIT_SCORES_VALUE, scores, worldWidth, worldHeight);
	}
}
