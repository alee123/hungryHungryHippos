package com.hungryhippos.networktesting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.system.JmeContext;

public class ClientMain extends SimpleApplication {
	private static Sphere sphere;
	Material mat;
	ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);
	private List<Vector3f> ballPos = new ArrayList<Vector3f>();
	private static final float wallThickness = .2f;
	
	static {
		/** Initialize the marble geometry */
		sphere = new Sphere(32, 32, 0.4f, true, false);
		sphere.setTextureMode(TextureMode.Projected);
	}  
	
	public static void main(String[] args) {
	    ClientMain app = new ClientMain();
	    Serializer.registerClass(NewPosMessage.class);
	    Serializer.registerClass(HelloMessage.class);
	    app.start(JmeContext.Type.Display); // standard display type
	  }

	@Override
	public void simpleInitApp() {
		initMaterials();
		Client myClient = null;
		try {
			//change IP address to that of the computer hosting the server
			myClient = Network.connectToServer("192.168.48.38",6143);
		} catch (IOException e) {
			e.printStackTrace();
		}
		myClient.addMessageListener(new Listener(this), HelloMessage.class, NewPosMessage.class);
		
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
    				makeMarble(pos, mat);
    			} 
    		} catch (IndexOutOfBoundsException e){
    			rootNode.detachChild(currentChildren.get(i));
    		}
    	}
	}
	
	@Override
    public void destroy() {
        super.destroy();
        executor.shutdown();
    }
	
	/** Initialize the materials used in this scene. */
	public void initMaterials() {
		mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	    mat.setColor("Color", new ColorRGBA(1,1,1,0.1f));
	    mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);    		
	    
	}
	
	private void makeMarble(Vector3f pos, Material mat) {
	    /** Create a marble geometry and attach to scene graph. */
	    Geometry ball_geo = new Geometry("marble", sphere);   
	    ball_geo.setMaterial(mat);
	    rootNode.attachChild(ball_geo);
	    /** Position the marble  */  
	    ball_geo.setLocalTranslation(pos);
	}
	
	class Listener implements MessageListener<Client>{
		ClientMain app;
		
		public Listener(ClientMain app) {
			this.app = app;
		}
		
		 @Override
		 public void messageReceived(Client source, Message message) {
			    if (message instanceof HelloMessage) {
			      HelloMessage helloMessage = (HelloMessage) message;
			      System.out.println("Client #"+source.getId()+" received: '"+ helloMessage.getHello()+"'");
			      List<Vector3f> pos = helloMessage.getBalls();
			      initWalls(helloMessage.getHello());
			      for (Vector3f p : pos){
			    	  makeMarble(p,mat);
			      }
			      Message outMessage = new HelloMessage(6f, pos);
			      source.send(outMessage);
			    } 
			    else if (message instanceof NewPosMessage) {
			    	NewPosMessage posMessage = (NewPosMessage) message;
			    	ballPos = posMessage.getBalls();
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
		    wall_geo.setMaterial(mat);
		    wall_geo.setQueueBucket(Bucket.Transparent); 
		    rootNode.attachChild(wall_geo);
		    /** Position the brick geometry  */
		    wall_geo.setLocalTranslation(loc);
		  }
		 
	}
}
