package com.hungryhippos.networktesting;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class FrogMessage extends AbstractMessage {
	private boolean canEat;       // custom message data
	private Vector3f frogPos;
	public FrogMessage() {}    // empty constructor
	public FrogMessage(boolean canEat, Vector3f frogPos) { 
		  this.canEat = canEat;
		  this.frogPos = frogPos;} // custom constor
	  public boolean getCanEat(){
		return canEat;
	  }
	  public Vector3f getFrogPos() {
		  return frogPos;
	  }
}
