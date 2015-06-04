package org.dsystems.evaluator;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

/** An evaluator that is able to evaluate arithmetic expressions on real numbers.
 * <br>Built-in operators:<ul>
 * <li>+: Addition</li>
 * <li>-: Subtraction</li>
 * <li>-: Unary minus</li>
 * <li>*: Multiplication</li>
 * <li>/: Division</li>
 * <li>^: Exponentiation</li>
 * <li>%: Modulo</li>
 * </ul>
 * Built-in functions:<ul>
 * <li>abs: absolute value</li>
 * <li>acos: arc cosine</li>
 * <li>asin: arc sine</li>
 * <li>atan: arc tangent</li>
 * <li>average: average of arguments</li>
 * <li>ceil: nearest upper integer</li>
 * <li>cos: cosine</li>
 * <li>cosh: hyperbolic cosine</li>
 * <li>floor: nearest lower integer</li>
 * <li>ln: natural logarithm (base e)</li>
 * <li>log: base 10 logarithm</li>
 * <li>max: maximum of arguments</li>
 * <li>min: minimum of arguments</li>
 * <li>round: nearest integer</li>
 * <li>sin: sine</li>
 * <li>sinh: hyperbolic sine</li>
 * <li>sum: sum of arguments</li>
 * <li>tan: tangent</li>
 * <li>tanh: hyperbolic tangent</li>
 * <li>random: pseudo-random number (between 0 and 1)</li>
 * </ul>
 * Built-in constants:<ul>
 * <li>e: Base of natural algorithms</li>
 * <li>pi: Ratio of the circumference of a circle to its diameter</li>
 * </ul>
 * @author Jean-Marc Astesana
 * @see <a href="../../../license.html">License information</a>
 */
public class ObjectEvaluator extends AbstractEvaluator<Object> implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The order or operations (operator precedence) is not clearly defined, especially between the unary minus operator and exponentiation
	 * operator (see <a href="http://en.wikipedia.org/wiki/Order_of_operations#Exceptions_to_the_standard">http://en.wikipedia.org/wiki/Order_of_operations</a>).
	 * These constants define the operator precedence styles.
	 */
	public static enum Style {
		/** The most commonly operator precedence, where the unary minus as a lower precedence than the exponentiation.
		 * <br>With this style, used by Google, Wolfram alpha, and many others, -2^2=-4.
		 */
		STANDARD, 
		/** The operator precedence used by Excel, or bash shell script language, where the unary minus as a higher precedence than the exponentiation.
		 * <br>With this style, -2^2=4.
		 */
		EXCEL 
	}
	
	/** A constant that represents pi (3.14159...) */
	public static final Constant PI = new Constant("pi");
	/** A constant that represents e (2.718281...) */
	public static final Constant E = new Constant("e");
	
	/** Returns the smallest integer >= argument */
	public static final Function CEIL = new Function("ceil", 1);
	/** Returns the largest integer <= argument */
	public static final Function FLOOR = new Function("floor", 1);
	/** Returns the closest integer of a number */
	public static final Function ROUND = new Function("round", 1);
	/** Returns the absolute value of a number */
	public static final Function ABS = new Function("abs", 1);

	/** Returns the trigonometric sine of an angle. The angle is expressed in radian.*/
	public static final Function SINE = new Function("sin", 1);
	/** Returns the trigonometric cosine of an angle. The angle is expressed in radian.*/
	public static final Function COSINE = new Function("cos", 1);
	/** Returns the trigonometric tangent of an angle. The angle is expressed in radian.*/
	public static final Function TANGENT = new Function("tan", 1);
	/** Returns the trigonometric arc-cosine of an angle. The angle is expressed in radian.*/
	public static final Function ACOSINE = new Function("acos", 1);
	/** Returns the trigonometric arc-sine of an angle. The angle is expressed in radian.*/
	public static final Function ASINE = new Function("asin", 1);
	/** Returns the trigonometric arc-tangent of an angle. The angle is expressed in radian.*/
	public static final Function ATAN = new Function("atan", 1);

	/** Returns the hyperbolic sine of a number.*/
	public static final Function SINEH = new Function("sinh", 1);
	/** Returns the hyperbolic cosine of a number.*/
	public static final Function COSINEH = new Function("cosh", 1);
	/** Returns the hyperbolic tangent of a number.*/
	public static final Function TANGENTH = new Function("tanh", 1);

	/** Returns the minimum of n numbers (n>=1) */
	public static final Function MIN = new Function("min", 1, Integer.MAX_VALUE);
	/** Returns the maximum of n numbers (n>=1) */
	public static final Function MAX = new Function("max", 1, Integer.MAX_VALUE);
	/** Returns the sum of n numbers (n>=1) */
	public static final Function SUM = new Function("sum", 1, Integer.MAX_VALUE);
	/** Returns the average of n numbers (n>=1) */
	public static final Function AVERAGE = new Function("avg", 1, Integer.MAX_VALUE);

	/** Returns the natural logarithm of a number */
	public static final Function LN = new Function("ln", 1);
	/** Returns the decimal logarithm of a number */
	public static final Function LOG = new Function("log", 1);
	
	/** Returns a pseudo random number */
	public static final Function RANDOM = new Function("random", 0);

	/** The negate unary operator in the Excel like operator precedence.*/
	public static final Operator NEGATE_HIGH = new Operator("-", 1, Operator.Associativity.RIGHT, 9);
	/** The exponentiation operator.*/
	public static final Operator EXPONENT = new Operator("^", 2, Operator.Associativity.LEFT, 8);
	/** The negate unary operator in the standard operator precedence.*/
	public static final Operator NEGATE = new Operator("-", 1, Operator.Associativity.RIGHT, 7);
	/** The multiplication operator.*/
	public static final Operator MULTIPLY = new Operator("*", 2, Operator.Associativity.LEFT, 6);
	/** The division operator.*/
	public static final Operator DIVIDE = new Operator("/", 2, Operator.Associativity.LEFT, 6);
	/** The <a href="http://en.wikipedia.org/wiki/Modulo_operation">modulo operator</a>.*/
	public static final Operator MODULO = new Operator("%", 2, Operator.Associativity.LEFT, 6);
	/** The substraction operator.*/
	public static final Operator MINUS = new Operator("-", 2, Operator.Associativity.LEFT, 5);
	/** The addition operator.*/
	public static final Operator PLUS = new Operator("+", 2, Operator.Associativity.LEFT, 5);
	/** The Relational LessThan operator.*/
	public static final Operator LESSTHAN = new Operator("<", 2, Operator.Associativity.LEFT, 4);
	/** The Relational GreaterThan operator.*/
	public static final Operator GREATERTHAN = new Operator(">", 2, Operator.Associativity.LEFT, 4);
	/** The Relational LessThan operator.*/
	public static final Operator LESSTHANEQUAL = new Operator("<=", 2, Operator.Associativity.LEFT, 4);
	/** The Relational GreaterThan operator.*/
	public static final Operator GREATERTHANEQUAL = new Operator(">=", 2, Operator.Associativity.LEFT, 4);
	/** The Logical EQUAL TO operator.*/
	public static final Operator EQUALTO = new Operator("==", 2, Operator.Associativity.LEFT, 3);
	/** The Logical NOT EQUAL TO operator.*/
	public static final Operator NOTEQUALTO = new Operator("!=", 2, Operator.Associativity.LEFT, 3);
	/** The Logical AND operator.*/
	public static final Operator AND = new Operator("&&", 2, Operator.Associativity.LEFT, 2);
	/** The Logical OR operator.*/
	public static final Operator OR = new Operator("||", 2, Operator.Associativity.LEFT, 2);

	/** The standard whole set of predefined operators */
	private static final Operator[] OPERATORS = new Operator[]{NEGATE, MINUS, PLUS, MULTIPLY, DIVIDE, EXPONENT, MODULO,
																EQUALTO, NOTEQUALTO, AND, OR, LESSTHAN, GREATERTHAN, LESSTHANEQUAL, GREATERTHANEQUAL};
	/** The excel like whole set of predefined operators */
	private static final Operator[] OPERATORS_EXCEL = new Operator[]{NEGATE_HIGH, MINUS, PLUS, MULTIPLY, DIVIDE, EXPONENT, MODULO};
	/** The whole set of predefined functions */
	private static final Function[] FUNCTIONS = new Function[]{SINE, COSINE, TANGENT, ASINE, ACOSINE, ATAN, SINEH, COSINEH, TANGENTH, MIN, MAX, SUM, AVERAGE, LN, LOG, ROUND, CEIL, FLOOR, ABS, RANDOM};
	/** The whole set of predefined constants */
	private static final Constant[] CONSTANTS = new Constant[]{PI, E};
	
	private static Parameters DEFAULT_PARAMETERS;
	private static final NumberFormat FORMATTER = NumberFormat.getNumberInstance(Locale.US);
	
	/** Gets a copy of DoubleEvaluator standard default parameters.
	 * <br>The returned parameters contains all the predefined operators, functions and constants.
	 * <br>Each call to this method create a new instance of Parameters. 
	 * @return a Paramaters instance
	 * @see Style
	 */
	public static Parameters getDefaultParameters() {
		return getDefaultParameters(Style.STANDARD);
	}
	
	/** Gets a copy of DoubleEvaluator default parameters.
	 * <br>The returned parameters contains all the predefined operators, functions and constants.
	 * <br>Each call to this method create a new instance of Parameters. 
	 * @return a Paramaters instance
	 */
	public static Parameters getDefaultParameters(Style style) {
		Parameters result = new Parameters();
		result.addOperators(style==Style.STANDARD?Arrays.asList(OPERATORS):Arrays.asList(OPERATORS_EXCEL));
		result.addFunctions(Arrays.asList(FUNCTIONS));
		result.addConstants(Arrays.asList(CONSTANTS));
		result.addFunctionBracket(BracketPair.PARENTHESES);
		result.addExpressionBracket(BracketPair.PARENTHESES);
		return result;
	}

	private static Parameters getParameters() {
		if (DEFAULT_PARAMETERS == null) {
			DEFAULT_PARAMETERS = getDefaultParameters();
		}
		return DEFAULT_PARAMETERS;
	}
	
	/** Constructor.
	 * <br>This default constructor builds an instance with all predefined operators, functions and constants. 
	 */
	public ObjectEvaluator() {
		this(getParameters());
	}

	/** Constructor.
	 * <br>This constructor can be used to reduce the set of supported operators, functions or constants,
	 * or to localize some function or constant's names.
	 * @param parameters The parameters of the evaluator.
	 */
	public ObjectEvaluator(Parameters parameters) {
		super(parameters);
	}

	@Override
	protected Object toValue(String literal, Object evaluationContext) {
//		return literal;
		ParsePosition p = new ParsePosition(0);
		Number result = FORMATTER.parse(literal, p);
		if (p.getIndex()==0 || p.getIndex()!=literal.length()) {
			//throw new IllegalArgumentException(literal+" is not a number");
			literal = literal.replace("\"", "");
			literal = literal.replace("'", "");
			//System.out.println("toValue(): return value: " + literal);
			return literal;
		}
		return result.doubleValue();
	}
	
	/* (non-Javadoc)
	 * @see net.astesana.javaluator.AbstractEvaluator#evaluate(net.astesana.javaluator.Constant)
	 */
	@Override
	protected Object evaluate(Constant constant, Object evaluationContext) {
		if (PI.equals(constant)) {
			return Math.PI;
		} else if (E.equals(constant)) {
			return Math.E;
		} else {
			return super.evaluate(constant, evaluationContext);
		}
	}

	/* (non-Javadoc)
	 * @see net.astesana.javaluator.AbstractEvaluator#evaluate(net.astesana.javaluator.Operator, java.util.Iterator)
	 */
	@Override
	protected Object evaluate(Operator operator, Iterator<Object> operands, Object evaluationContext) {
		
		try {
		if (NEGATE.equals(operator) || MINUS.equals(operator) || PLUS.equals(operator) 
				|| MULTIPLY.equals(operator) || DIVIDE.equals(operator) 
				|| EXPONENT.equals(operator) || MODULO.equals(operator)
				|| LESSTHAN.equals(operator) || LESSTHANEQUAL.equals(operator)
				|| GREATERTHAN.equals(operator) || GREATERTHANEQUAL.equals(operator)) {
			return evaluateArithmaticOperator(operator, operands, evaluationContext);
		} else if (EQUALTO.equals(operator) || NOTEQUALTO.equals(operator) ) {
			return evaluateEqualityOperator(operator, operands, evaluationContext);
		} else if (AND.equals(operator) || OR.equals(operator) ) {
			return evaluateLogicalOperator(operator, operands, evaluationContext);
		} else {
			return super.evaluate(operator, operands, evaluationContext);
		}
		}catch (Exception e){
			//e.printStackTrace();
			throw new IllegalArgumentException("IllegalArgumentException! Invalid Expression");
		}
	}

	private Object evaluateLogicalOperator(Operator operator,
			Iterator<Object> operands, Object evaluationContext) {
		if (AND.equals(operator)) {
			return (Boolean)operands.next() && (Boolean)operands.next();
		} else if (OR.equals(operator)) {
			return (Boolean)operands.next() || (Boolean)operands.next();
		}
		return null;
	}

	private Object evaluateEqualityOperator(Operator operator,
			Iterator<Object> operands, Object evaluationContext) {
		if (EQUALTO.equals(operator)) {
			return operands.next().equals(operands.next());
		} else if (NOTEQUALTO.equals(operator)) {
			return !(operands.next().equals(operands.next()));
		}
		return null;
	}

	private Object evaluateArithmaticOperator(Operator operator,
			Iterator<Object> operands, Object evaluationContext)  {
		if (NEGATE.equals(operator) || NEGATE_HIGH.equals(operator)) {
			return -(Double.valueOf(operands.next().toString()) );
		} else if (MINUS.equals(operator)) {
			return Double.valueOf(operands.next().toString())  - Double.valueOf(operands.next().toString()) ;
		} else if (PLUS.equals(operator)) {
			return Double.valueOf(operands.next().toString())  + Double.valueOf(operands.next().toString()) ;
		} else if (MULTIPLY.equals(operator)) {
			return Double.valueOf(operands.next().toString())  * Double.valueOf(operands.next().toString()) ;
		} else if (DIVIDE.equals(operator)) {
			return Double.valueOf(operands.next().toString())  / Double.valueOf(operands.next().toString()) ;
		} else if (EXPONENT.equals(operator)) {
			return Math.pow(Double.valueOf(operands.next().toString()) ,Double.valueOf(operands.next().toString()) );
		} else if (MODULO.equals(operator)) {
			return Double.valueOf(operands.next().toString())  % Double.valueOf(operands.next().toString()) ;
		} else if (LESSTHAN.equals(operator)) {
			return Double.valueOf(operands.next().toString()) < Double.valueOf(operands.next().toString());
		} else if (LESSTHANEQUAL.equals(operator)) {
			return Double.valueOf(operands.next().toString())  <= Double.valueOf(operands.next().toString()) ;
		} else if (GREATERTHAN.equals(operator)) {
			return Double.valueOf(operands.next().toString())  > Double.valueOf(operands.next().toString()) ;
		} else if (GREATERTHANEQUAL.equals(operator)) {
			return Double.valueOf(operands.next().toString())  >= Double.valueOf(operands.next().toString()) ;
		} else {
			throw new IllegalArgumentException();
		}
			
	}

	/* (non-Javadoc)
	 * @see net.astesana.javaluator.AbstractEvaluator#evaluate(net.astesana.javaluator.Function, java.util.Iterator)
	 */
	@Override
	protected Object evaluate(Function function, Iterator<Object> arguments, Object evaluationContext) {
		Double result;
		if (ABS.equals(function)) {
			result = Math.abs((Double) arguments.next());
		} else if (CEIL.equals(function)) {
			result = Math.ceil((Double) arguments.next());
		} else if (FLOOR.equals(function)) {
			result = Math.floor((Double) arguments.next());
		} else if (ROUND.equals(function)) {
			Double arg = (Double)arguments.next();
			if (arg==Double.NEGATIVE_INFINITY || arg==Double.POSITIVE_INFINITY) {
				result = arg;
			} else {
				result = (double) Math.round(arg);
			}
		} else if (SINEH.equals(function)) {
			result = Math.sinh((Double)arguments.next());
		} else if (COSINEH.equals(function)) {
			result = Math.cosh((Double)arguments.next());
		} else if (TANGENTH.equals(function)) {
			result = Math.tanh((Double)arguments.next());
		} else if (SINE.equals(function)) {
			result = Math.sin((Double)arguments.next());
		} else if (COSINE.equals(function)) {
			result = Math.cos((Double)arguments.next());
		} else if (TANGENT.equals(function)) {
			result = Math.tan((Double)arguments.next());
		} else if (ACOSINE.equals(function)) {
			result = Math.acos((Double)arguments.next());
		} else if (ASINE.equals(function)) {
			result = Math.asin((Double)arguments.next());
		} else if (ATAN.equals(function)) {
			result = Math.atan((Double)arguments.next());
		} else if (MIN.equals(function)) {
			result = (Double)arguments.next();
			while (arguments.hasNext()) {
				result = Math.min(result, (Double)arguments.next());
			}
		} else if (MAX.equals(function)) {
			result = (Double)arguments.next();
			while (arguments.hasNext()) {
				result = Math.max(result, (Double)arguments.next());
			}
		} else if (SUM.equals(function)) {
			result = 0.;
			while (arguments.hasNext()) {
				result = result + (Double)arguments.next();
			}
		} else if (AVERAGE.equals(function)) {
			result = 0.;
			int nb = 0;
			while (arguments.hasNext()) {
				result = result + (Double)arguments.next();
				nb++;
			}
			result = result/nb;
		} else if (LN.equals(function)) {
			result = Math.log((Double)arguments.next());
		} else if (LOG.equals(function)) {
			result = Math.log10((Double)arguments.next());
		} else if (RANDOM.equals(function)) {
			result = Math.random();
		} else {
			result = (Double) super.evaluate(function, arguments, evaluationContext);
		}
		errIfNaN(result, function);
		return result;
	}
	
	private void errIfNaN(Double result, Function function) {
		if (result.equals(Double.NaN)) {
			throw new IllegalArgumentException("Invalid argument passed to "+function.getName());
		}
	}
}
