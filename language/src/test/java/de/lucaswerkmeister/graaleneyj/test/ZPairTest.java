package de.lucaswerkmeister.graaleneyj.test;

import static de.lucaswerkmeister.graaleneyj.test.ZAssert.assertZReference;
import static org.junit.Assert.assertEquals;

import org.graalvm.polyglot.Value;
import org.junit.Test;

public class ZPairTest extends ZTest {

	@Test
	public void testPairAB() {
		Value pair = eval("\"Z355\"").execute();
		assertZReference("Z2", pair.getMember("Z1K1"));
		assertEquals("A", pair.getMember("Z2K1").asString());
		assertEquals("B", pair.getMember("Z2K2").asString());
	}

}
