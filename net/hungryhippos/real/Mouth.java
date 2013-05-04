/**
 * 
 */
package net.hungryhippos.real;

import com.jme3.math.Vector3f;

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
		if (x>=0 && x<=1){
			this.x = x;
		}
		else if (x>1){
			this.x = 1;
		}
		else if (x<0){
			this.x = 0;
		}
	}

	public void setY(float y) {
		if (y>=0 && y<=1){
			this.y = y;
		}
		else if (y>1){
			this.y = 1;
		}
		else if (y<0){
			this.y = 0;
		}
	}

	public void setZ(float z) {
		if (z>=0 && z<=1){
			this.z = z;
		}
		else if (z>1){
			this.z = 1;
		}
		else if (z<0){
			this.z = 0;
		}
	}

	public boolean isOpen() {
		return open;
	}

	public boolean isRecalibrate() {
		return recalibrate;
	}
	
	public Vector3f getPosition() {
		return new Vector3f(x,y,z);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public void set(float x, float y, float f, boolean b, boolean c) {
		setX(x);
		setY(y);
		setZ(f);
		setOpen(b);
		setRecalibrate(c);
		
	}

}
