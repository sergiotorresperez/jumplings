package com.garrapeta.jumplings.comms;

import java.util.List;

import com.garrapeta.jumplings.Score;

/**
 * Request model of the data received from the backend
 */
public class ResponseModel {
	public int status;
	public String errorMessage;
	
	public List<Score> localScores;
	public List<Score> globalScores;
}
