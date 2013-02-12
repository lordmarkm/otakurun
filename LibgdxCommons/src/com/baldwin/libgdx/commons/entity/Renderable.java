package com.baldwin.libgdx.commons.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Renderable {
	void render(SpriteBatch batch, Camera camera);
	void update();
	boolean isDisposable();
	void dispose();
}
