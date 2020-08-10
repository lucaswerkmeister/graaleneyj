package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

public class ZStringInteropTest extends ZTest {

	// TODO Some tests here currently include a Z1K2 to ensure the string is boxed.
	// But shouldn’t even plain strings appear to have members for us?

	@Test
	public void testHasMembers() {
		assertTrue(eval("{\"Z1K1\": \"Z6\", \"Z1K2\": \"Z0\", \"Z6K1\": \"a\"}").hasMembers());
	}

	@Test
	public void testGetMembers() {
		Set<String> keys = eval("{\"Z1K1\": \"Z6\", \"Z1K2\": \"Z0\", \"Z6K1\": \"a\"}").getMemberKeys();
		assertEquals(3, keys.size());
		assertTrue(keys.contains("Z1K1"));
		assertTrue(keys.contains("Z1K2"));
		assertTrue(keys.contains("Z6K1"));
	}

	@Test
	public void testReadMember_type() {
		assertEquals("Z6", eval("{\"Z1K1\": \"Z6\", \"Z1K2\": \"Z0\", \"Z6K1\": \"a\"}").getMember("Z1K1").toString());
	}

	@Test
	public void testReadMember_character() {
		assertEquals("a", eval("{\"Z1K1\": \"Z6\", \"Z1K2\": \"Z0\", \"Z6K1\": \"a\"}").getMember("Z6K1").asString());
	}

	@Test
	public void testReadMember_id() {
		assertEquals("Z0", eval("{\"Z1K1\": \"Z6\", \"Z1K2\": \"Z0\", \"Z6K1\": \"a\"}").getMember("Z1K2").toString());
	}

	@Test
	public void testReadMember_missing() {
		assertNull(eval("{\"Z1K1\": \"Z6\", \"Z1K2\": \"Z0\", \"Z6K1\": \"a\"}").getMember("Z1K3"));
	}

}
