package de.lucaswerkmeister.graaleneyj;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextPolicy;

import de.lucaswerkmeister.graaleneyj.nodes.ZNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZRootNode;
import de.lucaswerkmeister.graaleneyj.parser.ZCanonicalJsonParser;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;
import de.lucaswerkmeister.graaleneyj.runtime.ZList;
import de.lucaswerkmeister.graaleneyj.runtime.ZObject;

// TODO language name? context policy?
@TruffleLanguage.Registration(id = ZLanguage.ID, name = "Z language", contextPolicy = ContextPolicy.EXCLUSIVE)
// TODO tags
public class ZLanguage extends TruffleLanguage<ZContext> {

	public static final String ID = "z"; // TODO name?

	@Override
	protected ZContext createContext(Env env) {
		return new ZContext(env);
	}

	@Override
	protected CallTarget parse(ParsingRequest request) throws Exception {
		if (!request.getArgumentNames().isEmpty()) {
			// TODO I have no idea how hard this would be to support, just keeping it simple
			// for now
			throw new UnsupportedOperationException("Can’t parse with arguments yet");
		}
		JsonElement element = new Gson().fromJson(request.getSource().getReader(), JsonElement.class);
		ZNode node = ZCanonicalJsonParser.parseJsonElement(element);
		ZRootNode rootNode = new ZRootNode(this, node);
		return Truffle.getRuntime().createCallTarget(rootNode);
	}

	@Override
	protected boolean isObjectOfLanguage(Object object) {
		// TODO stub implementation
		return object instanceof ZList || object instanceof ZObject;
	}

}
