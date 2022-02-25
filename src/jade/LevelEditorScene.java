package jade;

import org.joml.Vector2f;
import org.joml.Vector4f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import components.Rigidbody;
import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import imgui.ImGui;
import imgui.ImVec2;
import renderer.Texture;
import util.AssetPool;




public class LevelEditorScene extends Scene {

	GameObject obj1;
	GameObject obj2;
	GameObject obj3, obj4;
	Texture texture, texture1, texture2;
	Spritesheet sprites;
	SpriteRenderer obj1Sprite;
	
	public LevelEditorScene() {
		
	}

	
	@Override
	public void init() {	
		
		this.camera = new Camera(new Vector2f(-500f, 0.0f));
		
		loadResource();
		
		texture = AssetPool.getTexture("asset/images/blendImage1.png");
		sprites = AssetPool.getSpritesheet("asset/images/spritesheets/decorationsAndBlocks.png");
			
		if(levelLoaded) {
			this.activeGameObject = gameObjects.get(0);
			return;
		}
		
		
			
		obj1 = new GameObject("Object 1", 
							new Transform(new Vector2f(100,100), new Vector2f(256, 256)), 2);
		obj1Sprite = new SpriteRenderer();
		obj1Sprite.setColor(new Vector4f(1f, 0f, 0f, 0.9f));
		obj1.addComponent(obj1Sprite);
		obj1.addComponent(new Rigidbody());
		this.addGameObjectToScene(obj1);
		this.activeGameObject = obj1;
		
		obj2 = new GameObject("Object 2", 
				new Transform(new Vector2f(400,100), new Vector2f(128, 128)), 0);
		SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
		Sprite obj2Sprite = new Sprite();
		obj2Sprite.setTexture(texture);
		obj2SpriteRenderer.setSprite(sprites.getSprite(0));
		obj2.addComponent(obj2SpriteRenderer);
		this.addGameObjectToScene(obj2);
				
	}

	
	private void loadResource() {
		// TODO Auto-generated method stub
		
		AssetPool.getShader("asset/shaders/default.glsl");
		
		AssetPool.addSpritesheet("asset/images/spritesheets/decorationsAndBlocks.png", 
			new Spritesheet(AssetPool.getTexture("asset/images/spritesheets/decorationsAndBlocks.png"), 
					16,16,81,0));
		
		AssetPool.getTexture("asset/images/blendImage1.png");
		
	}

	@Override
	public void update(float dt) {
		
		
		//System.out.printf("mouse pos %f %f\n", MouseListener.getOrthoX(), MouseListener.getOrthoY());
		//System.out.println("FPS : " + (1.0f/dt));
		for (GameObject go : this.gameObjects)
		{
			go.update(dt);
		}	
		
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
			float spriteWidth = sprite.getWidth() * 4;
			float spriteHeight = sprite.getHeight() * 4;
			int id = sprite.getTexId();
			Vector2f[] texCoords = sprite.getTexCoords();
			
			ImGui.pushID(i);
			if(ImGui.imageButton(id, spriteWidth, spriteHeight, 
					texCoords[0].x, texCoords[0].y, texCoords[2].x, texCoords[2].y)) {
				System.out.printf("Button %d Clicked\n", i);
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


















