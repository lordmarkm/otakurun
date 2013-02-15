package com.baldwin.libgdx.commons;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.baldwin.libgdx.commons.util.DisposableObjectPool;

public abstract class BasePlatform implements ApplicationListener, GestureListener {

	protected OrthographicCamera camera;
	protected int CAM_WIDTH = 800;
	protected int CAM_HEIGHT = 480;
	
	protected SpriteBatch batch;
	protected World world;
	protected DisposableObjectPool pool;
	
	@Override
	public void create() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, CAM_WIDTH, CAM_HEIGHT);
		
		batch = new SpriteBatch();
		
		/**
		 * You can set the world's gravity in its constructor. Here, the gravity
		 * is negative in the y direction (as in, pulling things down).
		 */
		world = new World(new Vector2(0.0f, -10.0f), true);
		
		pool = DisposableObjectPool.getInstance();
	}
	
	protected void clearScreen() {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}
	
	//unused ApplicationListener methods
	@Override
	public void resize(int width, int height) {}
	
	@Override
	public void pause() {}
	
	@Override
	public void resume() {}

	//GestureListener methods
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}
}
