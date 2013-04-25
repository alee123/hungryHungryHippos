package com.hungryhippos.game.jmonkey;
 
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
 
/** Sample 4 - how to trigger repeating actions from the main update loop.
 * In this example, we make the player character rotate. */
public class BasicPhysics extends SimpleApplication {
	private BulletAppState bulletAppState;
	private RigidBodyControl sphere1;
	private RigidBodyControl sphere2;
	private RigidBodyControl sphere3;
	private RigidBodyControl wallRBC;
 
    public static void main(String[] args){
        BasicPhysics app = new BasicPhysics();
        app.start();
    }
 
    protected Geometry player;
	protected Geometry player2;
	protected Geometry player3;
	protected Geometry wallGeometry;
 
    @Override
    public void simpleInitApp() {
    	
    	bulletAppState = new BulletAppState();
    	stateManager.attach(bulletAppState);
    	
    	bulletAppState.getPhysicsSpace().setGravity(Vector3f.ZERO);
 
        Sphere b = new Sphere(30, 30, 1);
        Box wall = new Box(50, 1, 50);
        
        player = createSphere(b);
        
        player2 = createSphere(b);
        player2.move(0,4f,0);
        
        player3 = createSphere(b);
        player3.move(0,8f,0);
        
        wallGeometry = new Geometry("wall", wall);
        Material mat2 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Blue);
        wallGeometry.setMaterial(mat2);
        wallGeometry.move(new Vector3f(0,-20,0));
        
        CollisionShape boxShape = CollisionShapeFactory.createBoxShape(player);
        sphere1 = new RigidBodyControl(boxShape,100f);
        sphere2 = new RigidBodyControl(boxShape, 100f);
        sphere3 = new RigidBodyControl(boxShape, 100f);
        sphere1.setLinearVelocity(new Vector3f(0, -50, 0));
        sphere2.setLinearVelocity(new Vector3f(0,-50,0));
        sphere3.setLinearVelocity(new Vector3f(0,-50,0));
        
        
        CollisionShape wallShape = CollisionShapeFactory.createBoxShape(wallGeometry);
        wallRBC = new RigidBodyControl(wallShape, 0);
        
        sphere1.setGravity(Vector3f.ZERO);
        sphere2.setGravity(Vector3f.ZERO);
        sphere3.setGravity(Vector3f.ZERO);
        wallRBC.setGravity(Vector3f.ZERO);
        
        player.addControl(sphere1);
        player2.addControl(sphere2);
        player3.addControl(sphere3);
        wallGeometry.addControl(wallRBC);
        
        rootNode.attachChild(player);
        rootNode.attachChild(player2);
        rootNode.attachChild(player3);
        rootNode.attachChild(wallGeometry);
        
        bulletAppState.getPhysicsSpace().add(player);
        bulletAppState.getPhysicsSpace().add(player2);
        bulletAppState.getPhysicsSpace().add(player3);
        bulletAppState.getPhysicsSpace().add(wallRBC);
    }

	private Geometry createSphere(Sphere b) {
		Geometry thing = new Geometry("red cube", b);
        Material mat2 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);
        thing.setMaterial(mat2);
        return thing;
	}
 

}