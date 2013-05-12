package com.hungryhippos.networktesting;

import java.util.List;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class NewPosMessage extends AbstractMessage {
	  private List<Vector3f> ballPos;
	  private List<Integer> scores;
	private List<Vector3f> frogPos;
	  public NewPosMessage() {}    // empty constructor
	  public NewPosMessage(List<Vector3f> ballPos, List<Vector3f> frogPos, List<Integer> scores) { 
		  this.ballPos = ballPos;
		  this.scores = scores;
		  this.frogPos = frogPos;} // custom constor
	  public List<Vector3f> getBalls() {
		  return ballPos;
	  }
	  public List<Integer> getScores() {
		  return scores;
	  }
	  public List<Vector3f> getFrogs() {
		  return frogPos;
	  }
}
