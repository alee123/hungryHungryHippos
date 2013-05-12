package com.hungryhippos.networktesting;

import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point2D_I32;
import georegression.struct.trig.Circle2D_F64;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.abst.feature.detect.interest.InterestPointDetector;
import boofcv.alg.enhance.impl.ImplEnhanceFilter;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.detect.interest.FactoryInterestPoint;
import boofcv.gui.feature.FancyInterestPointRender;
import boofcv.gui.feature.VisualizeShapes;
import boofcv.struct.BoofDefaults;
import boofcv.struct.image.ImageUInt8;


/**
 * InterestPointFactory takes in a BufferedImage and tries to find the objects in the image.
 * It initially finds a mouth object in the middle of the webcam's view.
 * For each image, the factory uses the found objects to try track the mouth's movement.
 * When it loses track of the mouth, it recalibrates  
 * @author sskikne and lpark
 *
 */
public class InterestPointFactory implements Analyzer {
	 
	
	static InterestPointDetector<ImageUInt8> detector = FactoryInterestPoint.fastHessian(
				new ConfigFastHessian(10, 2, 1, 2, 9, 3, 4));
	

	public static ImplEnhanceFilter IEF = new ImplEnhanceFilter();
	
	//Rectangle and the W and H ints are used to define our recalibration region.
    static ArrayList<Point2D_I32> rectangle = new ArrayList<Point2D_I32>();
    static int rectW = 320;
    static int rectH = 240;
    static int sizeH = 80;
    static int sizeW = 80;
    
    static int framenum = 0;
    static int mouthCount = 0;
    Circle2D_F64 mouth = null;
    int mouthguess = 0;
    Point2D_F64 mouthguesspt;
    Mouth myMouth;
    Graphics2D g2;
    
    
    boolean pass = false;
	Analyzer nextFactory;
	
	
	//constructor with another factory to pass to
    InterestPointFactory(Analyzer next, Mouth mouth2){
    	myMouth = mouth2;
    	rectangle.add(new Point2D_I32(rectW - sizeW, rectH - sizeH));
		rectangle.add(new Point2D_I32(rectW + sizeW, rectH - sizeH));
		rectangle.add(new Point2D_I32(rectW + sizeW, rectH + sizeH));
		rectangle.add(new Point2D_I32(rectW - sizeW, rectH + sizeH));

		pass = true;
		nextFactory = next;
	 }
	 
    //constructor with no other factory to pass to
	public InterestPointFactory(Mouth mouth2) {
    	myMouth = mouth2;
		rectangle.add(new Point2D_I32(rectW - sizeW, rectH - sizeH));
		rectangle.add(new Point2D_I32(rectW + sizeW, rectH - sizeH));
		rectangle.add(new Point2D_I32(rectW + sizeW, rectH + sizeH));
		rectangle.add(new Point2D_I32(rectW - sizeW, rectH + sizeH));
	}

	/**checks if point is in rectangle
	 * @param point location
	 * @return boolean
	 */
	public boolean inrectangle(Point2D_F64 point){
		if (point.x > rectW - sizeW && point.x < rectW + sizeW && point.y > rectH - sizeH && point.y < rectH + sizeH){
			return true;
		}
		return false;
	} 
	
	/* detect Interest Points
	 * analyze takes BufferedImage binary image 
	 * returns BufferedImage with white circle indicating location of the Mouth
	 * (non-Javadoc)
	 * @see net.sskikne.Facetrack.Analyzer#analyze(java.awt.image.BufferedImage, java.awt.image.BufferedImage)
	 */
	public BufferedImage analyze( BufferedImage workImage, BufferedImage showImage)
	{
    	ImageUInt8 input = new ImageUInt8(workImage.getWidth(),workImage.getHeight());

    	ConvertBufferedImage.convertFrom(workImage, input);
      
    	detector.detect(input);
    	
		g2 = showImage.createGraphics();
		FancyInterestPointRender render = new FancyInterestPointRender();
		
		//recalibrates or configures image as usual
		handleImage(render);
		g2.setStroke(new BasicStroke(7));
 			
		// just draw the features onto the input image
		render.draw(g2);
		
		// if there is a factory we would like to pass processed image to, pass image.
		if (pass){
			return nextFactory.analyze(workImage, showImage);
		}
		
		return showImage;
		
	}


	/**recalibrates the Mouth or configures image as usual
	 * @param FancyInterestPointRender render
	 */
	private void handleImage(FancyInterestPointRender render) {
		//if mouth doesn't move after a period of time, recalibrate mouth
		
		System.out.println(myMouth.isOpen());
		System.out.println(framenum);
		
		if (framenum > 30){
			myMouth.GUIrecalibrate = true;
			framenum = 1;
		}
		
		
		if (myMouth.GUIrecalibrate && myMouth.WebCamrecalibrate){
			//during recalibration process, 
			//if player hasn't confirmed recalibration within period of time, 
			//automatically confirm recalibration.
				
			if (framenum < 20){
				recalibrate(render);
				framenum ++;

			}
			else{
				
				myMouth.GUIrecalibrate = false;
				myMouth.WebCamrecalibrate = false;
			}
		}
		
		//if not recalibrating, then configure
		else{
			configure(render);
		}
		
	}

	/**Locates the mouth's position based on its previous position
	 * @param render
	 */
	private void configure(FancyInterestPointRender render) {
		Point2D_F64 closestpt = null;
		double closestDist = 800;
		float closestRadius = 0;
		
		Point2D_F64 closestBiggest = null;
		double closestBDist = 800;
		float closestBRadius = 0;
		
		//loop though all interest points
		for (int i = 0; i < detector.getNumberOfFeatures(); i++){
			Point2D_F64 pt = detector.getLocation(i);

			double scale = detector.getScale(i);
			int radius = (int)(scale* BoofDefaults.SCALE_SPACE_CANONICAL_RADIUS);
			
			//finds closest interest point and closest interest point of similar size
			if (radius > 17){
				if(mouth != null){
					if (mouth.center.distance(pt) < 100){
						//find closest interest point
						if (mouth.center.distance(pt) < closestDist){
							closestpt = pt;
							closestDist = mouth.center.distance(pt);
							closestRadius = radius;
						}
						//find closest interestpoint of similar size
						if (mouth.center.distance(pt) < closestBDist && (mouth.radius-radius < 10 || radius - mouth.radius < 10)){
							closestBiggest = pt;
							closestBDist = mouth.center.distance(pt);
							closestBRadius = radius;
							
						}
					}	
				}
			}

		}
		
		//if mouth doesn't exist, draw the "reset"/default objects
		if (mouth == null){
			drawResetObjects(render);
		}
		
		//draw interest point that represents mouth
		drawMouth(render, closestpt, closestRadius, closestBiggest,
				closestBRadius);
	}


	/**Draw the interest point that best represents the mouth
	 * @param render
	 * @param closestpt
	 * @param closestRadius
	 * @param closestBiggest
	 * @param closestBRadius
	 */
	private void drawMouth(FancyInterestPointRender render,
			Point2D_F64 closestpt, float closestRadius,
			Point2D_F64 closestBiggest, float closestBRadius) {
		//If no idea where mouth is, guess
		if (mouth == null && mouthguesspt != null && mouthguess != 0.0){
			
			mouth = new Circle2D_F64();
			mouth.center= mouthguesspt;
			mouth.radius= mouthguess;
		}
		//if mouth exists, draw closestBiggest "mouth" if exist, or closest "mouth".
		if(mouth != null){
			if (closestBiggest != null){
				mouth.center = new Point2D_F64(closestBiggest.x, closestBiggest.y);
				mouth.radius = closestBRadius;
			} else if (closestpt != null) {
				mouth.center = new Point2D_F64(closestpt.x, closestpt.y); 
				mouth.radius = closestRadius;
			}
			render.addCircle((int)mouth.center.x ,(int)mouth.center.y, (int) mouth.radius , Color.WHITE);
			
		}
		
		//check if new mouth is equal to old Mouth
		if (mouth != null){
			
			Mouth tempMouth = new Mouth(1,1,1);
			tempMouth.set((float)mouth.center.x ,(float)mouth.center.y, (float) mouth.radius/100, true, false);
			if (myMouth.equals(tempMouth)){
				framenum++;
				mouthCount ++;
				if (mouthCount >15){
					tempMouth.setOpen(false);
				}
			}
			else{
				framenum = 1;
				mouthCount = 1;
				tempMouth.setOpen(true);
			}
			//update myMouth
			myMouth = tempMouth;
			
		}
	}


	/**draw reset/default objects if no mouth exists
	 * @param FancyInterestPointRender render
	 */
	private void drawResetObjects(FancyInterestPointRender render) {
		//draw rectangle
		VisualizeShapes.drawPolygon(rectangle, true, g2);
		
		//loops through all interestpoints
		for( int i = 0; i < detector.getNumberOfFeatures(); i++ ) {
			Point2D_F64 pt = detector.getLocation(i);
			// note how it checks the capabilities of the detector
			if( detector.hasScale() ) {
				double scale = detector.getScale(i);	
				int radius = (int)(scale* BoofDefaults.SCALE_SPACE_CANONICAL_RADIUS);
				//find 1st interest point of certain size within rectangle. This becomes the mouth
				if (radius > 17){
					if (inrectangle(pt)){
						if (radius > mouthguess){
							mouthguesspt = pt;
							mouthguess = radius;
						}
						//draw interest points of certain size in rectangle,red
						render.addCircle((int)pt.x,(int)pt.y,radius, Color.RED);
						
					}else{
						//draw interest points of certain size outside rectangle, black
						render.addCircle((int)pt.x,(int)pt.y,radius, Color.BLACK);
					}
				} 			
			}
		}
	}
	
	/**recalibrates mouth
	 * @param FancyInterestPointRender render
	 */
	public void recalibrate(FancyInterestPointRender render){
		//draw rectangle
		VisualizeShapes.drawPolygon(rectangle, true, g2);
		
		//loop through all interest points
		for( int i = 0; i < detector.getNumberOfFeatures(); i++ ) {
			Point2D_F64 pt = detector.getLocation(i);
			if( detector.hasScale() ) {
				double scale = detector.getScale(i);	
				int radius = (int)(scale* BoofDefaults.SCALE_SPACE_CANONICAL_RADIUS);
				//find interest point of certain size in rectangle, this becomes recalibrated mouth
				// Closest fit becomes our mouth guess
				if (radius > 17){
					if (inrectangle(pt)){
						mouthguesspt = pt;
						mouthguess = radius;
						
						mouth.center = new Point2D_F64(pt.x, pt.y); 
						mouth.radius = radius;
						
						render.addCircle((int)pt.x,(int)pt.y,radius, Color.WHITE);
						
						//check if old Mouth and new mouth is same.
						Mouth tempMouth = new Mouth(1,1,1);
						tempMouth.set((float)mouth.center.x ,(float)mouth.center.y, (float) mouth.radius/100, true, false);
						if (myMouth.equals(tempMouth)){
							if (mouthCount > 15){
								mouthCount ++;
								tempMouth.setOpen(false);
							}
						}
						else{
							mouthCount = 1;
							tempMouth.setOpen(true);
						}
						//update myMouth
						myMouth = tempMouth;
						
						myMouth.set((float)mouth.center.x ,(float)mouth.center.y, (float) mouth.radius/100, true, false);
					}
				}
			}
			
		}
		
	}
 }

