package FullGame;

 
import java.util.HashMap;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Sphere;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
/** Sample 2 - How to use nodes as handles to manipulate objects in the scene.
 * You can rotate, translate, and scale objects by manipulating their parent nodes.
 * The Root Node is special: Only what is attached to the Root Node appears in the scene. */
 
public class Frog {
	private HashMap<String, Material> materials;

	public Frog(HashMap<String, Material> materials){
		this.materials = materials;
		
	}
 
	public Node makeFrog(){
		
		Material frog_mat = materials.get("frog_mat");
		Material eye_mat = materials.get("eye_mat");
		Material mouth_mat = materials.get("mouth_mat");
		Material puple_mat = materials.get("puple_mat");
		 
    	
        /** create the top half of the head*/
   	
    	Dome top = new Dome(Vector3f.ZERO, 32, 32, 1f, false); // Small hemisphere
    	Geometry top_geo = new Geometry("top", top); // wrap shape into geometry
    	top_geo.setMaterial(frog_mat);                         // assign material to geometry     
        
    	/** Make jaw. */
    	Dome jaw_outside = new Dome(Vector3f.ZERO, 32, 32, 1f, false); // Small hemisphere 	
    	Geometry jaw_outside_geo = new Geometry("jaw", jaw_outside); // wrap shape into geometry
    	jaw_outside_geo.setMaterial(frog_mat);                         // assign material to geometry
    	
    	Circle3d mouthFloor_mesh = new Circle3d(1f, 32); 
    	Geometry mouthFloor_geo = new Geometry("mouth", mouthFloor_mesh);
    	mouthFloor_geo.setMaterial(mouth_mat);

    	
    	Node jaw = new Node("jaw");
    	jaw.attachChild(jaw_outside_geo);
    	jaw.attachChild(mouthFloor_geo);
    	
    	/** Make eye meshes */
    	Sphere Eyeball = new Sphere(32, 32, .3f, false, false); 	
    	Dome Eyelid = new Dome(Vector3f.ZERO, 32, 32, .31f, false);
    	Circle3d Puple = new Circle3d(.1f, 32); 


    	/** Make the left eye*/  	
    	Geometry leftEyeball = new Geometry("leftEyeball", Eyeball); // wrap shape into geometry
    	leftEyeball.setMaterial(eye_mat);
    	Geometry leftEyelid = new Geometry("leftEyelid", Eyelid); // wrap shape into geometry
    	leftEyelid.setMaterial(frog_mat);
    	Geometry leftPuple = new Geometry("leftPuple", Puple);
    	leftPuple.setMaterial(puple_mat);
    	  	    	
    	Node leftEye = new Node("leftEye");
      	leftEye.attachChild(leftEyeball);
    	leftEye.attachChild(leftEyelid);
    	leftEyelid.rotate(-20*FastMath.DEG_TO_RAD, 0f, 20*FastMath.DEG_TO_RAD);
    	leftEye.attachChild(leftPuple);
    	leftPuple.setLocalTranslation(0f,-.05f,0.3f);
    	leftPuple.rotate(-90*FastMath.DEG_TO_RAD, 0f, 0f);
    	
    	/** Make the right eye*/
    	Geometry rightEyeball = new Geometry("rightEyeball", Eyeball); // wrap shape into geometry
    	rightEyeball.setMaterial(eye_mat);
    	Geometry rightEyelid = new Geometry("rightEyelid", Eyelid); // wrap shape into geometry
    	rightEyelid.setMaterial(frog_mat);
    	Geometry rightPuple = new Geometry("rightPuple", Puple);
    	rightPuple.setMaterial(puple_mat);
    	
    	Node rightEye = new Node("rightEye");
    	rightEye.attachChild(rightEyeball);
    	rightEye.attachChild(rightEyelid);
    	rightEyelid.rotate(-20*FastMath.DEG_TO_RAD, 0f, -20*FastMath.DEG_TO_RAD);
    	rightEye.attachChild(rightPuple);
    	rightPuple.setLocalTranslation(0f,-.05f,0.3f);
    	rightPuple.rotate(-90*FastMath.DEG_TO_RAD, 0f, 0f);
    	
        /** Create a pivot node at (0,0,0) and attach it to the root node */
        Node pivot = new Node("pivot");
 
        /** Attach head, mouth, and eyes to the *pivot* node. */
        pivot.attachChild(top_geo);
        pivot.attachChild(jaw);
        top_geo.rotate( 20*FastMath.DEG_TO_RAD , 0f , 0f );
        jaw.rotate( 210*FastMath.DEG_TO_RAD , 0f , 0f );
        pivot.attachChild(leftEye);
        leftEye.setLocalTranslation(-0.53f, 0.57735f, 0.57735f);
        pivot.attachChild(rightEye);
        rightEye.setLocalTranslation(0.53f, 0.57735f, 0.57735f);
        
       
        
        //pivot.rotate(0f, 90*FastMath.DEG_TO_RAD, 0f); //side view  
        return pivot;
        

        
    }
}