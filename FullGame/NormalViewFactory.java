
import java.awt.image.BufferedImage;

public class NormalViewFactory implements Analyzer {

	@Override
	public BufferedImage analyze(BufferedImage bufferedImage) {
		return bufferedImage;
	}

}
