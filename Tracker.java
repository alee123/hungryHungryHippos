

import georegression.struct.point.Point2D_I32;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.abst.feature.tracker.PointTrack;
import boofcv.abst.feature.tracker.PointTracker;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.tracker.FactoryPointTracker;
import boofcv.gui.feature.VisualizeFeatures;
import boofcv.gui.feature.VisualizeShapes;
import boofcv.struct.image.ImageUInt8;

public class Tracker implements analyzer{
	static PointTracker<ImageUInt8> tracker;
    static ArrayList<Point2D_I32> rectangle = new ArrayList<Point2D_I32>();
    static int rectW = 320;
    static int rectH = 240;
    static int sizeH = 80;
    static int sizeW = 80;
    static int framenum = 0;

    
	Tracker(){
		ConfigFastHessian configDetector = new ConfigFastHessian();
		configDetector.maxFeaturesPerScale = 200;
		configDetector.extractRadius = 3;
		configDetector.initialSampleSize = 2;
		tracker = FactoryPointTracker.dda_FH_SURF_Fast(configDetector, null, null, ImageUInt8.class );
		
		rectangle.add(new Point2D_I32(rectW - sizeW, rectH - sizeH));
		rectangle.add(new Point2D_I32(rectW + sizeW, rectH - sizeH));
		rectangle.add(new Point2D_I32(rectW + sizeW, rectH + sizeH));
		rectangle.add(new Point2D_I32(rectW - sizeW, rectH + sizeH));
    
		
	}
	 
	 public BufferedImage analyze ( BufferedImage bufferedImage )
 	{
     	ImageUInt8 test = new ImageUInt8(bufferedImage.getWidth(),bufferedImage.getHeight());
     	ConvertBufferedImage.convertFrom(bufferedImage, test);
         
 		tracker.process(test);
			if( tracker.getActiveTracks(null).size() < 4 ){
				tracker.spawnTracks();
			}
 		Graphics2D g2 = bufferedImage.createGraphics();
 		for( PointTrack p : tracker.getActiveTracks(null) ) {
 			VisualizeFeatures.drawPoint(g2, (int)p.x, (int)p.y, Color.blue);
 		}
 		
 		VisualizeShapes.drawPolygon(rectangle, true, g2);
 		
 		for( PointTrack p : tracker.getNewTracks(null) ) {
	    		if (framenum < 4){
	    			if ((int)p.x > rectW-sizeW && (int)p.x < rectW+sizeW && (int)p.y >rectH-sizeH && (int)p.y <rectH+sizeH ){
	    				VisualizeFeatures.drawPoint(g2, (int)p.x, (int)p.y, Color.green);
	    			} else {
	    				tracker.dropTrack(p);
	    			}
	    		} else {	    		
	    			tracker.dropTrack(p);
	    		}
 		}
 		System.out.println(framenum);
 		framenum++;
 		return bufferedImage;
 	}
}

