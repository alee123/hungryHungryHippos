/**
 * 
 */
package net.hungryhippos.real.eating;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.bullet.control.RigidBodyControl;

/**Keeps a list of recent collisions so the hippo can eat things that entered its mouth before it was closed
 * 
 * @author rboy
 * @author vcoleman
 *
 */
public class RecentCollisions {
	protected List<Queue<Spatial>> recentCollisions = new LinkedList<Queue<Spatial>>();
	private int numLists;
	private BulletAppState bulletAppState;
	
	/*Constructor
	 * 
	 * @param numLists Controls the number of timesteps into the past a hippo will eat.
	 */
	public RecentCollisions(int numLists, BulletAppState bulletAppState){
		this.numLists = numLists;
		this.bulletAppState = bulletAppState;
		for (int i=0; i<numLists; i++){
			recentCollisions.add(new LinkedList<Spatial>());
		}
	}

	public void addBall(Spatial geometry) {
		recentCollisions.get(numLists-1).add(geometry);
		
	}

	public void timeOutList() {
		recentCollisions.remove(0);
		recentCollisions.add(new LinkedList<Spatial>());
		
	}

	/*
	 * @return number of balls eaten
	 */
	public int eatBalls() {
		int total = 0;
		for(Queue<Spatial> list : recentCollisions){
			for (Spatial ball : list){
				if (bulletAppState.getPhysicsSpace().getRigidBodyList().contains(ball.getControl(0))){
					total++;
					ball.removeFromParent();
					bulletAppState.getPhysicsSpace().remove(ball);
				}
			}
		}
		recentCollisions = new LinkedList<Queue<Spatial>>();
		for (int i=0; i<numLists; i++){
			recentCollisions.add(new LinkedList<Spatial>());
		}
		return total;
	}
}
