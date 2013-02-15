package com.baldwin.otakurun.entity;

import static com.baldwin.otakurun.entity.Command.*;
import static com.baldwin.otakurun.entity.TokineState.*;

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
import com.baldwin.libgdx.commons.util.DisposableObjectPool;
import com.baldwin.libgdx.physics.Constants;
import com.baldwin.otakurun.entity.KetsuMetsu.KetsuMetsuType;
import com.baldwin.otakurun.hud.HUD;

public class Tokine extends Entity {

	public final static float body_width = 30f;
	public final static float body_height = 50f;
	private final static float run_speed = 3f;
	private final static float max_vertical_speed = 6f;
	
	public TokineState state;
	public boolean faceright = true;
	
	boolean doubleJumped = false;
	boolean mayDoubleJump = false;
	
	private float stateTime = 0f;

	private DisposableObjectPool pool = DisposableObjectPool.getInstance();
	
	private int power = 4;
	
	final Vector2 jumpImpulse = new Vector2(0f, 10f);
	final Vector2 leftImpulse = new Vector2(-0.55f, 0f);
	final Vector2 rightImpulse = new Vector2(0.55f, 0f);
	
	//nerfed impulses while airborne
	final Vector2 dblJumpImpulse = new Vector2(0f, 1f);
//	final Vector2 airborne_leftImpulse = new Vector2(-0.55f, 0f);
//	final Vector2 airborne_rightImpulse = new Vector2(0.55f, 0f);
	
	public Tokine() {
		this.sprite = new TokineSprite();
		state(ready);
	}

	@Override
	public void initBody(World world) {
		BodyDef tokineBodyDef = new BodyDef();
		tokineBodyDef.type = BodyDef.BodyType.DynamicBody;
		tokineBodyDef.position.set(1f, 1f);
		body = world.createBody(tokineBodyDef);
		body.setFixedRotation(true);

		PolygonShape tokineShape = new PolygonShape();
		tokineShape.setAsBox(body_width / (2*Constants.PIXELS_PER_METER), body_height / (2*Constants.PIXELS_PER_METER));

		FixtureDef tokineFixtureDef = new FixtureDef();
		tokineFixtureDef.shape = tokineShape;
		tokineFixtureDef.density = 1.0f;
		tokineFixtureDef.friction = 5.0f;

		body.createFixture(tokineFixtureDef);
		tokineShape.dispose();
	}

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
					jump();
					state(jump_start);
					break;
				case move_left:
					run();
					state(accelerating);
					break;
				case move_right:
					run();
					state(accelerating);
					break;
				case fire:
					spawnKetsu(power);
					state(ketsu);
					break;
				case makebox:
					spawnMetsu();
					state(metsu);
					break;
				}
				break;
			case accelerating:
				switch(command) {
				case jump:
					jump();
					state(jump_start);
					break;
				case move_left:
					run();
					if(isAnimationFinished(sprite.getSequence())) {
						state(run);	
					}
					break;
				case move_right:
					run();
					if(isAnimationFinished(sprite.getSequence())) {
						state(run);	
					}
					break;
				case fire:
					spawnKetsu(power);
					state(ketsu);
					break;
				case makebox:
					spawnMetsu();
					state(metsu);
					break;
				}
				break;
			case run:
				switch(command) {
				case jump:
					jump();
					state(jump_start);
					break;
				case move_left:
					run();
					break;
				case move_right:
					run();
					break;	
				case fire:
					spawnKetsu(power);
					state(ketsu);
					break;
				case makebox:
					spawnMetsu();
					state(metsu);
					break;
				}
				break;
			case jump_start:
				switch(command) {
				case move_left:
					run();
					break;
				case move_right:
					run();
					break;
				case fire:
					spawnKetsu(power);
					state(ketsu_in_air);
					break;
				case makebox:
					spawnMetsu();
					state(metsu);
					break;
				}
				break;
			case airborne_rising:
				switch(command) {
				case move_left:
					airControl();
					break;
				case move_right:
					airControl();
					break;
				case fire:
					spawnKetsu(power);
					state(ketsu_in_air);
					break;
				case makebox:
					spawnMetsu();
					state(metsu);
					break;
				}
				break;
			case airborne_falling:
				switch(command) {
				case move_left:
					airControl();
					break;
				case move_right:
					airControl();
					break;
				case fire:
					spawnKetsu(power);
					state(ketsu_in_air);
					break;
				case makebox:
					spawnMetsu();
					state(metsu);
					break;
				}
				break;
			case landing:
				switch(command) {
				case jump:
					jump();
					state(jump_start);
					break;
				case move_left:
					run();
					state(accelerating);
					break;
				case move_right:
					run();
					state(accelerating);
					break;
				case fire:
					spawnKetsu(power);
					state(ketsu);
					break;
				case makebox:
					spawnMetsu();
					state(metsu);
					break;
				}
				break;
			case ketsu:
			case metsu:
				break;
			default:
				throw new RuntimeException("Unhandled state: " + state);
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
		case ready:
		case accelerating:
		case decelerating:
		case run:
			if(body.getLinearVelocity().y < -0.5) {
				state(airborne_falling);
			}
			break;
		case jump_start:
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
		case ketsu:
			if(isAnimationFinished(sprite.getSequence())) {
				state(ready);
			}
			break;
		case ketsu_in_air:
			if(isAnimationFinished(sprite.getSequence())) {
				if(body.getLinearVelocity().y > 0) {
					state(airborne_rising);
				} else if(body.getLinearVelocity().y < 0) {
					state(airborne_falling);
				} else {
					state(ready);
				}
			}
			break;
		case metsu:
			if(isAnimationFinished(sprite.getSequence())) {
				state(ready);
			}
			break;
		case metsu_in_air:
			if(isAnimationFinished(sprite.getSequence())) {
				if(body.getLinearVelocity().y > 0) {
					state(airborne_rising);
				} else if(body.getLinearVelocity().y < 0) {
					state(airborne_falling);
				} else {
					state(ready);
				}
			}
			break;
		}

		sprite.state = this.state;
		
		//throttle velocity?
		if(body.getLinearVelocity().x > run_speed) {
			body.setLinearVelocity(run_speed, body.getLinearVelocity().y);
		} else if(body.getLinearVelocity().x < -run_speed) {
			body.setLinearVelocity(-run_speed, body.getLinearVelocity().y);
		}
		
		if(body.getLinearVelocity().y > max_vertical_speed) {
			body.setLinearVelocity(body.getLinearVelocity().x, max_vertical_speed);
		} else if(body.getLinearVelocity().y < -max_vertical_speed) {
			body.setLinearVelocity(body.getLinearVelocity().x, -max_vertical_speed);
		}
	}
	
	private void impulse(Vector2 impulse) {
		body.applyLinearImpulse(impulse, body.getWorldCenter());
	}
	
	private void run() {
//		impulse(faceright ? rightImpulse : leftImpulse);
		body.setLinearVelocity(faceright ? run_speed : -run_speed, body.getLinearVelocity().y);
	}
	
	private void jump() {
		impulse(jumpImpulse);
	}
	
	private void airControl() {
		body.setLinearVelocity(faceright ? run_speed : -run_speed, body.getLinearVelocity().y);
	}
	
	private List<Command> handleInput() {

		List<Command> commands = new ArrayList<Command>();

		switch(state) {
		case run:
		case accelerating:
		case decelerating:
		case ready:
		case landing:
			//fire and makebox commands exclude all other commands
			if(Gdx.input.isKeyPressed(Input.Keys.SPACE) || HUD.ketsu.isPressed()) {
				commands.add(fire);
				return commands;
			} 
			
			if(Gdx.input.isKeyPressed(Input.Keys.Q) || HUD.metsu.isPressed()) {
				commands.add(makebox);
				return commands;
			}
			
			if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || HUD.jump.isPressed()) {
				commands.add(jump);
			}
			if(Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT) || HUD.right.isPressed()) {
				commands.add(move_right);
				faceright = true;
			} else if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT) || HUD.left.isPressed()) {
				commands.add(move_left);
				faceright = false;
			}
			break;
		case jump_start:
		case airborne_rising:
		case airborne_falling:
			//fire and makebox commands exclude all other commands
			if(Gdx.input.isKeyPressed(Input.Keys.SPACE) || HUD.ketsu.isPressed()) {
				commands.add(fire);
				return commands;
			}
			if(Gdx.input.isKeyPressed(Input.Keys.Q) || HUD.metsu.isPressed()) {
				commands.add(makebox);
				return commands;
			}
			
			if(Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT) || HUD.right.isPressed()) {
				commands.add(move_right);
				faceright = true;
			} else if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT) || HUD.left.isPressed()) {
				commands.add(move_left);
				faceright = false;
			}
			break;
		case ketsu:
		case ketsu_in_air:
		case metsu:
		case metsu_in_air:
			break;
		default:
			throw new RuntimeException("Unhandled state: " + state);
		}

		return commands;

	}

	private void state(TokineState state) {
		this.state = state;
		sprite.state = state;
		stateTime = 0;
	}
	
	/**
	 * Animation.isAnimationFinished() doesn't quite work the way I expected it to
	 */
	private boolean isAnimationFinished(Animation animation) {
		int lastFrame = 0;
		switch(state) {
		case accelerating:
			lastFrame = 1;
			break;
		case decelerating:
			lastFrame = 4;
			break;
		case jump_start:
			lastFrame = 2;
			break;
		case landing:
			lastFrame = 2;
			break;
		case ketsu:
			lastFrame = 5;
			break;
		case ketsu_in_air:
			lastFrame = 5;
			break;
		case metsu:
			lastFrame = 7;
			break;
		case metsu_in_air:
			lastFrame = 7;
			break;
		default: 
			throw new IllegalStateException("Unhandled state: " + state);
		}
		int noLoopFrame = (int) Math.floor(stateTime / sprite.getSequence().frameDuration);
		return noLoopFrame > lastFrame;
	}
	
	@Override
	public void render(SpriteBatch batch, Camera camera) {
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
	}
	
	public Vector3 getPosition() {
		Animation a = sprite.getSequence();
		TextureRegion tokineframe = a.getKeyFrame(stateTime, true);
		float tokinex = Constants.PIXELS_PER_METER * body.getPosition().x - tokineframe.getRegionWidth() / 2;
		float tokiney = Constants.PIXELS_PER_METER * body.getPosition().y	- tokineframe.getRegionHeight() / 2;
		return new Vector3().set(tokinex, tokiney, 0);
	}
	
	private void spawnKetsu(int power) {
		for(int i = 0; i < power; i++) {
			KetsuMetsu ketsu = new KetsuMetsu(body.getWorld(), this, KetsuMetsuType.ketsu);
			pool.add(ketsu);
		}
	}
	
	private void spawnMetsu() {
		KetsuMetsu metsu = new KetsuMetsu(body.getWorld(), this, KetsuMetsuType.metsu);
		pool.add(metsu);
	}
	
	@Override
	public boolean isDisposable() {return false;}
	@Override
	public void dispose() {}
	

}