package com.game.TobyBall;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import com.game.TobyBall.Bomb.State;

public class TobyClient {
	
	private Client client;
	private ArrayList<Player> playerList;
	private Connection con;
	private Player currentPlayer;
	private int id;
	private ArrayList<Bomb> bombList;
	Bomb deleteBomb;
	public TobyClient(){
	
	}
	public TobyClient(String IP) throws IOException{
		client = new Client();
		playerList = new ArrayList<Player>();
		bombList = new ArrayList<Bomb>();
	    registerPackets();
	    new Thread(client).start();
	    client.connect(60000, IP, 54555, 54777);
	    currentPlayer = new Player();
	    id = -1;
	    //System.out.println(client.discoverHost(7777, 60000));
	    
	    new Thread(new Runnable(){
	    	public void run(){
		 client.addListener(new Listener(){
		    
			public void connected(Connection connection){
				
			}
			
		    public void disconnected(Connection connection){
		    	
		    }
		    
		    
		    
		    		
		    	
		    public void received(Connection connection, Object object){
		    	
		    	
		    	
			        	 if(object instanceof NewPlayerList){
			        		 final NewPlayerList list = (NewPlayerList)object; 
			        		 Gdx.app.postRunnable(new Runnable() {
			        			 boolean found = false;
			        	         @Override
			        	         public void run() {
			        	            
			        	       
					        	
					    		//playerList = list.playerList;
					    		for(Player serverList : list.playerList){
					    			found = false;
					    			
					    			if(playerList.size() > 0){
						    			for(Player clientList : playerList){
						    				
						    					
						    				
							    			if(serverList.id == clientList.id){
							    				if(serverList.id == id){
							    					currentPlayer = clientList;
							    				}
							    				found = true;
							    			
							    			}
							    			
							    		}
					    			}
					    			
					    			if(found == false){
					    				
					    				Player newPlayer = serverList;
					    				newPlayer.setImage();
					    				
					    				playerList.add(newPlayer);
					    			}
					    		}
					    		
			        	         }
			        	      });   
					    		
					    	}else if(object instanceof PlayerId){
					    		final PlayerId playerId = (PlayerId)object;
					    		Gdx.app.postRunnable(new Runnable() {
					    	         @Override
					    	         public void run() {
					    	            // process the result, e.g. add it to an Array<Result> field of the ApplicationListener.
					    	        	 
					    	       
					    	        	 	
					    	        	 	id = playerId.id;
					    	         }
					    	      });
					    		
					    		
					    	}else if(object instanceof UpdateAllOtherPositions){
					    		
					    		final UpdateAllOtherPositions newPos = (UpdateAllOtherPositions)object;
					    		Gdx.app.postRunnable(new Runnable() {
					    	         @Override
					    	         public void run() {
							    		
							    		
							    		for(Player p : playerList){
							    			if(p.getId() == newPos.id){
							    				
							    				//set the players last known position in order to simulate movement
							    				p.setLastKnownX(newPos.x);
							    				p.setLastKnownY(newPos.y);
						    					/*if(p.getPos().x >=  newPos.x - 15 && p.getPos().x <= newPos.x + 15){
						    						p.setXVelocity(0);
						    					}else if(p.getPos().x >= newPos.x){
						    						
						    						p.setXVelocity(-5);
						    					}else{
						    						
						    						p.setXVelocity(5);
						    					}
							    				//}
							    				
						    					if(p.getPos().y <= newPos.y - 15){
						    						p.setYVelocity(5);
						    					}else if(p.getPos().y >= newPos.y + 15){
						    						p.setYVelocity(-5);
						    					}else{
						    						p.setYVelocity(0);
						    					}
							    				
							    				
							    				if(Math.abs(p.getPos().x - newPos.x) >= 100 || Math.abs(p.getPos().y - newPos.y) >= 100){
							    					p.setXPosition(newPos.x);
							    					p.setYPosition(newPos.y);
						    					}*/
							    				//p.setPosition(new Point2D.Float(newPos.x, newPos.y));
							    			}
							    		}
					    	         }
					    	      });
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
							    					}
							    				}
							    			}
							    		}
					    	         }
					    	      });
					    	}else if(object instanceof ArmBomb){
					    		final ArmBomb inBomb = (ArmBomb)object;
					    		Gdx.app.postRunnable(new Runnable() {
					    	         @Override
					    	         public void run() {
							    		
							    		
							    		for(Bomb b : bombList){
							    			if(b.getId() == inBomb.bombId){
							    				b.setImage("assets/bomb_armed.png");
							    				b.setState(State.ARMED);
							    				for(Player p : playerList){
							    					if(p.getId() == inBomb.playerId){
							    						p.plantBomb(b);
							    					}
							    				}
							    			}
							    		}
							    	  }
					    	      });
					    	
					    	}else if(object instanceof ExplodeBomb){
					    		final ExplodeBomb inBomb = (ExplodeBomb)object;
					    		Gdx.app.postRunnable(new Runnable() {
					    	         @Override
					    	         public void run() {
							    		
							    		
							    		for(int i = 0; i < inBomb.bombIdList.length; i++){
							    			for(Bomb b : bombList){
								    			if(b.getId() == inBomb.bombIdList[i]){
								    				b.setState(State.EXPLODE);
								    			}
								    		}
							    		}
					    	         }
					    	      });
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
					    	}else if(object instanceof DeadPlayer){
					    		final DeadPlayer player = (DeadPlayer)object;
					    		Gdx.app.postRunnable(new Runnable() {
					    	         @Override
					    	         public void run() {
					    	        	 for(Player p : playerList){
					    	        		 if(p.getId() == player.playerId){
					    	        			 
					    	        			 if(player.x == 0){
					    	        				 p.makeDead();
					    	        			 }else{
					    	        				 p.setPosition(new Point2D.Float(player.x, player.y));
						    	        			 p.makeAlive();
					    	        			 }
					    	        			 
					    	        		 }
					    	        	 }
					    	         }
					    		});
					    	}
			         }
		    	});
	    	}
	    }).start();
		    	
		   
	            
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
        
    }
	
	public ArrayList<Player> getPlayerList(){
		return playerList;
	}
	
	public void pingForPlayerList(){
		NewPlayerList list = new NewPlayerList();
		list.playerList = new ArrayList<Player>();
		client.sendTCP(list);
	}
	
	public int getId(){
		return id;
	}
	
	public void sendPosition(Point2D.Float inPos){
		SendPosition newPos = new SendPosition();
		newPos.x = inPos.x;
		newPos.y = inPos.y;
		client.sendTCP(newPos);
	}
	
	public void updatePlayerPosition(float velocity, String inDirection){
		
		if(id > 0 && playerList.size() >= id){
			//I'm going to try this logic for position updates until I find that it's unreliable.  I'm tired of looping through each player every time
			if(inDirection == "x"){
				if((velocity < 0 && playerList.get(id - 1).getPos().x >= 0) || (velocity >= 0 && playerList.get(id - 1).getPos().x <= 1080 - playerList.get(id - 1).getImage().getWidth())){
					playerList.get(id - 1).updateX(velocity);
					//sendPosition(playerList.get(id - 1).getPos());
				}
			}else{
				if((velocity < 0 && playerList.get(id - 1).getPos().y > 0) || (velocity > 0 && playerList.get(id - 1).getPos().y <= 720 - playerList.get(id - 1).getImage().getHeight())){
					playerList.get(id - 1).updateY(velocity);
					//sendPosition(playerList.get(id - 1).getPos());
				}
			}
		}
		
			
		
		
			/*for(Player p : playerList){
				if(p.getId() == id){
					if(inDirection == "x"){
						
						p.updateX(velocity);
					}else{
						p.updateY(velocity);
					}
					
					sendPosition(playerList.get(id - 1).getPos());
					
				}
				
			}*/
				
					
					
			
		
	}
	
	public Player getPlayer(){
		/*Player player = new Player();
		for(Player p : playerList){
			if(p.getId() == id){
				player = p;
			}
		}*/
		
		return playerList.get(id - 1);
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
	
	public void sendDeadPlayer(){
		DeadPlayer dPlayer = new DeadPlayer();
		dPlayer.playerId = id;
		dPlayer.x = 0;
		dPlayer.y = 0;
		client.sendTCP(dPlayer);
			
		
	}
	
	public void getNewCoords(){
		DeadPlayer dPlayer = new DeadPlayer();
		dPlayer.playerId = id;
		dPlayer.x = -1;
		dPlayer.y = -1;
		client.sendTCP(dPlayer);
	}
    
    

}
