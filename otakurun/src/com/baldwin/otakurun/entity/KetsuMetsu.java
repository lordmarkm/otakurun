package com.baldwin.otakurun.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.baldwin.libgdx.commons.entity.Renderable;
import com.baldwin.libgdx.commons.util.DisposableObjectPool;
import com.baldwin.libgdx.physics.Constants;

public class KetsuMetsu implements Renderable {

	private static final float body_width = 20;
	private static final float body_height = 15;
	
	public KetsuMetsuType type;
	public boolean faceright;
	public Vector2 position;

	private DisposableObjectPool pool = DisposableObjectPool.getInstance();
	private World world;
	public Body body;
	public KetsuMetsuSprite sprite;

	private KetsuMetsuState state;
	
	private volatile float stateTime = 0f;
	
	public KetsuMetsu(World world, Tokine tokine, KetsuMetsuType type) {
		this.world = world;
		this.type = type;
		this.position = new Vector2().set(tokine.body.getPosition());
		this.faceright = tokine.faceright;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		
		if(tokine.faceright) {
			float x = tokine.body.getPosition().x + Constants.toMeters(Tokine.body_width / 2) + Constants.toMeters(body_width / 2);
			bodyDef.position.set(x, tokine.body.getPosition().y);
		} else {
			float x = tokine.body.getPosition().x - Constants.toMeters(Tokine.body_width / 2) - Constants.toMeters(body_width / 2);
			bodyDef.position.set(x, tokine.body.getPosition().y);
		}
		body = world.createBody(bodyDef);
		body.setFixedRotation(true);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(body_width / (2*Constants.PIXELS_PER_METER), body_height / (2*Constants.PIXELS_PER_METER));

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0f;
		fixtureDef.friction = 0f;

		body.createFixture(fixtureDef);
		body.setGravityScale(0f); //ignore the effects of gravity
		shape.dispose();

		float xImpulse = faceright ? 4f : -4f;
		body.applyLinearImpulse(new Vector2(xImpulse, 0f), body.getWorldCenter());
		
		sprite = new KetsuMetsuSprite();
		
		switch(type) {
		case ketsu:
			state = KetsuMetsuState.ketsu_travel;
			break;
		case metsu:
			state = KetsuMetsuState.metsu_travel;
			break;
		}
		
	}
	
	public enum KetsuMetsuType {ketsu, metsu}

	@Override
	public void render(SpriteBatch batch, Camera camera) {
		TextureRegion frame = sprite.getSequence().getKeyFrame(stateTime, false);
		
		/**
		 * CRITICALLY IMPORTANT - Project sprite's world coordinates to window coordinates
		 * TODO optimize this stupid code
		 */
		Vector3 pos = getPosition();
		camera.project(pos);
		batch.draw(frame, pos.x, pos.y);	
	}

	private boolean eventHandled = false; //handle events only once
	@Override
	public void update() {
		stateTime += Gdx.graphics.getDeltaTime();
		
		//handle events
		if(!eventHandled) {
			//if collide, do something
			
			//if metsu expires, make cube
			if(type == KetsuMetsuType.metsu && isAnimationFinished()) {
				System.out.println("Spawning metsu block.");
				MetsuBlock block = new MetsuBlock(world, this);
				pool.add(block);
				eventHandled = true;

			} 
			
			
		}
		
		sprite.state = this.state;
	}

	@Override
	public boolean isDisposable() {
		return isAnimationFinished();
	}

	@Override
	public void dispose() {
		sprite.dispose();
		world.destroyBody(body);
	}
	
	private boolean isAnimationFinished() {
		int lastFrame = 0;
		switch(state) {
		case ketsu_travel:
			lastFrame = 10;
			break;
		case metsu_travel: //is just reversed version of ^, but apparently libgdx still indexes 1->x even with playmode = reversed 
			lastFrame = 10;
			break;
		case explode:
			lastFrame = 5;
			break;
		default:
			throw new IllegalStateException("Unsupported state: " + state);
		}
		int noLoopFrame = (int) Math.floor(stateTime / sprite.getSequence().frameDuration);
		return noLoopFrame > lastFrame;
	}
	
	private Vector3 getPosition() {
		Animation a = sprite.getSequence();
		TextureRegion frame = a.getKeyFrame(stateTime, true);
		float x = Constants.PIXELS_PER_METER * body.getPosition().x - frame.getRegionWidth() / 2;
		float y = Constants.PIXELS_PER_METER * body.getPosition().y	- frame.getRegionHeight() / 2;
		return new Vector3().set(x, y, 0);
	}
}
