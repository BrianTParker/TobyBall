package com.game.TobyBall;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.game.TobyBall.Bomb.State;
import com.game.TobyBall.Bomb.State;
import com.game.screens.Splash;

public class TobyBallGame extends Game{
	
	public enum keyBoardState{
		DOWN, UP
	}
	
	public enum keyPress{
		ANY_KEY_PRESSED, NO_KEY_PRESSED
	}
	
	public enum leftOrRight{
		RIGHT, LEFT,NONE
	}
	
	public enum upOrDown{
		UP, DOWN, NONE
	}
	
	public enum gameState{
		SPLASH, MENU, PLAY
	}
	
	
	SpriteBatch batch;
	
	boolean explode;
	
	keyBoardState previousKeyboardState;
	keyBoardState currentKeyboardState;
	leftOrRight xDirection;
	upOrDown yDirection;
	TobyClient client;
	TobyServer server;
	gameState gameState;
	
	int playerListPing, bombListPing;
	boolean host;
	int[] explodedBombs;
	float time, waitTime;
	Bomb deleteBomb;
	int deathCount;
	boolean dead;
	int playerPositionCount;
	int playerVelocity;
	String playerDirection;
	keyPress kPress;
	BitmapFont font;
	CharSequence str = "Hello World!";
	Texture scoreBanner;
	String score;
	boolean stealthGame;
	
	
	//sound
	Sound bombPickup;
	Sound bombPlant;
	Sound bombExplode;
	Sound playerDeath;
	Sound throwBomb;
	
	
	//This method is only called once at the beginning of execution
	public void create () {
		
		//I'm trying to figure out how to have different screens.  I was going to use ENUMS to decide what state the game was in
		//gameState = gameState.SPLASH;
		//setScreen(new Splash());
		gameState = gameState.PLAY;
		
		//if this is true, the server is created
		host = true;
		
		//This turns on and off line of sight gameplay
		stealthGame = false;
		
		
		playerPositionCount = 0;
		dead = false;
		deathCount = 101;
		scoreBanner = new Texture("assets/scoreBanner.png");
		time = 0;
		bombListPing = 0;
		playerListPing = 0;
		waitTime = (float) .066;
		
		//i'm using these states so button presses only read once.  Otherwise you will pick up and place and explode bombs immediately
		previousKeyboardState = keyBoardState.UP;
		currentKeyboardState = keyBoardState.UP;
		score = "Score: 0";
		
		font = new BitmapFont();
		
		//sound
		bombPlant = Gdx.audio.newSound(Gdx.files.internal("assets/plant.ogg"));
		bombExplode = Gdx.audio.newSound(Gdx.files.internal("assets/explode.ogg"));
		playerDeath = Gdx.audio.newSound(Gdx.files.internal("assets/death.ogg"));
		throwBomb = Gdx.audio.newSound(Gdx.files.internal("assets/throw.ogg"));
		
		//setScreen(new SplashScreen(this));
		
		

		
		
		//This is used for drawing to the screen
		batch = new SpriteBatch(); 
		
		
		//create the server for the host
		if(host == true){
			try {
	 			
	 			server = new TobyServer();
	 		} catch (IOException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
		}
    	
	            
	     
		
		
    	try {
 			//Create a client object and connect to the server using the default localhost address.  This is for host only.  
    		//Right now i'm hard coding an ip instead of localhost when i create a client only version
 			client = new TobyClient("localhost");
 		} catch (IOException e) {
 			
 			e.printStackTrace();
 		}
	            
	    
    	//The server sets everything up for the game.  Below are the calls from the client to the server to get all the relevant 
    	//information needed to set up the game on the local client
    	client.makeWalls();
    	client.pingForBombs();
    	client.pingForPlayerList();
    	client.pingForId();
    	
	}

	
	//This is the game loop.  All drawing to the screen is done here
	public void render () {
		super.render();
		if(gameState == gameState.PLAY){
			playerVelocity = 0;
			playerDirection = "z";
			deleteBomb = null;
			kPress = keyPress.NO_KEY_PRESSED;
			xDirection = leftOrRight.NONE;
			yDirection = upOrDown.NONE;
			
			
			//any reference to the client is referring to the local player. 
			if(client.getPlayerIndex() == -1){
				client.setPlayerIndex();
				
			}
			
			
			
			
			//This is logic to determine if a key is pressed.  But won't continue to register as being pressed if the key is held down.
			//This is important since the space bar is used for multiple things and libGDX will register it as being pressed constantly if it's held down
			if(Gdx.input.isKeyPressed(Keys.SPACE)){
				
				previousKeyboardState = currentKeyboardState;
				currentKeyboardState = keyBoardState.DOWN;
				
			}else{
				previousKeyboardState = currentKeyboardState;
				currentKeyboardState = keyBoardState.UP;
			}
			
			
			
			//only get input if the player is alive
			if(!dead){
				
				//updates the players position by 5 pixels on the y axis
				if(Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.DPAD_UP)){
					client.updatePlayerPosition(5, "y");
					kPress = keyPress.ANY_KEY_PRESSED;
					yDirection = upOrDown.UP;
					
				}
				//updates the players position by -5 pixels on the y axis
				if(Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DPAD_DOWN)){
					client.updatePlayerPosition(-5, "y");
					kPress = keyPress.ANY_KEY_PRESSED;
					yDirection = upOrDown.DOWN;
					
				}
				//updates the players position by -5 pixels on the x axis
				if(Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.DPAD_LEFT)){
					client.updatePlayerPosition(-5, "x");
					kPress = keyPress.ANY_KEY_PRESSED;
					xDirection = leftOrRight.LEFT;
					
				}
				//updates the players position by 5 pixels on the x axis
				if(Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)){
					client.updatePlayerPosition(5, "x");
					kPress = keyPress.ANY_KEY_PRESSED;
					xDirection = leftOrRight.RIGHT;
					
				}
			}
			
			
			
			//This section is the logic for what happens when space bar is pressed
			if(currentKeyboardState == keyBoardState.DOWN && previousKeyboardState == keyBoardState.UP && !dead){
				
				//This is used to determine if some kind of action was taken directly with a bomb.  i.e. Picked up/ Placed bomb
				boolean bombAction = false;
				
				
				//iterate through each bomb to see if the players position intersects any of the bombs positions
				for(Bomb b : client.getBombs()){
					
					//If the bomb is not currently placed and armed, send a request to the server to pick it up.
					//The server resolves conflicts on a first come first server basis.  So if two people try to pick up the bomb, 
					//whoever sends the request to the server first gets it
					if(b.getState() != State.ARMED){
					
						if(client.getPlayer().getRect().overlaps(b.getRect()) && client.getPlayer().getBomb() == null){
							
							client.requestBomb(b);				
							
							bombAction = true;
							break;
						}else if(client.getPlayer().getBomb() != null && client.getPlayer().getBomb() == b){
							
							//If the player is moving and shift is not pressed, The bomb is thrown at a speed of 7 in whatever directing the player was moving
							if((xDirection != leftOrRight.NONE || yDirection != upOrDown.NONE) && !Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
								int x = 0, y = 0;
								if(xDirection == leftOrRight.RIGHT){
									x = 7;
								}else if(xDirection == leftOrRight.LEFT){
									x = -7;
								}
								if(yDirection == upOrDown.UP){
									y = 7;
								}else if(yDirection == upOrDown.DOWN){
									y = -7;
								}
								
								b.setXThrowVelocity(x);
								b.setYThrowVelocity(y);
								
								client.getPlayer().throwBomb(b);
								b.setImage("assets/bomb_armed.png");
								bombAction = true;	
								client.sendArmedBomb(b, client.getPlayer().getPos().x, client.getPlayer().getPos().y);
								
								throwBomb.play();
							}else{
								client.getPlayer().plantBomb(b);
								b.setImage("assets/bomb_armed.png");
								bombAction = true;	
								
								//Send information about the armed bomb to the server so it can update the other players
								client.sendArmedBomb(b, client.getPlayer().getPos().x, client.getPlayer().getPos().y);
								bombPlant.play();  //plays the bomb placement sound
								
							}
							
						}
					}
				}
				
				
				//If space bar was pressed, but no direct action was taken with a bomb, check to see if the player has any bombs planted that need to be exploded
				if(bombAction == false){
					if(client.getPlayer().getBomb() == null && client.getPlayer().getBombsPlanted().size() > 0){
						
						explodedBombs = new int[client.getPlayer().getBombsPlanted().size()];
						for(Bomb b2 : client.getBombs()){
							for(int i = 0; i < client.getPlayer().getBombsPlanted().size(); i++){
								
								if(b2.getId() == client.getPlayer().getBombsPlanted().get(i).getId()){
									
									b2.setState(State.EXPLODE);
									explodedBombs[i] = b2.getId();
									
									bombExplode.play();
									
									
								}
							}
						}
						
						client.getPlayer().removeBombsPlanted();
						
						client.explodeBomb(explodedBombs);
						
					}
				}
				
				
			}
			
			
			
			//After a player dies, wait 100 ticks before getting new coordinates
			if(deathCount <= 100){
				deathCount++;
			}else if(dead){
				client.getNewCoords();
				dead = false;
				
			}
			
			
			//Update the server on the players position so it can update all the other clients
			if(kPress == keyPress.ANY_KEY_PRESSED && client.getPlayerList().size() > 0 && playerPositionCount >= 3){
				
				client.sendPosition(client.getPlayer().getPos());
				playerPositionCount = 0;
				
			}
			
			
			
			
			
			//Clears the screen or something
			Gdx.gl.glClearColor(1, 1, 1, 1);
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			
			
			//All drawing happens between batch.begin() and batch.end()
			batch.begin();
			
			//iterate through each wall and draw it to the screen
			for(Wall w : client.getWalls()){
				batch.draw(w.getImage(), w.getX(), w.getY());
			}
			
			//Draw the vertical wall in the center of the screen
			batch.draw(client.verticalWall.getImage(), client.verticalWall.getX(), client.verticalWall.getY());
			
			
			//get the list of all bombs and draw them to the screen 
			if(client.getBombs().size() > 0){	
				for(Bomb b: client.getBombs()){
					
					if(b.getState() == State.INERT || b.getState() == State.ARMED || b.getState() == State.THROWN){	
						
						if(b.getState() == State.THROWN){
							
							//for thrown bombs, update their position.  the walls are passed in to check for collisions 
							b.updatePosition(client.getWalls(), client.verticalWall);
						}
						
						//actually draw them to the screen
						batch.draw(b.getImage(), b.getPos().x, b.getPos().y);
					}
					
					//If the bomb is in the explode state
					if(b.getState() == State.EXPLODE){
						
						//used to determine when the bomb should be deleted from the list of bombs
						boolean draw = false;
						
						//iterate through each exploded bombs shrapnel and draw them to the screen
						for(Shrapnel sh : b.getShrapnel()){
							
							
							//check to see the shrapnel has passes out of the screen or collided with a wall. 
							if(sh.getPos().x > 0 && sh.getPos().x < 1024 && sh.getPos().y > 0 && sh.getPos().y < 720 && !client.shrapnelWallCollision(sh)){
								sh.updatePosition(Gdx.graphics.getDeltaTime());
								batch.draw(sh.getImage(), sh.getPos().x, sh.getPos().y);
								
								
								draw = true;
							}
							
							
						
						}
						
						//Once all the shrapnel is gone, the bomb is marked for deletion
						if(draw == false){
							b.markForDeletion();
						}
						
					}
					
				}
			}
			
			
		   //iterate through each player
			if(client.getPlayerList().size() > 0){
				
				int scoreY = 100;
				
				
				
				for(Player p : client.getPlayerList()){
					
					//if the player in the iteration is not the local player
					if(p.getId() != client.getId() && client.getPlayer() != null){
						
						//pass in the remote player and perform line of sight logic against the local player.  If there is no line of sight, mark the remote player invisible
						client.checkVisibility(client.getPlayer(), p);
					}
					
					
					//draws the score to the screen
					score = "Player" + p.getId() + ": " + p.getScore();
					font.setColor(1,0,0, 1);
					font.draw(batch, score, 0,scoreY); 
					scoreY -= 20;
					
					//move and draw the players to the screen
					if(p.getImage() != null){
						
						if(p.isAlive()){
							
							//for remote players, simulate their movement
							if(p.getId() != client.getId()){
								p.simulateMovement();
								
							}
							
							//Checks to see if the player is invisible and/or is a stealth game.  
							if((!stealthGame) || (stealthGame && !p.isInvisible()) ){
								batch.draw(p.getImage(), p.getPos().x, p.getPos().y);
							}
							
						}
						
					}
					
				}
				
			}
			
			batch.end(); //End of the drawing
			
			
			//Logic to determine if the player intersects any shrapnel and kills them 
			if(client.getPlayerList().size() > 0){
				for(Bomb b: client.getBombs()){
					for(Shrapnel sh : b.getShrapnel()){
						
						if(client.getPlayerIndex() != -1){
							
							try{
								if(client.getPlayer().getRect().overlaps(sh.getRect()) && b.getState() == State.EXPLODE && client.getPlayer().isAlive()){
									client.getPlayer().makeDead();
									dead = true;
									client.sendDeadPlayer(sh.getPlayerId());
									deathCount = 0;
									playerDeath.play();
									
								}
							}catch(IndexOutOfBoundsException e){
								e.printStackTrace();
							}
						}
						
					}
				}
			}
			
			
	
			//Sends bombs that are marked for deletion to the server.  The server will let all the clients know and will send out a new bomb to replace it
			for(Bomb b : client.getBombs()){
				if(b.markedForDeletion()){
					client.sendBombDelete(b.getId());
					deleteBomb = b;
				}
			}
			
			//delete the bomb
			if(deleteBomb != null){
				client.getBombs().remove(deleteBomb);
			}
			

			
			playerListPing++;
			bombListPing++;
			playerPositionCount++;
		
		}
	}

	public void resize (int width, int height) {
		super.resize(width, height);
	}

	public void pause () {
	}

	public void resume () {
		super.dispose();
	}

	public void dispose () {
		super.dispose();
                Gdx.app.exit();
	}
	

	
	
	
	

}
