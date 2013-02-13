package com.baldwin.otakurun.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.baldwin.libgdx.commons.entity.Renderable;
import com.baldwin.libgdx.physics.Utils;

public class MetsuBlock implements Renderable {

	public static final int body_width = 40;
	public static final int body_height = 40;
	long duration = 3000; //metsu blocks last for 3 seconds
	long created = System.currentTimeMillis();
	protected World world;
	public Body body;
	
	private static Sprite sprite = new Sprite(new TextureRegion(OtakurunTextureAtlas.kekkai, 0, 0, 70, 70));
	static {
		sprite.setScale(0.7f);
	}
	
	public MetsuBlock(World world, KetsuMetsu metsu) {
		this.world = world;
		Vector2 pos = metsu.body.getPosition();
		body = Utils.createWall(world, pos, Utils.toMeters(body_width), Utils.toMeters(body_height));
	}
	
	@Override
	public void render(SpriteBatch batch, Camera camera) {
		Vector3 pos = new Vector3(Utils.toPixels(body.getPosition().x) - body_width/2, Utils.toPixels(body.getPosition().y) - body_height/2, 0f);
		camera.project(pos);
		sprite.setPosition(pos.x - body_width/4, pos.y - body_height/4);
		
		
		long life = duration - (System.currentTimeMillis() - created);
		
		float alpha = (float)life/(float)duration;
		if(alpha < 0) alpha = 0f;
		sprite.draw(batch, alpha);
	}

	@Override
	public void update() {
		//
	}

	@Override
	public boolean isDisposable() {
		return System.currentTimeMillis() - created > duration;
	}

	@Override
	public void dispose() {
		world.destroyBody(body);
	}

}
