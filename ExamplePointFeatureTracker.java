/**
 * 
 */
package net.sskikne.Facetrack;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.abst.feature.detect.interest.ConfigGeneralDetector;
import boofcv.abst.feature.tracker.PkltConfig;
import boofcv.abst.feature.tracker.PointTrack;
import boofcv.abst.feature.tracker.PointTracker;
import boofcv.alg.filter.derivative.GImageDerivativeOps;
import boofcv.factory.feature.tracker.FactoryPointTracker;
import boofcv.gui.feature.VisualizeFeatures;
import boofcv.gui.image.ImagePanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.SimpleImageSequence;
import boofcv.io.video.VideoMjpegCodec;
import boofcv.io.wrapper.images.JpegByteImageSequence;
import boofcv.misc.BoofMiscOps;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;

/**
 * @author sskikne
 *
 */
public class ExamplePointFeatureTracker< T extends ImageSingleBand, D extends ImageSingleBand>
{
	// type of input image
	Class<T> imageType;
	Class<D> derivType;
 
	// tracks point features inside the image
	PointTracker<T> tracker;
 
	// displays the video sequence and tracked features
	ImagePanel gui = new ImagePanel();
 
	public ExamplePointFeatureTracker(Class<T> imageType) {
		this.imageType = imageType;
		this.derivType = GImageDerivativeOps.getDerivativeType(imageType);
	}
 
	/**
	 * Processes the sequence of images and displays the tracked features in a window
	 */
	public void process(SimpleImageSequence<T> sequence) {
 
		// Figure out how large the GUI window should be
		T frame = sequence.next();
		gui.setPreferredSize(new Dimension(frame.getWidth(),frame.getHeight()));
		ShowImages.showWindow(gui,"KTL Tracker");
 
		// process each frame in the image sequence
		while( sequence.hasNext() ) {
			frame = sequence.next();
 
			// tell the tracker to process the frame
			tracker.process(frame);
 
			// if there are too few tracks spawn more
			if( tracker.getActiveTracks(null).size() < 100 )
				tracker.spawnTracks();
 
			// visualize tracking results
			updateGUI(sequence);
 
			// wait for a fraction of a second so it doesn't process to fast
			BoofMiscOps.pause(100);
		}
	}
 
	/**
	 * Draw tracked features in blue, or red if they were just spawned.
	 */
	private void updateGUI(SimpleImageSequence<T> sequence) {
		BufferedImage orig = sequence.getGuiImage();
		Graphics2D g2 = orig.createGraphics();
 
		// draw active tracks as blue dots
		for( PointTrack p : tracker.getActiveTracks(null) ) {
			VisualizeFeatures.drawPoint(g2, (int)p.x, (int)p.y, Color.blue);
		}
 
		// draw tracks which have just been spawned green
		for( PointTrack p : tracker.getNewTracks(null) ) {
			VisualizeFeatures.drawPoint(g2, (int)p.x, (int)p.y, Color.green);
		}
 
		// tell the GUI to update
		gui.setBufferedImage(orig);
		gui.repaint();
	}
 
	/**
	 * A simple way to create a Kanade-Lucas-Tomasi (KLT) tracker.
	 */
	public void createKLT() {
		PkltConfig<T, D> config = PkltConfig.createDefault(imageType, derivType);
		config.templateRadius = 3;
		config.pyramidScaling = new int[]{1,2,4,8};
 
		tracker = FactoryPointTracker.klt(config, new ConfigGeneralDetector(200, 3, 1));
	}
 
	/**
	 * Creates a SURF feature tracker.
	 */
	public void createSURF() {
		ConfigFastHessian configDetector = new ConfigFastHessian();
		configDetector.maxFeaturesPerScale = 200;
		configDetector.extractRadius = 3;
		configDetector.initialSampleSize = 2;
		tracker = FactoryPointTracker.dda_FH_SURF_Fast(configDetector, null, null, imageType);
	}
 
	public static void main( String args[] ) throws FileNotFoundException {
 
		Class imageType = ImageFloat32.class;
		
 
		// loads an MJPEG video sequence
		VideoMjpegCodec codec = new VideoMjpegCodec();
		List<byte[]> data = codec.read(new FileInputStream("/home/sskikne/Cookies.jpg"));
		SimpleImageSequence sequence = new JpegByteImageSequence(imageType,data,true);
 
		ExamplePointFeatureTracker app = new ExamplePointFeatureTracker(imageType);
 
		// Comment or un-comment to change the type of tracker being used
		app.createKLT();
//		app.createSURF();
 
		app.process(sequence);
	}
}