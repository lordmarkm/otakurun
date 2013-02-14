package com.baldwin.libgdx.physics;

public class Constants {

	public static final float PIXELS_PER_METER = 60.0f;
	public static float button_width = 40f;
	public static float button_height = button_width;
	
	public static float toMeters(float pixels) {
		return pixels / PIXELS_PER_METER;
	}
}
