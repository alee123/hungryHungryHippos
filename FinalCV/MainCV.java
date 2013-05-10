package net.sskikne.Facetrack;

import javax.swing.SwingUtilities;

/**
 * This is our debugging webcam builder. 
 * In the constructor, we choose the analyzers to pass to the webcam.
 * @author sskikne and lpark
 *
 */

public class MainCV {
	
	public static void main(String args[]){
    	
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                       new WebCam(new BinaryFactory(new InterestPointFactory(new Mouth(.5f,.5f,.5f))));

                }
        });
	}
}
