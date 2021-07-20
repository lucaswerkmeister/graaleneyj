package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.Source;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.lucaswerkmeister.graaleneyj.ZLanguage;

@Ignore("Python support broken since GraalVM 21.1.0, see https://github.com/oracle/graal/issues/3372")
public class CodePythonTest extends ZTest {

	@Before
	public void checkPython() {
		assumeTrue("python language is installed", context.getEngine().getLanguages().containsKey("python"));
	}

	private String nandWithOnlyPythonImplementation() {
		return "{\"Z1K1\": \"Z8\", \"Z8K5\": \"Z53\", \"Z8K1\": [{\"Z17K2\": \"Z53K1\"}, {\"Z17K2\": \"Z53K2\"}], "
				+ "\"Z8K4\": [{\"Z1K1\": \"Z14\", \"Z14K3\": {\"Z1K1\": \"Z16\", \"Z16K1\": \"python\", \"Z16K2\": \"K0 = not(Z53K1 and Z53K2)\"}}]}";
	}

	@Test
	public void testNandFalseFalse() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + nandWithOnlyPythonImplementation()
				+ ", \"K1\": \"Z42\", \"K2\": \"Z42\"}").asBoolean());
	}

	@Test
	public void testNandFalseTrue() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + nandWithOnlyPythonImplementation()
				+ ", \"K1\": \"Z42\", \"K2\": \"Z41\"}").asBoolean());
	}

	@Test
	public void testNandTrueFalse() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + nandWithOnlyPythonImplementation()
				+ ", \"K1\": \"Z41\", \"K2\": \"Z42\"}").asBoolean());
	}

	@Test
	public void testNandTrueTrue() {
		assertEquals(false, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + nandWithOnlyPythonImplementation()
				+ ", \"K1\": \"Z41\", \"K2\": \"Z41\"}").asBoolean());
	}

	private String callCounterThreeCalls() {
		String python = "import builtins\\n" + //
				"try:\\n" + //
				"    builtins.c += 1\\n" + //
				"except AttributeError:\\n" + //
				"    builtins.c = 1\\n" + //
				"K0 = builtins.c";
		String callCounter = "{\"Z1K1\": \"Z8\", \"Z8K5\": \"Z0\", \"Z8K1\": [{\"Z1K1\": \"Z17\", \"Z17K1\": \"Z1\", \"Z17K2\": \"Z0K1\"}], "
				+ "\"Z8K4\": [{\"Z1K1\": \"Z14\", \"Z14K3\": {\"Z1K1\": \"Z16\", \"Z16K1\": \"python\", \"Z16K2\": \""
				+ python + "\"}}]}";
		String call1 = "{\"Z1K1\": \"Z7\", \"Z7K1\": " + callCounter + ", \"K1\": \"ignored\"}";
		String call2 = "{\"Z1K1\": \"Z7\", \"Z7K1\": " + callCounter + ", \"K1\": " + call1 + "}";
		String call3 = "{\"Z1K1\": \"Z7\", \"Z7K1\": " + callCounter + ", \"K1\": " + call2 + "}";
		return call3;
	}

	@Test
	public void testFunctionCallContextIsolation() {
		assertEquals(1, eval(callCounterThreeCalls()).asInt());
	}

	@Test
	public void testFunctionCallContextIsolationDisabled() {
		try (Context context = Context.newBuilder() //
				.allowPolyglotAccess(PolyglotAccess.ALL) //
				.option("z.useInnerContexts", "false") //
				.build()) {
			Source source = Source.newBuilder(ZLanguage.ID, callCounterThreeCalls(), "test").buildLiteral();
			assertEquals(3, context.eval(source).asInt());
		}
	}

}
