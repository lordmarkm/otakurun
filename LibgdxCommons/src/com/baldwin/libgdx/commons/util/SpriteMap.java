package com.baldwin.libgdx.commons.util;
import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.baldwin.libgdx.commons.exception.SpriteSequenceNotFoundException;

/**
 * Simple hashmap for sprite sequences, throws exception if sequence is not found
 * @author mbmartinez
 *
 */
public class SpriteMap extends HashMap<Object, Animation> {
	private static final long serialVersionUID = 1L;

	@Override
	public Animation get(Object state) {
		Animation t = super.get(state);
		if(null == t) {
			throw new SpriteSequenceNotFoundException("Could not find animation for state " + state);
		}
		return t;
	}
	
}
