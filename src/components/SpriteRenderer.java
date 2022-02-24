package components;

import org.joml.Vector2f;
import org.joml.Vector4f;

import jade.Component;
import jade.Transform;
import renderer.Texture;

public class SpriteRenderer extends Component {

	private Vector4f color;
	//private Vector2f[] texCoords;
	//private Texture texture;
	private Sprite sprite;
	private Transform lastTransform;
	private boolean isDirty = false;
	
	public SpriteRenderer(Vector4f color) {
		this.color = color;
		//this.texture = null;
		this.sprite = new Sprite(null);
		this.isDirty = true;	
	}
	
	public SpriteRenderer(Sprite sprite) {
		this.color = new Vector4f(1,1,1,1);
		//this.texture = null;
		this.sprite = sprite;
		this.isDirty = true;
		
	}
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
}
