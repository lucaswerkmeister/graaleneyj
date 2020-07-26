package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.source.Source;

import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.runtime.UnusableImplementationException;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;

public class ZImplementationCodeNode extends ZImplementationNode {

	private final String language;

	/** {@code null} to indicate an unusable language. */
	private final String source;

	private final String functionId;

	private final String[] argumentNames;

	public ZImplementationCodeNode(String language, String source, String functionId, String[] argumentNames) {
		super(functionId);
		switch (language) {
		case "javascript":
			// TODO functions can have side-effects by accessing and modifying properties of
			// the globalThis :(
			this.language = "js";
			this.source = "let K0;\n" + source + "\nreturn K0;";
			break;
		case "python":
			// TODO doesn’t actually work yet because GraalPython doesn’t support parse
			// requests with argument names
			this.language = "python";
			// this.source = source + "\nK0";
			this.source = null;
			break;
		default:
			this.language = language;
			this.source = null;
		}
		this.functionId = functionId;
		this.argumentNames = argumentNames;
	}

	@Override
	public CallTarget makeCallTarget() {
		if (source != null) {
			Source s = Source.newBuilder(language, source, functionId).build();
			ZContext c = lookupContextReference(ZLanguage.class).get(); // TODO @CachedContext?
			return c.parse(s, argumentNames);
		} else {
			RuntimeException exception = new UnusableImplementationException("Unusable code language: " + language);
			ZRootNode throwNode = new ZRootNode(null, // TODO where does the language come from?
					new ZThrowConstantNode(exception));
			return Truffle.getRuntime().createCallTarget(throwNode);
		}
	}

}
