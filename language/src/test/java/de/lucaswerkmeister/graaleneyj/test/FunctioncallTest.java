package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FunctioncallTest extends ZTest {

	private String booleanIdentityFunction() {
		String negateWithOnlyJsImplementation = "{\"Z1K1\": \"Z8\", \"Z1K2\": \"Z56\", \"Z8K1\": [{\"Z1K2\": \"Z56K1\"}], "
				+ "\"Z8K4\": [{\"Z1K1\": \"Z14\", \"Z14K1\": {\"Z1K1\": \"Z16\", \"Z16K1\": \"javascript\", \"Z16K2\": \"K0 = !Z56K1\"}}]}";
		String negateOfZ0K1FunctionCall = "{\"Z1K1\": \"Z7\", \"Z7K1\": " + negateWithOnlyJsImplementation
				+ ", \"K1\": {\"Z1K1\": \"Z18\", \"Z18K1\": \"Z0K1\"}}";
		String negateOfNegateOfZ0K1FunctionCall = "{\"Z1K1\": \"Z7\", \"Z7K1\": " + negateWithOnlyJsImplementation
				+ ", \"K1\": " + negateOfZ0K1FunctionCall + "}";
		return "{\"Z1K1\": \"Z8\", \"Z1K2\": \"Z0\", \"Z8K1\": [{\"Z1K2\": \"Z0K1\"}], "
				+ "\"Z8K4\": [{\"Z1K1\": \"Z14\", \"Z14K1\": " + negateOfNegateOfZ0K1FunctionCall + "}]}";
	}

	@Test
	public void testIdentityOfTrue() {
		assertEquals(true,
				eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + booleanIdentityFunction() + ", \"K1\": \"Z54\"}").asBoolean());
	}

	@Test
	public void testIdentityOfFalse() {
		assertEquals(false,
				eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + booleanIdentityFunction() + ", \"K1\": \"Z55\"}").asBoolean());
	}

}
