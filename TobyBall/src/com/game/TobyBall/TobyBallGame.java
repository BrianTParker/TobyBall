package com.game.TobyBall;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.game.TobyBall.Bomb.State;
import com.game.TobyBall.Bomb.State;

public class TobyBallGame extends Game{
	
	public enum keyBoardState{
		DOWN, UP
	}
	
	public enum keyPress{
		ANY_KEY_PRESSED, NO_KEY_PRESSED
	}
	
	
	SpriteBatch batch;
	Bomb bomb;
	Bomb bomb2;
	Bomb bomb3;
	boolean explode;
	Player player;
	ArrayList<Bomb> bombList;
	keyBoardState previousKeyboardState;
	keyBoardState currentKeyboardState;
	TobyClient client;
	TobyServer server;
	ArrayList<Player> playerList;
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

	public void create () {
		host = true;
		playerPositionCount = 0;
		dead = false;
		deathCount = 101;
		
		time = 0;
		bombListPing = 0;
		playerListPing = 0;
		waitTime = (float) .066;
		previousKeyboardState = keyBoardState.UP;
		currentKeyboardState = keyBoardState.UP;
		bombList = new ArrayList<Bomb>();
		playerList = new ArrayList<Player>();
		
		
		
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
	            
	        
		
	}

	public void render () {
		super.render();
		playerVelocity = 0;
		playerDirection = "z";
		deleteBomb = null;
		kPress = keyPress.NO_KEY_PRESSED;
		if(client.getId() == -1){
			client.pingForId();
		}
		
		if(client.getBombs().size() <= 0 && bombListPing >= 200){
			if(client.getBombs().size() < 1){
				client.pingForBombs();
			}
			
			bombListPing = 0;
		}
		
		if(client.getPlayerList().size() <= 0 && playerListPing >= 150){
			client.pingForPlayerList();
			playerListPing = 0;
		}
		if(Gdx.input.isKeyPressed(Keys.SPACE)){
			
			previousKeyboardState = currentKeyboardState;
			currentKeyboardState = keyBoardState.DOWN;
			
			
			
			
			
			
			
		}else{
			previousKeyboardState = currentKeyboardState;
			currentKeyboardState = keyBoardState.UP;
		}
		
		if(currentKeyboardState == keyBoardState.DOWN && previousKeyboardState == keyBoardState.UP && !dead){
			
			boolean bombAction = false;
			
			player = client.getPlayer();
			
			for(Bomb b : client.getBombs()){
				
				if(player.getRect().overlaps(b.getRect()) && player.getBomb() == null ){
					
					client.requestBomb(b);				
					
					bombAction = true;
					break;
				}else if(client.getPlayer().getBomb() != null && client.getPlayer().getBomb() == b){
					
					//b.plantBomb(player.getPos().x, player.getPos().y );
					player.plantBomb(b);
					b.setImage("assets/bomb_armed.png");
					bombAction = true;	
					client.sendArmedBomb(b);
					
				}					
			}
			
			if(bombAction == false){
				if(player.getBomb() == null && player.getBombsPlanted().size() > 0){
					explodedBombs = new int[player.getBombsPlanted().size()];
					for(Bomb b2 : client.getBombs()){
						for(int i = 0; i < player.getBombsPlanted().size(); i++){
							if(b2 == player.getBombsPlanted().get(i)){
								b2.setState(State.EXPLODE);
								explodedBombs[i] = b2.getId();
							}
						}
					}
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
		
		if(!dead){
			if(Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.DPAD_UP)){
				client.updatePlayerPosition(5, "y");
				kPress = keyPress.ANY_KEY_PRESSED;
			}
			if(Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DPAD_DOWN)){
				client.updatePlayerPosition(-5, "y");
				kPress = keyPress.ANY_KEY_PRESSED;
				
				
			}
			if(Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.DPAD_LEFT)){
				client.updatePlayerPosition(-5, "x");
				kPress = keyPress.ANY_KEY_PRESSED;
				
				
			}
			if(Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)){
				client.updatePlayerPosition(5, "x");
				kPress = keyPress.ANY_KEY_PRESSED;
				
				
			}
		}
		
		if(kPress == keyPress.ANY_KEY_PRESSED && client.getPlayerList().size() > 0 && playerPositionCount >= 3){
			//client.updatePlayerPosition(playerVelocity, playerDirection);
			client.sendPosition(client.getPlayer().getPos());
			playerPositionCount = 0;
			
		}
		
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		
		
		
		
		if(client.getBombs().size() > 0){	
			for(Bomb b: client.getBombs()){
				
				if(b.getState() == State.INERT || b.getState() == State.ARMED){	
					
					
					batch.draw(b.getImage(), b.getPos().x, b.getPos().y);
				}else if(b.getState() == State.EXPLODE){
					
					boolean draw = false;
					for(Shrapnel sh : b.getShrapnel()){
						
						
						sh.updatePosition(Gdx.graphics.getDeltaTime());
						if(sh.getPos().x > 0 && sh.getPos().x < 1080 && sh.getPos().y > 0 && sh.getPos().y < 720){
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
			for(Player p : client.getPlayerList()){
				
				if(p.getImage() != null){
					if(p.isAlive()){
						if(p.getId() != client.getId()){
							p.simulateMovement();
							
						}
						
						batch.draw(p.getImage(), p.getPos().x, p.getPos().y);
					}
					
				}
				
			}
			
		}
		
		batch.end();
		
		if(client.getPlayerList().size() > 0){
			for(Bomb b: client.getBombs()){
				for(Shrapnel sh : b.getShrapnel()){
					if(client.getPlayer().getRect().overlaps(sh.getRect()) && b.getState() == State.EXPLODE && client.getPlayer().isAlive()){
						client.getPlayer().makeDead();
						dead = true;
						client.sendDeadPlayer();
						deathCount = 0;
						
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
