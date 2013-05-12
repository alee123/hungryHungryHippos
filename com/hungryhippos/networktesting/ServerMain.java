package com.hungryhippos.networktesting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.network.ConnectionListener;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.system.JmeContext;

public class ServerMain extends SimpleApplication {
	private RigidBodyControl wall_phy;
	private BulletAppState bulletAppState;
	private RigidBodyControl ball_phy;
	private static final Sphere sphere;
	private float wallSide = 5f;
	private float wallThickness = .2f;
	Server myServer = null;
	public List<FrogControl> frog_phy = new ArrayList<FrogControl>();
	public List<Node> frog_nodes = new ArrayList<Node>();
	public List<Integer> scores = new ArrayList<Integer>();
	public List<Boolean> canEat = new ArrayList<Boolean>();
	
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
	    Serializer.registerClass(FrogMessage.class);
	  }

	@Override
	public void simpleInitApp() {
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		bulletAppState.getPhysicsSpace().setGravity(Vector3f.ZERO);
		WorldGenerator generator = new WorldGenerator();
		generator.initWorld();
		
		try {
			myServer = Network.createServer(6143);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//myServer.addConnectionListener(new HelloConnectionListener());
		myServer.addConnectionListener(new InitConnectionListener());
		myServer.addMessageListener(new Listener());
	    myServer.start();
	    
	  }
	
	@Override
	public void simpleUpdate(float tpf){
		for (int i = 0; i< canEat.size(); i++){
			if (canEat.get(i)){
				Integer score = scores.get(i); 
				score += frog_phy.get(i).getRecentCollisions().eatBalls();
				scores.set(i,score);
			}
		}
		
		if (timer.getTime() > 200){
			for (FrogControl phy : frog_phy){
				phy.getRecentCollisions().timeOutList();
				timer.reset();
			}
	    }
		
		ArrayList<Vector3f> ballPos = new ArrayList<Vector3f>();
		for (Spatial thing : rootNode.getChildren()){
			if (thing.getName() == "marble"){
				ballPos.add(thing.getLocalTranslation());
			}
		}
		
		
		myServer.broadcast(new NewPosMessage(ballPos, scores));
	}
	
	private class WorldGenerator{
		   
		private Material mat;		

		public void initWorld() {
			   	initMaterials();
			    initWalls();
			    initMarbles();
		   }
		   
		   	/** Initialize the materials used in this scene. */
			public void initMaterials() {
				mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			}
		 
		  /** This loop builds a wall out of individual bricks. **/
		public void initWalls() {
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
			  ArrayList<Material> materials = new ArrayList<Material>();
			  int index;
		      Random randomGenerator = new Random();  
			  materials.add(mat);
			  materials.add(mat);
			  materials.add(mat);
			  for (int i=0;i<3;i++){
				  for (int j=0;j<3;j++){		  
					  for (int k=0;k<3;k++){
						  Vector3f vel = new Vector3f(i, j, k).mult(2);
						  Vector3f pos = new Vector3f(i,j,k);
						  index = randomGenerator.nextInt(materials.size());
						  makeMarble(vel, pos, materials.get(index));
					  }
				  }
			  }
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
		  
		   
	   }

	  private class InitConnectionListener implements ConnectionListener {

			@Override
			public void connectionAdded(Server server, HostedConnection client) {
				if (frog_phy.size()>=2) {
					server.broadcast(Filters.in(client), new NopeMessage());
				}
				ArrayList<Vector3f> ballPos = new ArrayList<Vector3f>();
				for (Spatial thing : rootNode.getChildren()){
					if (thing.getName() == "marble"){
						ballPos.add(thing.getLocalTranslation());
					}
				}
				Message message = new HelloMessage(wallSide, ballPos, frog_phy.size());
				initHippo(frog_phy.size());
				server.broadcast(Filters.in(client), message);
			}

			@Override
			public void connectionRemoved(Server server, HostedConnection client) {
			}
			
			public void initHippo(int playerNum){
				frog_nodes.add(new Node("hippo"));
				Vector3f hippo_loc = new Vector3f(3,3,3);
				frog_nodes.get(playerNum).setLocalTranslation(hippo_loc);
				frog_phy.add(new FrogControl(new SphereCollisionShape(1.5f),3, bulletAppState));
				frog_nodes.get(playerNum).addControl(frog_phy.get(playerNum));
				bulletAppState.getPhysicsSpace().add(frog_phy.get(playerNum));
				scores.add(0);
				canEat.add(false);
			}
		}
	  
	  private class Listener implements MessageListener<HostedConnection> {

			@Override
			public void messageReceived(HostedConnection source, Message message) {
				if (message instanceof HelloMessage) {
				      HelloMessage helloMessage = (HelloMessage) message;
				      System.out.println("Server received '" +helloMessage.getHello() +"' from client #"+source.getId() );
				}
				if (message instanceof FrogMessage) {
					FrogMessage frogMessage = (FrogMessage) message;
					frog_nodes.get(source.getId()).setLocalTranslation(frogMessage.getFrogPos());
					if (frogMessage.getCanEat()){
						canEat.set(source.getId(), true);
					}
				}
			}
		}
	
	}
