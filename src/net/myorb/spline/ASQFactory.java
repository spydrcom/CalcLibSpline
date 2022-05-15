
package net.myorb.spline;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

/**
 * spline factory for real numbers quadrature 
 *  using adaptive spline algorithm
 * @author Michael Druckman
 */
public class ASQFactory extends GenericFactory <Double>
{

	/**
	 * constructor must identify data type manager to super class
	 */
	public ASQFactory ()
	{
		super (new ExpressionFloatingFieldManager ());
	}

}

