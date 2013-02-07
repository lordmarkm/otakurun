package com.baldwin.otakurun.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.baldwin.libgdx.commons.entity.StatefulSprite;

/**
 * Ready state:
 * 60,57 32x49
 * 92,57 32x49
 * 124,57 32x49
 * 156,57 32x49
 * 188,57 32x49
 * 
 * Accelerating (pre-run)
 * 57,145 36x46
 * 
 * Run
 * 95,140 38x49
 * 133,140 46x49
 * 183,140 46x51
 * 232,140 39x51
 * 277,140 39x51
 * 316,140 39x51
 * 355,140 40x51
 * 396,140 40x51
 * 440,140 40x51
 * 482,140 40x51
 * 
 * Decelerating (post-run)
 * 527,140 39x51
 * 566,140 29x51
 * 601,140 26x51
 * 630,140 27x51
 * 
 * @author mbmartinez
 * 
 * Handle only animations here! Movement, acceleration, collisions, etc should
 * all be offloaded to some box2d physics handler
 */

public class TokineSprite extends StatefulSprite {

	final String S_READY = "ready";
	final String S_ACCEL = "accel";
	final String S_RUN = "run";
	
	public TokineSprite() {
		super();
		sheet = new Texture(Gdx.files.internal("data/sprites/tokine.png"));

		TextureRegion readyRegion = new TextureRegion(sheet, 60, 57, 160, 49);
		TextureRegion[] readySequence = readyRegion.split(32, 49)[0];
		Animation readyAnimation = new Animation(0.15f, readySequence);
		sequences.put(S_READY, readyAnimation);

		TextureRegion accelerating = new TextureRegion(sheet, 57, 145, 36, 46);
		Animation acceleratingAnimation = new Animation(0.15f, accelerating);
		sequences.put(S_ACCEL, acceleratingAnimation);

		TextureRegion run1 = new TextureRegion(sheet, 95, 140, 38, 49);
		TextureRegion run2 = new TextureRegion(sheet, 133, 140, 46, 49);
		TextureRegion run3 = new TextureRegion(sheet, 183, 140, 46, 51);
		TextureRegion run4 = new TextureRegion(sheet, 232, 140, 39, 51);
		TextureRegion run5 = new TextureRegion(sheet, 277, 140, 39, 51);
		TextureRegion run6 = new TextureRegion(sheet, 316, 140, 39, 51);
		TextureRegion run7 = new TextureRegion(sheet, 355, 140, 40, 51);
		TextureRegion run8 = new TextureRegion(sheet, 396, 140, 40, 51);
		TextureRegion run9 = new TextureRegion(sheet, 440, 140, 40, 51);
		TextureRegion run10 = new TextureRegion(sheet, 482, 140, 40, 51);
		Animation run = new Animation(0.065f, new TextureRegion[]{run1,run2,run3,run4,run5,run6,run7,run8,run9,run10});
		sequences.put(S_RUN, run);
		
		this.state = S_RUN;
	}

}