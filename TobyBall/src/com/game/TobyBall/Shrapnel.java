package com.game.TobyBall;

import java.awt.geom.Point2D;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Shrapnel{
	
	
	private int speed, angle;
	private float x, y;
	Point2D.Float position;
	Texture image;
	Rectangle shrapnelRect;
	String imageLocation;
	
	public Shrapnel(){
		
	}
	public Shrapnel(float inX, float inY){
		 
		/*imageLocation = "assets/shrapnel.png";
		speed = 150;
		x = inX;
		y = inY;
		position = new Point2D.Float(x,y);
		angle = rand.nextInt(360);*/
		
		
		
		
	}
	
	public Shrapnel(float inX, float inY, int inAngle, Texture inImage){
		
		//imageLocation = "assets/shrapnel.png";
		speed = 150;
		x = inX;
		y = inY;
		//position = new Point2D.Float(x,y);
		angle = inAngle;
		image = inImage;
		shrapnelRect = new Rectangle(x, y, image.getWidth(), image.getHeight());
	}
	
	
	public void updatePosition(double dtime) {
	       
	    x = (float) (x + speed*Math.cos(angle)*dtime);
	    y = (float) (y + speed*Math.sin(angle)*dtime); 
	    shrapnelRect.setX(x);
	    shrapnelRect.setY(y);
	    
	    
	}
	
	public Point2D.Float getPos(){
		position = new Point2D.Float(x, y);
		return position;
	}
	
	public Texture getImage(){
		return image;
	}
	
	public Rectangle getRect(){
		return shrapnelRect;
	}
	
	public void setPos(Point2D.Float inPos){
		position = inPos;
	}
	
	public void setImage(String inLoc){
		image = new Texture(inLoc);
		shrapnelRect = new Rectangle(x, y, image.getWidth(), image.getHeight());
	}
	
	public void setX(float inX){
		x = inX;
	}
	
	public void setY(float inY){
		y = inY;
	}
	
	
}
