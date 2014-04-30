package com.game.TobyBall;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.kryonet.EndPoint;

public class Player {
	
	
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
		bombHolding = inBomb;
	}
	
	public void plantBomb(Bomb b){
		bombsPlanted.add(bombHolding);
		bombHolding = null;
		b.plantBomb(pos.x, pos.y);
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

}
