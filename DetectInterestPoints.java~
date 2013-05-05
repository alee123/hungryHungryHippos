package net.sskikne.Facetrack;

import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point2D_I32;
import georegression.struct.trig.Circle2D_F64;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.abst.feature.detect.interest.InterestPointDetector;
import boofcv.alg.enhance.EnhanceImageOps;
import boofcv.alg.enhance.impl.ImplEnhanceFilter;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.detect.interest.FactoryInterestPoint;
import boofcv.gui.ListDisplayPanel;
import boofcv.gui.feature.FancyInterestPointRender;
import boofcv.gui.feature.VisualizeShapes;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.BoofDefaults;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;

public class DetectInterestPoints implements analyzer {
	 static InterestPointDetector<ImageUInt8> detector = FactoryInterestPoint.fastHessian(
				new ConfigFastHessian(10, 2, 5, 2, 9, 3, 4));
	 
//	 static InterestPointDetector<ImageUInt8> detector = FactoryInterestPoint.fastHessian(
//				new ConfigFastHessian(10, 2, 5, 2, 9, 3, 4));
	


	public static ImplEnhanceFilter IEF = new ImplEnhanceFilter();
    static ArrayList<Point2D_I32> rectangle = new ArrayList<Point2D_I32>();
    static int rectW = 320;
    static int rectH = 240;
    static int sizeH = 80;
    static int sizeW = 80;
    static int framenum = 0;
    Circle2D_F64 mouth = null;
    int mouthguess = 0;
    Point2D_F64 mouthguesspt;
	 DetectInterestPoints(){
			rectangle.add(new Point2D_I32(rectW - sizeW, rectH - sizeH));
			rectangle.add(new Point2D_I32(rectW + sizeW, rectH - sizeH));
			rectangle.add(new Point2D_I32(rectW + sizeW, rectH + sizeH));
			rectangle.add(new Point2D_I32(rectW - sizeW, rectH + sizeH));
	 }
	 
	public boolean inrectangle(Point2D_F64 point){
		if (point.x > rectW - sizeW && point.x < rectW + sizeW && point.y > rectH - sizeH && point.y < rectH + sizeH){
			return true;
		}
		return false;
	} 
    public BufferedImage analyze( BufferedImage bufferedImage )
	{
//    	if (framenum == 1){
//    		ExampleFitPolygon p = new ExampleFitPolygon();
//    		p.fit(bufferedImage);
//    	}
    	ImageUInt8 input = new ImageUInt8(bufferedImage.getWidth(),bufferedImage.getHeight());
//    	ImageUInt8 output = new ImageUInt8(bufferedImage.getWidth(),bufferedImage.getHeight());
//     	
    	ConvertBufferedImage.convertFrom(bufferedImage, input);
                  
//    	ImageFloat32 input = ConvertBufferedImage.convertFromSingle(bufferedImage, null, ImageFloat32.class);
    	detector.detect(input);
//    	histogram(bufferedImage);
    	
		Graphics2D g2 = bufferedImage.createGraphics();
		FancyInterestPointRender render = new FancyInterestPointRender();
		VisualizeShapes.drawPolygon(rectangle, true, g2);
 
		for( int i = 0; i < detector.getNumberOfFeatures(); i++ ) {
			Point2D_F64 pt = detector.getLocation(i);
			// note how it checks the capabilities of the detector
			if( detector.hasScale() ) {
				double scale = detector.getScale(i);
				int radius = (int)(scale* BoofDefaults.SCALE_SPACE_CANONICAL_RADIUS);
				if(mouth != null){
					
					if (mouth.center.distance(pt) < 3 && ((mouth.radius - radius) < 2 || (-mouth.radius + radius) < 2 )){
						System.out.println("Here");
						System.out.println("Was" + mouth.center);
						System.out.println("Will be" + pt);
						System.out.println(mouth.center.distance(pt));
						mouth.center = pt;
						mouth.radius = radius;
					}
				}
				if (inrectangle(pt)){
					if (mouth == null){
						if (radius > mouthguess){
							mouthguesspt = pt;
							mouthguess = radius;
						}
					}
//					render.addCircle((int)pt.x,(int)pt.y,radius, Color.RED);
					
				}
////				else{
//					render.addCircle((int)pt.x,(int)pt.y,radius, Color.BLACK);
//				}
			
			} else {
				render.addPoint((int) pt.x, (int) pt.y);
			}
		}
		if (mouth == null){
			mouth = new Circle2D_F64();
			mouth.center= mouthguesspt;
			mouth.radius= mouthguess;
		}
		else{
			render.addCircle((int)mouth.center.x ,(int)mouth.center.y, (int) mouth.radius , Color.WHITE);
			
		}
		// make the circle's thicker
		g2.setStroke(new BasicStroke(7));
 
			
		// just draw the features onto the input image
		render.draw(g2);
		framenum++;
		return bufferedImage;
//		ShowImages.showWindow(test,"Procedural Fixed Type");
	}
    public static ImageUInt8 histogram(BufferedImage image) {
		ImageUInt8 gray = ConvertBufferedImage.convertFrom(image,(ImageUInt8)null);
		ImageUInt8 adjusted = new ImageUInt8(gray.width, gray.height);
 
		int histogram[] = new int[256];
		int transform[] = new int[256];
 
		ListDisplayPanel panel = new ListDisplayPanel();
 
		ImageStatistics.histogram(gray,histogram);
		EnhanceImageOps.equalize(histogram, transform);
		EnhanceImageOps.applyTransform(gray, transform, adjusted);
		
		EnhanceImageOps.equalizeLocal(gray, 50, adjusted, histogram, transform);
		return gray;
		
		//ccv
	}
}

