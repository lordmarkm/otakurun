package com.baldwin.libgdx.physics;

public class Constants {

	public static final float PIXELS_PER_METER = 60.0f;
	
	public static float toMeters(float pixels) {
		return pixels / PIXELS_PER_METER;
	}
}
