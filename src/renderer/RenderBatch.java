package renderer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.*;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector4f;

import components.SpriteRenderer;
import jade.Window;
import util.AssetPool;

public class RenderBatch implements Comparable<RenderBatch>{

	//Vertex
	//======
	//pos            color                         tex Coords      tex id	 
	//float, float,  float, float, float ,float,   float, float,   float
	private final int POS_SIZE = 2;
	private final int COLOR_SIZE = 4;
	private final int TEX_COORDS_SIZE = 2;
	private final int TEX_ID_SIZE = 1;
	private final int ENTITY_ID_SIZE = 1;
	
	private final int POS_OFFSET = 0;
	private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
	private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
	private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
	private final int ENTITY_ID_OFFSET = TEX_ID_OFFSET + ENTITY_ID_SIZE * Float.BYTES;
	
	private final int VERTEX_SIZE = POS_SIZE + COLOR_SIZE + TEX_COORDS_SIZE 
									+ TEX_ID_SIZE + ENTITY_ID_OFFSET;
	private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;
	
	private SpriteRenderer[] sprites;
	private int numSprites;
	private boolean hasRoom;
	private float[] vertices;
	private int[] texSlots = {0,1,2,3,4,5,6,7};
	
	private List<Texture> textures;
	private int vaoId, vboId, maxBatchSize;
	//private Shader shader;
	private int zIndex;
	
	public RenderBatch(int maxBatchSize, int zIndex) {
		//shader = new Shader("asset/shaders/default.glsl");
		//shader.compile();
		System.out.println("Create new renderBatch");
		//shader = AssetPool.getShader("asset/shaders/default.glsl");
		
		this.zIndex = zIndex;
		this.sprites = new SpriteRenderer[maxBatchSize];
		this.maxBatchSize = maxBatchSize;
		this.textures = new ArrayList<>();
		
		//4 vertices quads
		vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];
		
		this.numSprites = 0;
		this.hasRoom = true;
	}
	
	public void start() {
	
		//Generate and bind a vertex array object
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		
		//Allocate space for vertices
		vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
		
		//Create and upload indices buffer
		int eboId = glGenBuffers();
		int[] indices = generateIndicies();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW); 
		
		//Enable the buffer attribute pointers
		glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
		glEnableVertexAttribArray(3);
		
		glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
		glEnableVertexAttribArray(4);
	}
	
	public void addSprite(SpriteRenderer spr) {
		//get index and add renderObject
		int index = this.numSprites;
		//[0 1 2 3 4 5]
		this.sprites[index] = spr;
		this.numSprites++;
		
		if(spr.getTexture() != null) {
			if(!textures.contains(spr.getTexture())) {
				textures.add(spr.getTexture());
			}
		}
		
		//Add properties to local vertices array
		loadVertexProperties(index);
		
		if(numSprites >= this.maxBatchSize)	{
			this.hasRoom = false;
		}
	}

	private void loadVertexProperties(int index) {
		// TODO Auto-generated method stub
		SpriteRenderer sprite = this.sprites[index];
		
		//Find offset within array (4 vertices per sprite)
		int offset = index * 4 * VERTEX_SIZE;
		//float float    float float float float 
		
		Vector4f color = sprite.getColor();
		Vector2f[] texCoords = sprite.getTexCoords();
		//we find the matching [texture index] in textures arrayList
		int texId = 0;
		if(sprite.getTexture() != null) {
			for(int i=0; i<textures.size(); i++) {
				if(textures.get(i).equals(sprite.getTexture())) {
					texId = i+1;
				}
			}
		}
		//Add vertice with the appropriate propertices
		
		// *   *
		// *   *
		
		float xAdd = 1.0f;
		float yAdd = 1.0f;
		for(int i=0; i<4; i++) {
			if(i==1) {
				yAdd = 0.0f;
			} else if(i==2) {
				xAdd = 0.0f;
			} else if(i==3) {
				yAdd = 1.0f;
			}
			
			//Load position
			vertices[offset] = sprite.gameObject.transform.position.x + 
					(xAdd * sprite.gameObject.transform.scale.x);
			vertices[offset+1] = sprite.gameObject.transform.position.y + 
					(yAdd * sprite.gameObject.transform.scale.y);
			
			//Load color 
			vertices[offset + 2 ] = color.x;
			vertices[offset + 3 ] = color.y;
			vertices[offset + 4 ] = color.z;
			vertices[offset + 5 ] = color.w;
			
			//Load tex Coords
			vertices[offset + 6 ] = texCoords[i].x;
			vertices[offset + 7 ] = texCoords[i].y;
			
			
			//Load tex ID
			vertices[offset + 8 ] = texId;
			
			//Load entity id
			vertices[offset + 9] = sprite.gameObject.getuid() + 1;
			System.out.println("[RenderBatch 191]" + sprite.gameObject.getuid() + " " + 
					sprite.getuid());
			
			offset += VERTEX_SIZE;
		}
		
	}

	public void render() {
		//for now, we will rebuffer all data every frame
		boolean rebufferData = false;
		
		for(int i=0; i< numSprites ; i++) {
			SpriteRenderer sprite = sprites[i];
			if(sprite.isDirty()) {
				loadVertexProperties(i);
				sprite.setClean();
				rebufferData = true;
			}
		}
		
		if(rebufferData) {
			
			//for now, we will rebuffer all data every frame
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
		}
		
		//User shader
		Shader shader = Renderer.getBoundShader();
		shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
		shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());
		//active texture slot And Bind
		for(int i=0; i<textures.size(); i++) {
			glActiveTexture(GL_TEXTURE0 + i + 1);
			textures.get(i).bind();
		}
		//upload 7 integer array
		shader.uploadIntArray("uTextures", texSlots);
		
		glBindVertexArray(vaoId);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		
		glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		glBindVertexArray(0);
		
		for(int i=0; i < textures.size(); i++ ) {
			textures.get(i).unBind();
		}
		
		
		shader.detach();
		
	}
	
	
	
	private int[] generateIndicies() {
		
		int[] elements = new int[maxBatchSize * 6];
		for(int i= 0; i < maxBatchSize; i++) {
			loadElementIndices(elements, i);
		}
		return elements;
	}
	 
	private void loadElementIndices(int[] elements, int index) {
		int offsetArrayIndex = 6 * index;
		int offset = 4 * index;
		
		//3,2,0,0,2,1    7,6,4,4,6,5
		// Triangle 1
		elements[offsetArrayIndex] = offset + 3;
		elements[offsetArrayIndex+1] = offset + 2;
		elements[offsetArrayIndex+2] = offset + 0;
		
		// Triangle 2
		elements[offsetArrayIndex+3] = offset + 0;
		elements[offsetArrayIndex+4] = offset + 2;
		elements[offsetArrayIndex+5] = offset + 1;
		
		
	}
	
	public boolean hasRoom() {
		return this.hasRoom;
	}
	
	public boolean hasTextureRoom() {
		return this.textures.size() < 8;
	}
	
	public boolean hasTexture(Texture tex) {
		return this.textures.contains(tex);
	}

	public int zIndex() {
		return this.zIndex;
	}

	@Override
	public int compareTo(RenderBatch o) {
		// TODO Auto-generated method stub
		return Integer.compare(this.zIndex, o.zIndex());
	}
	
}
