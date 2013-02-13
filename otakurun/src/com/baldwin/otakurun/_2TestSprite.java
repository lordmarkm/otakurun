package com.baldwin.otakurun;

import com.badlogic.gdx.Gdx;
import com.baldwin.libgdx.commons.BasePlatform;
import com.baldwin.libgdx.commons.SimpleTiledMapHelper;
import com.baldwin.libgdx.commons.entity.StatefulSprite;
import com.baldwin.otakurun.entity.TokineSprite;

/**
 * 2nd class written, after TestBackground
 * @author mbmartinez
 */
public class _2TestSprite extends BasePlatform {
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
	
	private StatefulSprite sprite;
	private float stateTime = 0f;
	
	@Override
	public void create() {
		super.create();
		
		/**
		 * If the viewport's size is not yet known, determine it here.
		 */
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		tiledMapHelper = new SimpleTiledMapHelper();
		tiledMapHelper.setResourceDirectory("data/tiledmap/cave");
		tiledMapHelper.loadMap("data/tiledmap/cave/cave.tmx");
		tiledMapHelper.prepareCamera(screenWidth, screenHeight);

		sprite = new TokineSprite();
		
		lastRender = System.nanoTime();
	}

	@Override
	public void render() {
		long now = System.nanoTime();
		
		clearScreen();
		handleScreenDragged();

		tiledMapHelper.getCamera().update();
		tiledMapHelper.render();

		stateTime += Gdx.graphics.getDeltaTime();
		batch.begin();
		batch.draw(sprite.getSequence().getKeyFrame(stateTime, true), 50, 50);
		batch.end();
		
		if (now - lastRender < 30000000) { // 30 ms, ~33FPS
			try {
				Thread.sleep(30 - (now - lastRender) / 1000000);
			} catch (InterruptedException e) {
			}
		}

		lastRender = now;
	}

	private void handleScreenDragged() {
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
	}
	
	@Override
	public void dispose() {
		tiledMapHelper.dispose();
	}
	
	@Override
	public void resize(int width, int height) {}
	
	@Override
	public void pause() {}
	
	@Override
	public void resume() {}
}
