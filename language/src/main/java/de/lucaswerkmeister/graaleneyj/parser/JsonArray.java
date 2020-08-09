package de.lucaswerkmeister.graaleneyj.parser;

import java.util.ArrayList;
import java.util.List;

public class JsonArray extends JsonElement {

	private final List<JsonElement> elements = new ArrayList<>();

	public void add(JsonElement element) {
		elements.add(element);
	}

	public int size() {
		return elements.size();
	}

	public JsonElement get(int index) {
		return elements.get(index);
	}

}
