package com.game.TobyBall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SplashScreen implements Screen{
	
	Texture splashTexture;
	Sprite splashSprite;
	SpriteBatch batch;
	
	TobyBallGame game;
	
	public SplashScreen(TobyBallGame game){
		this.game = game;
	}
	
	@Override
	public void dispose() {
		
		
	}

	@Override
	public void hide() {
		
		
	}

	@Override
	public void pause() {
		
		
	}

	@Override
	public void render(float arg0) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		splashSprite.draw(batch);
		batch.end();
		
	}

	@Override
	public void resize(int arg0, int arg1) {
		
		
	}

	@Override
	public void resume() {
		
		
	}

	@Override
	public void show() {
		splashTexture = new Texture("assets/TobyBallSplash.png");
		splashTexture.setFilter(TextureFilter.Linear,  TextureFilter.Linear);
		
		splashSprite = new Sprite(splashTexture);
		//splashSprite.setOrigin(splashSprite.getWidth()/2, splashSprite.getHeight()/2);
		//splashSprite.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		
		batch = new SpriteBatch();
	}

	/**
	 * @param args
	 */
	

}
