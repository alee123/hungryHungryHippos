package FullGame;

import java.awt.image.BufferedImage;

public interface Analyzer {
	
	InterestPointFactory nextFactory = null;

	BufferedImage analyze(BufferedImage workImage, BufferedImage showImage);

	
	

}
