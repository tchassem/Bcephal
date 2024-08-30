/**
 * 
 */
package com.moriset.bcephal.configuration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * @author MORISET-004
 *
 */
class InitTest {

	@Test
	void test() {
		assertTrue(true, "");
		//assertTrue(false, " fail");
		
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression("'Hello World'");
		String message = (String) exp.getValue();
		assertEquals("Hello World", message);
	}

}
