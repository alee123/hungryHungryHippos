package net.dianavermilya.hello;
 
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
 
/** Sample 4 - how to trigger repeating actions from the main update loop.
 * In this example, we make the player character rotate. */
public class HelloLoop extends SimpleApplication {
 
    public static void main(String[] args){
        HelloLoop app = new HelloLoop();
        app.start();
    }
 
    protected Geometry player;
 
    @Override
    public void simpleInitApp() {
 	   AmbientLight al = new AmbientLight();
 	   al.setColor(ColorRGBA.White.mult(2f));
 	   rootNode.addLight(al);
 
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        player = new Geometry("blue cube", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
	    mat.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
    	mat.setColor("Ambient", ColorRGBA.Blue);   // ... color of this object
    	mat.setColor("Diffuse", ColorRGBA.Blue);   // ... color of light being reflected
        player.setMaterial(mat);
        rootNode.attachChild(player);
    }
 
    /* This is the update loop */
    @Override
    public void simpleUpdate(float tpf) {
        // make the player rotate
        player.rotate(0, 2*tpf, 0); 
    }
}