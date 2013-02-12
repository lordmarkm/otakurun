package com.baldwin.libgdx.commons.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Helper class to hold display (sprite) and logic (body) parts of an on-screen entity
 * @author mbmartinez
 */
public abstract class Entity implements Renderable {

	public Body body;
	public StatefulSprite sprite;
	public abstract void initBody(World world, float pixelsPerMeter);
	public abstract void update();
	
}
