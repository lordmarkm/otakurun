package com.baldwin.otakurun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.baldwin.libgdx.commons.BasePlatform;
import com.baldwin.libgdx.commons.CollidingTiledMapHelper;
import com.baldwin.libgdx.commons.entity.Entity;
import com.baldwin.libgdx.commons.util.DisposableObjectPool;
import com.baldwin.libgdx.physics.Constants;
import com.baldwin.otakurun.entity.Tokine;

/**
 * 5th test class written, meant to test projectile generation projectile collision with static objects
 * @author mbmartinez
 */
public class _5TestProjectiles extends BasePlatform {
	private long lastRender;

	private CollidingTiledMapHelper tiledMapHelper;
	private int screenWidth;
	private int screenHeight;
	
	private World world;
	private Entity tokine;
	
	@SuppressWarnings("unused")
	private Box2DDebugRenderer debugRenderer;
	
	private DisposableObjectPool pool;
	
	@Override
	public void create() {
		super.create();
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		tiledMapHelper = new CollidingTiledMapHelper();
		tiledMapHelper.setResourceDirectory("data/tiledmap/cave");
		tiledMapHelper.loadMap("data/tiledmap/cave/cave.tmx");

		camera = (OrthographicCamera) tiledMapHelper.prepareCamera(screenWidth, screenHeight);

		tokine = new Tokine();
		tokine.initBody(world);

		tiledMapHelper.loadCollisions(world);

		debugRenderer = new Box2DDebugRenderer(true, true, true, true, true);

		lastRender = System.nanoTime();
		
	}

	@Override
	public void render() {
		long now = System.nanoTime();
		update();
		clearScreen();
		
		camera.position.x = Constants.PIXELS_PER_METER * tokine.body.getPosition().x;
		handleScreenBoundaries();
		tiledMapHelper.getCamera().update();
		tiledMapHelper.render();
		batch.begin();
		tokine.render(batch, camera);
		pool.render(batch, camera);
		batch.end();
		
		/**
		 * Draw this last, so we can see the collision boundaries on top of the
		 * sprites and map.
		 */
		camera.update();
//		debugRenderer.render(world, tiledMapHelper.getCamera().combined.scale(
//				Constants.PIXELS_PER_METER,
//				Constants.PIXELS_PER_METER,
//				Constants.PIXELS_PER_METER));
		
		if (now - lastRender < 30000000) { // 30 ms, ~33FPS
			try {
				Thread.sleep(30 - (now - lastRender) / 1000000);
			} catch (InterruptedException e) {
			}
		}
		lastRender = now;
	
	}

	private void handleScreenBoundaries() {
		/**
		 * Ensure that the camera is only showing the map, nothing outside.
		 */
		if (tiledMapHelper.getCamera().position.x < screenWidth / 2) {
			tiledMapHelper.getCamera().position.x = screenWidth / 2;
		}
		
		if (tiledMapHelper.getCamera().position.x >= tiledMapHelper.getWidth() - screenWidth / 2) {
			tiledMapHelper.getCamera().position.x = tiledMapHelper.getWidth() - screenWidth / 2;
		}

		if (tiledMapHelper.getCamera().position.y < screenHeight / 2) {
			tiledMapHelper.getCamera().position.y = screenHeight / 2;
		}
		
		if (tiledMapHelper.getCamera().position.y >= tiledMapHelper.getHeight()	- screenHeight / 2) {
			tiledMapHelper.getCamera().position.y = tiledMapHelper.getHeight() - screenHeight / 2;
		}
	}
	
	private void update() {
		pool.update();
		tokine.update();
		world.step(Gdx.graphics.getDeltaTime(), 3, 3);
	}
	
	@Override
	public void dispose() {
		tiledMapHelper.dispose();
	}
}
