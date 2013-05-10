package net.sskikne.Facetrack;

import java.awt.image.BufferedImage;

public class NormalViewFactory implements Analyzer {

	@Override
	public BufferedImage analyze(BufferedImage workImage, BufferedImage showImage) {
		return showImage;
	}

}
