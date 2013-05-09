package com.hungryhippos.networktesting;

import java.io.IOException;
import java.util.ArrayList;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.ConnectionListener;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.system.JmeContext;

public class ServerMain extends SimpleApplication {
	Material mat;
	private RigidBodyControl wall_phy;
	private BulletAppState bulletAppState;
	private RigidBodyControl ball_phy;
	private static final Sphere sphere;
	Server myServer = null;
	
	static {
		/** Initialize the marble geometry */
		sphere = new Sphere(32, 32, 0.4f, true, false);
		sphere.setTextureMode(TextureMode.Projected);
	}
	
	
	  public static void main(String[] args) {
	    ServerMain app = new ServerMain();
	    app.start(JmeContext.Type.Headless); // headless type for servers!
	    Serializer.registerClass(NewPosMessage.class);
	    Serializer.registerClass(HelloMessage.class);
	  }

	@Override
	public void simpleInitApp() {
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		initWorld();
		
		try {
			myServer = Network.createServer(6143);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//myServer.addConnectionListener(new HelloConnectionListener());
		myServer.addConnectionListener(new InitConnectionListener());
		myServer.addMessageListener(new ServerListener());
	    myServer.start();
	    
	  }
	
	@Override
	public void simpleUpdate(float tpf){
		ArrayList<Vector3f> ballPos = new ArrayList<Vector3f>();
		for (Spatial thing : rootNode.getChildren()){
			if (thing.getName() == "marble"){
				ballPos.add(thing.getLocalTranslation());
			}
		}
		myServer.broadcast(new NewPosMessage(ballPos));
	}
	
	public void initWorld() {
	   	initMaterials();
	    initWalls();
	    initMarbles();
   }
   
   	/** Initialize the materials used in this scene. */
	public void initMaterials() {
		mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	    mat.setColor("Color", new ColorRGBA(1,1,1,0.1f));
	    mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);    		
	    
	}
 
  /** This loop builds a wall out of individual bricks. **/
	public void initWalls() {
	    Box level = new Box(Vector3f.ZERO, 5, 1, 5);   
	
	    Vector3f bottom_loc = new Vector3f(0,-1*5,0);
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
	    /** Make brick physical with a mass > 0.0f. */
	    wall_phy = new RigidBodyControl(0.0f);
	    /** Add physical brick to physics space. */
	    wall_geo.addControl(wall_phy);
	    bulletAppState.getPhysicsSpace().add(wall_phy);
	    wall_phy.setRestitution(1f);
	    wall_phy.setFriction(0f);
	    wall_phy.setDamping(0f, 0f);
	  }
  
  
	  public void initMarbles(){
		  Vector3f vel = new Vector3f(0, -1, 0);
		  Vector3f pos = new Vector3f(0, 0, 2);
		  makeMarble(vel, pos, mat);
		  vel = new Vector3f(0, -1, 0);
		  pos = new Vector3f(2, 0, 0);
		  makeMarble(vel, pos, mat);
		  vel = new Vector3f(0, -1, 0);
		  pos = new Vector3f(-2, 0, 0);
		  makeMarble(vel, pos, mat);
		  vel = new Vector3f(0, -1, 0);
		  pos = new Vector3f(0, 0, -2);
		  makeMarble(vel, pos, mat);
		  vel = new Vector3f(0, -1, 0);
		  pos = new Vector3f(0, 0, 0);
		  makeMarble(vel, pos, mat);
	  }
 
	  /** This method creates one individual physical marble. **/
	
	   private void makeMarble(Vector3f vel_vect, Vector3f pos, Material mat) {
	    /** Create a marble geometry and attach to scene graph. */
	    Geometry ball_geo = new Geometry("marble", sphere);   
	    ball_geo.setMaterial(mat);
	    rootNode.attachChild(ball_geo);
	    /** Position the marble  */  
	    ball_geo.setLocalTranslation(pos);
	    /** Make the ball physical with a mass > 0.0f */
	    ball_phy = new RigidBodyControl(.001f);
	    /** Add physical ball to physics space. */
	    ball_geo.addControl(ball_phy);
	    bulletAppState.getPhysicsSpace().add(ball_phy);
	    /** Accelerate the physical ball to shoot it. */
	    ball_phy.setLinearVelocity(vel_vect);
	    ball_phy.setRestitution(1f);
	    ball_phy.setFriction(0f);
	    ball_phy.setDamping(0f, 0f);
	  }

	  private class InitConnectionListener implements ConnectionListener {

			@Override
			public void connectionAdded(Server server, HostedConnection client) {
				ArrayList<Vector3f> ballPos = new ArrayList<Vector3f>();
				for (Spatial thing : rootNode.getChildren()){
					if (thing.getName() == "marble"){
						ballPos.add(thing.getLocalTranslation());
					}
				}
				Message message = new HelloMessage(5f, ballPos);
				//System.out.println("good");
				server.broadcast(message);
			}

			@Override
			public void connectionRemoved(Server server, HostedConnection client) {
			}
			
		}
	
	}
