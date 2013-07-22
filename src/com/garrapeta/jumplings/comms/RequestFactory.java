package com.garrapeta.jumplings.comms;

import java.util.List;

import com.garrapeta.jumplings.Score;

/**
 * Factory to create requests to the backend
 */
public class RequestFactory {
	
	public static RequestModel createDownloadScoresRequestModel(List<Score> scores) {
		return  new RequestModel(RequestModel.JSON_ACTION_DOWNLOAD_SCORES_VALUE, scores);
	}
	
	public static RequestModel createSubmitScoresRequestModel(List<Score> scores) {
		return  new RequestModel(RequestModel.JSON_ACTION_SUBMIT_SCORES_VALUE, scores);
	}
}