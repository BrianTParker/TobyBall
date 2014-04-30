package com.game.TobyBall;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Bomb {
	
	enum State{
		EXPLODE, ARMED, INERT, TRAVELLING
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
		shrapnelAngles = new int[20];
		markForDelete = false;
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
			
		}
	}
	
	public Rectangle getRect(){
		return bombRect;
	}
	
	public void setPos(Point2D.Float inPos){
		pos = inPos;
	}
	
	public void plantBomb(float inX, float inY){
		pos = new Point2D.Float(inX, inY);
		state = State.ARMED;
		imageLocation = "assets/bomb_armed.png";
		
		for(Shrapnel sh : shrapnel){
			sh.setX(inX);
			sh.setY(inY);
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
	

}
