package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.source.Source;

import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;

public class ZImplementationCodeNode extends ZImplementationNode {

	private final String language;

	private final String source;

	@CompilationFinal
	private CallTarget callTarget = null;

	public ZImplementationCodeNode(String language, String source) {
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
			this.source = source + "\nK0";
			break;
		default:
			throw new IllegalArgumentException("Unknown language: " + language);
		}
	}

	@Override
	public CallTarget getCallTarget() {
		if (callTarget == null) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			Source s = Source.newBuilder(language, source, "(code)").build(); // TODO (code) -> function name
			ZContext c = lookupContextReference(ZLanguage.class).get(); // TODO @CachedContext?
			callTarget = c.parse(s, "Z53K1", "Z53K2"); // TODO extremely obvious hard-coding lmao
		}
		return callTarget;
	}

}
