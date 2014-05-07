// TESTING TESTING

package com.game.TobyBall;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class TobyBall {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new LwjglApplication(new TobyBallGame(), "Game", 1080, 720, false);

	}

}
