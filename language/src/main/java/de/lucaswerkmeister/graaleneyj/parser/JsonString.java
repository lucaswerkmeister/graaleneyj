package de.lucaswerkmeister.graaleneyj.parser;

public class JsonString extends JsonElement {

	private final String string;

	public JsonString(String string) {
		this.string = string;
	}

	public String getString() {
		return string;
	}

	@Override
	public String getAsString() {
		return string;
	}

}
