package renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import components.SpriteRenderer;
import jade.GameObject;

public class Renderer {
	private final int MAX_BATCH_SIZE = 10000;
	private List<RenderBatch> batches;
	
	public Renderer() {
		this.batches = new ArrayList<>();
	}
	
	public void add(GameObject go) {
		SpriteRenderer spr = go.getComponents(SpriteRenderer.class);
		if(spr != null) {
			add(spr);
		}
	}
	
	private void add(SpriteRenderer spr) {
		boolean added = false;
		
		for(RenderBatch batch : batches)
		{
			if (batch.hasRoom() && (batch.zIndex() == spr.gameObject.zIndex())) {
			
				Texture tex = spr.getTexture();
				if(tex == null || (batch.hasTexture(tex) || batch.hasTextureRoom())) 
				{					
					batch.addSprite(spr);
					added = true;
					break;
				}
			}
		}
		
		if(!added) {
			System.out.println("New Batch");
			RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, spr.gameObject.zIndex());
			newBatch.start();
			batches.add(newBatch);
			newBatch.addSprite(spr);
			Collections.sort(batches);
		}
		
	}
	
	public void render() {
		for (RenderBatch batch : batches) {
			batch.render();
		}
	}
}
