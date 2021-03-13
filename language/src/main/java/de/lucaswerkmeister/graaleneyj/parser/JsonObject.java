package de.lucaswerkmeister.graaleneyj.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public class JsonObject extends JsonElement {

	private final Map<String, JsonElement> elements = new HashMap<>();

	public void add(String key, JsonElement value) {
		elements.put(key, value); // TODO what if key was already present?
	}

	public JsonElement get(String key) {
		JsonElement element = elements.get(key);
		if (element != null) {
			return element;
		} else {
			throw new NoSuchElementException(key);
		}
	}

	public JsonObject getAsJsonObject(String key) {
		JsonElement element = elements.get(key);
		if (element instanceof JsonObject) {
			return (JsonObject) element;
		} else if (element != null) {
			throw new IllegalStateException("not an object: " + key);
		} else {
			throw new NoSuchElementException(key);
		}
	}

	public JsonArray getAsJsonArray(String key) {
		JsonElement element = elements.get(key);
		if (element instanceof JsonArray) {
			return (JsonArray) element;
		} else if (element != null) {
			throw new IllegalStateException("not an array: " + key);
		} else {
			throw new NoSuchElementException(key);
		}
	}

	public int size() {
		return elements.size();
	}

	public Set<Entry<String, JsonElement>> entrySet() {
		return Collections.unmodifiableSet(elements.entrySet());
	}

	public Set<String> keySet() {
		return Collections.unmodifiableSet(elements.keySet());
	}

	public JsonObject asJsonObject() {
		return this;
	}

}
