package com.hungryhippos.networktesting;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class HelloMessage extends AbstractMessage{
	  private Float hello;       // custom message data
	  private List<Vector3f> ballPos;
	  private int playerNum;
	  public HelloMessage() {}    // empty constructor
	  public HelloMessage(Float s, List<Vector3f> pos, int playerNum) { 
		  hello = s;
		  this.ballPos = pos;
		  this.playerNum = playerNum;} // custom constor
	  public Float getHello(){
		return hello;
	  }
	  public List<Vector3f> getBalls() {
		  return ballPos;
	  }
	  public int getPlayerNum() {
		  return playerNum;
	  }
}
