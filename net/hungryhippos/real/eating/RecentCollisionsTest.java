package net.hungryhippos.real.eating;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;

import FullGame.RecentCollisions;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class RecentCollisionsTest extends SimpleApplication{
	RecentCollisions recentCollisions;
	BulletAppState bulletAppState;
	private RigidBodyControl    ball_phy;

	@Before
	public void setUp() throws Exception {
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		recentCollisions = new RecentCollisions(4,bulletAppState);
	}

	@Test
	public void addBall() {
		assertTrue(recentCollisions.recentCollisions.get(3).size() == 0);
		recentCollisions.addBall(new Geometry("thing", new Box(1,1,1)));
		assertTrue(recentCollisions.recentCollisions.get(3).size() == 1);
	}
	
	@Test
	public void timeOutList() {
		recentCollisions.addBall(new Geometry("thing", new Box(1,1,1)));
		recentCollisions.timeOutList();
		assertTrue(recentCollisions.recentCollisions.get(2).size() == 1);
		assertTrue(recentCollisions.recentCollisions.get(3).size() == 0);
		assertTrue(recentCollisions.recentCollisions.size() == 4);
	}
	
	@Test
	public void eatBalls() {
		for (int i=0; i<4; i++){
			recentCollisions.timeOutList();
			Geometry ball_geo = new Geometry("thing", new Box(1,1,1));
			ball_phy = new RigidBodyControl(.001f);
			ball_geo.addControl(ball_phy);
			rootNode.attachChild(ball_geo);
			bulletAppState.getPhysicsSpace().add(ball_phy);
			recentCollisions.addBall(rootNode.getChild(i));
		}

		
		recentCollisions.eatBalls();
		assertTrue(rootNode.getChildren().size() == 0);
		assertTrue(bulletAppState.getPhysicsSpace().getRigidBodyList().size() == 0);
		assertTrue(recentCollisions.recentCollisions.size() == 4);
		assertTrue(recentCollisions.recentCollisions.get(0).size() == 0);
		assertTrue(recentCollisions.recentCollisions.get(1).size() == 0);
		assertTrue(recentCollisions.recentCollisions.get(2).size() == 0);
		assertTrue(recentCollisions.recentCollisions.get(3).size() == 0);
		
		
		
	}

	@Override
	public void simpleInitApp() {
		// TODO Auto-generated method stub
		
	}

}
