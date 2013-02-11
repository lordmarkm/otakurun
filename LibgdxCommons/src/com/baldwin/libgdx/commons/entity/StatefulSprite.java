package com.baldwin.libgdx.commons.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.baldwin.libgdx.commons.util.SpriteMap;

/**
 * Helper class for handling entities that have multiple
 * @author mbmartinez
 */
public abstract class StatefulSprite {

	public Object state;
	protected Texture sheet;
	protected SpriteMap sequences = new SpriteMap();
	
	public Animation getSequence() {
		return sequences.get(state);
	}
	
}