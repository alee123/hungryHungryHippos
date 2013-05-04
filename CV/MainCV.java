package CV;

import javax.swing.SwingUtilities;

import net.hungryhippos.real.Mouth;

public class MainCV {
	public static void main(String args[]){
    	
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                        new WebCam(new BinaryFactory(new InterestPointFactory(new Mouth(.5f,.5f,.5f))));
                }
        });
	}
}
