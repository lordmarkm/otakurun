package com.baldwin.otakurun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.baldwin.libgdx.commons.BasePlatform;
import com.baldwin.libgdx.commons.SimpleTiledMapHelper;
import com.baldwin.libgdx.commons.entity.Entity;
import com.baldwin.otakurun.entity.Tokine;

/**
 * 3rd class written, meant to test physics and stuff.
 * Removed some repetitive javadoc
 * @author mbmartinez
 */
public class TestMovement extends BasePlatform {
	private long lastRender;

	private SimpleTiledMapHelper tiledMapHelper;

	/**
	 * The screen's width and height. This may not match that computed by
	 * libgdx's gdx.graphics.getWidth() / getHeight() on devices that make use
	 * of on-screen menu buttons.
	 */
	private int screenWidth;
	private int screenHeight;
	
	/**
	 * This is the main box2d "container" object. All bodies will be loaded in
	 * this object and will be simulated through calls to this object.
	 */
	private World world;
	
	/**
	 * This is the player character. It will be created as a dynamic object.
	 */
	private Entity tokine;
	
	/**
	 * Box2d works best with small values. If you use pixels directly you will
	 * get weird results -- speeds and accelerations not feeling quite right.
	 * Common practice is to use a constant to convert pixels to and from
	 * "meters".
	 */
	public static final float PIXELS_PER_METER = 60.0f;
	
	private Box2DDebugRenderer debugRenderer;
	
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

		/**
		 * You can set the world's gravity in its constructor. Here, the gravity
		 * is negative in the y direction (as in, pulling things down).
		 */
		world = new World(new Vector2(0.0f, -10.0f), true);
		
		tokine = new Tokine();
		tokine.initBody(world, PIXELS_PER_METER);

		tiledMapHelper.loadCollisions("data/tiledmap/cave/CaveBaseForeground-collisions.txt", world, PIXELS_PER_METER);

		debugRenderer = new Box2DDebugRenderer(true, true, true, true, true);

		lastRender = System.nanoTime();
	}

	@Override
	public void render() {
		long now = System.nanoTime();
		
		clearScreen();
		update();
		
		tiledMapHelper.getCamera().position.x = PIXELS_PER_METER * tokine.body.getPosition().x;
		handleScreenBoundaries();

		tiledMapHelper.getCamera().update();
		tiledMapHelper.render();

		batch.begin();
		
		tokine.render(batch);
		

		
		batch.end();
		
		/**
		 * Draw this last, so we can see the collision boundaries on top of the
		 * sprites and map.
		 */
		camera.update();
		debugRenderer.render(world, tiledMapHelper.getCamera().combined.scale(
				PIXELS_PER_METER,
				PIXELS_PER_METER,
				PIXELS_PER_METER));
		
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
	
	private void update() {
		
		tokine.update();
		
		if(tokine.body.getLinearVelocity().y == 0) {
			tokine.body.applyLinearImpulse(new Vector2(0f, 2f), tokine.body.getWorldCenter());
		}
		world.step(Gdx.graphics.getDeltaTime(), 3, 3);
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
