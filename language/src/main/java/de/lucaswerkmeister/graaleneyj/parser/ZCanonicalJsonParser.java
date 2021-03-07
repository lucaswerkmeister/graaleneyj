package de.lucaswerkmeister.graaleneyj.parser;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.builtins.ZAbstractBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.builtins.ZCharacterToStringBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.builtins.ZHeadBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.builtins.ZReifyBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.builtins.ZSameBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.builtins.ZStringToCharacterlistFactory;
import de.lucaswerkmeister.graaleneyj.builtins.ZTailBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.builtins.ZValueBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.nodes.ZCharacterLiteralNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZCharacterLiteralNode.ZCharacterLiteralMemberNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZCharacterLiteralNodeGen;
import de.lucaswerkmeister.graaleneyj.nodes.ZFunctionCallNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZFunctionNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZIfNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZIfNodeFactory;
import de.lucaswerkmeister.graaleneyj.nodes.ZImplementationBuiltinNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZImplementationCodeNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZImplementationFunctioncallNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZImplementationNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZListLiteralNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZObjectLiteralNode.ZObjectLiteralMemberNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZObjectLiteralNodeGen;
import de.lucaswerkmeister.graaleneyj.nodes.ZPersistentObjectNodeGen;
import de.lucaswerkmeister.graaleneyj.nodes.ZReadArgumentNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZReferenceLiteralNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZReferenceLiteralNodeGen;
import de.lucaswerkmeister.graaleneyj.nodes.ZRootNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZStringLiteralNode.ZStringLiteralMemberNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZStringLiteralNodeGen;
import de.lucaswerkmeister.graaleneyj.nodes.ZThrowConstantNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZTypeNode.ZTypeMemberNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZTypeNodeGen;
import de.lucaswerkmeister.graaleneyj.runtime.UnusableImplementationException;

public class ZCanonicalJsonParser {

	private final ZLanguage language;

	private Source currentSource;

	public ZCanonicalJsonParser(ZLanguage language) {
		this.language = language;
	}

	public ZRootNode parseSource(Source source) throws IOException {
		assert currentSource == null;
		currentSource = source;

		CharStream cs = CharStreams.fromReader(source.getReader());
		ZJsonLexer lexer = new ZJsonLexer(cs);
		TokenStream ts = new CommonTokenStream(lexer);
		ZJsonParser jsonParser = new ZJsonParser(ts);
		JsonElement element = jsonParser.value().element;
		ZNode node = parseJsonElement(element);
		ZRootNode rootNode = new ZRootNode(language, node, source.createSection(0, source.getLength()));

		currentSource = null;
		return rootNode;
	}

	public ZNode parseJsonElement(JsonElement json) {
		if (json instanceof JsonObject) {
			return parseJsonObject((JsonObject) json);
		}
		if (json instanceof JsonArray) {
			return parseJsonArray((JsonArray) json);
		}
		if (json instanceof JsonString) {
			return parseJsonString((JsonString) json);
		}
		throw new IllegalStateException("JSON element was neither object nor array nor string");
	}

	public ZNode parseJsonObject(JsonObject json) {
		String type = json.get(ZConstants.ZOBJECT_TYPE).getAsString(); // TODO error handling
		switch (type) {
		case ZConstants.PERSISTENTOBJECT:
			return parseJsonObjectAsPersistentObject(json);
		case ZConstants.TYPE:
			return parseJsonObjectAsType(json);
		case ZConstants.STRING:
			return parseJsonObjectAsStringLiteral(json);
		case ZConstants.FUNCTIONCALL:
			return parseJsonObjectAsFunctionCall(json);
		case ZConstants.FUNCTION:
			return parseJsonObjectAsFunction(json);
		case ZConstants.REFERENCE:
			return parseJsonObjectAsReference(json);
		case ZConstants.ARGUMENTREFERENCE:
			return parseJsonObjectAsArgumentReference(json);
		case ZConstants.CHARACTER:
			return parseJsonObjectAsCharacter(json);
		}
		ZObjectLiteralMemberNode[] members = new ZObjectLiteralMemberNode[json.size()];
		int i = 0;
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			members[i] = new ZObjectLiteralMemberNode(entry.getKey(), parseJsonElement(entry.getValue()));
			i++;
		}
		ZNode ret = ZObjectLiteralNodeGen.create(members);
		ret.setSourceSection(json.getSourceCharIndex(), json.getSourceLength());
		return ret;
	}

	public ZNode parseJsonObjectAsPersistentObject(JsonObject json) {
		String id = json.get(ZConstants.PERSISTENTOBJECT_ID).getAsString();
		ZNode value = parseJsonElement(json.get(ZConstants.PERSISTENTOBJECT_VALUE));
		ZNode label = null;
		if (json.keySet().contains(ZConstants.PERSISTENTOBJECT_LABEL)) {
			label = parseJsonElement(json.get(ZConstants.PERSISTENTOBJECT_LABEL));
		}
		ZNode ret = ZPersistentObjectNodeGen.create(id, value, label);
		ret.setSourceSection(json.getSourceCharIndex(), json.getSourceLength());
		return ret;
	}

	public ZNode parseJsonObjectAsType(JsonObject json) {
		String identity = json.get(ZConstants.TYPE_IDENTITY).getAsString();
		ZTypeMemberNode[] members = new ZTypeMemberNode[json.size() - 2];
		int i = 0;
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			if (ZConstants.ZOBJECT_TYPE.equals(entry.getKey())) {
				continue;
			}
			if (ZConstants.TYPE_IDENTITY.equals(entry.getKey())) {
				continue;
			}
			members[i] = new ZTypeMemberNode(entry.getKey(), parseJsonElement(entry.getValue()));
			i++;
		}
		ZNode ret = ZTypeNodeGen.create(identity, members);
		ret.setSourceSection(json.getSourceCharIndex(), json.getSourceLength());
		return ret;
	}

	/**
	 * Parse a JSON object as a string literal. This is used when the string value
	 * would otherwise look like a reference, or if the string has extra members.
	 */
	public ZNode parseJsonObjectAsStringLiteral(JsonObject json) {
		ZStringLiteralMemberNode[] extraMembers = new ZStringLiteralMemberNode[json.size() - 2];
		int i = 0;
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			if (ZConstants.ZOBJECT_TYPE.equals(entry.getKey())
					|| ZConstants.STRING_STRING_VALUE.equals(entry.getKey())) {
				continue;
			}
			extraMembers[i] = new ZStringLiteralMemberNode(entry.getKey(), parseJsonElement(entry.getValue()));
			i++;
		}
		ZNode ret = ZStringLiteralNodeGen.create(json.get(ZConstants.STRING_STRING_VALUE).getAsString(), extraMembers);
		ret.setSourceSection(json.getSourceCharIndex(), json.getSourceLength());
		return ret;
	}

	/**
	 * Parse a JSON object as a function call. Arguments may use global keys (named
	 * arguments) or local keys (positional arguments); additionally, the function
	 * being called may be a reference to another function, and the global keys
	 * might not use the function as the prefix (e. g. the function could be
	 * Z104/if_boolean but the arguments would be Z31K1, Z31K2, Z31K3). We cope with
	 * this by collecting all keys except Z1K* and Z7K* and asserting that they
	 * share the same prefix and are contiguous.
	 */
	public ZNode parseJsonObjectAsFunctionCall(JsonObject json) {
		ZNode function = parseJsonElement(json.get(ZConstants.FUNCTIONCALL_FUNCTION));
		SortedMap<Integer, ZNode> arguments = new TreeMap<>();
		String prefix = null;
		for (String key : json.keySet()) {
			if (key.startsWith(ZConstants.ZOBJECT + "K") || key.startsWith(ZConstants.FUNCTIONCALL + "K")) {
				continue;
			}
			int kIndex = key.indexOf('K');
			if (prefix == null) {
				prefix = key.substring(0, kIndex);
			} else if (!prefix.equals(key.substring(0, kIndex))) {
				throw new IllegalArgumentException("Function call key " + key + " does not share prefix " + prefix
						+ " with other function call keys");
			}
			arguments.put(Integer.parseInt(key.substring(kIndex + 1)), parseJsonElement(json.get(key)));
		}
		if (arguments.firstKey() != 1 || arguments.lastKey() != arguments.size()) {
			throw new IllegalArgumentException("Function call keys are not contiguous: " + arguments.keySet());
		}
		ZNode ret;
		if (function instanceof ZReferenceLiteralNode
				&& ZConstants.IF.equals(((ZReferenceLiteralNode) function).getId())) {
			if (arguments.size() != 3) {
				throw new IllegalArgumentException("Call to if with " + arguments.size() + " ≠ 3 arguments");
			}
			ret = new ZIfNode(arguments.get(1), arguments.get(2), arguments.get(3));
		} else {
			ret = new ZFunctionCallNode(function, arguments.values().toArray(new ZNode[arguments.size()]));
		}
		ret.setSourceSection(json.getSourceCharIndex(), json.getSourceLength());
		return ret;
	}

	public ZFunctionNode parseJsonObjectAsFunction(JsonObject json) {
		String functionId = json.get(ZConstants.FUNCTION_IDENTITY).getAsString();
		JsonArray arguments = json.getAsJsonArray(ZConstants.FUNCTION_ARGUMENTS);
		String[] argumentNames = new String[arguments.size()];
		for (int i = 0; i < argumentNames.length; i++) {
			argumentNames[i] = arguments.get(i).getAsJsonObject().get(ZConstants.PARAMETER_KEYID).getAsString();
		}
		JsonArray implementationJsons = json.getAsJsonArray(ZConstants.FUNCTION_IMPLEMENTATIONS);
		ZImplementationNode[] implementationNodes = new ZImplementationNode[implementationJsons.size()];
		for (int i = 0; i < implementationNodes.length; i++) {
			implementationNodes[i] = parseJsonObjectAsImplementation(implementationJsons.get(i).getAsJsonObject(),
					functionId, argumentNames);
		}
		ZFunctionNode ret = new ZFunctionNode(implementationNodes, functionId);
		ret.setSourceSection(json.getSourceCharIndex(), json.getSourceLength());
		return ret;
	}

	public ZImplementationNode parseJsonObjectAsImplementation(JsonObject json, String functionId,
			String[] argumentNames) {
		SourceSection implementationSourceSection = currentSource.createSection(json.getSourceCharIndex(),
				json.getSourceLength());
		ZImplementationNode ret;
		// TODO error if more than one of functioncall/builtin/code keys defined
		// TODO keySet.contains is ugly, JsonObject should have a method for that
		if (json.keySet().contains(ZConstants.IMPLEMENTATION_FUNCTIONCALL)) {
			ZNode node = parseJsonObjectAsFunctionCall(json.getAsJsonObject(ZConstants.IMPLEMENTATION_FUNCTIONCALL));
			ZRootNode rootNode = new ZRootNode(language, node, implementationSourceSection);
			ret = new ZImplementationFunctioncallNode(rootNode, functionId);
		} else if (json.keySet().contains(ZConstants.IMPLEMENTATION_BUILTIN)) {
			String builtin = json.get(ZConstants.IMPLEMENTATION_IMPLEMENTS).getAsString();
			switch (builtin) {
			case ZConstants.IF:
				// note: parseJsonObjectAsFunctionCall usually parses “if” calls specially, this
				// builtin implementation is used when “if” is called indirectly
				ret = makeBuiltin(ZIfNodeFactory.getInstance(), functionId, implementationSourceSection);
				break;
			case ZConstants.SAME:
				ret = makeBuiltin(ZSameBuiltinFactory.getInstance(), functionId, implementationSourceSection,
						ZValueBuiltinFactory.getInstance());
				break;
			case ZConstants.VALUE:
				ret = makeBuiltin(ZValueBuiltinFactory.getInstance(), functionId, implementationSourceSection);
				break;
			case ZConstants.REIFY:
				ret = makeBuiltin(ZReifyBuiltinFactory.getInstance(), functionId, implementationSourceSection);
				break;
			case ZConstants.ABSTRACT:
				ret = makeBuiltin(ZAbstractBuiltinFactory.getInstance(), functionId, implementationSourceSection);
				break;
			case ZConstants.CHARACTERTOSTRING:
				ret = makeBuiltin(ZCharacterToStringBuiltinFactory.getInstance(), functionId,
						implementationSourceSection);
				break;
			case ZConstants.STRINGTOCHARACTERLIST:
				ret = makeBuiltin(ZStringToCharacterlistFactory.getInstance(), functionId, implementationSourceSection);
				break;
			case ZConstants.HEAD:
				ret = makeBuiltin(ZHeadBuiltinFactory.getInstance(), functionId, implementationSourceSection);
				break;
			case ZConstants.TAIL:
				ret = makeBuiltin(ZTailBuiltinFactory.getInstance(), functionId, implementationSourceSection);
				break;
			default:
				ZThrowConstantNode throwConstantNode = new ZThrowConstantNode(
						new UnusableImplementationException("Unknown builtin: " + builtin));
				throwConstantNode.setSourceSection(json.getSourceCharIndex(), json.getSourceLength());
				ret = new ZImplementationBuiltinNode(
						new ZRootNode(language, throwConstantNode, implementationSourceSection), functionId);
				break;
			}
		} else if (json.keySet().contains(ZConstants.IMPLEMENTATION_CODE)) {
			JsonObject code = json.getAsJsonObject(ZConstants.IMPLEMENTATION_CODE);
			String sourceLanguage = code.get(ZConstants.CODE_LANGUAGE).getAsString();
			String source = code.get(ZConstants.CODE_SOURCE).getAsString();
			ret = new ZImplementationCodeNode(language, sourceLanguage, source, functionId, argumentNames);
		} else {
			ZThrowConstantNode throwConstantNode = new ZThrowConstantNode(
					new UnusableImplementationException("Neither function call nor builtin nor code: "
							+ json.get(ZConstants.IMPLEMENTATION_IMPLEMENTS).getAsString()));
			throwConstantNode.setSourceSection(json.getSourceCharIndex(), json.getSourceLength());
			ret = new ZImplementationBuiltinNode(
					new ZRootNode(language, throwConstantNode, implementationSourceSection), functionId);
		}
		ret.setSourceSection(json.getSourceCharIndex(), json.getSourceLength());
		return ret;
	}

	private ZImplementationBuiltinNode makeBuiltin(NodeFactory<? extends ZNode> factory, String functionId,
			SourceSection implementationSourceSection) {
		return makeBuiltin(factory, functionId, implementationSourceSection, null);
	}

	/**
	 * Create a builtin-based implementation node for the given function using the
	 * given factory, optionally wrapping each argument in a different builtin.
	 *
	 * @param factory
	 * @param functionId
	 * @param implementationSourceSection
	 * @param wrapArgumentsFactory        Wrap each argument of the outer builtin
	 *                                    with a call to this builtin, which must
	 *                                    accept a single argument. Used, for
	 *                                    instance, by the Z33/same builtin to wrap
	 *                                    each argument in a call to the Z36/value
	 *                                    builtin.
	 * @return
	 */
	private ZImplementationBuiltinNode makeBuiltin(NodeFactory<? extends ZNode> factory, String functionId,
			SourceSection implementationSourceSection, NodeFactory<? extends ZNode> wrapArgumentsFactory) {
		int argumentCount = factory.getExecutionSignature().size();
		ZNode[] argumentNodes = new ZNode[argumentCount];
		for (int i = 0; i < argumentCount; i++) {
			ZNode argumentNode;
			if (wrapArgumentsFactory != null) {
				argumentNode = wrapArgumentsFactory.createNode((Object) new ZNode[] { new ZReadArgumentNode(i) });
			} else {
				argumentNode = new ZReadArgumentNode(i);
			}
			argumentNodes[i] = argumentNode;
		}
		ZNode builtinNode = factory.createNode((Object) argumentNodes);
		ZRootNode rootNode = new ZRootNode(language, builtinNode, implementationSourceSection);
		return new ZImplementationBuiltinNode(rootNode, functionId);
	}

	public ZReferenceLiteralNode parseJsonObjectAsReference(JsonObject json) {
		// TODO error handling, and check whether it’s okay to throw away all other keys
		String id = json.get(ZConstants.REFERENCE_ID).getAsString();
		ZReferenceLiteralNode ret = ZReferenceLiteralNodeGen.create(id);
		ret.setSourceSection(json.getSourceCharIndex(), json.getSourceLength());
		return ret;
	}

	public ZReadArgumentNode parseJsonObjectAsArgumentReference(JsonObject json) {
		// TODO is it safe to assume that ZwhateverKi is always the (i-1)th argument?
		String reference = json.get(ZConstants.ARGUMENTREFERENCE_REFERENCE).getAsString();
		int index = Integer.parseInt(reference.substring(reference.indexOf('K') + 1));
		ZReadArgumentNode ret = new ZReadArgumentNode(index - 1);
		ret.setSourceSection(json.getSourceCharIndex(), json.getSourceLength());
		return ret;
	}

	public ZCharacterLiteralNode parseJsonObjectAsCharacter(JsonObject json) {
		ZCharacterLiteralMemberNode[] extraMembers = new ZCharacterLiteralMemberNode[json.size() - 2];
		int i = 0;
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			if (ZConstants.ZOBJECT_TYPE.equals(entry.getKey())
					|| ZConstants.CHARACTER_CHARACTER.equals(entry.getKey())) {
				continue;
			}
			extraMembers[i] = new ZCharacterLiteralMemberNode(entry.getKey(), parseJsonElement(entry.getValue()));
			i++;
		}
		ZCharacterLiteralNode ret = ZCharacterLiteralNodeGen
				.create(json.get(ZConstants.CHARACTER_CHARACTER).getAsString(), extraMembers);
		ret.setSourceSection(json.getSourceCharIndex(), json.getSourceLength());
		return ret;
	}

	public ZListLiteralNode parseJsonArray(JsonArray json) {
		ZNode[] nodes = new ZNode[json.size()];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = parseJsonElement(json.get(i));
		}
		ZListLiteralNode ret = new ZListLiteralNode(nodes);
		ret.setSourceSection(json.getSourceCharIndex(), json.getSourceLength());
		return ret;
	}

	/**
	 * A string represents a reference if it starts with a capital letter followed
	 * by a digit, otherwise it represents a string literal.
	 */
	public ZNode parseJsonString(JsonString json) {
		String string = json.getString();
		ZNode ret;
		if (string.length() < 2) {
			ret = ZStringLiteralNodeGen.create(string);
		} else if (string.charAt(0) > 127 || string.charAt(1) > 127) {
			ret = ZStringLiteralNodeGen.create(string);
		} else if (!Character.isUpperCase(string.charAt(0))) {
			ret = ZStringLiteralNodeGen.create(string);
		} else if (!Character.isDigit(string.charAt(1))) {
			ret = ZStringLiteralNodeGen.create(string);
		} else {
			ret = ZReferenceLiteralNodeGen.create(string);
		}
		ret.setSourceSection(json.getSourceCharIndex(), json.getSourceLength());
		return ret;
	}

}
