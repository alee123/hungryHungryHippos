package com.hungryhippos.networktesting;

import java.util.ArrayList;

import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;

public class ClientListener implements MessageListener<Client>{
	 @Override
	 public void messageReceived(Client source, Message message) {
		    if (message instanceof HelloMessage) {
		      HelloMessage helloMessage = (HelloMessage) message;
		      System.out.println("Client #"+source.getId()+" received: '"+ helloMessage.getHello()+"'");
		    } 
		  }
	}
