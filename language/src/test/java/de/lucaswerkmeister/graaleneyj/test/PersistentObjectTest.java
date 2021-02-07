package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.Test;

public class PersistentObjectTest extends ZTest {

	@Test
	public void testProjectNameLabelDefault() {
		Value projectName = eval("\"Z28\"").execute();
		assertEquals("Z28/project_name", projectName.toString());
	}

	@Test
	public void testProjectNameLabelGerman() {
		context = Context.newBuilder().option("z.userLanguage", "de").build();
		Value projectName = eval("\"Z28\"").execute();
		assertEquals("Z28/Projektname", projectName.toString());
	}

	@Test
	public void testUnlabeled() {
		Value value = eval("{\"Z1K1\": \"Z2\", \"Z2K1\": \"Z0\", \"Z2K2\": \"eneyj\"}");
		assertEquals("Z0", value.toString());
	}

}
