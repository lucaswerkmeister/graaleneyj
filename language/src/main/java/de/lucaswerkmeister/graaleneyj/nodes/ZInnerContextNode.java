package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleContext;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.CachedLanguage;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;

import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.runtime.UnusableImplementationException;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;

public abstract class ZInnerContextNode extends RootNode {

	private final Source source;

	private final String[] argumentNames;

	public ZInnerContextNode(ZLanguage language, Source source, String[] argumentNames) {
		super(language);
		this.source = source;
		this.argumentNames = argumentNames;
	}

	@Specialization
	public Object doGeneric(VirtualFrame frame,
			@CachedContext(ZLanguage.class) ContextReference<ZContext> contextReference,
			@CachedLanguage ZLanguage language, @Cached IndirectCallNode callNode) {
		ZContext outerZContext = contextReference.get();
		TruffleContext innerTruffleContext = outerZContext.makeInnerContext();
		Object outerTruffleContext = innerTruffleContext.enter(this);
		try {
			ZContext innerZContext = contextReference.get();
			if (innerZContext.canParseLanguage(source.getLanguage())) {
				CallTarget callTarget = innerZContext.parse(source, argumentNames);
				return callNode.call(callTarget, frame.getArguments());
			} else {
				throw new UnusableImplementationException("Unusable code language: " + source.getLanguage());
			}
		} finally {
			innerTruffleContext.leave(this, outerTruffleContext);
			innerTruffleContext.close();
		}
	}

}
