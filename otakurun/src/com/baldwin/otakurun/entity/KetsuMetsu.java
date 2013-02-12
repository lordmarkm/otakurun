package com.baldwin.otakurun.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.baldwin.libgdx.commons.entity.Renderable;
import com.baldwin.libgdx.physics.Constants;

public class KetsuMetsu implements Renderable {

	private static final float sprite_width = 20;
	private static final float sprite_height = 15;
	
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
		bodyDef.position.set(tokine.body.getPosition());
		body = world.createBody(bodyDef);
		body.setFixedRotation(true);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(sprite_width / (2*Constants.PIXELS_PER_METER), sprite_height / (2*Constants.PIXELS_PER_METER));

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0f;
		fixtureDef.friction = 0f;

		body.createFixture(fixtureDef);
		shape.dispose();

		float xImpulse = faceright ? 2f : -2f;
		body.applyLinearImpulse(new Vector2(xImpulse, 0f), body.getWorldCenter());
	}
	
	public enum KetsuMetsuType {ketsu, metsu}

	@Override
	public void render(SpriteBatch batch, Camera camera) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		stateTime += Gdx.graphics.getDeltaTime();
		
		//collide, do something
		
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
}
