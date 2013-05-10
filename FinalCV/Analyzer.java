package net.sskikne.Facetrack;

import java.awt.image.BufferedImage;

/**
 * Analyzer is an interface for any class that can analyze photos from the webcam.
 * Each Analyzer can work by itself or pass its finished image off to another analyzer.
 * @author sskikne and lpark
 *
 */


public interface Analyzer {
	
	// The workImage is analyzed by each analyzer.
	// The showImage is used in analyzers get add onto the image. It will edit and return showImage.
	// An example of the difference is turning an image into black and white so it easier to analyze, but drawing the analysis on showImage.
	BufferedImage analyze(BufferedImage workImage, BufferedImage showImage);

	
	

}
