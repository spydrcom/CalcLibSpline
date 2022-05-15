
package net.myorb.spline;

import net.myorb.math.computational.Spline;
import net.myorb.math.computational.splines.CubicSplineInterpreter;
import net.myorb.math.computational.splines.SplineMechanisms;
import net.myorb.math.computational.Parameterization;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.Function;

/**
 * spline factory for generic data type quadrature.
 *  CubicSplineGenerator is used to best fit the function.
 *  factory object uses adaptive spline algorithm
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class CubicSplineFactory <T> implements Spline.Factory <T>, Environment.AccessAcceptance <T>
{

	/**
	 * allow construction with no parameter.
	 *  caller must use AccessAcceptance interface to set data type context
	 */
	public CubicSplineFactory () {}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Environment.AccessAcceptance#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment <T> environment)
	{
		this.spline = new CubicSplineInterpreter ();
		Environment.provideAccess (this.spline, environment);
		this.mgr = (ExpressionComponentSpaceManager <T>) environment.getSpaceManager ();
		this.environment = environment;
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
		CubicSplineGenerator <T> factory =
			new CubicSplineGenerator <>
			(
				f, spline, mgr, configuration
			);
		environment.provideAccessTo (factory);
		factory.generateSpline (lo, hi);
		return factory;
	}
	protected ExpressionComponentSpaceManager <T> mgr;
	protected Environment <T> environment;
	protected SplineMechanisms spline;

}

