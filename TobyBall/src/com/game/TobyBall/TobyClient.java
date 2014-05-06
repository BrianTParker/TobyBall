package com.game.TobyBall;


import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import com.game.TobyBall.Bomb.State;

public class TobyClient {
	
	private Client client;
	List<Player> playerList =  Collections.synchronizedList(new ArrayList<Player>());
	private Connection con;
	private Player currentPlayer;
	private int id;
	private ArrayList<Bomb> bombList;
	Bomb deleteBomb;
	int playerIndex;
	Sound bombPickup;
	Sound bombPlant;
	Sound bombExplode;
	Sound playerDeath;
	ArrayList<Wall> wallList = new ArrayList<Wall>();
	public Wall verticalWall;
	
	public TobyClient(){
	
	}
	public TobyClient(String IP) throws IOException{
		
		
		client = new Client();
		
		//list of all bombs in the game
		bombList = new ArrayList<Bomb>();
		
	    registerPackets();  //every class that is going to be serialized by the kryonet library has to be registered.  Otherwise an exception is thrown
	    new Thread(client).start(); //starts the client Listener
	    
	    client.connect(80000, IP, 54555, 54777); //first value is the timeout, then IP address, then TCP/UDP ports
	    currentPlayer = new Player();
	    id = -1;
	    playerIndex = -1;
	    
	    bombPickup = Gdx.audio.newSound(Gdx.files.internal("assets/pickup.ogg"));
	    bombPlant = Gdx.audio.newSound(Gdx.files.internal("assets/plant.ogg"));
		bombExplode = Gdx.audio.newSound(Gdx.files.internal("assets/explode.ogg"));
		playerDeath = Gdx.audio.newSound(Gdx.files.internal("assets/death.ogg"));
	    //System.out.println(client.discoverHost(7777, 60000));
	  
	    		
	    	
		
		 client.addListener(new Listener(){
		    
			 //function is only called on initial connection
			public void connected(Connection connection){
				
				pingForId();
				pingForPlayerList();
				pingForBombs();
				
			}
			
			//only called on disconnect
		    public void disconnected(Connection connection){
		    	
		    }
		    
		    
		    
		    		
		    //called whenever a packet is received from the client
		    public void received(Connection connection, Object object){
		    	
		    			
		    			//this is a giant if statement that checks what class the packet is
		    			//depending on what kind of packet it is, you handle the information differently
		    			//This is the main bread and butter of the client/server communication
				    	
				    	//this is where threading gets a little tricky.  The main game loop is running in the main thread and the client listener is running
						 //in a separate thread.  libGDX requires that all logic happens in the main thread so each one of these if statements has a postRunnable  
						 //that syncs the listener thread back with the main thread.  Any variables or objects that you want to access inside the postRunnable
						 //have to be declared final.  This gave me the biggest headache.  
			        	
		    			
		    			
		    			//A new list of players was received from the server
		    			if(object instanceof NewPlayerList){
			        		 
		    				//cast the object to the appropriate class
		    				final NewPlayerList list = (NewPlayerList)object;
			        		 Gdx.app.postRunnable(new Runnable() {
			        			 boolean found = false;
			        	         @Override
			        	         public void run() {
			        	            
			        	       
					        	
					    		//iterate through the player list from the server and add any players to your local list that are new
					    		for(Player serverList : list.playerList){
					    			found = false;
					    			int indexCount = 0;
					    			if(playerList.size() > 0){
						    			for(Player clientList : playerList){
						    				
						    					
						    				
							    			if(serverList.id == clientList.id){
							    				if(serverList.id == id){
							    					currentPlayer = clientList;
							    					playerIndex = indexCount;
							    				}
							    				found = true;
							    			
							    			}
							    			indexCount++;
							    		}
					    			}
					    			
					    			if(found == false){ 
					    				
					    				Player newPlayer = serverList;
					    				serverList.setImage();
					    				newPlayer.setImage();
					    				
					    				playerList.add(serverList); //add the new player to the local list
					    				
					    				
					    			}
					    		}
					    		
					    		//I think i was trying to get the index of the local player here, so you didn't have to iterate through the list of players each time 
					    		//you wanted to find yourself
					    		if(id == 1){
			    					playerIndex = 0;
			    				}else{
			    					for(Player p2 : playerList){
			    						if(p2.getId() == id){
			    							playerIndex = playerList.indexOf(p2);
			    						}
			    					}
			    				}
					    		
			        	         }
			        	      });   
					    	
			        		
			        		 //the server is sending the id of the local player
					    	}else if(object instanceof PlayerId){
					    		final PlayerId playerId = (PlayerId)object;
					    		Gdx.app.postRunnable(new Runnable() {
					    	         @Override
					    	         public void run() {
					    	            
					    	        	 
					    	       
					    	        	 	
					    	        	 	id = playerId.id;
					    	         }
					    	      });
					    		
					    	
					    	//update the position of a remote player
					    	}else if(object instanceof UpdateAllOtherPositions){
					    		
					    		final UpdateAllOtherPositions newPos = (UpdateAllOtherPositions)object;
					    		Gdx.app.postRunnable(new Runnable() {
					    	         @Override
					    	         public void run() {
							    		
							    		
							    		for(Player p : playerList){
							    			if(p.getId() == newPos.id){
							    				
							    				//set the players last known position in order to simulate movement.
							    				//the local copy of the player is moved towards the last known position
							    				p.setLastKnownX(newPos.x);
							    				p.setLastKnownY(newPos.y);
						    					
							    			}
							    		}
					    	         }
					    	      });
					    		
					    	//get a list of bombs from the server and add any new ones to the local list
					    	}else if(object instanceof SendBombs){
					    		
					    		final SendBombs bList = (SendBombs)object;
					    		Gdx.app.postRunnable(new Runnable() {
					    	         @Override
					    	         public void run() {
							    		
					    	        	 
							    		for(Bomb serverBomb : bList.bombList){
							    			
							    			boolean found = false;
							    			for(Bomb clientBomb : bombList){
							    				if(clientBomb.getId() == serverBomb.getId()){
							    					found = true;
							    				}
							    			}
							    			if(found == false){
							    				Bomb newBomb = serverBomb;
							    				newBomb.setImage();
							    				for(int i = 0; i < newBomb.getAngles().length; i++){
							    					newBomb.addShrapnel(new Shrapnel(newBomb.getPos().x, newBomb.getPos().y, newBomb.getAngles()[i], new Texture("assets/shrapnel.png")));
							    					
							    				}
							    				
							    				bombList.add(newBomb);
							    			}
							    		}
							    		
					    		
					    	         }
					    	      });
					    		
					    	//information from the server that lets the client know a player has picked up a bomb and is holding it
					    	//Packet contains the bomb id and player id 
					    	}else if(object instanceof RequestBomb){
					    		
					    		final RequestBomb inBomb = (RequestBomb)object;
					    		Gdx.app.postRunnable(new Runnable() {
					    	         @Override
					    	         public void run() {
							    		
							    		for(Bomb b : bombList){
							    			if(b.getId() == inBomb.bombId){
							    				for(Player p : playerList){
							    					if(p.getId() == inBomb.playerId){
							    						b.setState(State.TRAVELLING);
							    						p.setBombHolding(b);
							    						
							    						bombPickup.play();
							    						//bombPickup.dispose();
							    					}
							    				}
							    			}
							    		}
							    		
					    	         }
					    	      });
					    		
					    	//information from the server that lets the client know a bomb has moved to the ARMED/THROWN state
					    	//Packet contains the bomb id of the exploded bomb, player id who planted it, and the x/y positions and velocities of the bomb
					    	}else if(object instanceof ArmBomb){
					    		final ArmBomb inBomb = (ArmBomb)object;
					    		Gdx.app.postRunnable(new Runnable() {
					    	         @Override
					    	         public void run() {
							    		
							    		
							    		for(Bomb b : bombList){
							    			if(b.getId() == inBomb.bombId){
							    				b.setImage("assets/bomb_armed.png");
							    				if(inBomb.thrown == 1){
							    					b.setState(State.THROWN);
							    					b.setXThrowVelocity(inBomb.xVel);
							    					b.setYThrowVelocity(inBomb.yVel);
							    					for(Player p : playerList){
								    					if(p.getId() == inBomb.playerId){
								    						p.throwBomb(b, inBomb.xPos, inBomb.yPos);
								    						bombPlant.play();
								    					}
								    				}
							    				}else{
							    					b.setState(State.ARMED);
							    					for(Player p : playerList){
								    					if(p.getId() == inBomb.playerId){
								    						p.plantBomb(b, inBomb.xPos, inBomb.yPos);
								    						bombPlant.play();
								    					}
								    				}
							    				}
							    				
							    				
							    			}
							    		}
							    	  }
					    	      });
					    	
					    	//information from the server that lets the client know a bomb has exploded
					    	//Packet contains a list of bomb ids
					    	}else if(object instanceof ExplodeBomb){
					    		final ExplodeBomb inBomb = (ExplodeBomb)object;
					    		Gdx.app.postRunnable(new Runnable() {
					    	         @Override
					    	         public void run() {
							    		
							    		
							    		for(int i = 0; i < inBomb.bombIdList.length; i++){
							    			for(Bomb b : bombList){
							    				
								    			if(b.getId() == inBomb.bombIdList[i]){
								    				b.setState(State.EXPLODE);
								    				//bombExplode.play();
								    			}
								    		}
							    		}
							    		
					    	         }
					    	      });
					    		
					    	//I think this is a hold over from one of my attempts in client/server communication
					    	//I don't think the server is sending this packet ever in the current version of the game but I don't want to delete this section yet
					    	}else if(object instanceof UpdateGameState){
					    		final UpdateGameState newState = (UpdateGameState)object;
					    		Gdx.app.postRunnable(new Runnable() {
					    	         @Override
					    	         public void run() {
							    		
							    		
							    		for(int i = 0; i < newState.playerIds.length; i++){
							    			for(Player p : playerList){
							    				if(p.getId() == newState.playerIds[i]){
							    					if(p.getId() != id){
							    						p.updateX(newState.playerX[i]);
							    						p.updateY(newState.playerY[i]);
							    					}
							    				}
							    			}
							    		}
					    	         
					    		
							    		for(int p = 0; p < newState.bombIds.length; p++){
							    			for(Bomb b : bombList){
							    				if(b.getId() == newState.bombIds[p]){
							    					b.setPos(new Point2D.Float(newState.bombX[p], newState.bombY[p]));
							    					b.setState(newState.bombStates[p]);
							    				}
							    			}
							    		}
					    	         }
					    	      });
					    		
					    	//new bomb information from the server.  The client adds it to the local list
					    	}else if(object instanceof NewBomb){
					    		final NewBomb newBomb = (NewBomb)object;
					    		Gdx.app.postRunnable(new Runnable() {
					    	         @Override
					    	         public void run() {
					    	        	newBomb.newBomb.setImage("assets/bomb.png");
					    				for(int i = 0; i < newBomb.newBomb.getAngles().length; i++){
					    					newBomb.newBomb.addShrapnel(new Shrapnel(newBomb.newBomb.getPos().x, newBomb.newBomb.getPos().y, newBomb.newBomb.getAngles()[i], new Texture("assets/shrapnel.png")));
					    					
					    				}
					    	        	bombList.add(newBomb.newBomb);
					    	         }
					    		});
					    		
					    	//Information from the server letting the client know that a player has died.  
					    	//Packet contains the playerId who died, as well as the playerID that did the killing
					    	}else if(object instanceof DeadPlayer){
					    		final DeadPlayer player = (DeadPlayer)object;
					    		Gdx.app.postRunnable(new Runnable() {
					    	         @Override
					    	         public void run() {
					    	        	 for(Player p : playerList){
					    	        		 
					    	        		 if(p.getId() == player.playerId){
					    	        			 
					    	        			 if(p.getId() == player.killerId){
					    	        				 if(p.getScore() > 0){
					    	        					 p.decreaseScore();
					    	        				 }
					    	        				 
					    	        			 }
					    	        			 if(player.x == 0){
					    	        				 p.makeDead();
					    	        				 playerDeath.play();
					    	        				 for(Bomb b : p.getBombsPlanted()){
					    	        					 if(b.getState() != State.EXPLODE){
					    	        						 sendBombDelete(b.getId());
						    	        					 deleteBomb(b);
					    	        					 }
					    	        					 
					    	        				 }
					    	        				 if(p.getBomb() != null){
					    	        					 sendBombDelete(p.getBomb().getId());
						    	        				 deleteBomb(p.getBomb());
						    	        				 p.removeBombHolding();
					    	        				 }
					    	        				 
					    	        				 p.removeBombsPlanted();
					    	        				 
					    	        			 }else{
					    	        				 p.setPosition(new Point2D.Float(player.x, player.y));
					    	        				 p.setLastKnownX(player.x);
					    	        				 p.setLastKnownY(player.y);
						    	        			 p.makeAlive();
					    	        			 }
					    	        			 
					    	        		 }else if(p.getId() == player.killerId && p.getId() != player.playerId){
					    	        			 p.increaseScore();
					    	        			
					    	        		 }
					    	        	 }
					    	         }
					    		});
					    		
					    	//information from the server letting the client know a player has disconnected
					    	}else if(object instanceof PlayerDisconnect){
					    		final PlayerDisconnect playerDis = (PlayerDisconnect)object;
					    		
					    		Gdx.app.postRunnable(new Runnable() {
					    	         @Override
					    	         public void run() {
					    	        	 
					    	        	 Player playerToDelete = null;
					    	        	 for(Player p : playerList){
					    	        		 if(p.getId() == playerDis.playerId){
					    	        			 
					    	        			 playerToDelete = p;
					    	        			 if(playerIndex > playerList.indexOf(p)){
						    	        			 playerIndex--;
						    	        		 }
					    	        			 break;
					    	        		 }
					    	        	 }
					    	        	 
					    	        	 if(playerToDelete != null){
					    	        		 
					    	        		 playerList.remove(playerToDelete);
					    	        		 
					    	        	 }
					    	         }
					    		});
					    		
					    	//information from the server letting the client know that a thrown bomb has been caught
					    	}else if(object instanceof BombSteal){
					    		final BombSteal bomb = (BombSteal)object;
					    		
					    		Gdx.app.postRunnable(new Runnable() {
					    	         @Override
					    	         public void run() {
					    	        	 getBomb(bomb.bombId).setState(State.TRAVELLING);
					    	        	 getPlayer(bomb.oldOwner).removeBombPlanted(getBomb(bomb.bombId));
					    	        	 getPlayer(bomb.newOwner).setBombHolding(getBomb(bomb.bombId));
					    	         }
					    		});
					    	}
			         }
		    	});
	    
	   
		   
	            
	 }
	    
	
	private void registerPackets(){
        Kryo kryo = client.getKryo();
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
        
    }
	
	public List<Player> getPlayerList(){
		return playerList;
	}
	
	//ping the server for a new list of players
	public void pingForPlayerList(){
		NewPlayerList list = new NewPlayerList();
		list.playerList = new ArrayList<Player>();
		client.sendTCP(list);
	}
	
	public int getId(){
		return id;
	}
	
	//sends the local players position to the server
	public void sendPosition(Point2D.Float inPos){
		SendPosition newPos = new SendPosition();
		newPos.x = inPos.x;
		newPos.y = inPos.y;
		client.sendTCP(newPos);
	}
	
	
	//takes a velocity and x/y direction.  
	//if there is no collision with a wall, then update the players position 
	public void updatePlayerPosition(float velocity, String inDirection){
		
		//get the index of the local player in the list of players
		if(playerIndex >= playerList.size()){
			for(Player p : playerList){
				if(p.getId() == id){
					playerIndex = playerList.indexOf(p);
				}
			}
		}else{
		
			if(playerIndex >= 0 && playerList.size() >= playerIndex){
				
				if(inDirection == "x"){
					if(((velocity < 0 && playerList.get(playerIndex).getPos().x >= 0) || (velocity >= 0 && playerList.get(playerIndex).getPos().x <= 1080 - playerList.get(playerIndex).getImage().getWidth())) && !wallCollision(playerList.get(playerIndex), velocity, inDirection)){
						playerList.get(playerIndex).updateX(velocity);
						
					}
				}else{
					if(((velocity < 0 && playerList.get(playerIndex).getPos().y > 0) || (velocity > 0 && playerList.get(playerIndex).getPos().y <= 720 - playerList.get(playerIndex).getImage().getHeight())) && !wallCollision(playerList.get(playerIndex), velocity, inDirection)){
						playerList.get(playerIndex).updateY(velocity);
						
						
					}
				}
			}
		}
		
			
		
		

					
					
			
		
	}
	
	public Player getPlayer(){
		Player player = new Player();
		/*for(Player p : playerList){
			if(p.getId() == id){
				player = p;
			}
		}*/
		Iterator<Player> it = playerList.iterator();
		while(it.hasNext()){
			player = it.next();
			
			if(player.getId() == id){
				break;
			}
				
			
		}
		if(player.getId() != id){
			player = null;
		}
		
		
		
		return player;
		
		
		//return playerList.get(playerIndex);
		
		
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
	
	public void pingForBombs(){
		
		SendBombs newList = new SendBombs();
		newList.bombList = new ArrayList<Bomb>();
		Bomb bomb = new Bomb();
		newList.bombList.add(bomb);
		
		client.sendTCP(newList);
	}
    
	public ArrayList<Bomb> getBombs(){
		return bombList;
	}
	
	public void requestBomb(Bomb inBomb){
		RequestBomb bomb = new RequestBomb();
		bomb.bombId = inBomb.getId();
		bomb.playerId = id;
		client.sendTCP(bomb);
	}
	
	public void sendArmedBomb(Bomb inBomb){
		ArmBomb bomb = new ArmBomb();
		bomb.bombId = inBomb.getId();
		
		bomb.playerId = id;
		if(inBomb.getState() == State.THROWN){
			bomb.thrown = 1;
			
		}else{
			bomb.thrown = 0;
			
		}
		bomb.xVel = inBomb.getXThrow();
		bomb.yVel = inBomb.getYThrow();
		
		client.sendTCP(bomb);
		
		
	}
	
	public void sendArmedBomb(Bomb inBomb, float inX, float inY){
		ArmBomb bomb = new ArmBomb();
		bomb.bombId = inBomb.getId();
		
		bomb.playerId = id;
		if(inBomb.getState() == State.THROWN){
			bomb.thrown = 1;
			
		}else{
			bomb.thrown = 0;
			
		}
		bomb.xVel = inBomb.getXThrow();
		bomb.yVel = inBomb.getYThrow();
		bomb.xPos = inX;
		bomb.yPos = inY;
		client.sendTCP(bomb);
		
		
	}
	
	public void explodeBomb(int[] inList){
		ExplodeBomb bomb = new ExplodeBomb();
		bomb.bombIdList = new int[inList.length];
		
		for(int i = 0; i< inList.length; i++){
			bomb.bombIdList[i] = inList[i];
			
		}
		
		client.sendTCP(bomb);
		
	}
	
	public void pingForId(){
		PlayerId idRequest = new PlayerId();
		idRequest.id = 0;
		client.sendTCP(idRequest);
	}
	
	public void sendBombDelete(int inId){
		DeleteBomb newBomb = new DeleteBomb();
		newBomb.bombId = inId;
		client.sendTCP(newBomb);
	}
	
	public void deleteBomb(Bomb inBomb){
		bombList.remove(inBomb);
	}
	
	public void sendDeadPlayer(int inId){
		DeadPlayer dPlayer = new DeadPlayer();
		dPlayer.playerId = id;
		dPlayer.x = 0;
		dPlayer.y = 0;
		dPlayer.killerId = inId;
		client.sendTCP(dPlayer);
			
		
	}
	
	public void getNewCoords(){
		DeadPlayer dPlayer = new DeadPlayer();
		dPlayer.playerId = id;
		dPlayer.x = -1;
		dPlayer.y = -1;
		dPlayer.killerId = -1;
		client.sendTCP(dPlayer);
	}
	
	public int getPlayerIndex(){
		return playerIndex;
	}
	
	public void setPlayerIndex(){
		for(Player p : playerList){
			if(p.getId() == id){
				playerIndex = playerList.indexOf(p);
				currentPlayer = p;
			}
		}
	}
	
	private boolean wallCollision(Player p, float velocity, String direction){
		boolean collide = false;
		Rectangle testRect;
		float newX = p.getPos().x;
		float newY = p.getPos().y;
		if(direction.equals("x")){
			newX = p.getPos().x + velocity;
		}else{
			newY = p.getPos().y + velocity;
		}
		testRect = new Rectangle(newX, newY, p.getImage().getWidth(), p.getImage().getHeight());
		for(Wall w : wallList){
			if(testRect.overlaps(w.getRectangle())){
				collide = true;
				break;
			}
		}
		
		if(testRect.overlaps(verticalWall.getRectangle())){
			collide = true;
		}
		
		return collide;
	}
	
	public boolean shrapnelWallCollision(Shrapnel sh){
		boolean collide = false;
		
		for(Wall w : wallList){
			if(sh.getRect().overlaps(w.getRectangle())){
				collide = true;
				break;
			}
		}
		
		if(sh.getRect().overlaps(verticalWall.getRectangle())){
			collide = true;
		}
		
		return collide;
	}
	
	public void makeWalls(){
		
		wallList.add(new Wall(80, 160, "assets/wall.png"));
		wallList.add(new Wall(80, 520, "assets/wall.png"));
		wallList.add(new Wall(700, 520, "assets/wall.png"));
		wallList.add(new Wall(700, 160, "assets/wall.png"));
		verticalWall = new Wall(500,200, "assets/wall2.png");
	}
	
	public ArrayList<Wall> getWalls(){
		return wallList;
	}
	
	public void update(){
		try {
			client.update(60000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
	public Bomb getBomb(int inId){
		Bomb bomb = null;
		
		for(Bomb b : bombList){
			if(b.getId() == inId){
				bomb = b;
				break;
			}
		}
		
		return bomb;
	}
	
	public void checkVisibility(){
		Player player = getPlayer();
		
		Rectangle testRect = new Rectangle(player.getRect().getX(), player.getRect().getY(),10,10);
		
		Point2D.Float temp = new Point2D.Float(player.getPos().x + 5, player.getPos().y + 5);
		Point2D.Float temp2;
		boolean invisible = false;
		
		for(Player p : playerList){
			
			if(p != getPlayer()){
				temp2 = new Point2D.Float(p.getPos().x + 5, p.getPos().y + 5);
				while(temp.distance(temp2) >= 16){
					if(temp.x > temp2.x){
						temp.x -= 5;
					}else{
						temp.x += 5;
					}
					if(temp.y > temp2.y){
						temp.y -= 5;
					}else{
						temp.y += 5;
					}
					
					testRect.setX(temp.x);
					testRect.setY(temp.y);
					
					for(Wall w : wallList){
						if(w.getRectangle().overlaps(testRect)){
							invisible = true;
							break;
						}
					}
					if(invisible == true){
						p.makeInvisible();
						break;
					}
				}
				if(invisible == false){
					p.makeVisible();
				}
			}
		}
	}
	
	public void checkVisibility(Player player1, Player player2){
		
		
		
		Line2D.Float line = new Line2D.Float(player1.getPos().x + 5, player1.getPos().y + 5, player2.getPos().x + 5, player2.getPos().y + 5);
		boolean invisible = false;
		Rectangle2D.Float wallRect;
		for(Wall w : wallList){
			wallRect = new Rectangle2D.Float(w.getX(), w.getY(), 256, 16);
			if(line.intersects(wallRect)){
				invisible = true;
				break;
			}
		}
		
		wallRect = new Rectangle2D.Float(verticalWall.getX(), verticalWall.getY(), 16, 256);
		if(line.intersects(wallRect)){
			invisible = true;
		}
		
		if(invisible == true){
			player2.makeInvisible();
		}
		else if(invisible == false){
			player2.makeVisible();
		}
	}
	
	public float getAngle(Player player1, Player player2) {
	    float angle = (float) Math.toDegrees(Math.atan2(player2.getPos().x - player1.getPos().x, player2.getPos().y - player1.getPos().y));

	    if(angle < 0){
	        angle += 360;
	    }

	    return angle;
	}
	
	
    

}
