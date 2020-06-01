package de.lucaswerkmeister.graaleneyj.test;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Before;

import de.lucaswerkmeister.graaleneyj.ZLanguage;

public abstract class ZTest {

	private Context context;

	@Before
	public void setUp() {
		context = Context.create("z");
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
