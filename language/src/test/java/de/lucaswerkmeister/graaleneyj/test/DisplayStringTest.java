package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DisplayStringTest extends ZTest {

	@Test
	public void testZFunction() {
		assertEquals("ZFunction", eval("{\"Z1K1\": \"Z8\", \"Z8K4\": []}").toString());
	}

	@Test
	public void testZListZeroElements() {
		assertEquals("[]", eval("[]").toString());
	}

	@Test
	public void testZListOneElement() {
		assertEquals("[abc]", eval("[\"abc\"]").toString());
	}

	@Test
	public void testZListTwoElements() {
		assertEquals("[abc, def]", eval("[\"abc\", \"def\"]").toString());
	}

	@Test
	public void testZListThreeElements() {
		assertEquals("[abc, def, ghi]", eval("[\"abc\", \"def\", \"ghi\"]").toString());
	}

	@Test
	public void testZNothing() {
		assertEquals("nothing", eval("Z23").toString());
	}

	@Test
	public void testZObjectOneMember() {
		assertEquals("{\"Z1K1\": Z1}", eval("{\"Z1K1\": \"Z1\"}").toString());
	}

	@Test
	public void testZReference() {
		assertEquals("Z53", eval("Z53").toString());
	}

}
