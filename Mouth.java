
public class Mouth {
	private float x;
	private float y;
	private float z;
	private boolean open;
	private boolean recalibrate;
	
	public Mouth (){
		
	}
	
	public Mouth (float x, float y, float z, boolean open, boolean recalibrate){
		this.x = x;
		this.y = y;
		this.z = z;
		this.open = open;
		this.recalibrate = recalibrate;
	}
	
	
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public float getZ() {
		return z;
	}
	public void setZ(float z) {
		this.z = z;
	}
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
	}
	public boolean isRecalibrate() {
		return recalibrate;
	}
	public void setRecalibrate(boolean recalibrate) {
		this.recalibrate = recalibrate;
	}
}