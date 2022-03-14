package components;

public class FontRenderer extends Component {

	@Override
	public void start() {
		if(gameObject.getComponents(SpriteRenderer.class) != null) {
			System.out.println("Found font Renderer!");
		}
	}
	@Override
	public void update(float dt) {
		// TODO Auto-generated method stub
		
	}

}
