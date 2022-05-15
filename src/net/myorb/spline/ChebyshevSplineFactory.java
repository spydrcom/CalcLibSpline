
package net.myorb.spline;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.computational.Spline;

import net.myorb.math.Function;

/**
 * spline factory for generic data type quadrature.
 *  ChebyshevSplineGenerator is used to best fit the function.
 *  factory object uses adaptive spline algorithm
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class ChebyshevSplineFactory <T> implements Spline.Factory <T>, Environment.AccessAcceptance <T>
{

	/**
	 * allow construction with no parameter.
	 *  caller must use AccessAcceptance interface to set data type context
	 */
	public ChebyshevSplineFactory () {}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Environment.AccessAcceptance#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment<T> environment)
	{
		this.mgr = (ExpressionComponentSpaceManager <T>) environment.getSpaceManager ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Spline.Factory#generateSpline(net.myorb.math.Function, double, double, net.myorb.math.computational.Parameterization)
	 */
	public Spline.Operations <T> generateSpline
		(
			Function <T> f, double lo, double hi,
			Parameterization configuration
		)
	{
		ChebyshevSplineGenerator <T> factory = new ChebyshevSplineGenerator <> (f, mgr, configuration);
		SplineSupport support = ChebyshevSplineGenerator.getChebyshevSplineSupport (configuration);
		factory.initFrom (mgr, configuration, support); factory.setFunction (f);
		factory.generateSpline (lo, hi);
		return factory;
	}
	protected ExpressionComponentSpaceManager <T> mgr;

}
