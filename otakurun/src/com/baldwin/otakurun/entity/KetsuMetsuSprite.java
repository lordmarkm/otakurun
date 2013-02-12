package com.baldwin.otakurun.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.baldwin.libgdx.commons.entity.StatefulSprite;

/**
 * From big circle to horizontal line:
 * 70,626 27x27
 * 108,626 27x27
 * 141,627 25x25
 * 172,630 19x19
 * 192,633 14x14
 * 218,637 8x8
 * 232,637 20x8
 * 257,638 26x6
 * 289,638 30x6
 * 325,638 28x6
 * @author mbmartinez
 *
 */
public class KetsuMetsuSprite extends StatefulSprite {

	static {
		Texture sheet = OtakurunTextureAtlas.kekkaishi;
		TextureRegion ket1 = new TextureRegion(sheet, 70, 626, 27, 27);
		TextureRegion ket2 = new TextureRegion(sheet, 108, 626, 27, 27);
		TextureRegion ket3 = new TextureRegion(sheet, 141, 627, 25, 25);
		TextureRegion ket4 = new TextureRegion(sheet, 172, 630, 19, 19);
		TextureRegion ket5 = new TextureRegion(sheet, 192, 633, 14, 14);
		TextureRegion ket6 = new TextureRegion(sheet, 218, 637, 8, 8);
		TextureRegion ket7 = new TextureRegion(sheet, 232, 637, 20, 8);
		TextureRegion ket8 = new TextureRegion(sheet, 257, 638, 26, 6);
		TextureRegion ket9 = new TextureRegion(sheet, 289, 638, 30, 6);
		TextureRegion ket10 = new TextureRegion(sheet, 325, 638, 28, 6);
		Animation ketsu = new Animation(0.065f, ket1, ket2, ket3, ket4, ket5, ket6, ket7, ket8, ket9, ket10, ket10);
		sequences.put(KetsuMetsuState.travel, ketsu);
	}
	
	public KetsuMetsuSprite() {
		super();
		this.state = KetsuMetsuState.travel;
	}
}
