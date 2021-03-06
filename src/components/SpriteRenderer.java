package components;

import org.joml.Vector2f;
import org.joml.Vector4f;

import imgui.ImGui;
import jade.Transform;
import renderer.Texture;

public class SpriteRenderer extends Component {

	private Vector4f color = new Vector4f(1,1,1,1);
	//private Vector2f[] texCoords;
	//private Texture texture;
	private Sprite sprite = new Sprite();
	
	//transient -> Gson do not serialize
	private transient Transform lastTransform; 
	private transient boolean isDirty = true;
	
	public int number = 3;
	public Vector4f position = new Vector4f(1,1,1,1);
	
	
//	public SpriteRenderer(Vector4f color) {
//		this.color = color;
//		//this.texture = null;
//		this.sprite = new Sprite(null);
//		this.isDirty = true;	
//	}
//	
//	public SpriteRenderer(Sprite sprite) {
//		this.color = new Vector4f(1,1,1,1);
//		//this.texture = null;
//		this.sprite = sprite;
//		this.isDirty = true;
//		
//	}
	@Override
	public void start() {
		this.lastTransform = gameObject.transform.copy();
	}
	
	@Override
	public void update(float dt) {

		if(! this.lastTransform.eqauls(this.gameObject.transform)) {
			this.gameObject.transform.copy(this.lastTransform);
			isDirty = true;
		}
	}

	@Override
	public void imgui() {
		float[] imColor = {this.color.x, this.color.y, this.color.z, this.color.w };
		if(ImGui.colorPicker4("Color : ", imColor)) {
			this.color.set(imColor);
			this.isDirty = true;
		}
		
		super.imgui();
	}
	
	
	public Vector4f getColor() {
		return this.color;
	}
	
	public Texture getTexture() {
		//return this.texture;
		return sprite.getTexture();
	}
	
	public Vector2f[] getTexCoords() {
			
		return sprite.getTexCoords();
	}
	
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
		this.isDirty = true;
	}
	
	public void setColor(Vector4f color) {
		
		if(!this.color.equals(color)) {			
			this.isDirty = true;
			this.color.set(color);
		}
	}
	
	public boolean isDirty() {
		return this.isDirty;
	}
	
	public void setClean() {
		this.isDirty = false;
	}
	
	public void setTexture(Texture texture) {
		this.sprite.setTexture(texture);
	}
	
}







