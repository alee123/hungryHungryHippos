

import au.edu.jcu.v4l4j.VideoFrame;
import boofcv.gui.image.ShowImages;
import boofcv.io.v4l4j.V4l4jVideo;

public class testWebcam {
	static V4l4jVideo webcam;
	private static String   device = "/dev/video0";
	static VideoFrame currframe;
	
	public static void main(String args[]){
		webcam = new V4l4jVideo();
		webcam.start(device, 640, 480, null );
		webcam.nextFrame(currframe);

		ShowImages.showWindow(currframe.getBufferedImage(),"Procedural Fixed Type");
		
		
		
	}
}
