package com.baldwin.libgdx.commons.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.baldwin.libgdx.commons.entity.Renderable;

public class DisposableObjectPool {
	
	private List<Renderable> renderables = new ArrayList<Renderable>();
	
	public void render(SpriteBatch batch, Camera camera) {
		for(Iterator<Renderable> i = renderables.iterator(); i.hasNext();) {
			Renderable r = i.next();
			r.render(batch, camera);
		}
	}
	
	public void update() {
		System.out.println("Updating " + renderables.size() + " renderables");
		for(Iterator<Renderable> i = renderables.iterator(); i.hasNext();) {
			Renderable r = i.next();
			r.update();
			if(r.isDisposable()) {
				r.dispose();
				i.remove();
			}
		}
	}
	
	public void add(Renderable r) {
		renderables.add(r);
	}
	
}