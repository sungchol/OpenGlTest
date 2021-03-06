package jade;

import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.opengl.GL15.*;

import Scenes.LevelEditorScene;
import Scenes.LevelScene;
import Scenes.Scene;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL.*;

import static org.lwjgl.glfw.GLFW.*;

import renderer.DebugDraw;
import renderer.Framebuffer;
import renderer.PickingTexture;
import renderer.Renderer;
import renderer.Shader;
import util.AssetPool;
import util.Time;

public class Window {
	
	public int width, height;
	private String title;
	private static Window window = null;
	private long glfwWindow = 0L;
	private ImGuiLayer imguiLayer;
	private Framebuffer framebuffer;
	private PickingTexture pickingTexture;
	
	public float r, g, b, a;
	private boolean fadeToBlack = false;
	
	private static Scene currentScene; // = new LevelEditorScene();;
	
	
	private Window()
	{
		this.width = 1920;
		this.height = 1080;
		this.title = "mario";
		this.r = 1;
		this.g = 1;
		this.b = 1;
		this.a = 1;
	}

	public static void changeScene(int newScene)
	{
		switch(newScene)
		{
		case 0:
			currentScene = new LevelEditorScene();
			
			break;
			
		case 1:
			currentScene= new LevelScene();
			break;
			
		default:
			assert false : "Unknown scene '"+ newScene + "'";
			break;
		}
		
		currentScene.load();
		currentScene.init();
		currentScene.start();
		
	}
	
	public static Window get() {
		if(Window.window == null) {
			Window.window = new Window();
		}
		
		return Window.window;
	}
	
	public static Scene getScene() {
		return get().currentScene;
	}
	
	public void run()
	{
		System.out.println("hello " + Version.getVersion());
		
		init();
		loop();

		// Destroy the audio context
//        alcDestroyContext(audioContext);
//        alcCloseDevice(audioDevice);	

        // Free the memory
		//glfwFreeCallbacks(glfwWindow);
		
		glfwDestroyWindow(glfwWindow);
        
        // Terminate GLFW and the free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	public void init() {
		//System.err.println("error");
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // Configure GLFW
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);

        // Create the window
        glfwWindow = GLFW.glfwCreateWindow(this.width, this.height, this.title, 0, 0);
        if (glfwWindow == 0) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
        	Window.setWidth(newWidth);
        	Window.setHeight(newHeight);
        });

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        GLFW.glfwSwapInterval(1);

        // Make the window visible
        GLFW.glfwShowWindow(glfwWindow);

        // Initialize the audio device
        //String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        //audioDevice = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        //audioContext = alcCreateContext(audioDevice, attributes);
        //alcMakeContextCurrent(audioContext);

        //ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        //ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        //if (!alCapabilities.OpenAL10) {
        //    assert false : "Audio library not supported.";
        //}

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        
//
//        //this.framebuffer = new Framebuffer(3840, 2160);
//        //this.pickingTexture = new PickingTexture(3840, 2160);
        
//
//        this.imguiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        
        this.framebuffer = new Framebuffer(3840, 2160);
        this.pickingTexture = new PickingTexture(3840, 2160);
        
        this.imguiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        this.imguiLayer.initImGui();
        
        glViewport(0, 0, 3840, 2160);
//
//        Window.changeScene(new LevelEditorSceneInitializer());
        changeScene(0);
	}
	
	public void loop() {
		
		float beginTime = (float) GLFW.glfwGetTime(); //Time.getTime();
		float endTime;
		float dt = -1.0f;
		
		Shader defaultShader = AssetPool.getShader("asset/shaders/default.glsl");
		Shader pickingShader = AssetPool.getShader("asset/shaders/pickingShader.glsl");
		
		while (!glfwWindowShouldClose(glfwWindow)) {
	            	            
			 glfwPollEvents();

			 //Render pass 1. Render a Picking texture
			 glDisable(GL_BLEND); //?????? ???????????? ???????? ??
			 pickingTexture.enableWriting();
			 
			 glViewport(0,0,3840,2160);
			 glClearColor(0f,0f,0f,0f);
			 glClear(GL_COLOR_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
			 
			 Renderer.bindShader(pickingShader);
			 //Renderer.bindShader(defaultShader);
			 currentScene.render();
			 
			
			 
			 pickingTexture.disableWriting();
			 glDisable(GL_BLEND);
			 
			 
			 //Render pass 2. Render actual Game
			 
			 
			 DebugDraw.beginFrame();
			 
			 this.framebuffer.bind();	
			 glClearColor(r, g, b, a);
			 glClear(GL_COLOR_BUFFER_BIT);
			 
			 
			 if(dt>=0) {
				 DebugDraw.draw();
				 Renderer.bindShader(defaultShader);
				 currentScene.update(dt);
				 currentScene.render();
			 }
			 this.framebuffer.unbind();
			 
			 this.imguiLayer.update(dt, currentScene);
			 
			 glfwSwapBuffers(glfwWindow);
			 endTime = (float) GLFW.glfwGetTime(); //Time.getTime();
			 dt = endTime - beginTime;
			 beginTime = endTime;
		}
		
		currentScene.saveExit();
	}
	
	public static int getWidth() {
		return get().width;
	}
	public static int getHeight() {
		return get().height;
	}
	
	public static void setWidth(int width) {
		get().width = width;
	}
	
	public static void setHeight(int height) {
		get().height = height;
	}
	
	public static Framebuffer getFramebuffer() {
		return get().framebuffer;
	}
	
	public static float getTargetAspectRatio() {
		//return get().width / get().height;
		return 16.0f / 9.0f;
	}
}








