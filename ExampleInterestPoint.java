package net.sskikne.Facetrack;

import georegression.struct.point.Point2D_F64;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.abst.feature.detect.interest.InterestPointDetector;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.detect.interest.FactoryInterestPoint;
import boofcv.gui.feature.FancyInterestPointRender;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.BoofDefaults;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;


import georegression.struct.point.Point2D_I32;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import boofcv.abst.feature.tracker.PointTrack;
import boofcv.abst.feature.tracker.PointTracker;
import boofcv.alg.filter.blur.BlurImageOps;
import boofcv.alg.filter.derivative.GradientSobel;
import boofcv.core.image.border.FactoryImageBorderAlgs;
import boofcv.factory.feature.tracker.FactoryPointTracker;
import boofcv.gui.feature.VisualizeFeatures;
import boofcv.gui.feature.VisualizeShapes;
import boofcv.gui.image.ImagePanel;
import boofcv.gui.image.VisualizeImageData;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageUInt8;

import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.CaptureCallback;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.VideoFrame;
import au.edu.jcu.v4l4j.exceptions.StateException;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

/**
 * This class demonstrates how to perform a simple push-mode capture.
 * It starts the capture and display the video stream in a JLabel
 * @author gilles
 *
 */
public class ExampleInterestPoint extends WindowAdapter implements CaptureCallback{
        private static int      width = 640, height = 480, std = V4L4JConstants.STANDARD_WEBCAM, channel = 0;
        private static String   device = "/dev/video0";

        private VideoDevice     videoDevice;
        private FrameGrabber    frameGrabber;

        private static JLabel          label;
        private JFrame          frame;
        
        private static ImagePanel gauss;
        static PointTracker<ImageUInt8> tracker;
        static Class<ImageUInt8> imageType = ImageUInt8.class ;
        static ArrayList<Point2D_I32> rectangle = new ArrayList<Point2D_I32>();
        static int rectW = 320;
        static int rectH = 240;
        static int sizeH = 80;
        static int sizeW = 80;
        static int framenum = 0;
        static int checkframenum =0;
        static ExampleInterestPoint IP = new ExampleInterestPoint();
        static InterestPointDetector<ImageFloat32> detector = FactoryInterestPoint.fastHessian(
				new ConfigFastHessian(10, 2, 100, 2, 9, 3, 4));
        
        public static void main(String args[]){
        	ConfigFastHessian configDetector = new ConfigFastHessian();
    		configDetector.maxFeaturesPerScale = 200;
    		configDetector.extractRadius = 3;
    		configDetector.initialSampleSize = 2;
    		tracker = FactoryPointTracker.dda_FH_SURF_Fast(configDetector, null, null, imageType);
    		
    		
 
    		
    		rectangle.add(new Point2D_I32(rectW - sizeW, rectH - sizeH));
    		rectangle.add(new Point2D_I32(rectW + sizeW, rectH - sizeH));
    		rectangle.add(new Point2D_I32(rectW + sizeW, rectH + sizeH));
    		rectangle.add(new Point2D_I32(rectW - sizeW, rectH + sizeH));
                SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                                new SimpleViewer();
                        }
                });
        }

        /**
         * Builds a WebcamViewer object
         * @throws V4L4JException if any parameter if invalid
         */
        public ExampleInterestPoint(){
                // Initialise video device and frame grabber
                try {
                        initFrameGrabber();
                } catch (V4L4JException e1) {
                        System.err.println("Error setting up capture");
                        e1.printStackTrace();
                        
                        // cleanup and exit
                        cleanupCapture();
                        return;
                }
                
                // create and initialise UI
                initGUI();
                
                // start capture
                try {
                        frameGrabber.startCapture();
                } catch (V4L4JException e){
                        System.err.println("Error starting the capture");
                        e.printStackTrace();
                }
        }

        /**
         * Initialises the FrameGrabber object
         * @throws V4L4JException if any parameter if invalid
         */
        private void initFrameGrabber() throws V4L4JException{
                videoDevice = new VideoDevice(device);
                frameGrabber = videoDevice.getJPEGFrameGrabber(width, height, channel, std, 80);
                frameGrabber.setCaptureCallback(this);
                width = frameGrabber.getWidth();
                height = frameGrabber.getHeight();
                System.out.println("Starting capture at "+width+"x"+height);
        }

        /** 
         * Creates the UI components and initialises them
         */
        private void initGUI(){
                frame = new JFrame();
                label = new JLabel();
                gauss = new ImagePanel();
                frame.getContentPane().add(label);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.addWindowListener(this);
                frame.setVisible(true);
                frame.setSize(width, height);       
        }
        
        /**
         * this method stops the capture and releases the frame grabber and video device
         */
        private void cleanupCapture() {
                try {
                        frameGrabber.stopCapture();
                } catch (StateException ex) {
                        // the frame grabber may be already stopped, so we just ignore
                        // any exception and simply continue.
                }

                // release the frame grabber and video device
                videoDevice.releaseFrameGrabber();
                videoDevice.release();
        }

        /**
         * Catch window closing event so we can free up resources before exiting
         * @param e
         */
        public void windowClosing(WindowEvent e) {
                cleanupCapture();

                // close window
                frame.dispose();            
        }


        @Override
        public void exceptionReceived(V4L4JException e) {
                // This method is called by v4l4j if an exception
                // occurs while waiting for a new frame to be ready.
                // The exception is available through e.getCause()
                e.printStackTrace();
        }

        @Override
        public void nextFrame(VideoFrame frame) {
                // This method is called when a new frame is ready.
                // Don't forget to recycle it when done dealing with the frame.
                
                // draw the new frame onto the JLabel
        		BufferedImage x = frame.getBufferedImage();
//              procedural(x);
                frame.recycle();
                
        }

        public static void procedural( BufferedImage bufferedImage )
    	{
        	ImageFloat32 input = ConvertBufferedImage.convertFromSingle(bufferedImage, null, ImageFloat32.class);
        	detector.detect(input);
        	
    		Graphics2D g2 = bufferedImage	.createGraphics();
    		FancyInterestPointRender render = new FancyInterestPointRender();
     
     
    		for( int i = 0; i < detector.getNumberOfFeatures(); i++ ) {
    			Point2D_F64 pt = detector.getLocation(i);
     
    			// note how it checks the capabilities of the detector
    			if( detector.hasScale() ) {
    				double scale = detector.getScale(i);
    				int radius = (int)(scale* BoofDefaults.SCALE_SPACE_CANONICAL_RADIUS);
    				render.addCircle((int)pt.x,(int)pt.y,radius);
    			} else {
    				render.addPoint((int) pt.x, (int) pt.y);
    			}
    		}
    		// make the circle's thicker
    		g2.setStroke(new BasicStroke(3));
     
    		// just draw the features onto the input image
    		render.draw(g2);
    		label.getGraphics().drawImage(bufferedImage , 0, 0, width, height, null);
//    		ShowImages.showWindow(test,"Procedural Fixed Type");
    	}
}

//
//
//
//public class ExampleInterestPoint {
// 
//	public static <T extends ImageSingleBand>
//	void detect( BufferedImage image , Class<T> imageType ) {
//		T input = ConvertBufferedImage.convertFromSingle(image, null, imageType);
// 
//		// Create a Fast Hessian detector from the SURF paper.
//		// Other detectors can be used in this example too.
//		InterestPointDetector<T> detector = FactoryInterestPoint.fastHessian(
//				new ConfigFastHessian(10, 2, 100, 2, 9, 3, 4));
// 
//		// find interest points in the image
//		detector.detect(input);
// 
//		// Show the features
//		displayResults(image, detector);
//	}
// 
//	private static <T extends ImageSingleBand> void displayResults(BufferedImage image,
//															 InterestPointDetector<T> detector)
//	{
//		Graphics2D g2 = image.createGraphics();
//		FancyInterestPointRender render = new FancyInterestPointRender();
// 
// 
//		for( int i = 0; i < detector.getNumberOfFeatures(); i++ ) {
//			Point2D_F64 pt = detector.getLocation(i);
// 
//			// note how it checks the capabilities of the detector
//			if( detector.hasScale() ) {
//				double scale = detector.getScale(i);
//				int radius = (int)(scale* BoofDefaults.SCALE_SPACE_CANONICAL_RADIUS);
//				render.addCircle((int)pt.x,(int)pt.y,radius);
//			} else {
//				render.addPoint((int) pt.x, (int) pt.y);
//			}
//		}
//		// make the circle's thicker
//		g2.setStroke(new BasicStroke(3));
// 
//		// just draw the features onto the input image
//		render.draw(g2);
//		ShowImages.showWindow(image, "Detected Features");
//	}
// 
////	public static void main( String args[] ) {
////		BufferedImage image = UtilImageIO.loadImage("/home/sskikne/Cookies.jpg");
////		detect(image, ImageFloat32.class);
////	}
//}