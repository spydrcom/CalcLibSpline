
package net.myorb.spline;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;

/**
 * a spline factory specifically for complex number functions 
 * @author Michael Druckman
 */
public class ComplexSplineFactory
	extends GenericFactory < ComplexValue <Double> >
{

	/**
	 * constructor must identify data type manager to super class
	 */
	public ComplexSplineFactory ()
	{
		super (new ExpressionComplexFieldManager ());
	}

}
