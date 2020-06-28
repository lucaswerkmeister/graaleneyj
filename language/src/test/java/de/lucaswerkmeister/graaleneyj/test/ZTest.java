package de.lucaswerkmeister.graaleneyj.test;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Before;

import de.lucaswerkmeister.graaleneyj.ZLanguage;

public abstract class ZTest {

	protected Context context;

	@Before
	public void setUp() {
		context = Context.newBuilder() //
				.allowPolyglotAccess(PolyglotAccess.ALL) // for CodeJsTest, CodePythonTest
				.allowHostAccess(HostAccess.newBuilder() //
						.allowArrayAccess(true) // for BuiltinTest
						.build())
				.build();
	}

	@After
	public void tearDown() {
		context = null;
	}

	protected Value eval(String code) {
		Source source = Source.newBuilder(ZLanguage.ID, code, "test").buildLiteral();
		return context.eval(source);
	}

}
