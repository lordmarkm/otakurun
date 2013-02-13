package com.baldwin.libgdx.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ShapeRenderator {
	private static ShapeRenderer renderer;
	private static Color kekkai_color = new Color(1f,1f,1f,1f);
	
	public static ShapeRenderer get() {
		if(null == renderer) {
			renderer = new ShapeRenderer();
			renderer.setColor(kekkai_color);
		}
		return renderer;
	}
}
