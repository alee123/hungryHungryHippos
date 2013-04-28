package net.hungryhippos.real;

import net.hungryhippos.real.eating.RecentCollisions;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;

/*Makes the hippo eat things.
 * 
 * @author rboy
 * @author vcoleman
 * 
 */

public class HippoControl extends RigidBodyControl implements PhysicsCollisionListener{
	private BulletAppState bulletAppState;
	private RecentCollisions recentCollisions;

	public HippoControl(int numLists, BulletAppState bulletAppState) {
		constructHelper(numLists, bulletAppState);
	}
	
	public HippoControl(float mass, int numLists, BulletAppState bulletAppState) {
		super(mass);
		constructHelper(numLists, bulletAppState);
	}
	
	private void constructHelper(int numLists, BulletAppState bulletAppState) {
		this.bulletAppState = bulletAppState;
		bulletAppState.getPhysicsSpace().addCollisionListener(this);
		recentCollisions = new RecentCollisions(numLists, bulletAppState);
	}
	
	public RecentCollisions getRecentCollisions(){
		return recentCollisions;
	}

	@Override
	public void collision(PhysicsCollisionEvent event) {
		String nameA = event.getNodeA().getName();
		String nameB = event.getNodeB().getName();
		if (nameA.equals("hippo")){
			if (nameB.equals("marble")){
				recentCollisions.addBall(event.getNodeB());
			}
		}
		else if (nameB.equals("hippo")){
			if (nameA.equals("marble")){
				recentCollisions.addBall(event.getNodeA());
			}
		}
	}

}
