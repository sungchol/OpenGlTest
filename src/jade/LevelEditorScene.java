package jade;

import org.joml.Vector2f;
import org.joml.Vector4f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import imgui.ImGui;
import renderer.Texture;
import util.AssetPool;




public class LevelEditorScene extends Scene {

	GameObject obj1;
	GameObject obj2;
	GameObject obj3, obj4;
	Texture texture, texture1, texture2;
	Spritesheet spritesheet;
	SpriteRenderer obj1Sprite;
	
	public LevelEditorScene() {
		
	}

	
	@Override
	public void init() {	
		
		this.camera = new Camera(new Vector2f(0f, 0.0f));

		loadResource();
		
		texture = AssetPool.getTexture("asset/images/blendImage1.png");
		texture1 = AssetPool.getTexture("asset/images/testImage.png");
		texture2 = AssetPool.getTexture("asset/images/blendImage2.png");
		spritesheet = AssetPool.getSpritesheet("asset/images/spritesheet.png");
			
		obj1 = new GameObject("Object 1", 
							new Transform(new Vector2f(100,100), new Vector2f(256, 256)), 2);
		obj1Sprite = new SpriteRenderer();
		obj1Sprite.setColor(new Vector4f(1f, 0f, 0f, 1f));
		obj1.addComponent(obj1Sprite);
		this.addGameObjectToScene(obj1);
		this.activeGameObject = obj1;
		
		obj2 = new GameObject("Object 2", 
				new Transform(new Vector2f(400,100), new Vector2f(64, 64)), 0);
		SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
		Sprite obj2Sprite = new Sprite();
		obj2Sprite.setTexture(texture);
		//obj2SpriteRenderer.setSprite(obj2Sprite);
		obj2SpriteRenderer.setSprite(spritesheet.getSprite(0));
		obj2.addComponent(obj2SpriteRenderer);
		this.addGameObjectToScene(obj2);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		System.out.println(gson.toJson(obj1));
		
		String serialized = gson.toJson(new Vector2f(1, 0.5f));
		System.out.println(serialized);
		Vector2f vec = gson.fromJson(serialized, Vector2f.class);
		System.out.println(vec);
		
		serialized = gson.toJson(obj1);
		System.out.println(serialized);
		GameObject obj_serial = gson.fromJson(serialized, GameObject.class);
		System.out.println(obj_serial);
		
	}

	
	private void loadResource() {
		// TODO Auto-generated method stub
		
		AssetPool.getShader("asset/shaders/default.glsl");
		AssetPool.getTexture("asset/images/blendImage1.png");
		AssetPool.getTexture("asset/images/blendImage2.png");
		AssetPool.getTexture("asset/images/testImage2.png");
		
		Texture spriteSheetTexture = new Texture();
		spriteSheetTexture.init("asset/images/spritesheet.png");
		AssetPool.addSpritesheet("asset/images/spritesheet.png",
			new Spritesheet(spriteSheetTexture, 16, 16, 26, 0));
	}

	private int spriteIndex =0;
	private float spriteFliptime = 0.2f;
	private float spriteFliptimeLeft = 0.0f;
	
	@Override
	public void update(float dt) {
		
		
		
		spriteFliptimeLeft -= dt; 
		if(spriteFliptimeLeft<=0) {
			spriteFliptimeLeft = spriteFliptime;
			spriteIndex++;
			if(spriteIndex > 2) {
				spriteIndex = 0;
			}
			//obj3.getComponents(SpriteRenderer.class).setSprite(spritesheet.getSprite(spriteIndex+3));
			//obj1.getComponents(SpriteRenderer.class).setSprite(spritesheet.getSprite(spriteIndex+1));
			obj2.getComponents(SpriteRenderer.class).setSprite(spritesheet.getSprite(spriteIndex+1));
		}
		
		obj1.transform.position.x += dt * 40;
		obj2.transform.position.x += dt * 20;
		
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
		ImGui.text("some text");
		ImGui.end();
	}
	
	
}
