package com.game.TobyBall;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.kryonet.EndPoint;

public class Player {
    
        Health health = new Health( 0, 5 );
    
	Point2D.Float pos;
	Texture image;
	
	Bomb bombHolding;
	ArrayList<Bomb> bombsPlanted;
	Rectangle playerRect;
	int id;
	EndPoint endPoint;
	String imageLocation;
	boolean alive;
	int xVelocity, yVelocity;
	float lastKnownX, lastKnownY;
	int lives;
	int score;
	boolean invisible;
	
	public Player(){
		
	}
	
	public Player(Point2D.Float inPos, int inId, String inImageLocation){
		alive = true;
		pos = inPos;
		imageLocation = inImageLocation;
		//playerRect = new Rectangle(pos.x, pos.y, image.getWidth(), image.getHeight());
		bombsPlanted = new ArrayList<Bomb>();
		id = inId;
		xVelocity = 0;
		yVelocity = 0;
		lives = 5;
		score = 0;
		lastKnownX = pos.x;
		lastKnownY = pos.y;
		invisible = false;
		
		
	}
	
	public Player(Point2D.Float inPos){
		pos = inPos;
		image = new Texture("assets/player1.png");
		playerRect = new Rectangle(pos.x, pos.y, image.getWidth(), image.getHeight());
		bombsPlanted = new ArrayList<Bomb>();
		alive = true;
		
		
	}
	
	
	
	public Point2D.Float getPos(){
		return pos;
	}
	
	public Texture getImage(){
		return image;
	}
	
	public void updateX(float inX){
		pos.x += inX;
		playerRect.setX(pos.x);
	}
	
	public void updateY(float inY){
		pos.y += inY;
		playerRect.setY(pos.y);
	}
	
	public Rectangle getRect(){
		return playerRect;
	}
	
	public Bomb getBomb(){
		return bombHolding;
	}
	
	public void setBombHolding(Bomb inBomb){
		inBomb.setOwner(id);
		bombHolding = inBomb;
		
	}
	
	public void plantBomb(Bomb b){
		bombsPlanted.add(bombHolding);
		bombHolding = null;
		b.plantBomb(pos.x, pos.y, id);
	}
	
	public void plantBomb(Bomb b, float inX, float inY){
		bombsPlanted.add(bombHolding);
		bombHolding = null;
		b.plantBomb(inX, inY, id);
	}
	
	public void throwBomb(Bomb b){
	
		bombsPlanted.add(bombHolding);
		bombHolding = null;
		b.throwBomb(pos.x, pos.y, id);
	}
	
	public void throwBomb(Bomb b, float inX, float inY){
		
		bombsPlanted.add(bombHolding);
		bombHolding = null;
		b.throwBomb(inX, inY, id);
	}
	
	public ArrayList<Bomb> getBombsPlanted(){
		return bombsPlanted;
	}
	
	public void setImage(){
		
		image = new Texture(imageLocation);
		playerRect = new Rectangle(pos.x, pos.y, image.getWidth(), image.getHeight());
	}
	
	public int getId(){
		return id;
	}
	
	public void setPosition(Point2D.Float newPos){
		pos = newPos;
		if(playerRect != null){
			playerRect.setX(pos.x);
			playerRect.setY(pos.y);
		}
		
	}
	
	public String getImageLocation(){
		return imageLocation;
	}
	
	public void makeDead(){
		alive = false;
	}
	
	public void makeAlive(){
		alive = true;
	}
	
	public boolean isAlive(){
		return alive;
	}
	
	public void setXVelocity(int inVel){
		xVelocity = inVel;
	}
	
	public void setYVelocity(int inVel){
		yVelocity = inVel;
	}
	
	public void simulateMovement(){
		
		if(pos.x == lastKnownX){
			xVelocity = 0;
		}else if(pos.x >= lastKnownX){
			
			xVelocity = -5;
		}else{
			
			xVelocity = 5;
		}
		//}
		
		if(pos.y == lastKnownY){
			yVelocity = 0;
		}else if(pos.y > lastKnownY){
			yVelocity = -5;
		}else{
			yVelocity = 5;
		}
		
		
		if(Math.abs(pos.x - lastKnownX) >= 100 || Math.abs(pos.y - lastKnownY) >= 100){
			//lag is too great, so just hard set the players position
			pos.x = lastKnownX;
			pos.y = lastKnownY;
		}else{
			//Move toward the last known position
			pos.x += xVelocity;
			pos.y += yVelocity;
		}
		
		playerRect.setX(pos.x);
		playerRect.setY(pos.y);
		
		
	}
	
	public void setXPosition(float inX){
		pos.x = inX;
	}
	
	public void setYPosition(float inY){
		pos.y = inY;
	}
	
	public void setLastKnownX(float inX){
		lastKnownX = inX;
	}
	
	public void setLastKnownY(float inY){
		lastKnownY = inY;
	}
	
	public void increaseScore(){
		score++;
	}
	
	public void decreaseScore(){
		score--;
	}
	
	public void removeBombPlanted(Bomb inBomb){
		bombsPlanted.remove(inBomb);
		
	}
	
	public void removeBombsPlanted(){
		
		bombsPlanted.clear();
	}
	
	public void removeBombHolding(){
		bombHolding = null;
	}
	
	public int getScore(){
		return score;
	}
	
	public boolean isInvisible(){
		return invisible;
	}
	
	public void makeInvisible(){
		invisible = true;
	}
	
	public void makeVisible(){
		invisible = false;
	}

        public int getHealth(){
            return health.getValue();
        }
}
