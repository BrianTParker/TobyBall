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
    
    ArrayList<Player> playerList;
    ArrayList<Bomb> bombList;
    Random bombRand;
    int bombId;
    int playerId;
    int numPlayers;
    ArrayList<Wall> wallList = new ArrayList<Wall>();
    Wall verticalWall;
    
    public TobyServer() throws IOException{
    	bombId = 1;
    	playerId = 1;
    	bombRand = new Random();
    	playerList = new ArrayList<Player>();
    	makeWalls();
    	populateBombList();
        server = new Server();
        Log.set(Log.LEVEL_DEBUG);
        new Thread(server).start();
        server.bind(54555, 54777);
        numPlayers = 0;
        
        
        
        Kryo kryo = server.getKryo();
        
        //every class has to be registered with the kryo library
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
        kryo.register(PlayerDisconnect.class);
        kryo.register(BombSteal.class);
        
        
        //The main server listener
        server.addListener(new Listener(){
		 	
        	//method called whenever a client disconnects
			public void disconnected(Connection connection){
				numPlayers--;
				System.out.println( "Client " + connection.getID() + " Disconnected");
				PlayerDisconnect playerDis = new PlayerDisconnect();
				playerDis.playerId = connection.getID();
				System.out.println("Client Id: " + connection.getID());
				server.sendToAllTCP(playerDis);
				
				Player playerToDelete = null;
	        	 for(Player p : playerList){
	        		 if(p.getId() == playerDis.playerId){
	        			 
	        			 playerToDelete = p;
	        			 
	        		 }
	        	 }
	        	 
	        	 if(playerToDelete != null){
	        		 playerList.remove(playerToDelete);
	        	 }
			 	}
			
			
			 //method called whenever a client connects    
		     public void connected(Connection connection){
		    	 
		    	 
		    	 
		    	 
	        	 numPlayers++;
	        	 Random rand = new Random();
	        	 String asset;
	        	 if(numPlayers == 1){
	        		 asset = "assets/player1.png";
	        	 }else if(numPlayers == 2){
	        		 asset = "assets/player2.png";
	        	 }else if(numPlayers == 3){
	        		 asset = "assets/player3.png";
	        	 }else if(numPlayers == 4){
	        		 asset = "assets/player4.png";
	        	 }else{
	        		 asset = "assets/player5.png";
	        	 }
	        		
	        	 //create a new player object and add it to the list of players
		         playerList.add(new Player(getValidPosition(), connection.getID(),asset ));
		         
		         
		         //create a packet containing the new players id
		         PlayerId id = new PlayerId();
		         
		         id.id = connection.getID();
		         
		         //send the packet back to the client who just connected to let them know what their id is
		         connection.sendTCP(id);
		         
		        	
		         
		         playerId++;
		         
		         //create a packet containing the new list of players and send it out to every connected client
		         NewPlayerList list = new NewPlayerList();
		         list.playerList = playerList;
		         server.sendToAllTCP(list);
		         
		         
		         //create a packet containing the list of bombs
		         SendBombs newBList = new SendBombs();
	        	 newBList.bombList = bombList;
	        	 
	        	 
	        	 //send the list of bombs to the player who just connected
	        	 connection.sendTCP(newBList);
			            
			    
		         
		        
		         
		     }
		     
		     
		     //The main communication method.  This is called whenever a packet is received from a client.  The instance of the packet determines what to do with it
		     public void received(Connection connection, Object object){
		         
		    	 
		    	 //A client is requesting a new list of players
		         if(object instanceof NewPlayerList){
		        	 NewPlayerList list = new NewPlayerList();
		        	 list.playerList = playerList;
		        	 connection.sendTCP(list);
		        	 
		         //A client is requesting their id
		         }else if(object instanceof PlayerId){
		        	 PlayerId playerId = new PlayerId();
		        	 playerId.id = connection.getID();
		        	 connection.sendTCP(playerId);
		        	 
		         //a client is sending their position.  the server updates it's version of the player and then updates all the other clients	 
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
		        	 
		        	 //update all clients except the one who just sent the packet
		        	 server.sendToAllExceptTCP(connection.getID(), position);
		        	 
		         
		         //a client is requesting a list of bombs
		         }else if(object instanceof SendBombs){
		        	 
		        	 SendBombs newBList = (SendBombs)object;
		        	 newBList.bombList = bombList;
		        	 connection.sendTCP(newBList);
		        	 
		         //a client is asking the server if they can pick up a bomb or if they can catch a thrown bomb
		         }else if(object instanceof RequestBomb){
		        	 
		        	 RequestBomb inBomb = (RequestBomb)object;
		        	 State oldState;
		        	 int oldOwner = -1;
		        	 int newOwner = -1;
		        	 for(Bomb b : bombList){
		        		 if(b.getId() == inBomb.bombId){
		        			 
		        			 
		        		 }
		        		 if(b.getId() == inBomb.bombId && (b.state == State.INERT || b.state == State.THROWN)){
		        			 oldState = b.getState();
		        			 
		        			 oldOwner = b.owner;
		        			 b.setState(State.TRAVELLING);
		        			 for(Player p : playerList){
		        				 if(p.getId() == inBomb.playerId){
		        					 newOwner = p.getId();
		        					 p.setBombHolding(b);
		        				 }
		        			 }
		        			 if(oldState == State.THROWN){
		        				 
		        				 BombSteal bomb = new BombSteal();
		        				 bomb.oldOwner = oldOwner;
		        				 bomb.newOwner = newOwner;
		        				 bomb.bombId = b.getId();
		        				 server.sendToAllTCP(bomb);
		        			 }else{
		        				 server.sendToAllTCP(inBomb);
		        			 }
		        			 
		        			 
		        		 }
		        	 }
		        	 
		         //A client is telling the server that a bomb has been armed
		         }if(object instanceof ArmBomb){
		        	 
		        	 ArmBomb inBomb = (ArmBomb)object;
		        	 
		        	 for(Bomb b : bombList){
		        		 if(b.getId() == inBomb.bombId){
		        			 
		        			 if(inBomb.thrown == 1){
		        				 
		        				 b.setState(State.THROWN);
		        				 b.setXThrowVelocity(inBomb.xVel);
		        				 b.setYThrowVelocity(inBomb.yVel);
		        			 }else{
		        				 b.setState(State.ARMED);
		        			 }
		        			 
		        			 for(Player p : playerList){
		        				 if(p.getId() == connection.getID()){
		        					 if(inBomb.thrown == 1){
		        						 p.throwBomb(b, inBomb.xPos, inBomb.yPos);
		        					 }else{
		        						 p.plantBomb(b, inBomb.xPos, inBomb.yPos); 
		        					 }
		        					 
		        				 }
		        			 }
		        			 
		        			 
		        		 }
		        		 
		        	 }
		        	 //System.out.println("Sending to all other clients");
		        	 
		        	 server.sendToAllExceptTCP(connection.getID(), inBomb);
		         
		        	 
		         //a client is telling the server that its local player exploded his bombs
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
		        	 
		         //A client is telling the server that a bomb is done exploding and should be deleted
		         }else if(object instanceof DeleteBomb){
		        	 
		        	 Bomb newBomb = null;
		        	 DeleteBomb inBomb = (DeleteBomb)object;
		        	 Bomb bombToDelete = null;
		        	 for(Bomb b : bombList){
		        		 if(b.getId() == inBomb.bombId){
		        			 b.increaseDelete();
		        			 if(b.getDeleteCount() >= playerList.size()){
		        				 
		        				 bombToDelete = b;
		        				newBomb = new Bomb(getValidPosition(), bombId);
	        		    		for(int p = 0; p < 50; p++){
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
		        	 
		         //A client is telling the server that its local player has died
		         }else if(object instanceof DeadPlayer){
		        	 DeadPlayer player = (DeadPlayer)object;
		        	 
		        	 if(player.x == 0){
		        		 for(Player p : playerList){
		        			 if(p.getId() == player.killerId && p.getId() != player.playerId){
		        				 p.increaseScore();
		        				 
		        				 break;
		        			 }
		        		 }
		        		 server.sendToAllTCP(player);
		        	 }else{
		        		//get new coordinates
		        		 Point2D.Float newPos = getValidPosition();
			        	 player.x = newPos.x;
			        	 player.y = newPos.y;
			        	 server.sendToAllTCP(player);
		        	 }
		        	 
		         }
		         
		     }
			
		});

	       
        
        
    }
    
    private void populateBombList(){
    	
    	bombList = new ArrayList<Bomb>();
    	
    	int numBombs = 5;
    	int numShrapnel = 50;
    	
    	//this was a little tricky.  Kryonet was having issues serializing bombs that had images and a list of shrapnel objects so i'm essentially 
    	//creating the instructions on how to build each bomb, and then the individual clients actually build them on their end.  
    	//Each bomb has a list of random angles that are used for determining the trajectory of each piece of shrapnel.  This has to be pre-determined 
    	//on the server so that it's the same for each client
    	for(int i = 0; i < numBombs; i++){
    		
    		Bomb newBomb = new Bomb(getValidPosition(), bombId);
    		
    		for(int p = 0; p < numShrapnel; p++){
    			
    			newBomb.addAngle(bombRand.nextInt(360), p);
    			
    		}
    		bombList.add(newBomb);
    		bombId++;
    	}
    }
    
    
    //This is not being used for anything
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
    
    public Player getPlayer(int inId){
		Player player = null;
		
		for(Player p : playerList){
			if(p.getId() == inId){
				player = p;
				break;
			}
		}
		
		return player;
	}
    
    public void makeWalls(){
		
		wallList.add(new Wall(80, 160, "assets/wall.png"));
		wallList.add(new Wall(80, 520, "assets/wall.png"));
		wallList.add(new Wall(700, 520, "assets/wall.png"));
		wallList.add(new Wall(700, 160, "assets/wall.png"));
		verticalWall = new Wall(500,200, "assets/wall2.png");
	}
    
    public boolean wallCollision(float inX, float inY){
		
		boolean collide = false;
		
		Rectangle testRect = new Rectangle(inX, inY, 20, 20);
		for(Wall w : wallList){
			collide = false;
			if(w.getRectangle().overlaps(testRect)){
				collide = true;
				break;
				
			}
		}
		
		if(testRect.overlaps(verticalWall.getRectangle())){
			collide = true;
		}
		
		
		return collide;
			
		
		
	}
    
    //returns a valid position for bomb placement so that bombs aren't placed inside of walls
    public Point2D.Float getValidPosition(){
    	float x, y;
    	x = (float)bombRand.nextInt(1000);
    	y = (float)bombRand.nextInt(700);
    	
    	while(wallCollision(x, y) == true){
    		x = (float)bombRand.nextInt(1000);
        	y = (float)bombRand.nextInt(700);
    	}
    	
    	return new Point2D.Float(x, y);
    	
    }
    
    

}
