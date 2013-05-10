package net.sskikne.Facetrack;

import java.awt.image.BufferedImage;

import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;

/**
 * BinaryFactory is an image analyzer that creates a black and white image based on the areas of high contrast
 * It is very useful for finding mouths, because the mouth is high contrast to the lips and cheeks. This 
 * allows us to pass a black and white image with almost everything blacked out except for the mouth. 
 * @author sskikne and lpark 
 *
 */

public class BinaryFactory implements Analyzer {
	boolean pass = false;
	public Analyzer nextFactory;
	
	/*
	 * Use this constructor when Binary factory should pass on the image after it is processed 
	 */
	BinaryFactory (Analyzer next){
		pass = true;
		nextFactory = next;
		
	}

	/**
	 * This method turns the input image into a binary image and returns it or passes it on to the next analyzer.
	 * Part of the following code was written by someone else. The comments belong to them.  
	 */
	
	public BufferedImage analyze(BufferedImage workImage, BufferedImage showImage) {
		// convert into a usable format
		ImageFloat32 input = ConvertBufferedImage.convertFromSingle(workImage, null, ImageFloat32.class);
		ImageUInt8 binary = new ImageUInt8(input.width,input.height);
 
		// the mean pixel value is often a reasonable threshold when creating a binary image
		double mean = ImageStatistics.mean(input);
 
		// create a binary image by thresholding
		ThresholdImageOps.threshold(input,binary,(float)(mean -70.0),true);

		// remove small blobs through erosion and dilation
		// The null in the input indicates that it should internally declare the work image it needs
		// this is less efficient, but easier to code.
		ImageUInt8 filtered = BinaryImageOps.erode8(binary,null);
		filtered = BinaryImageOps.dilate8(filtered, null);
 
		BufferedImage visualFiltered = VisualizeBinaryData.renderBinary(filtered, null);
		
		// Either pass the finished image on or return the processed image. Pass the showImage along so the drawings can be drawn on a color pic. 
		if (pass){
			return nextFactory.analyze(visualFiltered, showImage);
		}
		return visualFiltered;
	}

}
