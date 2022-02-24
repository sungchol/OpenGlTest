package jade;

import org.joml.Vector2f;

import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import renderer.Texture;
import util.AssetPool;




public class LevelEditorScene extends Scene {

	GameObject obj1;
	GameObject obj2;
	GameObject obj3, obj4;
	Texture texture, texture1, texture2;
	Spritesheet spritesheet;

	
	public LevelEditorScene() {
		
	}

	
	@Override
	public void init() {	
		
		this.camera = new Camera(new Vector2f(0f, 0.0f));

		loadResource();
		
		texture = AssetPool.getTexture("asset/images/blendImage1.png");
		texture1 = AssetPool.getTexture("asset/images/testImage.png");
		texture2 = AssetPool.getTexture("asset/images/blendImage2.png");
		
		obj4 = new GameObject("Object 4", 
				new Transform(new Vector2f(600, 100), new Vector2f(128,128)), 1);
		//obj4.addComponent(new SpriteRenderer(spritesheet.getSprite(2)));
		obj4.addComponent(new SpriteRenderer(new Sprite(texture1)));
		this.addGameObjectToScene(obj4);
		
		obj1 = new GameObject("Object 1", 
							new Transform(new Vector2f(100,100), new Vector2f(256, 256)), 2);
		obj1.addComponent(new SpriteRenderer(new Sprite(texture2)));
		this.addGameObjectToScene(obj1);
		
		obj2 = new GameObject("Object 2", 
				new Transform(new Vector2f(400,100), new Vector2f(64, 64)), 0);
		obj2.addComponent(new SpriteRenderer(new Sprite(texture2)));
		this.addGameObjectToScene(obj2);
		
		spritesheet = AssetPool.getSpritesheet("asset/images/spritesheet.png");
		
		obj3 = new GameObject("Object 3", 
				new Transform(new Vector2f(500, 100), new Vector2f(64,64)), 0);
		obj3.addComponent(new SpriteRenderer(spritesheet.getSprite(1)));
		this.addGameObjectToScene(obj3);	
		
	}

	
	private void loadResource() {
		// TODO Auto-generated method stub
		
		AssetPool.getShader("asset/shaders/default.glsl");
		AssetPool.getTexture("asset/images/blendImage1.png");
		AssetPool.getTexture("asset/images/blendImage2.png");
		AssetPool.getTexture("asset/images/testImage2.png");
		AssetPool.addSpritesheet("asset/images/spritesheet.png",
			new Spritesheet(new Texture("asset/images/spritesheet.png"), 16, 16, 26, 0));
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
			obj2.getComponents(SpriteRenderer.class).setSprite(spritesheet.getSprite(spriteIndex+5));
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

	
	
}
