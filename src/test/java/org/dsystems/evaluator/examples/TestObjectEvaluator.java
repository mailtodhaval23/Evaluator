package org.dsystems.evaluator.examples;

import java.util.HashMap;
import java.util.Map;

import org.dsystems.evaluator.DoubleEvaluator;
import org.dsystems.evaluator.ObjectEvaluator;
import org.dsystems.evaluator.StaticVariableSet;
import org.junit.Test;

public class TestObjectEvaluator {

	public TestObjectEvaluator () {
		
	}
	
	@Test
	public void Test() {
		System.out.println("Running first test!!!");
	}
	
	@Test
	public void TestObjEvaluator() {
		ObjectEvaluator evaluator = new ObjectEvaluator();
		Map<String, Object> record = new HashMap<String, Object>();
	    record.put("Error", "E002");
	    record.put("Temperature", 10);
	    record.put("Pressure", 15);
		final StaticVariableSet<Object> variables = new StaticVariableSet<Object>(record);
	    String expression = "3+5==5+3";
	    // Evaluate an expression
	    Object result = evaluator.evaluate(expression);
	    System.out.println(expression + " --> " + result);
	    expression = "Error==\"E001\"";
	    System.out.println(expression + " --> " + evaluator.evaluate(expression,variables));
	    expression = "test==\"Test\" || test==test";
	    System.out.println(expression + " --> " + evaluator.evaluate(expression));
	    expression = "Temperature<15 && Pressure > 10";
	    try {
	    System.out.println(expression + " --> " + evaluator.evaluate(expression,record)); 
	    } catch (Exception e) {
	    	System.out.println("Incorrect expression!!! --> " + expression);
	    }
	}
	@Test
	public void TestSimple() {
		DoubleEvaluator evaluator = new DoubleEvaluator();
	    String expression = "(2^3-1)*sin(pi/4)/ln(pi^2)";
	    // Evaluate an expression
	    Double result = evaluator.evaluate(expression);
	    // Ouput the result
	    System.out.println(expression + " = " + result);
	}

}
