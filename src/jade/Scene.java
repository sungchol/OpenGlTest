package jade;

import java.util.ArrayList;
import java.util.List;

import imgui.ImGui;
import renderer.Renderer;

public abstract class Scene {

	protected Renderer renderer;
	protected Camera camera;
	private boolean isRunning = false;
	protected List<GameObject> gameObjects = new ArrayList<>();
	protected GameObject activeGameObject = null;
	
	public Scene() {
		renderer = new Renderer();
	}
	
	public void init() {
		
	}
	public void start()	{
		for(GameObject go : gameObjects) {
			go.start();
			this.renderer.add(go);
		}
		isRunning = true;
	}
	
	public void addGameObjectToScene(GameObject go)	{
		if(!isRunning) {
			gameObjects.add(go);
			
		} else {
			gameObjects.add(go);
			go.start();
			this.renderer.add(go);
		}
	}
	public abstract void update(float dt);	
	
	public Camera camera() {
		return this.camera;
	}
	
	public void sceneImgui() {
		if(activeGameObject != null) {
			ImGui.begin("Inspector");
				//run the component imgui 
				activeGameObject.imgui();
			ImGui.end();	
		}
		
		imgui();
	}
	
	public void imgui() {
		//create custom Gui Window : override
	}
	
	
}
