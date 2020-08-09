package de.lucaswerkmeister.graaleneyj.parser;

public abstract class JsonElement {

	JsonElement() {
		/* package-private constructor to prevent foreign subclasses */
	}

	public JsonObject getAsJsonObject() {
		throw new IllegalStateException("not an object");
	}

	public String getAsString() {
		throw new IllegalStateException("not a string");
	}

}
