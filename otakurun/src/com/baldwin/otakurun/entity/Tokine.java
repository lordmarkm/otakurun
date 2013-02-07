package com.baldwin.otakurun.entity;

import static com.baldwin.otakurun.entity.Command.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.baldwin.libgdx.commons.entity.Entity;
import com.baldwin.otakurun.util.Constants;

public class Tokine extends Entity {

	public TokineState state;
	public Command command;
	public boolean faceright = true;
	
	private float stateTime = 0f;
	
	public Tokine() {
		this.sprite = new TokineSprite();
		this.state = TokineState.ready;
	}
	
	@Override
	public void initBody(World world, float pixelsPerMeter) {
		BodyDef tokineBodyDef = new BodyDef();
		tokineBodyDef.type = BodyDef.BodyType.DynamicBody;
		tokineBodyDef.position.set(1f, 1f);
		body = world.createBody(tokineBodyDef);
		body.setFixedRotation(true);
		
		PolygonShape tokineShape = new PolygonShape();
		tokineShape.setAsBox(30f / (2*pixelsPerMeter), 50f / (2*pixelsPerMeter));
		
		FixtureDef tokineFixtureDef = new FixtureDef();
		tokineFixtureDef.shape = tokineShape;
		tokineFixtureDef.density = 1.0f;
		tokineFixtureDef.friction = 5.0f;
		
		body.createFixture(tokineFixtureDef);
		tokineShape.dispose();
	}

	@Override
	public void update() {
		handleInput();
		switch(state) {
		
			case ready:
				break;
			
			case accelerating:
				break;
			
			case run:
				break;
		
			case decelerating:
				break;
				
			case airborne:
				break;
		}
		
	}
	
	private void handleInput() {
		if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			command = jump;
		}
		
		else if(Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)) {
			command = move_right;
		} 
		
		else if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)) {
			command = move_left;
		}
		
		else {
			command = none;
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		stateTime += Gdx.graphics.getDeltaTime();
		TextureRegion tokineframe = sprite.getSequence().getKeyFrame(stateTime, true);
		float tokinex = Constants.PIXELS_PER_METER * body.getPosition().x - tokineframe.getRegionWidth() / 2;
		float tokiney = Constants.PIXELS_PER_METER * body.getPosition().y	- tokineframe.getRegionHeight() / 2;
		batch.draw(sprite.getSequence().getKeyFrame(stateTime, true), tokinex, tokiney);
	}
	
}
