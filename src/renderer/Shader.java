package renderer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

public class Shader {
	
	private int shaderProgramID;
	private boolean beingUsed = false;
	
	private String vertexSource;
	private String fragmentSource;
	private String filepath;
	
	public Shader(String filepath)
	{
		this.filepath = filepath;
		
		try {
			
			String source = new String(Files.readAllBytes(Paths.get(filepath)));
			String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");
			
			//Find first pattern after #type 'pattern'
			int index = source.indexOf("#type") + 6;
			int eol = source.indexOf("\n", index); 
			String firstPattern = source.substring(index, eol).trim();
			
			//Find second Pattern after #type 'pattern'
			index = source.indexOf("#type", eol) + 6;
			eol = source.indexOf("\n", index);
			String secondPattern = source.substring(index, eol).trim();
		
			if(firstPattern.equals("vertex"))
			{	
				vertexSource = splitString[1];
			} else if(firstPattern.equals("fragment"))
			{
				fragmentSource = splitString[1];	
			} else 
			{
				throw new IOException("Unexpected token '" + firstPattern + "'");
			}
			
			if(secondPattern.equals("vertex"))
			{	
				vertexSource = splitString[2];
			} else if(secondPattern.equals("fragment"))
			{
				fragmentSource = splitString[2];	
			} else 
			{
				throw new IOException("Unexpected token '" + secondPattern + "'");
			}
			
			
		} catch(IOException e)
		{
			e.printStackTrace();
			assert false : "error : Could not open file for shader : '" + filepath + "'"; 
		}
	}
	
	public void compile()
	{
		//==============================
		//= compile and link shaders
		//==============================

		int vertexID, fragmentID;
		
		//first load and compile the vertex shader
		vertexID = glCreateShader(GL_VERTEX_SHADER);
			
		//pass source Code to the Gpu
		glShaderSource(vertexID, vertexSource);
		glCompileShader(vertexID);
		
		//check for errors in compilation 0 fail 1 success
		int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
		if(success == GL_FALSE) { 
			int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR : '"+ filepath + "' \n\tVertext Shader compilation failed ");
			System.out.println(glGetShaderInfoLog(vertexID, len));
			assert false : "";
		}
		
		//first load and compile the vertex shader
		fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
		
		//pass source Code to the Gpu
		glShaderSource(fragmentID, fragmentSource);
		glCompileShader(fragmentID);
		
		//check for errors in compilation 0 fail 1 success
		success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
		if(success == GL_FALSE) { 
			int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR : '"+ filepath +"' \n\tFragment Shader compilation failed ");
			System.out.println(glGetShaderInfoLog(fragmentID, len));
			assert false : "";
		}
		

		//link Shaders and check for errors
		shaderProgramID = glCreateProgram();
		glAttachShader(shaderProgramID, vertexID);
		glAttachShader(shaderProgramID, fragmentID);
		glLinkProgram(shaderProgramID);

		//check for linking errors
		success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
		if(success == GL_FALSE) {
			int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR : '" + filepath +"' \n\tLinking of shaders failed ");
			System.out.println(glGetProgramInfoLog(shaderProgramID, len));
			assert false : "";
		}
	}
	
	public void use()
	{
		if(!beingUsed) {
			// Bind Shader program
			glUseProgram(shaderProgramID);
			beingUsed = true;
		}
		
	}
	
	public void detach()
	{
		glUseProgram(0);
		beingUsed = false;
	}
	
	public void uploadMat4f(String varname, Matrix4f mat4)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varname);
		use();
		FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
		mat4.get(matBuffer);
		GL30.glUniformMatrix4fv(varLocation, false, matBuffer);
		
	}
	
	public void uploadMat3f(String varname, Matrix3f mat3)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varname);
		use();
		FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
		mat3.get(matBuffer);
		GL30.glUniformMatrix3fv(varLocation, false, matBuffer);
		
	}
	
	public void uploadVec4f(String varname, Vector4f vec4)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varname);
		use();
		GL30.glUniform4f(varLocation, vec4.x, vec4.y, vec4.z, vec4.w);
	}
	
	public void uploadVec3f(String varname, Vector3f vec3)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varname);
		use();
		GL30.glUniform3f(varLocation, vec3.x, vec3.y, vec3.z);
	}
	
	public void uploadVec2f(String varname, Vector2f vec2)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varname);
		use();
		GL30.glUniform2f(varLocation, vec2.x, vec2.y);
	}
	
	public void uploadFloat(String varname, float val)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varname);
		use();
		GL30.glUniform1f(varLocation, val);
	}
	
	public void uploadInt(String varname, int val)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varname);
		use();
		GL30.glUniform1i(varLocation, val);
	}
	
	public void uploadTexture(String varname, int slot)
	{
		int varLocation = glGetUniformLocation(shaderProgramID, varname);
		use();
		GL30.glUniform1i(varLocation, slot);
	}
	
	public void uploadIntArray(String varname, int[] array) {
		int varLocation = glGetUniformLocation(shaderProgramID, varname);
		use();
		//v means it has point or array[]
		GL30.glUniform1iv(varLocation, array);
	}
}
