package de.lucaswerkmeister.graaleneyj.nodes;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.lucaswerkmeister.graaleneyj.parser.ZCanonicalJsonParser;

public class ZReferenceLiteralNode extends ZNode {
	
	private final String id;
	private static final Map<String, Object> registry = new HashMap<>();
	
	public ZReferenceLiteralNode(String id) {
		this.id = id;
	}
	
	@Override
	public Object execute(VirtualFrame virtualFrame) {
		// TODO for now, global scope is all we have
		if (registry.containsKey(id)) {
			return registry.get(id);
		} else {
			CompilerDirectives.transferToInterpreter();
			try (FileReader file = new FileReader("/home/lucas/git/abstracttext/eneyj/data/" + id + ".json")) {
				JsonElement element = new Gson().fromJson(file, JsonElement.class);
				ZNode node = ZCanonicalJsonParser.parseJsonElement(element);
				Object value = node.execute(virtualFrame);
				registry.put(id, value);
				return value;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
