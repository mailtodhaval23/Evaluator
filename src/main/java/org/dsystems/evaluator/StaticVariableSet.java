package org.dsystems.evaluator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/** A static variable set.
 * <br>Here, static means that the values of variables are set before starting to evaluate the expressions.
 * @param <T> The type of the values of the variable (the one handled by the evaluator).
 * @author Jean-Marc Astesana
 * @see <a href="../../../license.html">License information</a>
 */
public class StaticVariableSet<T> implements AbstractVariableSet<T>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Map<String, T> varToValue;
	
	/** Constructor.
	 * <br>Builds a new empty variable set.
	 */
	public StaticVariableSet() {
		this.varToValue = new HashMap<String, T>();
	}

	public StaticVariableSet(Map<String, T> varToValue) {
		super();
		this.varToValue = varToValue;
	}
	
	/** Gets the value of a variable.
	 * @param variableName The name of the variable.
	 * @return The value of the variable.
	 */
	public T get(String variableName) {
		return this.varToValue.get(variableName);
	}
	

	/** Sets a variable value.
	 * @param variableName The variable name
	 * @param value The variable value (null to remove a variable from the set).
	 */
	public void set(String variableName, T value) {
		this.varToValue.put(variableName, value);
	}
}
