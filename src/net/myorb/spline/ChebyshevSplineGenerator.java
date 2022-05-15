
package net.myorb.spline;

import net.myorb.math.computational.Spline;
import net.myorb.math.computational.Parameterization;
import net.myorb.math.computational.splines.Representation;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;

import net.myorb.math.Function;

/**
 * spline factory for generic data type quadrature 
 *  using adaptive spline algorithm and Chebyshev T-Polynomial calculus
 * @param <T> data type on which operations are to be executed
 * @author Michael Druckman
 */
public class ChebyshevSplineGenerator <T>
	extends ParameterizedAdaptiveAlgorithms <T>
	implements Spline.Operations <T>
{


	public ChebyshevSplineGenerator
		(
			Function <T> f,
			ExpressionComponentSpaceManager <T> mgr,
			Parameterization configuration
		)
	{
		super
		(
			f, mgr, configuration,
			getChebyshevSplineSupport (configuration)
		);
	}
	public static SplineSupport getChebyshevSplineSupport 
		(
			Parameterization configuration
		)
	{
		return new ChebyshevPolynomialSplineSupport (configuration);
	}


	/**
	 * identify domain to cover for spline
	 * @param lo the lo end of the range
	 * @param hi the hi end of the range
	 */
	public void generateSpline (double lo, double hi)
	{
		this.lo = lo;
		this.ubound = hi;
		this.run ();
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Spline.Operations#getRepresentation()
	 */
	public Representation getRepresentation ()
	{
		return this;
	}


}

