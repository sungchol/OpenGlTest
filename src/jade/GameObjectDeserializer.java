package jade;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import components.Component;
import components.SpriteRenderer;

public class GameObjectDeserializer 
			implements JsonDeserializer<GameObject> {
	
	@Override
	public GameObject deserialize(JsonElement json, Type arg1, JsonDeserializationContext context)
			throws JsonParseException {
		
		JsonObject jsonObject = json.getAsJsonObject();
		String name = jsonObject.get("name").getAsString();
		JsonArray components = jsonObject.getAsJsonArray("components");
		Transform transform = context.deserialize(jsonObject.get("transform"), Transform.class);
		
		int zIndex = context.deserialize(jsonObject.get("zIndex"), int.class);
		GameObject go = new GameObject(name, transform, zIndex);
			
		for (JsonElement e : components) {
			Component c = context.deserialize(e, Component.class);
			go.addComponent(c);
		}
		

		return go;
	}
	
}
