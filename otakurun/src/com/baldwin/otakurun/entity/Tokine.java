package com.baldwin.otakurun.entity;

import static com.baldwin.otakurun.entity.Command.jump;
import static com.baldwin.otakurun.entity.Command.move_left;
import static com.baldwin.otakurun.entity.Command.move_right;
import static com.baldwin.otakurun.entity.TokineState.accelerating;
import static com.baldwin.otakurun.entity.TokineState.airborne_falling;
import static com.baldwin.otakurun.entity.TokineState.airborne_rising;
import static com.baldwin.otakurun.entity.TokineState.decelerating;
import static com.baldwin.otakurun.entity.TokineState.jump_start;
import static com.baldwin.otakurun.entity.TokineState.landing;
import static com.baldwin.otakurun.entity.TokineState.ready;
import static com.baldwin.otakurun.entity.TokineState.run;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.baldwin.libgdx.commons.entity.Entity;
import com.baldwin.otakurun.util.Constants;

public class Tokine extends Entity {

	final float sprite_width = 30f;
	final float sprite_height = 50f;
	
	public TokineState state;
	public boolean faceright = true;
	
	boolean doubleJumped = false;
	boolean mayDoubleJump = false;
	
	private float stateTime = 0f;

	public Tokine() {
		this.sprite = new TokineSprite();
		state(ready);
	}

	@Override
	public void initBody(World world, float pixelsPerMeter) {
		BodyDef tokineBodyDef = new BodyDef();
		tokineBodyDef.type = BodyDef.BodyType.DynamicBody;
		tokineBodyDef.position.set(1f, 1f);
		body = world.createBody(tokineBodyDef);
		body.setFixedRotation(true);

		PolygonShape tokineShape = new PolygonShape();
		tokineShape.setAsBox(sprite_width / (2*pixelsPerMeter), sprite_height / (2*pixelsPerMeter));

		FixtureDef tokineFixtureDef = new FixtureDef();
		tokineFixtureDef.shape = tokineShape;
		tokineFixtureDef.density = 1.0f;
		tokineFixtureDef.friction = 5.0f;

		body.createFixture(tokineFixtureDef);
		tokineShape.dispose();
	}

	final Vector2 jumpImpulse = new Vector2(0f, 2f);
	final Vector2 leftImpulse = new Vector2(-0.15f, 0f);
	final Vector2 rightImpulse = new Vector2(0.15f, 0f);
	
	//nerfed impulses while airborne
	final Vector2 dblJumpImpulse = new Vector2(0f, 1f);
	final Vector2 airborne_leftImpulse = new Vector2(-0.05f, 0f);
	final Vector2 airborne_rightImpulse = new Vector2(0.05f, 0f);
	
	@Override
	public void update() {
		stateTime += Gdx.graphics.getDeltaTime();
		
		/*
		 * Get commands, there may be more than one at the same time, for example if left and CTRL
		 * are both pressed
		 */
		List<Command> commands = handleInput();

		/*
		 * Now, handle each command depending on the current state
		 */
		for(Command command : commands) {
			switch(state) {
			case decelerating:
			case ready:
				switch(command) {
				case jump:
					state(jump_start);
					break;
				case move_left:
					state(accelerating);
					break;
				case move_right:
					state(accelerating);
					break;
				}
				break;
			case accelerating:
				switch(command) {
				case jump:
					state(jump_start);
					break;
				case move_left:
					body.applyLinearImpulse(leftImpulse, body.getWorldCenter());
					if(isAnimationFinished(sprite.getSequence())) {
						state(run);	
					}
					break;
				case move_right:
					body.applyLinearImpulse(rightImpulse, body.getWorldCenter());
					if(isAnimationFinished(sprite.getSequence())) {
						state(run);	
					}
					break;	
				}
				break;
			case run:
				switch(command) {
				case jump:
					state(jump_start);
					break;
				case move_left:
					body.applyLinearImpulse(leftImpulse, body.getWorldCenter());
					break;
				case move_right:
					body.applyLinearImpulse(rightImpulse, body.getWorldCenter());
					break;	
				case none:
				}
				break;
			case jump_start:
				switch(command) {
				case move_left:
					body.applyLinearImpulse(leftImpulse, body.getWorldCenter());
					break;
				case move_right:
					body.applyLinearImpulse(rightImpulse, body.getWorldCenter());
					break;
				}
				break;
			case airborne_rising:
				switch(command) {
				case move_left:
					body.applyLinearImpulse(airborne_leftImpulse, body.getWorldCenter());
					break;
				case move_right:
					body.applyLinearImpulse(airborne_rightImpulse, body.getWorldCenter());
					break;
				}
				break;
			case airborne_falling:
				switch(command) {
				case move_left:
					body.applyLinearImpulse(airborne_leftImpulse, body.getWorldCenter());
					break;
				case move_right:
					body.applyLinearImpulse(airborne_rightImpulse, body.getWorldCenter());
					break;
				}
				break;
			case landing:
				switch(command) {
				case jump:
					body.applyLinearImpulse(jumpImpulse, body.getWorldCenter());
					state(jump_start);
					break;
				case move_left:
					body.applyLinearImpulse(leftImpulse, body.getWorldCenter());
					state(accelerating);
					break;
				case move_right:
					body.applyLinearImpulse(rightImpulse, body.getWorldCenter());
					state(accelerating);
					break;	
				}
				break;
			}
		}
		
		/*
		 *If no command is issued and the "decelerating" sprite sequence has run its course,
		 *change sprite state back to ready 
		 */
		if(commands.isEmpty()) {
			switch(state) {
			case run:
				state(decelerating);
				break;
			case accelerating:
				if(isAnimationFinished(sprite.getSequence())) {
					state(decelerating);
				}
				break;
			case decelerating:
				if(isAnimationFinished(sprite.getSequence())) {
					state(ready);
				}
				break;
			case landing:
				if(isAnimationFinished(sprite.getSequence())) {
					state(ready);
				}
			}
		}
		
		/*
		 * handle things that happen once regardless of presence or absence of command
		 */
		switch(state) {
		case jump_start:
			body.applyLinearImpulse(jumpImpulse, body.getWorldCenter());
			if(isAnimationFinished(sprite.getSequence())) {
				state(airborne_rising);
			}
			break;
		case airborne_rising:
			if(body.getLinearVelocity().y <= 0) {
				state(airborne_falling);
			}
			break;
		case airborne_falling:
			if(body.getLinearVelocity().y == 0) {
				doubleJumped = false;
				state(landing);
			}
			break;
		}

		sprite.state = this.state;
		
		//throttle velocity?
		if(body.getLinearVelocity().x > 4) {
			body.setLinearVelocity(4, body.getLinearVelocity().y);
		} else if(body.getLinearVelocity().x < -4) {
			body.setLinearVelocity(-4, body.getLinearVelocity().y);
		}
		if(body.getLinearVelocity().y > 4) {
			body.setLinearVelocity(body.getLinearVelocity().x, 4);
		} else if(body.getLinearVelocity().y < -4) {
			body.setLinearVelocity(body.getLinearVelocity().x, -4);
		}
	}
	
	private List<Command> handleInput() {

		List<Command> commands = new ArrayList<Command>();

		switch(state) {
		case run:
		case accelerating:
		case decelerating:
		case ready:
		case landing:
			if(Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isTouched()) commands.add(jump);
			if(Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)) {
				commands.add(move_right);
				faceright = true;
			} else if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)) {
				commands.add(move_left);
				faceright = false;
			}
			break;
		case jump_start:
		case airborne_rising:
		case airborne_falling:
			if(Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)) {
				commands.add(move_right);
				faceright = true;
			} else if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)) {
				commands.add(move_left);
				faceright = false;
			}
			break;
		default:
			throw new RuntimeException("Unhandled state: " + state);
		}

		return commands;

	}

	private void state(TokineState state) {
		System.out.println("State change: " + this.state + "->" + state);
		this.state = state;
		stateTime = 0;
	}
	
	/**
	 * Animation.isAnimationFinished() doesn't quite work the way I expected it to
	 */
	private boolean isAnimationFinished(Animation animation) {
//		return animation.isAnimationFinished(stateTime);
		int index = animation.getKeyFrameIndex(stateTime);
		switch(state) {
		case accelerating:
			if(index >= 1) return true; 
			break;
		case decelerating:
			if(index >= 4) return true;
			break;
		case jump_start:
			if(index >= 2) return true;
			break;
		case landing:
			if(index >= 2) return true;
			break;
		default: 
			throw new IllegalStateException("Unhandled state: " + state);
		}
		return false;
	}
	
	@Override
	public TextureRegion render(SpriteBatch batch, Camera camera) {
		TextureRegion frame = sprite.getSequence().getKeyFrame(stateTime, true);
		if((faceright && frame.isFlipX()) || (!faceright && !frame.isFlipX())) {
			frame.flip(true, false);
		}
		
		/**
		 * CRITICALLY IMPORTANT - Project sprite's world coordinates to window coordinates
		 * TODO optimize this stupid code
		 */
		Vector3 pos = getPosition();
		camera.project(pos);
		batch.draw(frame, pos.x, pos.y);
		
		return frame;
	}
	
	public Vector3 getPosition() {
		Animation a = sprite.getSequence();
		TextureRegion tokineframe = a.getKeyFrame(stateTime, true);
		float tokinex = Constants.PIXELS_PER_METER * body.getPosition().x - tokineframe.getRegionWidth() / 2;
		float tokiney = Constants.PIXELS_PER_METER * body.getPosition().y	- tokineframe.getRegionHeight() / 2;
		return new Vector3().set(tokinex, tokiney, 0);
	}
}
