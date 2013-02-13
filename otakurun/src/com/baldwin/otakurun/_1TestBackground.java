package com.baldwin.otakurun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.baldwin.libgdx.commons.BasePlatform;
import com.baldwin.libgdx.commons.SimpleTiledMapHelper;

public class _1TestBackground extends BasePlatform {
	/**
	 * The time the last frame was rendered, used for throttling framerate
	 */
	private long lastRender;

	private SimpleTiledMapHelper tiledMapHelper;

	/**
	 * The screen coordinates of where a drag event began, used when updating
	 * the camera position.
	 */
	private int lastTouchedX;
	private int lastTouchedY;

	/**
	 * The screen's width and height. This may not match that computed by
	 * libgdx's gdx.graphics.getWidth() / getHeight() on devices that make use
	 * of on-screen menu buttons.
	 */
	private int screenWidth;
	private int screenHeight;
	
	@Override
	public void create() {
		/**
		 * If the viewport's size is not yet known, determine it here.
		 */
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		tiledMapHelper = new SimpleTiledMapHelper();
		tiledMapHelper.setResourceDirectory("data/tiledmap/cave");
		tiledMapHelper.loadMap("data/tiledmap/cave/cave.tmx");
		tiledMapHelper.prepareCamera(screenWidth, screenHeight);

		lastRender = System.nanoTime();
	}

	@Override
	public void render() {
		long now = System.nanoTime();
		
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		if (Gdx.input.justTouched()) {
			lastTouchedX = Gdx.input.getX();
			lastTouchedY = Gdx.input.getY();
		} else if (Gdx.input.isTouched()) {
			tiledMapHelper.getCamera().position.x += lastTouchedX
					- Gdx.input.getX();

			/**
			 * Camera y is opposite of Gdx.input y, so the subtraction is
			 * swapped.
			 */
			tiledMapHelper.getCamera().position.y += Gdx.input.getY()
					- lastTouchedY;

			lastTouchedX = Gdx.input.getX();
			lastTouchedY = Gdx.input.getY();
		}

		/**
		 * Ensure that the camera is only showing the map, nothing outside.
		 */
		if (tiledMapHelper.getCamera().position.x < screenWidth / 2) {
			tiledMapHelper.getCamera().position.x = screenWidth / 2;
		}
		if (tiledMapHelper.getCamera().position.x >= tiledMapHelper.getWidth()
				- screenWidth / 2) {
			tiledMapHelper.getCamera().position.x = tiledMapHelper.getWidth()
					- screenWidth / 2;
		}

		if (tiledMapHelper.getCamera().position.y < screenHeight / 2) {
			tiledMapHelper.getCamera().position.y = screenHeight / 2;
		}
		if (tiledMapHelper.getCamera().position.y >= tiledMapHelper.getHeight()
				- screenHeight / 2) {
			tiledMapHelper.getCamera().position.y = tiledMapHelper.getHeight()
					- screenHeight / 2;
		}

		tiledMapHelper.getCamera().update();

		tiledMapHelper.render();

		now = System.nanoTime();
		if (now - lastRender < 30000000) { // 30 ms, ~33FPS
			try {
				Thread.sleep(30 - (now - lastRender) / 1000000);
			} catch (InterruptedException e) {
			}
		}

		lastRender = now;
	}

	@Override
	public void dispose() {
		tiledMapHelper.dispose();
	}
}
