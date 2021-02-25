package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleContext;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.runtime.UnusableImplementationException;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;

public abstract class ZInnerContextNode extends RootNode {

	private final Source source;

	private final String[] argumentNames;

	private final SourceSection sourceSection;

	public ZInnerContextNode(ZLanguage language, Source source, String[] argumentNames, SourceSection sourceSection) {
		super(language);
		this.source = source;
		this.argumentNames = argumentNames;
		this.sourceSection = sourceSection;
	}

	@Specialization
	public Object doGeneric(VirtualFrame frame,
			@CachedContext(ZLanguage.class) ContextReference<ZContext> contextReference,
			@Cached IndirectCallNode callNode) {
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

	@Override
	public boolean isInternal() {
		return true;
	}

	@Override
	protected boolean isInstrumentable() {
		return false;
	}

	@Override
	public String getName() {
		return source.getName();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public SourceSection getSourceSection() {
		return sourceSection;
	}

}
