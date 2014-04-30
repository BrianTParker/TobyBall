package com.game.TobyBall;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.game.TobyBall.Bomb.State;

public class TobyServer {
	
	
	
    private Server server;
    Connection connection;
    ArrayList<Player> playerList;
    ArrayList<Bomb> bombList;
    Random bombRand;
    int bombId;
    
    
    public TobyServer() throws IOException{
    	bombId = 1;
    	bombRand = new Random();
    	playerList = new ArrayList<Player>();
    	populateBombList();
        server = new Server();
        Log.set(Log.LEVEL_DEBUG);
        new Thread(server).start();
        server.bind(54555, 54777);
        
        Kryo kryo = server.getKryo();
        
        kryo.register(NewPlayerList.class);
        kryo.register(ArrayList.class);
        kryo.register(Player.class);
        kryo.register(Point2D.Float.class);
        kryo.register(Texture.class);
        kryo.register(Bomb.class);
        kryo.register(Rectangle.class);
        kryo.register(EndPoint.class);
        kryo.register(ArrayList.class.getClass());
        kryo.register(String.class);
        kryo.register(PlayerId.class);
        kryo.register(SendPosition.class);
        kryo.register(State.class);
        kryo.register(Shrapnel.class);
        kryo.register(boolean.class);
        kryo.register(SendBombs.class);
        kryo.register(RequestBomb.class);
        kryo.register(ArmBomb.class);
        kryo.register(ExplodeBomb.class);
        kryo.register(UpdateAllOtherPositions.class);
        kryo.register(int[].class);
        kryo.register(UpdateGameState.class);
        kryo.register(Player[].class);
        kryo.register(Bomb[].class);
        kryo.register(float[].class);
        kryo.register(State[].class);
        kryo.register(DeleteBomb.class);
        kryo.register(NewBomb.class);
        kryo.register(DeadPlayer.class);
        
        server.addListener(new Listener(){
		 	
			public void disconnected(Connection con){
				
				System.out.println( "Client " + connection.getID() + " Disconnected");
			 	}
			     
		     public void connected(Connection con){
		    	 connection = con;
		    	 Gdx.app.postRunnable(new Runnable() {
			         @Override
			         public void run() {
			        	 Random rand = new Random();
			        	 String asset = "assets/player1.png";
				         playerList.add(new Player(new Point2D.Float((float)rand.nextInt(1000),(float)rand.nextInt(700)), connection.getID(),asset ));
				         
				         
				         NewPlayerList list = new NewPlayerList();
				         list.playerList = playerList;
				         server.sendToAllTCP(list);
				         
				         PlayerId id = new PlayerId();
				         id.id = connection.getID();
				         connection.sendTCP(id);
				         
				         SendBombs newBList = new SendBombs();
			        	 newBList.bombList = bombList;
			        	 
			        	 connection.sendTCP(newBList);
			            
			         }
			    });
		         
		        
		         
		     }
		
		     public void received(Connection connection, Object object){
		         
		         if(object instanceof NewPlayerList){
		        	 NewPlayerList list = new NewPlayerList();
		        	 list.playerList = playerList;
		        	 connection.sendTCP(list);
		         }else if(object instanceof PlayerId){
		        	 PlayerId playerId = new PlayerId();
		        	 playerId.id = connection.getID();
		        	 connection.sendTCP(playerId);
		        	 
		        	 
		         }else if(object instanceof SendPosition){
		         
		        	 SendPosition newPos = (SendPosition)object;
		        	 for(Player p : playerList){
		        		 if(p.getId() == connection.getID()){
		        			 p.setPosition(new Point2D.Float(newPos.x, newPos.y));
		        		 }
		        	 }
		        	 UpdateAllOtherPositions position = new UpdateAllOtherPositions();
		        	 position.id = connection.getID();
		        	 position.x = newPos.x;
		        	 position.y = newPos.y;
		        	 server.sendToAllExceptTCP(connection.getID(), position);
		         }else if(object instanceof SendBombs){
		        	 
		        	 SendBombs newBList = (SendBombs)object;
		        	 newBList.bombList = bombList;
		        	 connection.sendTCP(newBList);
		         }else if(object instanceof RequestBomb){
		        	 
		        	 RequestBomb inBomb = (RequestBomb)object;
		        	 for(Bomb b : bombList){
		        		 if(b.getId() == inBomb.bombId && b.state == State.INERT){
		        			 b.setState(State.TRAVELLING);
		        			 for(Player p : playerList){
		        				 if(p.getId() == inBomb.playerId){
		        					 p.setBombHolding(b);
		        				 }
		        			 }
		        			 
		        			 server.sendToAllTCP(inBomb);
		        			 
		        		 }
		        	 }
		         }if(object instanceof ArmBomb){
		        	 
		        	 ArmBomb inBomb = (ArmBomb)object;
		        	 for(Bomb b : bombList){
		        		 if(b.getId() == inBomb.bombId){
		        			 b.setState(State.ARMED);
		        			 for(Player p : playerList){
		        				 if(p.getId() == connection.getID()){
		        					 p.plantBomb(b);
		        				 }
		        			 }
		        		 }
		        		 
		        	 }
		        	 //System.out.println("Sending to all other clients");
		        	 
		        	 server.sendToAllExceptTCP(connection.getID(), inBomb);
		        	 
		         }else if(object instanceof ExplodeBomb){
		        	 ExplodeBomb inBomb = (ExplodeBomb)object;
		        	 for(int i = 0; i < inBomb.bombIdList.length; i++){
		        		 for(Bomb b : bombList){
			        		 if(b.getId() == inBomb.bombIdList[i]){
			        			 b.setState(State.EXPLODE);
			        		 }
			        	 }
		        	 }
		        	 
		        	 server.sendToAllExceptTCP(connection.getID(), inBomb);
		         }else if(object instanceof DeleteBomb){
		        	 
		        	 Bomb newBomb = null;
		        	 DeleteBomb inBomb = (DeleteBomb)object;
		        	 Bomb bombToDelete = null;
		        	 for(Bomb b : bombList){
		        		 if(b.getId() == inBomb.bombId){
		        			 b.increaseDelete();
		        			 if(b.getDeleteCount() >= playerList.size()){
		        				 
		        				 bombToDelete = b;
		        				newBomb = new Bomb(new Point2D.Float((float)bombRand.nextInt(1000), (float)bombRand.nextInt(700)), bombId);
	        		    		for(int p = 0; p < 20; p++){
	        		    			//newBomb.addShrapnel(new Shrapnel(newBomb.getPos().x + 8,newBomb.getPos().y + 8, bombRand.nextInt(360)));
	        		    			newBomb.addAngle(bombRand.nextInt(360), p);
	        		    			//newBomb.addShrapnel(new Shrapnel());
	        		    		}
	        		    		
	        		    		bombId++;
	        		    		NewBomb nBomb = new NewBomb();
	        		    		nBomb.newBomb = newBomb;
	        		    		server.sendToAllTCP(nBomb);
		        			 }
		        		 }
		        	 }
		        	 if(bombToDelete != null){
		        		 bombList.remove(bombToDelete);
		        	 }
		        	 if(newBomb != null){
		        		 bombList.add(newBomb);
		        	 }
		         }else if(object instanceof DeadPlayer){
		        	 DeadPlayer player = (DeadPlayer)object;
		        	 if(player.x == 0){
		        		 server.sendToAllExceptTCP(connection.getID(), player);
		        	 }else{
		        		//get new coordinates
			        	 player.x = (float)bombRand.nextInt(1000);
			        	 player.y = (float)bombRand.nextInt(700);
			        	 server.sendToAllTCP(player);
		        	 }
		        	 
		         }
		         
		     }
			
		});

	       
        
        
    }
    
    private void populateBombList(){
    	
    	bombList = new ArrayList<Bomb>();
    	
    	for(int i = 0; i < 10; i++){
    		Bomb newBomb = new Bomb(new Point2D.Float((float)bombRand.nextInt(1000), (float)bombRand.nextInt(700)), bombId);
    		for(int p = 0; p < 20; p++){
    			//newBomb.addShrapnel(new Shrapnel(newBomb.getPos().x + 8,newBomb.getPos().y + 8, bombRand.nextInt(360)));
    			newBomb.addAngle(bombRand.nextInt(360), p);
    			//newBomb.addShrapnel(new Shrapnel());
    		}
    		bombList.add(newBomb);
    		bombId++;
    	}
    }
    
    public void updateClients(){
    	UpdateGameState gameState = new UpdateGameState();
    	gameState.playerIds = new int[playerList.size()];
    	gameState.bombIds = new int[bombList.size()];
    	gameState.playerX = new float[playerList.size()];
    	gameState.playerY = new float[playerList.size()];
    	gameState.bombX = new float[bombList.size()];
    	gameState.bombY = new float[bombList.size()];
    	gameState.bombStates = new State[bombList.size()];
    	for(int i = 0; i < playerList.size(); i++){
    		gameState.playerIds[i] = playerList.get(i).getId();
    		gameState.playerX[i] = playerList.get(i).getPos().x;
    		gameState.playerY[i] = playerList.get(i).getPos().y;
    		
    	}
    	
    	for(int p = 0; p < bombList.size(); p++){
    		gameState.bombIds[p] = bombList.get(p).getId();
    		gameState.bombX[p] = bombList.get(p).getPos().x;
    		gameState.bombY[p] = bombList.get(p).getPos().y;
    		gameState.bombStates[p] = bombList.get(p).getState();
    	}
    	
    	server.sendToAllTCP(gameState);
    	
    }
    
    

}
