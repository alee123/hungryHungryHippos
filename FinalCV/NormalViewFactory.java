package net.sskikne.Facetrack;

import java.awt.image.BufferedImage;

/**
 * This simple Analyzer is used when there is a delay in the video analysis.
 * By just passing this analyzer, we can see how past the webcam is running by itself.
 * It simply returns the image. 
 * @author sskikne and lpark
 *
 */
public class NormalViewFactory implements Analyzer {

	@Override
	public BufferedImage analyze(BufferedImage workImage, BufferedImage showImage) {
		return showImage;
	}

}
