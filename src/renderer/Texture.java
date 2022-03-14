package renderer;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class Texture {
	
	private String filepath;
	private transient int texId;
	private int width, height;
	
//	public Texture(String filepath)
//	{
//
//	}
	public Texture() {
		texId = -1;
		width = -1;
		height = -1;
	}
	
	public Texture(int width, int height) {
		
		this.filepath = "Generated";
		
		//Generate texture on GPU
		texId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texId);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0,
				GL_RGB, GL_UNSIGNED_BYTE, 0);
	}
	
	public void init(String filepath) {
		this.filepath = filepath;
		
		//Generate texture on GPU
		texId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texId);
		
		//Set texture parameters
		//Repeat Image in both directions
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		
		//When stretching the image, pixelate
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		//When shrinking the image, pixelate
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		IntBuffer channels = BufferUtils.createIntBuffer(1);

		stbi_set_flip_vertically_on_load(true);
		ByteBuffer image = stbi_load(filepath, width, height, channels, 0);
		
		if(image != null)
		{
			this.width = width.get(0);
			this.height = height.get(0);
			
			if(channels.get(0) == 3) {
				//level 0, width, height, border=0 don't care, 
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0,
						GL_RGB, GL_UNSIGNED_BYTE, image);
			} else if(channels.get(0) == 4) {
				//level 0, width, height, border=0 don't care, 
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0,
						GL_RGBA, GL_UNSIGNED_BYTE, image);
			} else {
				assert false : "Error : (Texture) Unknown Number of channels '" + channels.get(0) + "'"; 
			}
			
			
		} else {
			assert false : "Error : (Texture) Could not load image '" + filepath + "'";
		}
		
		stbi_image_free(image);
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, texId);
	}
	
	public void unBind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public String getFilepath()
	{
		return this.filepath;
	}
	
	public int getHeight() {
		return this.height;
	}

	public int getId() {
		return this.texId;
	}
	
	public boolean eqauls(Object o) {
		if(o == null) return false;
		if( !(o instanceof Texture)) return false;
		Texture oTex = (Texture) o;
		return oTex.getWidth() == this.width && 
			   oTex.getHeight() == this.height && 
			   oTex.getId() == this.texId && 
			   oTex.getFilepath().equals(this.filepath);
		
	}
}
