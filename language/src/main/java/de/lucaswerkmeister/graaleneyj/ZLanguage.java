package de.lucaswerkmeister.graaleneyj;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextPolicy;
import com.oracle.truffle.api.object.Shape;

import de.lucaswerkmeister.graaleneyj.nodes.ZNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZRootNode;
import de.lucaswerkmeister.graaleneyj.parser.JsonElement;
import de.lucaswerkmeister.graaleneyj.parser.ZCanonicalJsonParser;
import de.lucaswerkmeister.graaleneyj.parser.ZJsonLexer;
import de.lucaswerkmeister.graaleneyj.parser.ZJsonParser;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;

// TODO language name? context policy?
@TruffleLanguage.Registration(id = ZLanguage.ID, name = "Z language", contextPolicy = ContextPolicy.EXCLUSIVE)
// TODO tags
public class ZLanguage extends TruffleLanguage<ZContext> {

	public static final String ID = "z"; // TODO name?

	private final ZCanonicalJsonParser parser = new ZCanonicalJsonParser(this);
	private final Shape initialZObjectShape = Shape.newBuilder().build();

	@Override
	protected ZContext createContext(Env env) {
		return new ZContext(env, initialZObjectShape);
	}

	@Override
	protected CallTarget parse(ParsingRequest request) throws Exception {
		if (!request.getArgumentNames().isEmpty()) {
			// TODO I have no idea how hard this would be to support, just keeping it simple
			// for now
			throw new UnsupportedOperationException("Can’t parse with arguments yet");
		}
		CharStream cs = CharStreams.fromReader(request.getSource().getReader());
		ZJsonLexer lexer = new ZJsonLexer(cs);
		TokenStream ts = new CommonTokenStream(lexer);
		ZJsonParser jsonParser = new ZJsonParser(ts);
		JsonElement element = jsonParser.value().element;
		ZNode node = parser.parseJsonElement(element);
		ZRootNode rootNode = new ZRootNode(this, node);
		return Truffle.getRuntime().createCallTarget(rootNode);
	}

}
