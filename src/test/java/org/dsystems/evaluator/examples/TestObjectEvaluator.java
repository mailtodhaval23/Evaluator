package org.dsystems.evaluator.examples;

import java.util.HashMap;
import java.util.Map;

import org.dsystems.evaluator.DoubleEvaluator;
import org.dsystems.evaluator.ObjectEvaluator;
import org.dsystems.evaluator.StaticVariableSet;
import org.dsystems.utils.Record;

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
		Record record = new Record();
	    record.put("Error", "E001");
	    record.put("Temperature", 10);
	    record.put("Pressure", 15);
	    record.put("P1Cases", 2);
	    record.put("Testdate", "10/8/2015");
	    //record.put("Testdate", "");
	    record.put("result", "C2");
		//final StaticVariableSet<Object> variables = new StaticVariableSet<Object>(record);
	    Record variables = record;
	    String expression = "3+5==5+3";
	    // Evaluate an expression
	    //Object result = evaluator.evaluate(expression);
	    /*System.out.println(expression + " --> " + result);
	    expression = "Error==\"E001\"";
	    System.out.println(expression + " --> " + evaluator.evaluate(expression,variables));
	    expression = "test==\"Test\" || test==test";
	    System.out.println(expression + " --> " + evaluator.evaluate(expression));
	    expression = "if(if(Error==\"E001\",Temperature<15 && Pressure < 10,Pressure >= 10), \"LT15\",\"GTOREQ15\")";
	    System.out.println(expression + " --> " + evaluator.evaluate(expression,variables));
	    expression = "if(P1Cases>5,RED,if(P1Cases>3&&P1Cases<=5,YELLOW,GREEN))";
	    System.out.println(expression + " --> " + evaluator.evaluate(expression,variables));
	    expression = "date==null || date==\"\"";
	    System.out.println(expression + " --> " + evaluator.evaluate(expression,variables));
	    */
	    expression = "isempty(Testdate)";
	    System.out.println(expression + " --> " + evaluator.evaluate(expression,variables));
	    expression = "if(isempty(Testdate), 0, if(datecompare(today,Testdate,'MM-dd-yyyy','MM/dd/yyyy') == before, 50, if(result == \"\", 0, 100)))";
	    System.out.println(expression + " --> " + evaluator.evaluate(expression,variables));
	    try {
		    expression = "Temperature<15 && Pressure > 10";
	    System.out.println(expression + " --> " + evaluator.evaluate(expression,variables));
	    } catch (Exception e) {
	    	System.out.println("Incorrect expression!!! --> " + expression);
	    }
	}
	//@Test
	public void TestSimple() {
		DoubleEvaluator evaluator = new DoubleEvaluator();
	    String expression = "(2^3-1)*sin(pi/4)/ln(pi^2)";
	    // Evaluate an expression
	    Double result = evaluator.evaluate(expression);
	    // Ouput the result
	    System.out.println(expression + " = " + result);
	}

}
