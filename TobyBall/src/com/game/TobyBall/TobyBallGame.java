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
	
	
	SpriteBatch batch;
	
	boolean explode;
	
	keyBoardState previousKeyboardState;
	keyBoardState currentKeyboardState;
	leftOrRight xDirection;
	upOrDown yDirection;
	TobyClient client;
	TobyServer server;
	
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

	public void create () {
		
		host = true;
		stealthGame = false;
		
		playerPositionCount = 0;
		dead = false;
		deathCount = 101;
		scoreBanner = new Texture("assets/scoreBanner.png");
		time = 0;
		bombListPing = 0;
		playerListPing = 0;
		waitTime = (float) .066;
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
		//bomb = new Bomb(new Point2D.Float(500,500));
		//bomb2 = new Bomb(new Point2D.Float(700, 200));
		//bomb3 = new Bomb(new Point2D.Float(100, 200));
		
		//bombList.add(bomb);
		//bombList.add(bomb2);
		//bombList.add(bomb3);
		
		
		
		batch = new SpriteBatch(); 
		//player = new Player(new Point2D.Float(200,200));
		
		
		if(host == true){
			try {
	 			
	 			server = new TobyServer();
	 		} catch (IOException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
		}
    	
	            
	     
		
		
    	try {
 			
 			client = new TobyClient("localhost");
 		} catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
	            
	        
    	client.makeWalls();
    	client.pingForBombs();
    	client.pingForPlayerList();
    	client.pingForId();
    	//client.setPlayerIndex();
	}

	public void render () {
		super.render();
		
		playerVelocity = 0;
		playerDirection = "z";
		deleteBomb = null;
		kPress = keyPress.NO_KEY_PRESSED;
		xDirection = leftOrRight.NONE;
		yDirection = upOrDown.NONE;
		/*if(client.getId() == -1){
			client.pingForId();
		}*/
		
		/*if(client.getBombs().size() <= 0 && bombListPing >= 200){
			if(client.getBombs().size() < 1){
				client.pingForBombs();
			}
			
			bombListPing = 0;
		}*/
		
		if(client.getPlayerIndex() == -1){
			client.setPlayerIndex();
			
		}
		
		/*if(client.getPlayerList().size() <= 0 && playerListPing >= 150){
			client.pingForPlayerList();
			playerListPing = 0;
		}*/
		if(Gdx.input.isKeyPressed(Keys.SPACE)){
			
			previousKeyboardState = currentKeyboardState;
			currentKeyboardState = keyBoardState.DOWN;
			
			
			
			
			
			
			
		}else{
			previousKeyboardState = currentKeyboardState;
			currentKeyboardState = keyBoardState.UP;
		}
		
		if(!dead){
			if(Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.DPAD_UP)){
				client.updatePlayerPosition(5, "y");
				kPress = keyPress.ANY_KEY_PRESSED;
				yDirection = upOrDown.UP;
				
			}
			if(Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DPAD_DOWN)){
				client.updatePlayerPosition(-5, "y");
				kPress = keyPress.ANY_KEY_PRESSED;
				yDirection = upOrDown.DOWN;
				
			}
			if(Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.DPAD_LEFT)){
				client.updatePlayerPosition(-5, "x");
				kPress = keyPress.ANY_KEY_PRESSED;
				xDirection = leftOrRight.LEFT;
				
			}
			if(Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)){
				client.updatePlayerPosition(5, "x");
				kPress = keyPress.ANY_KEY_PRESSED;
				xDirection = leftOrRight.RIGHT;
				
			}
		}
		
		
		
		
		if(currentKeyboardState == keyBoardState.DOWN && previousKeyboardState == keyBoardState.UP && !dead){
			
			boolean bombAction = false;
			
			
			
			for(Bomb b : client.getBombs()){
				
				if(b.getState() != State.ARMED){
				
					if(client.getPlayer().getRect().overlaps(b.getRect()) && client.getPlayer().getBomb() == null){
						
						client.requestBomb(b);				
						
						bombAction = true;
						break;
					}else if(client.getPlayer().getBomb() != null && client.getPlayer().getBomb() == b){
						
						//b.plantBomb(player.getPos().x, player.getPos().y );
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
							client.sendArmedBomb(b, client.getPlayer().getPos().x, client.getPlayer().getPos().y);
							bombPlant.play();
							
						}
						
					}
				}
			}
			
			
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
		
		if(deathCount <= 100){
			deathCount++;
		}else if(dead){
			client.getNewCoords();
			dead = false;
			
		}
		
		
		
		if(kPress == keyPress.ANY_KEY_PRESSED && client.getPlayerList().size() > 0 && playerPositionCount >= 3){
			//client.updatePlayerPosition(playerVelocity, playerDirection);
			client.sendPosition(client.getPlayer().getPos());
			playerPositionCount = 0;
			
		}
		
		
		
		
		
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		
		batch.begin();
		
		
		for(Wall w : client.getWalls()){
			batch.draw(w.getImage(), w.getX(), w.getY());
		}
		batch.draw(client.verticalWall.getImage(), client.verticalWall.getX(), client.verticalWall.getY());
		
		if(client.getBombs().size() > 0){	
			for(Bomb b: client.getBombs()){
				
				if(b.getState() == State.INERT || b.getState() == State.ARMED || b.getState() == State.THROWN){	
					
					if(b.getState() == State.THROWN){
						
						b.updatePosition(client.getWalls(), client.verticalWall);
					}
					
					batch.draw(b.getImage(), b.getPos().x, b.getPos().y);
				}
				if(b.getState() == State.EXPLODE){
					
					boolean draw = false;
					for(Shrapnel sh : b.getShrapnel()){
						
						
						
						if(sh.getPos().x > 0 && sh.getPos().x < 1024 && sh.getPos().y > 0 && sh.getPos().y < 720 && !client.shrapnelWallCollision(sh)){
							sh.updatePosition(Gdx.graphics.getDeltaTime());
							batch.draw(sh.getImage(), sh.getPos().x, sh.getPos().y);
							
							
							draw = true;
						}
						
						
					
					}
					
					if(draw == false){
						b.markForDeletion();
					}
					
				}
				
			}
		}
		
		
	   
		if(client.getPlayerList().size() > 0){
			
			int scoreY = 100;
			
			
			
			for(Player p : client.getPlayerList()){
				
				if(p.getId() != client.getId() && client.getPlayer() != null){
					
					client.checkVisibility(client.getPlayer(), p);
				}
				
				score = "Player" + p.getId() + ": " + p.getScore();
				font.setColor(1,0,0, 1);
				font.draw(batch, score, 0,scoreY); 
				scoreY -= 20;
				if(p.getImage() != null){
					
					if(p.isAlive()){
						if(p.getId() != client.getId()){
							p.simulateMovement();
							
						}
						
						if((!stealthGame) || (stealthGame && !p.isInvisible()) ){
							batch.draw(p.getImage(), p.getPos().x, p.getPos().y);
						}
						
					}
					
				}
				
			}
			
		}
		
		batch.end();
		
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
		
		
		/*Iterator<Bomb> bombIter = client.getBombs().iterator();
		while(bombIter.hasNext()){
			if(bombIter.next().markedForDeletion()){
				client.sendBombDelete(bombIter.);
				//bombIter.remove();
				
			}
		}*/
		
		for(Bomb b : client.getBombs()){
			if(b.markedForDeletion()){
				client.sendBombDelete(b.getId());
				deleteBomb = b;
			}
		}
		
		if(deleteBomb != null){
			client.getBombs().remove(deleteBomb);
		}
		
		/*
		if(bombList.size() < 3){
			Random rand = new Random();
			Bomb newBomb = new Bomb(new Point2D.Float(rand.nextInt(1000), rand.nextInt(700)));
			bombList.add(newBomb);
		}*/
		
		playerListPing++;
		bombListPing++;
		playerPositionCount++;
		
		
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
	}
	

	
	
	
	

}
