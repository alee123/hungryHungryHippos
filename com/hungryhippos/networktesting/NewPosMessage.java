package com.hungryhippos.networktesting;

import java.util.List;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class NewPosMessage extends AbstractMessage {
	  private List<Vector3f> ballPos;
	  public NewPosMessage() {}    // empty constructor
	  public NewPosMessage(List<Vector3f> pos) { 
		  this.ballPos = pos;} // custom constor
	  public List<Vector3f> getBalls() {
		  return ballPos;
	  }
}
