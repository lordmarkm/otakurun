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
import com.baldwin.libgdx.physics.Constants;

public class KetsuMetsu implements Renderable {

	private static final float body_width = 20;
	private static final float body_height = 15;
	
	public KetsuMetsuType type;
	public boolean faceright;
	public Vector2 position;
	
	private World world;
	public Body body;
	public KetsuMetsuSprite sprite;

	private KetsuMetsuState state;
	
	private float stateTime = 0f;
	
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
		
		state = KetsuMetsuState.travel;
	}
	
	public enum KetsuMetsuType {ketsu, metsu}

	@Override
	public void render(SpriteBatch batch, Camera camera) {
		TextureRegion frame = sprite.getSequence().getKeyFrame(stateTime, true);
		
		/**
		 * CRITICALLY IMPORTANT - Project sprite's world coordinates to window coordinates
		 * TODO optimize this stupid code
		 */
		Vector3 pos = getPosition();
		camera.project(pos);
		batch.draw(frame, pos.x, pos.y);	
	}

	@Override
	public void update() {
		stateTime += Gdx.graphics.getDeltaTime();
		
		//if collide, do something
		
		sprite.state = this.state;
	}

	@Override
	public boolean isDisposable() {
		return isAnimationFinished();
	}

	@Override
	public void dispose() {
		world.destroyBody(body);
	}
	
	private boolean isAnimationFinished() {
		int frame = sprite.getSequence().getKeyFrameIndex(stateTime);
		switch(state) {
		case travel:
			return frame == 10;
		case explode:
			return frame == 5;
		default:
			throw new IllegalStateException("Unsupported state: " + state);
		}
	}
	
	private Vector3 getPosition() {
		Animation a = sprite.getSequence();
		TextureRegion frame = a.getKeyFrame(stateTime, true);
		float x = Constants.PIXELS_PER_METER * body.getPosition().x - frame.getRegionWidth() / 2;
		float y = Constants.PIXELS_PER_METER * body.getPosition().y	- frame.getRegionHeight() / 2;
		return new Vector3().set(x, y, 0);
	}
}
