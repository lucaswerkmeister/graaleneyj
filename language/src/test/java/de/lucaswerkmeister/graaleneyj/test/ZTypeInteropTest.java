package de.lucaswerkmeister.graaleneyj.test;

import static de.lucaswerkmeister.graaleneyj.test.ZAssert.assertZReference;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.graalvm.polyglot.Value;
import org.junit.Test;

public class ZTypeInteropTest extends ZTest {

	@Test
	public void testKeys() {
		Value stringType = eval("\"Z6\"").execute();
		assertTrue(stringType.hasMembers());
		assertEquals(Set.of("Z1K1", "Z4K1", "Z4K2", "Z4K4"), stringType.getMemberKeys());
		assertZReference("Z4", stringType.getMember("Z1K1"));
		assertZReference("Z6", stringType.getMember("Z4K1"));
		assertTrue(stringType.getMember("Z4K2").hasMembers());
		assertZReference("Z582", stringType.getMember("Z4K4"));
	}

}
