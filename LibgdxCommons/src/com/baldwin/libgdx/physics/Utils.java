package com.baldwin.libgdx.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Utils {

	public static float toMeters(int pixels) {
		return pixels / (Constants.PIXELS_PER_METER * 2);
	}
	
	public static int toPixels(float meters) {
		return new Float(meters * Constants.PIXELS_PER_METER).intValue();
	}
	
	/**
	 * @param world
	 * @param position
	 * @param width - in meters
	 * @param height - in meters
	 * @return
	 */
	public static Body createWall(World world, Vector2 position, float width, float height) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(position);
		Body body = world.createBody(bodyDef);
		body.setFixedRotation(true);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width, height);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1.0f;
		body.createFixture(fixtureDef);
		shape.dispose();
		
		return body;
	}
}
