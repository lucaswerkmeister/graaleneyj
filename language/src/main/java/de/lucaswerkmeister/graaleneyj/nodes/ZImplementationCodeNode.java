package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.source.Source;

import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.runtime.UnusableImplementationException;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;

public class ZImplementationCodeNode extends ZImplementationNode {

	private final ZLanguage zLanguage;

	private final String sourceLanguage;

	/** {@code null} to indicate an unusable language. */
	private final String source;

	private final String functionId;

	private final String[] argumentNames;

	public ZImplementationCodeNode(ZLanguage zLanguage, String sourceLanguage, String source, String functionId,
			String[] argumentNames) {
		super(functionId);
		this.zLanguage = zLanguage;
		switch (sourceLanguage) {
		case "javascript":
			this.sourceLanguage = "js";
			this.source = "(function(){\nlet K0;\n" + source + "\nreturn K0;\n})()";
			break;
		case "python":
			this.sourceLanguage = "python";
			this.source = source + "\nK0";
			break;
		default:
			this.sourceLanguage = sourceLanguage;
			this.source = null;
		}
		this.functionId = functionId;
		this.argumentNames = argumentNames;
	}

	@Override
	public CallTarget makeCallTarget() {
		if (source != null) {
			Source s = Source.newBuilder(sourceLanguage, source, functionId).build();
			ZContext context = lookupContextReference(ZLanguage.class).get();
			if (context.useInnerContexts()) {
				ZInnerContextNode innerContextNode = ZInnerContextNodeGen.create(zLanguage, s, argumentNames,
						getSourceSection());
				return Truffle.getRuntime().createCallTarget(innerContextNode);
			} else {
				if (context.canParseLanguage(sourceLanguage)) {
					return context.parse(s, argumentNames);
				}
			}
		}

		RuntimeException exception = new UnusableImplementationException("Unusable code language: " + sourceLanguage);
		ZRootNode throwNode = new ZRootNode(zLanguage, new ZThrowConstantNode(exception), getSourceSection());
		return Truffle.getRuntime().createCallTarget(throwNode);
	}

}
