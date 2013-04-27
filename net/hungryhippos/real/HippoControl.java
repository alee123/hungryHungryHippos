package net.hungryhippos.real;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;

public class HippoControl extends RigidBodyControl implements PhysicsCollisionListener{
	private BulletAppState bulletAppState;

	public HippoControl(BulletAppState bulletAppState) {
		constructHelper(bulletAppState);
	}

	public HippoControl(float mass, BulletAppState bulletAppState) {
		super(mass);
		constructHelper(bulletAppState);
	}

	public HippoControl(CollisionShape shape, BulletAppState bulletAppState) {
		super(shape);
		constructHelper(bulletAppState);
	}

	public HippoControl(CollisionShape shape, float mass, BulletAppState bulletAppState) {
		super(shape, mass);
		constructHelper(bulletAppState);
	}
	
	private void constructHelper(BulletAppState bulletAppState) {
		this.bulletAppState = bulletAppState;
		bulletAppState.getPhysicsSpace().addCollisionListener(this);
	}

	@Override
	public void collision(PhysicsCollisionEvent event) {
		String nameA = event.getNodeA().getName();
		String nameB = event.getNodeB().getName();
		if (nameA.equals("hippo")){
			if (nameB.equals("marble")){
				Spatial nodeB = event.getNodeB();
				nodeB.removeFromParent();
				bulletAppState.getPhysicsSpace().remove(nodeB);
			}
		}
		else if (nameB.equals("hippo")){
			if (nameA.equals("marble")){
				Spatial nodeA = event.getNodeA();
				nodeA.removeFromParent();
				bulletAppState.getPhysicsSpace().remove(nodeA);
			}
		}
	}

}
