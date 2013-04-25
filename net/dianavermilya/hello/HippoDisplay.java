package net.dianavermilya.hello;
 
import java.util.ArrayList;
import java.util.Random;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
 
/**
 * Example 12 - how to give objects physical properties so they bounce and fall.
 * @author base code by double1984, updated by zathras
 */
public class HippoDisplay extends SimpleApplication {
 
	public static void main(String args[]) {
	    HippoDisplay app = new HippoDisplay();
	    app.start();
	}
 
	/** Prepare the Physics Application State (jBullet) */
	private BulletAppState bulletAppState;
 
	/** Prepare Materials */
	Material wall_mat;
	Material red_ball_mat;
	Material blue_ball_mat;
	Material green_ball_mat;
 
	/** Prepare geometries and physical nodes for bricks and cannon balls. */
	private RigidBodyControl    ball_phy;
	private static final Sphere sphere;
	private RigidBodyControl    wall_phy;
	  
	 
	/** dimensions used for transparent walls */
	private static final float wallSide = 3f;
	private static final float wallThickness = .1f;
  

 
	static {
		/** Initialize the marble geometry */
		sphere = new Sphere(32, 32, 0.4f, true, false);
		sphere.setTextureMode(TextureMode.Projected);
	}
 
  @Override
	public void simpleInitApp() {
	    /** Set up Physics Game */
	    bulletAppState = new BulletAppState();
	    stateManager.attach(bulletAppState);
	    
	    bulletAppState.getPhysicsSpace().setGravity(Vector3f.ZERO);
	 
	    /** Configure cam to look at scene */
	    cam.setLocation(new Vector3f(0, 4f, 6f));
	    cam.lookAt(new Vector3f(2, 2, 0), Vector3f.UNIT_Y);
	    
	    /** Initialize the scene, materials, and physics space */
	    initMaterials();
	    initWalls();
	    initMarbles();
	    initLighting();
	}
	 
   	/** Initialize the materials used in this scene. */
	public void initMaterials() {
		wall_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	    wall_mat.setColor("Color", new ColorRGBA(1,1,1,0.1f));
	    wall_mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);    		
	  
	    red_ball_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");  	
	    red_ball_mat.setColor("Color", new ColorRGBA(1,0,0,0.01f));
    	//red_ball_mat.setColor("Ambient", ColorRGBA.Red);   // ... color of this object
    	//red_ball_mat.setColor("Diffuse", ColorRGBA.Red);   // ... color of light being reflected
  	    
	    blue_ball_mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
	    blue_ball_mat.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
    	blue_ball_mat.setColor("Ambient", ColorRGBA.Blue);   // ... color of this object
    	blue_ball_mat.setColor("Diffuse", ColorRGBA.Blue);   // ... color of light being reflected
	   
	    green_ball_mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
	    green_ball_mat.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
    	green_ball_mat.setColor("Ambient", ColorRGBA.Green);   // ... color of this object
    	green_ball_mat.setColor("Diffuse", ColorRGBA.Green);   // ... color of light being reflected
	    
	}
 
  /** This loop builds a wall out of individual bricks. **/
public void initWalls() {
    Box facing = new Box(Vector3f.ZERO, wallSide, wallSide, wallThickness);
    Box side = new Box(Vector3f.ZERO, wallThickness, wallSide, wallSide);
    Box level = new Box(Vector3f.ZERO, wallSide, wallThickness, wallSide); 

    Vector3f front_loc = new Vector3f(0, 0, 3);
    makeWall(front_loc, facing);

    Vector3f back_loc = new Vector3f(0, 0, -3);
    makeWall(back_loc, facing);

    Vector3f left_loc = new Vector3f(-3, 0, 0);
    makeWall(left_loc, side);    
    
    Vector3f right_loc = new Vector3f(3, -0, 0);
    makeWall(right_loc, side); 

    Vector3f top_loc = new Vector3f(0,3,0);
    makeWall(top_loc, level);    

    Vector3f bottom_loc = new Vector3f(0,-3,0);
    makeWall(bottom_loc, level);  
  }
 
/* This method creates one individual physical wall. */
  public void makeWall(Vector3f loc, Box orientedBox) {
    /** Create a brick geometry and attach to scene graph. */
	Geometry wall_geo = new Geometry("Wall", orientedBox);
    wall_geo.setMaterial(wall_mat);
    wall_geo.setQueueBucket(Bucket.Transparent); 
    rootNode.attachChild(wall_geo);
    /** Position the brick geometry  */
    wall_geo.setLocalTranslation(loc);
    /** Make brick physical with a mass > 0.0f. */
    wall_phy = new RigidBodyControl(0.0f);
    /** Add physical brick to physics space. */
    wall_geo.addControl(wall_phy);
    bulletAppState.getPhysicsSpace().add(wall_phy);
  }
  
  
  public void initMarbles(){
	  ArrayList<Material> materials = new ArrayList<Material>();
	  int index;
      Random randomGenerator = new Random();  
	  materials.add(blue_ball_mat);
	  materials.add(red_ball_mat);
	  materials.add(green_ball_mat);
	  for (int i=0;i<3;i++){
		  for (int j=0;j<3;j++){		  
			  for (int k=0;k<4;k++){
				  Vector3f vel = new Vector3f(i, j, k).mult(10);
				  index = randomGenerator.nextInt(materials.size());
				  makeMarble(vel, materials.get(index));
			  }
		  }
	  }
  }
 
  /** This method creates one individual physical marble. **/

   public void makeMarble(Vector3f vel_vect, Material mat) {
    /** Create a marble geometry and attach to scene graph. */
    Geometry ball_geo = new Geometry("marble", sphere);   
    ball_geo.setMaterial(mat);
    rootNode.attachChild(ball_geo);
    /** Position the marble  */
    Vector3f ball_loc = new Vector3f(0,0,0);    
    ball_geo.setLocalTranslation(ball_loc);
    /** Make the ball physical with a mass > 0.0f */
    ball_phy = new RigidBodyControl(.001f);
    /** Add physical ball to physics space. */
    ball_geo.addControl(ball_phy);
    bulletAppState.getPhysicsSpace().add(ball_phy);
    /** Accelerate the physical ball to shoot it. */
    ball_phy.setLinearVelocity(vel_vect);
  }
   public void initLighting(){
	   AmbientLight al = new AmbientLight();
	   al.setColor(ColorRGBA.White.mult(2f));
	   rootNode.addLight(al);
	   
	   
	   /*
	   PointLight lamp_light = new PointLight();
	   lamp_light.setColor(ColorRGBA.White);
	   lamp_light.setRadius(100f);
	   lamp_light.setPosition(new Vector3f(0,0,0));
	   rootNode.addLight(lamp_light);
	   */
   }
 
}