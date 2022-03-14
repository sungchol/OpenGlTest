package Scenes;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import components.GridLines;
import components.MouseControls;
import components.Rigidbody;
import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import imgui.ImGui;
import imgui.ImVec2;
import jade.Camera;
import jade.GameObject;
import jade.Prefab;
import jade.Transform;
import renderer.DebugDraw;
import renderer.Texture;
import util.AssetPool;
import util.Settings;




public class LevelEditorScene extends Scene {

	GameObject obj1;
	GameObject obj2;
	GameObject obj3, obj4;
	Texture texture, texture1, texture2;
	Spritesheet sprites;
	SpriteRenderer obj1Sprite;
	
	MouseControls mouseControls = new MouseControls();
	GameObject levelEditorStuff = new GameObject("LevelEditor",	new Transform(new Vector2f()), 0);
	
	public LevelEditorScene() {
		
	}

	
	@Override
	public void init() {	
		
		levelEditorStuff.addComponent(mouseControls);
		levelEditorStuff.addComponent(new GridLines());
		this.camera = new Camera(new Vector2f(-500.0f, 0.0f));
		
		loadResource();
		
		texture = AssetPool.getTexture("asset/images/blendImage1.png");
		sprites = AssetPool.getSpritesheet("asset/images/spritesheets/decorationsAndBlocks.png");
		
//		if(levelLoaded) {
//			if(gameObjects.size() > 0)
//			{
//				this.activeGameObject = gameObjects.get(0);				
//			}
//			return;
//		}
		
	}

	
	private void loadResource() {
		// TODO Auto-generated method stub
		
		AssetPool.getShader("asset/shaders/default.glsl");
		
		AssetPool.addSpritesheet("asset/images/spritesheets/decorationsAndBlocks.png", 
			new Spritesheet(AssetPool.getTexture("asset/images/spritesheets/decorationsAndBlocks.png"), 
					16,16,81,0));
		
		AssetPool.getTexture("asset/images/blendImage1.png");
		
		for(GameObject g: gameObjects) {
			if(g.getComponents(SpriteRenderer.class) != null) {
				SpriteRenderer spr = g.getComponents(SpriteRenderer.class);
				if(spr.getTexture() != null) {
					spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
				}
			}
		}
	}

	@Override
	public void update(float dt) {
		
		levelEditorStuff.update(dt);
				
		for (GameObject go : this.gameObjects)
		{
			go.update(dt);
		}	
		
		//this.renderer.render();
	}

	@Override
	public void render() {
		this.renderer.render();
	}
	
	@Override
	public void imgui()
	{
		ImGui.begin("New Window");
		
		ImVec2 windowPos = new ImVec2();
		ImGui.getWindowPos(windowPos);
		
		ImVec2 windowSize = new ImVec2();
		ImGui.getWindowSize(windowSize);
		
		ImVec2 itemSpacing = new ImVec2();
		ImGui.getStyle().getItemSpacing(itemSpacing);
		
		float windowX2 = windowPos.x + windowSize.x;
		
		for(int i=0; i < sprites.size(); i++) {
			Sprite sprite = sprites.getSprite(i);
			float spriteWidth = sprite.getWidth()*2;
			float spriteHeight = sprite.getHeight()*2;
			int id = sprite.getTexId();
			Vector2f[] texCoords = sprite.getTexCoords();
			
			ImGui.pushID(i);
			if(ImGui.imageButton(id, spriteWidth, spriteHeight, 
					texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
				
				System.out.printf("Button %d Clicked [LevelEditer 128]\n", i);
				//GameObject object = Prefab.generateSpriteObject(sprite, spriteWidth, spriteHeight);
				GameObject object = Prefab.generateSpriteObject(sprite, Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
				
				//Attach this to the mouse cursor
				levelEditorStuff.getComponents(MouseControls.class).pickupObject(object);
				
			}
			ImGui.popID();
			
			ImVec2 lastButtonPos = new ImVec2();
			ImGui.getItemRectMax(lastButtonPos);
			
			float lastButtonX2 = lastButtonPos.x;
			float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
			
			if(i + 1 <sprites.size() && nextButtonX2 < windowX2) {
				ImGui.sameLine();
			}
		}	
		
		
		
		ImGui.end();
	}
	
	
}


















