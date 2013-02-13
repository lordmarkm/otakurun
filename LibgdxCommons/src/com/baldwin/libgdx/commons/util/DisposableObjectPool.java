package com.baldwin.libgdx.commons.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.baldwin.libgdx.commons.entity.Renderable;

public class DisposableObjectPool {
	
	private static DisposableObjectPool pool = new DisposableObjectPool();
	private DisposableObjectPool() {
		//
	}
	public static DisposableObjectPool getInstance() {
		return pool;
	}
	
	private List<Renderable> renderables = new ArrayList<Renderable>();
	private Queue<Renderable> addQueue = new ConcurrentLinkedQueue<Renderable>();
	
	public void render(SpriteBatch batch, Camera camera) {
		for(Iterator<Renderable> i = renderables.iterator(); i.hasNext();) {
			Renderable r = i.next();
			r.render(batch, camera);
		}
	}
	
	public void update() {
		for(Iterator<Renderable> i = renderables.iterator(); i.hasNext();) {
			Renderable r = i.next();
			r.update();
			if(r.isDisposable()) {
				r.dispose();
				i.remove();
			}
		}
		
		while(addQueue.size() > 0) {
			renderables.add(addQueue.remove());
		}
	}
	
	public void add(Renderable r) {
		addQueue.add(r);
	}
	
}