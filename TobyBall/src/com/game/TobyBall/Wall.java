package com.game.TobyBall;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Wall {
	
	
	private float x, y;
	private Rectangle wallRect;
	Texture image;
	float originX, originY;
	TextureRegion region;
	float angle;
	
	public Wall(float inX, float inY, String textureLocation){
		
		x = inX;
		y = inY;
		image = new Texture(textureLocation);
		
		
		region = new TextureRegion(image, 0, 0, image.getWidth(), image.getHeight());
		originX = region.getRegionWidth()/2f;
		originY = region.getRegionHeight()/2f;
		
		wallRect = new Rectangle(x, y, image.getWidth(), image.getHeight());
		
	}
	
	public Texture getImage(){
		return image;
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public TextureRegion getRegion(){
		return region;
	}
	
	public float getOriginX(){
		return originX;
	}
	
	public float getOriginY(){
		return originY;
	}
	
	public float getAngle(){
		return angle;
	}
	
	public Rectangle getRectangle(){
		return wallRect;
	}

}
