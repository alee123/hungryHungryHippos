package FullGame;

import com.jme3.math.Vector3f;
import georegression.struct.point.Vector2D_F32;

/**
 * 
 */
//import com.jme3.math.Vector3f;

/**Tracks the position and attributes of the players mouth
 * @author rboy, vcoleman
 *
 */
public class Mouth {
	//x, y, and z should be values between 0 and 1
	private float x;
	private float y;
	private float z;
	private boolean open;
	private boolean recalibrate;
	public boolean GUIrecalibrate = false;
	public boolean WebCamrecalibrate = true;

	/**
	 * 
	 */
	public Mouth(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.open = true;
		this.recalibrate = false;
	}
	
	public void setOpen(boolean open){
		this.open = open;
	}
	
	public void setRecalibrate(boolean recalibrate){
		this.recalibrate = recalibrate;
	}

	public void setX(float x) {
		this.x = x/640;
	}

	public void setY(float y) {
		this.y = y/480;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public boolean isOpen() {
		return open;
	}

	public boolean isRecalibrate() {
		return recalibrate;
	}
	
	public Vector3f getPosition() {
		return new Vector3f(x,z,1-y);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return z;
	}

	public float getZ() {
		return y;
	}

	public void set(float x, float y, float f, boolean b, boolean c) {
		setX(x);
		setY(y);
		setZ(f);
		setOpen(b);
		setRecalibrate(c);
		
	}
	public boolean equals(Mouth oldMouth){
		if (this.x == oldMouth.x){
			if (this.y == oldMouth.y){
				if (this.z == oldMouth.z){
					return true;
				}
			}
		}
		return false;
	}
	
	public String toString(){
		return "Mouth " + this.x + " " + this.y + " " + this.z;
	}

}
