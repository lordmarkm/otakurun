package com.baldwin.otakurun.entity.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.baldwin.libgdx.commons.entity.Entity;
import com.baldwin.libgdx.physics.Constants;

public abstract class Monster extends Entity {

	public int hp;
	public int max_hp;
	
	protected World world;
	protected long stateTime;
	
	@Override
	public abstract void render(SpriteBatch batch, Camera camera);

	@Override
	public boolean isDisposable() {
		return hp <=0;
	}

	@Override
	public void dispose() {
		world.destroyBody(body);
	}

	@Override
	public abstract void initBody(World world);

	@Override
	public void update() {
		stateTime += Gdx.graphics.getDeltaTime();
	}
	
	public Vector3 getPosition() {
		Animation a = sprite.getSequence();
		TextureRegion tokineframe = a.getKeyFrame(stateTime, true);
		float tokinex = Constants.PIXELS_PER_METER * body.getPosition().x - tokineframe.getRegionWidth() / 2;
		float tokiney = Constants.PIXELS_PER_METER * body.getPosition().y	- tokineframe.getRegionHeight() / 2;
		return new Vector3().set(tokinex, tokiney, 0);
	}

}
