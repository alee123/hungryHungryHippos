package com.hungryhippos.networktesting;

import java.util.ArrayList;

import com.jme3.math.Vector3f;
import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.Server;

public class HelloConnectionListener implements ConnectionListener {

	@Override
	public void connectionAdded(Server server, HostedConnection client) {
		Message message = new HelloMessage(1f, new ArrayList<Vector3f>());
		server.broadcast(message);
	}

	@Override
	public void connectionRemoved(Server server, HostedConnection client) {
		// TODO Auto-generated method stub
		
	}
	
}
