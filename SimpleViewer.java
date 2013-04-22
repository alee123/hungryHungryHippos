package net.sskikne.Facetrack;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.abst.feature.tracker.PointTrack;
import boofcv.abst.feature.tracker.PointTracker;
import boofcv.alg.filter.blur.BlurImageOps;
import boofcv.alg.filter.derivative.GradientSobel;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.core.image.border.FactoryImageBorderAlgs;
import boofcv.factory.feature.tracker.FactoryPointTracker;
import boofcv.gui.feature.VisualizeFeatures;
import boofcv.gui.image.ImagePanel;
import boofcv.gui.image.ShowImages;
import boofcv.gui.image.VisualizeImageData;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageSingleBand;
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
public class SimpleViewer extends WindowAdapter implements CaptureCallback{
        private static int      width = 640, height = 480, std = V4L4JConstants.STANDARD_WEBCAM, channel = 0;
        private static String   device = "/dev/video0";

        private VideoDevice     videoDevice;
        private FrameGrabber    frameGrabber;

        private static JLabel          label;
        private JFrame          frame;
        
        private static ImagePanel gauss;
        static PointTracker<ImageUInt8> tracker;
        static Class<ImageUInt8> imageType = ImageUInt8.class ;

        public static void main(String args[]){
        	ConfigFastHessian configDetector = new ConfigFastHessian();
    		configDetector.maxFeaturesPerScale = 200;
    		configDetector.extractRadius = 3;
    		configDetector.initialSampleSize = 2;
    		tracker = FactoryPointTracker.dda_FH_SURF_Fast(configDetector, null, null, imageType);
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
        public SimpleViewer(){
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
//                label.getGraphics().drawImage(x , 0, 0, width, height, null);
                System.out.println("new image");
//              BufferedImage bi = frameGrabber.getVideoFrame();
                procedural(x);
                frame.recycle();
                
        }

        public static void procedural( BufferedImage bufferedImage )
    	{
        	ImageUInt8 test = new ImageUInt8(bufferedImage.getWidth(),bufferedImage.getHeight());
        	ConvertBufferedImage.convertFrom(bufferedImage, test);
            
    		ImageUInt8 blurred = new ImageUInt8(test.width,test.height);
//    		ImageSInt16 derivX = new ImageSInt16(test.width,test.height);
//    		ImageSInt16 derivY = new ImageSInt16(test.width,test.height);
     
    		// Gaussian blur: Convolve a Gaussian kernel
//    		BlurImageOps.gaussian(test,blurred,-1,4,null);
    		tracker.process(test);
			if( tracker.getActiveTracks(null).size() < 10 ){
				tracker.spawnTracks();
			}
//			
 
    		// Calculate image's derivative
//    		GradientSobel.process(blurred, derivX, derivY, FactoryImageBorderAlgs.extend(test));
    		// display the results
//    		BufferedImage outputImage = VisualizeImageData.colorizeSign(derivX,null,-1);
    		Graphics2D g2 = bufferedImage.createGraphics();
    		for( PointTrack p : tracker.getActiveTracks(null) ) {
    			VisualizeFeatures.drawPoint(g2, (int)p.x, (int)p.y, Color.blue);
    		}
     
    		// draw tracks which have just been spawned green
    		for( PointTrack p : tracker.getNewTracks(null) ) {
    			VisualizeFeatures.drawPoint(g2, (int)p.x, (int)p.y, Color.green);
    		}

    		label.getGraphics().drawImage(bufferedImage , 0, 0, width, height, null);
//    		ShowImages.showWindow(test,"Procedural Fixed Type");
    	}
}

