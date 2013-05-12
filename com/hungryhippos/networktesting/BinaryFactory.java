package com.hungryhippos.networktesting;

import java.awt.image.BufferedImage;
import java.util.List;

import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSInt32;
import boofcv.struct.image.ImageUInt8;

public class BinaryFactory implements Analyzer {
	boolean pass = false;
	public Analyzer nextFactory;
	
	BinaryFactory (Analyzer next){
		pass = true;
		nextFactory = next;
		
	}
	@Override
	public BufferedImage analyze(BufferedImage workImage, BufferedImage showImage) {
		// convert into a usable format
		ImageFloat32 input = ConvertBufferedImage.convertFromSingle(workImage, null, ImageFloat32.class);
		ImageUInt8 binary = new ImageUInt8(input.width,input.height);
		ImageSInt32 labeli = new ImageSInt32(input.width,input.height);
 
		// the mean pixel value is often a reasonable threshold when creating a binary image
		double mean = ImageStatistics.mean(input);
 
		// create a binary image by thresholding
		ThresholdImageOps.threshold(input,binary,(float)(mean -70.0),true);
		// remove small blobs through erosion and dilation
		// The null in the input indicates that it should internally declare the work image it needs
		// this is less efficient, but easier to code.
		ImageUInt8 filtered = BinaryImageOps.erode8(binary,null);
		filtered = BinaryImageOps.dilate8(filtered, null);
 
		// Detect blobs inside the image using an 8-connect rule
		List<Contour> contours = BinaryImageOps.contour(filtered, 8, labeli);
 
		// colors of contours
		int colorExternal = 0xFFFFFF;
		int colorInternal = 0xFF2020;
 
		// display the results
//		BufferedImage visualBinary = VisualizeBinaryData.renderBinary(binary, null);
		BufferedImage visualFiltered = VisualizeBinaryData.renderBinary(filtered, null);
//		BufferedImage visualLabel = VisualizeBinaryData.renderLabeled(labeli, contours.size(), null);
//		BufferedImage visualContour = VisualizeBinaryData.renderContours(contours,colorExternal,colorInternal,
//				input.width,input.height,null);
 
		if (pass){
			return nextFactory.analyze(visualFiltered, showImage);
		}
		return visualFiltered;
	}

}
