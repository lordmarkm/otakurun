package com.baldwin.otakurun.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.baldwin.libgdx.commons.BasePlatform;
import com.baldwin.libgdx.commons.CollidingTiledMapHelper;
import com.baldwin.libgdx.physics.Constants;
import com.baldwin.otakurun.entity.OtakurunTextureAtlas;
import com.baldwin.otakurun.entity.Tokine;
import com.baldwin.otakurun.hud.HUD;

public class Otakurun extends BasePlatform {
	protected CollidingTiledMapHelper tiledMapHelper;
	protected int screenWidth;
	protected int screenHeight;
	
	protected Tokine tokine;
	protected Stage stage;

	protected long lastRender;
	protected Box2DDebugRenderer debugRenderer;
	
	@Override
	public void create() {
		super.create();
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		tiledMapHelper = new CollidingTiledMapHelper();
		tiledMapHelper.setResourceDirectory("data/tiledmap/cave");
		tiledMapHelper.loadMap("data/tiledmap/cave/cave.tmx");
		tiledMapHelper.loadCollisions(world);
		camera = (OrthographicCamera) tiledMapHelper.prepareCamera(screenWidth, screenHeight);
		
		tokine = new Tokine();
		tokine.initBody(world);
		
		initHUD();
		
		debugRenderer = new Box2DDebugRenderer(true, true, true, true, true);

		lastRender = System.nanoTime();
	}
	
	protected void initHUD() {
		float btnOffsetFromBottom = 10f;
		
		stage = new Stage(screenWidth, screenHeight, true, batch);
		Gdx.input.setInputProcessor(stage);
		
		Group _hud = new Group();
		
		Texture _buttons = OtakurunTextureAtlas.hud_buttons;
		TextureRegion[] buttons = new TextureRegion(_buttons).split(64, 64)[0];
		
		//make the buttons?
		HUD.left = new Button(new TextureRegionDrawable(buttons[0]));
		HUD.left.setPosition(10f, btnOffsetFromBottom);
		HUD.left.setSize(Constants.button_width, Constants.button_height);
		
		HUD.right = new Button(new TextureRegionDrawable(buttons[1]));
		HUD.right.setPosition(60f, btnOffsetFromBottom);
		HUD.right.setSize(Constants.button_width, Constants.button_height);
		
		HUD.jump = new Button(new TextureRegionDrawable(buttons[2]));
		HUD.jump.setPosition(stage.getWidth() - 50f, btnOffsetFromBottom);
		HUD.jump.setSize(Constants.button_width, Constants.button_height);
		
		HUD.metsu = new Button(new TextureRegionDrawable(buttons[3]));
		HUD.metsu.setPosition(stage.getWidth() - 100f, btnOffsetFromBottom);
		HUD.metsu.setSize(Constants.button_width, Constants.button_height);
		
		HUD.ketsu = new Button(new TextureRegionDrawable(buttons[4]));
		HUD.ketsu.setPosition(stage.getWidth() - 150f, btnOffsetFromBottom);
		HUD.ketsu.setSize(Constants.button_width, Constants.button_height);
		
		_hud.addActor(HUD.left);
		_hud.addActor(HUD.right);
		_hud.addActor(HUD.jump);
		_hud.addActor(HUD.metsu);
		_hud.addActor(HUD.ketsu);
		_hud.setTouchable(Touchable.enabled);
		
		stage.addActor(_hud);
	}
	
	@Override
	public void render() {
		long now = System.nanoTime();
		update();
		clearScreen();
		
		camera.position.x = Constants.PIXELS_PER_METER * tokine.body.getPosition().x;
		camera.position.y = Constants.PIXELS_PER_METER * tokine.body.getPosition().y;
		handleScreenBoundaries();
		tiledMapHelper.getCamera().update();
		tiledMapHelper.render();
		batch.begin();
		tokine.render(batch, camera);
		pool.render(batch, camera);
		batch.end();
		
		stage.draw();
		
		/**
		 * Draw this last, so we can see the collision boundaries on top of the
		 * sprites and map.
		 */
		camera.update();
		debugRenderer.render(world, tiledMapHelper.getCamera().combined.scale(
				Constants.PIXELS_PER_METER,
				Constants.PIXELS_PER_METER,
				Constants.PIXELS_PER_METER));
		
		if (now - lastRender < 30000000) { // 30 ms, ~33FPS
			try {
				Thread.sleep(30 - (now - lastRender) / 1000000);
			} catch (InterruptedException e) {
			}
		}
		lastRender = now;
	
	}
	
	protected void update() {
		pool.update();
		tokine.update();
		stage.act();
		world.step(Gdx.graphics.getDeltaTime(), 3, 3);
	}
	
	protected void handleScreenBoundaries() {
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
	
	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, true);
	}
	
	@Override
	public void dispose() {
		tiledMapHelper.dispose();
		stage.dispose();
	}
}
