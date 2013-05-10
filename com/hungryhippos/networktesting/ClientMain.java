package com.hungryhippos.networktesting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.system.JmeContext;

public class ClientMain extends SimpleApplication {
	private static Sphere sphere;
	private List<Vector3f> ballPos = new ArrayList<Vector3f>();
	private Material wall_mat;
	private Material ball_mat;
	private static final float wallThickness = .2f;
	private float wallSide;
	private WebCam webcam;
	private InterestPointFactory interestPoint;
	private Mouth mouth = new Mouth(0f, 0f, 0f);
	private Node hippo_node;
	private boolean canEat;
	private ActionListener actionListener = new ActionListener() {
		public void onAction(String name, boolean keyPressed, float tpf) {
			if (name.equals("Eating") && !keyPressed) {
				canEat = true;
			}
		}
	};
	private Client myClient = null;
	public int playerNum;
	public List<Integer> scores = new ArrayList<Integer>();
	
	static {
		/** Initialize the marble geometry */
		sphere = new Sphere(32, 32, 0.4f, true, false);
		sphere.setTextureMode(TextureMode.Projected);
	}  
	
	public static void main(String[] args) {
	    ClientMain app = new ClientMain();
	    Serializer.registerClass(NewPosMessage.class);
	    Serializer.registerClass(HelloMessage.class);
	    Serializer.registerClass(FrogMessage.class);
	    app.start(JmeContext.Type.Display); // standard display type
	  }

	@Override
	public void simpleInitApp() {
		initMaterials();
		initHippo();
		initKeys();
		
	    interestPoint = new InterestPointFactory(mouth );
	    webcam = new WebCam(new BinaryFactory(interestPoint));
	    
		try {
			//change IP address to that of the computer hosting the server
			myClient = Network.connectToServer("192.168.48.38",6143);
		} catch (IOException e) {
			e.printStackTrace();
		}
		myClient.addMessageListener(new Listener(), HelloMessage.class, NewPosMessage.class);
		myClient.start();	
	}
	
	@Override
	public void simpleUpdate(float tpf) {
		List<Spatial> currentChildren = new ArrayList<Spatial>(); 
    	for (Spatial child : rootNode.getChildren()) {
    		if (child.getName() == "marble") {
    			currentChildren.add(child);
    		}
    	};
    	for (int i=0; i < Math.max(currentChildren.size(),ballPos.size()); i++) {
    		try {
    			Vector3f pos = ballPos.get(i);
    			try {
    				Spatial marble = currentChildren.get(i);
    				marble.setLocalTranslation(pos);			    				
    			} catch (IndexOutOfBoundsException e){
    				makeMarble(pos, ball_mat);
    			} 
    		} catch (IndexOutOfBoundsException e){
    			rootNode.detachChild(currentChildren.get(i));
    		}
    	}
    	mouth = interestPoint.myMouth;
    	Vector3f hippo_pos = mouth.getPosition();
        hippo_pos.setY(0);
        hippo_pos = hippo_pos.mult(wallSide*3).subtract(new Vector3f(wallSide,wallSide,wallSide));

        if (hippo_pos.getZ()>wallSide) { hippo_pos.setZ(wallSide);}
        else if (hippo_pos.getZ()<-1*wallSide) { hippo_pos.setZ(-1*wallSide);}

        if (hippo_pos.getX()>wallSide) { hippo_pos.setX(wallSide);}
        else if (hippo_pos.getX()<-1*wallSide) { hippo_pos.setX(-1*wallSide);}
        
        if (playerNum == 0){
        	hippo_node.setLocalTranslation(hippo_pos);
        }
        else if (playerNum == 1){
        	hippo_node.setLocalTranslation(hippo_pos.getX(), hippo_pos.getZ(), -1 * hippo_pos.getY());
        }
        else {
        	hippo_node.setLocalTranslation(hippo_pos.getY(), hippo_pos.getZ(), hippo_pos.getX());
        }
        
        Message frogMessage = new FrogMessage(canEat, hippo_pos);
	    myClient.send(frogMessage);
	    canEat = false;
	}

	
	/** Initialize the materials used in this scene. */
	public void initMaterials() {
		wall_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	    wall_mat.setColor("Color", new ColorRGBA(1,1,1,0.1f));
	    wall_mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);    		
	  
	    ball_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");  	
	    //ball_mat.setBoolean("UseMaterialColors", true);
	    ball_mat.setColor("Color", new ColorRGBA(1,0,0,0.01f));
    	//ball_mat.setColor("Ambient", ColorRGBA.Red);   // ... color of this object
    	//ball_mat.setColor("Diffuse", ColorRGBA.White);   // ... color of light being reflectedd
	    
	}
	
	private void makeMarble(Vector3f pos, Material mat) {
	    /* Create a marble geometry and attach to scene graph. */
	    Geometry ball_geo = new Geometry("marble", sphere);   
	    ball_geo.setMaterial(mat);
	    rootNode.attachChild(ball_geo);
	    /* Position the marble  */  
	    ball_geo.setLocalTranslation(pos);
	}
	
	public void initHippo(){ 
	    HashMap<String, Material> materials = new HashMap<String, Material>();
		Material frog_mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");   	
	    materials.put("frog_mat", frog_mat);

		Material eye_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	    eye_mat.setColor("Color", ColorRGBA.White);	        
	    materials.put("eye_mat", eye_mat);

	    Material mouth_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	    mouth_mat.setColor("Color", new ColorRGBA(.5f, 0f,0f, 0f));		    
	    materials.put("mouth_mat", mouth_mat);

	    Material puple_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	    puple_mat.setColor("Color", ColorRGBA.Black);
	    materials.put("puple_mat", puple_mat);
		   
		Frog frog = new Frog(materials);
		hippo_node = frog.makeFrog();
		   
		rootNode.attachChild(hippo_node);
		hippo_node.rotate(90*FastMath.DEG_TO_RAD, 0f, 180*FastMath.DEG_TO_RAD);
   		Vector3f hippo_loc = new Vector3f(3,3,3);
		hippo_node.setLocalTranslation(hippo_loc);		

	}
	
	private void initKeys() {
		   inputManager.addMapping("Eating", new KeyTrigger(KeyInput.KEY_SPACE));
		   inputManager.addListener(actionListener,  new String[]{"Eating"});
	   }
	
	class Listener implements MessageListener<Client>{
		
		 @Override
		 public void messageReceived(Client source, Message message) {
			    if (message instanceof HelloMessage) {
			      HelloMessage helloMessage = (HelloMessage) message;
			      System.out.println("Client #"+source.getId()+" received: '"+ helloMessage.getHello()+"'");
			      List<Vector3f> pos = helloMessage.getBalls();
			      wallSide = helloMessage.getHello();
			      initWalls(wallSide);
			      for (Vector3f p : pos){
			    	  makeMarble(p,ball_mat);
			      }
			      playerNum = helloMessage.getPlayerNum();
			      
			      if (playerNum == 0){
			    	  cam.setLocation(new Vector3f(0, 2*wallSide, 0));
					  cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
			      }
			      else if (playerNum == 1){
			    	  cam.setLocation(new Vector3f(0, 0, 2*wallSide));
					  cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
			      }
			      else {
			    	  cam.setLocation(new Vector3f(2*wallSide, 0, 0));
					  cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
			      }
			      
			    } 
			    else if (message instanceof NewPosMessage) {
			    	NewPosMessage posMessage = (NewPosMessage) message;
			    	ballPos = posMessage.getBalls();
			    	scores  = posMessage.getScores();
			    }
		 }
		 
		 public void initWalls(float wallSide) {
			    /*Box level = new Box(Vector3f.ZERO, sideLen, 1, sideLen);   
			
			    Vector3f bottom_loc = new Vector3f(0,-1*sideLen,0);
			    makeWall(bottom_loc, level);  */
			 	Box facing = new Box(Vector3f.ZERO, wallSide, wallSide, wallThickness);
			    Box side = new Box(Vector3f.ZERO, wallThickness, wallSide, wallSide);
			    Box level = new Box(Vector3f.ZERO, wallSide, wallThickness, wallSide); 

			    Vector3f front_loc = new Vector3f(0, 0, wallSide);
			    makeWall(front_loc, facing);

			    Vector3f back_loc = new Vector3f(0, 0, -1*wallSide);
			    makeWall(back_loc, facing);

			    Vector3f left_loc = new Vector3f(-1*wallSide, 0, 0);
			    makeWall(left_loc, side);    
			    
			    Vector3f right_loc = new Vector3f(wallSide, -0, 0);
			    makeWall(right_loc, side); 

			    Vector3f top_loc = new Vector3f(0,wallSide,0);
			    makeWall(top_loc, level);    

			    Vector3f bottom_loc = new Vector3f(0,-1*wallSide,0);
			    makeWall(bottom_loc, level);  
			    
			  }
		 
		 /* This method creates one individual physical wall. */
		  private void makeWall(Vector3f loc, Box orientedBox) {
		    /** Create a brick geometry and attach to scene graph. */
			Geometry wall_geo = new Geometry("Wall", orientedBox);
		    wall_geo.setMaterial(wall_mat);
		    wall_geo.setQueueBucket(Bucket.Transparent); 
		    rootNode.attachChild(wall_geo);
		    /** Position the brick geometry  */
		    wall_geo.setLocalTranslation(loc);
		  }
		 
	}
}
