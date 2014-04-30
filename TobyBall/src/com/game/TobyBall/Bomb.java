package com.game.TobyBall;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Bomb {
	
	enum State{
		EXPLODE, ARMED, INERT, TRAVELLING, THROWN
	}
	
	Point2D.Float pos;
	ArrayList<Shrapnel> shrapnel;
	Texture image;
	State state;
	Rectangle bombRect;
	boolean markForDelete;
	String imageLocation;
	int id;
	int[] shrapnelAngles;
	int deleteCount;
	int throwXVelocity, throwYVelocity;
	int owner;
	
	public Bomb(){
		
	}
	
	public Bomb(Point2D.Float inPos, int inId){
		deleteCount = 0;
		imageLocation = "assets/bomb.png";
		pos = inPos;
		shrapnel = new ArrayList<Shrapnel>();
		id = inId;
		//populateShrapnel();
		state = State.INERT;
		shrapnelAngles = new int[50];
		markForDelete = false;
		throwXVelocity = 0;
		throwYVelocity = 0;
		int owner = -1;
	}
	
	
	public void addAngle(int inAngle, int position){
		shrapnelAngles[position] = inAngle;
	}
	
	public int[] getAngles(){
		return shrapnelAngles;
	}
	
	public void addShrapnel(Shrapnel inShrap){
		shrapnel.add(inShrap);
	}
	
	public Texture getImage(){
		return image;
	}
	
	public Point2D.Float getPos(){
		return pos;
	}
	
	public ArrayList<Shrapnel> getShrapnel(){
		return shrapnel;
	}
	
	public State getState(){
		return state;
	}
	
	public void setState(State inState){
		state = inState;
		if(inState == State.ARMED){
			imageLocation = "assets/bomb_armed.png";
			
		}else if(inState == State.EXPLODE){
			for(Shrapnel sh : shrapnel){
				sh.setX(pos.x);
				sh.setY(pos.y);
			}
		}
	}
	
	public Rectangle getRect(){
		return bombRect;
	}
	
	public void setPos(Point2D.Float inPos){
		pos = inPos;
	}
	
	public void plantBomb(float inX, float inY, int inId){
		pos = new Point2D.Float(inX, inY);
		state = State.ARMED;
		imageLocation = "assets/bomb_armed.png";
		
		for(Shrapnel sh : shrapnel){
			sh.setX(inX);
			sh.setY(inY);
			sh.setPlayerId(inId);
		}
		
		
	}
	
	public void throwBomb(float inX, float inY, int inId){
		pos = new Point2D.Float(inX, inY);
		state = State.THROWN;
		imageLocation = "assets/bomb_armed.png";
		
		for(Shrapnel sh : shrapnel){
			sh.setX(inX);
			sh.setY(inY);
			sh.setPlayerId(inId);
		}
	}
	
	public void removeShrapnel(Shrapnel inShrap){
		shrapnel.remove(inShrap);
	}
	
	public void markForDeletion(){
		markForDelete = true;
	}
	
	public boolean markedForDeletion(){
		return markForDelete;
	}
	
	public void setImage(String inLoc){
		image = new Texture(inLoc);
		bombRect = new Rectangle(pos.x, pos.y, image.getWidth(), image.getHeight());
	}
	
	public void setImage(){
		image = new Texture(imageLocation);
		bombRect = new Rectangle(pos.x, pos.y, image.getWidth(), image.getHeight());
	}
	
	public int getId(){
		return id;
	}
	
	public void increaseDelete(){
		deleteCount++;
	}
	
	public int getDeleteCount(){
		return deleteCount;
	}
	
	public void setXThrowVelocity(int inSpeed){
		throwXVelocity = inSpeed;
	}
	
	public void setYThrowVelocity(int inSpeed){
		throwYVelocity = inSpeed;
	}
	
	public void updatePosition(ArrayList<Wall> wallList, Wall verticalWall){
		
		float testX, testY;
		Rectangle testRect;
		
		testX = pos.x + throwXVelocity;
		testY = pos.y + throwYVelocity;
		testRect = new Rectangle(testX, testY, image.getWidth(), image.getHeight());
		
		for(Wall w : wallList){
			
			if(throwYVelocity > 0 && testRect.overlaps(w.getRectangle())){
				throwYVelocity = -7;
				
			}else if(throwYVelocity < 0 && testRect.overlaps(w.getRectangle())){
				throwYVelocity = 7;
			}
			
		}
		
		if(throwXVelocity > 0 && testRect.overlaps(verticalWall.getRectangle())){
			throwXVelocity = -7;
			
		}else if(throwXVelocity < 0 && testRect.overlaps(verticalWall.getRectangle())){
			throwXVelocity = 7;
		}
		
		
		
		if(pos.x >= 1060){
			throwXVelocity = -7;
		}else if(pos.x <= 0){
			throwXVelocity = 7;
		}
		if(pos.y >= 705){
			throwYVelocity = -7;
		}else if(pos.y <= 0){
			throwYVelocity = 7;
		}
		pos.x += throwXVelocity;
		pos.y += throwYVelocity;
		bombRect = new Rectangle(pos.x, pos.y, image.getWidth(), image.getHeight());
		
	}
	
	public int getXThrow(){
		return throwXVelocity;
	}
	public int getYThrow(){
		return throwYVelocity;
	}
	
	public void setOwner(int inId){
		owner = inId;
	}

}
