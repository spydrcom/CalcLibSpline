
package net.myorb.spline;

import net.myorb.math.matrices.Vector;

import net.myorb.math.computational.VC31LUD;
import net.myorb.math.computational.Regression;
import net.myorb.math.computational.Parameterization;
import net.myorb.math.computational.splines.ChebyshevSpline;

import net.myorb.spline.ParameterizedComponentSpline.Sequence;

import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.DataSequence;

/**
 * Spline Support implementation using Chebyshev T-Polynomial specifics
 * @author Michael Druckman
 */
public class ChebyshevPolynomialSplineSupport
		extends ChebyshevSpline  implements SplineSupport
{


	/*
	 * values derived from
	 * the Chebyshev descriptive constants
	 * found in ChebyshevSpline class
	 */
	public static final double SPLINE_DELTA = SPLINE_RANGE / SPLINE_SPACES;


	/*
	 * provide constant sequences of domain values used to build samples for spline evaluations
	 */

	public static final Sequence xAxisComb = getSplineDomain (SPLINE_LO + SPLINE_DELTA/2);
	public static final Sequence xAxis = getSplineDomain (SPLINE_LO);

	public static Sequence getSplineDomain (double starting)
	{
		return new Sequence
		(
			DataSequence.evenlySpaced
			(
				starting, SPLINE_DELTA, SPLINE_TICKS, realManager
			)
		);
	}

	/* (non-Javadoc)
	 * @see net.myorb.spline.SplineSupport#copyDomian(net.myorb.spline.ParameterizedComponentSpline.Sequence)
	 */
	public void copyDomian (Sequence to)
	{
		/* the sample sets will always have a size matching the tick count of the optimal section size */
		for (int i = 0; i < SPLINE_TICKS; i++) { to.add (xAxis.at (i)); to.add (xAxisComb.at (i)); }
	}


	/*
	 * constants provided directly by the spline description
	 */

	/* (non-Javadoc)
	 * @see net.myorb.spline.SplineSupport#getSplineLo()
	 */
	public double getSplineLo () { return SPLINE_LO; }

	/* (non-Javadoc)
	 * @see net.myorb.spline.SplineSupport#getSplineDelta()
	 */
	public double getSplineDelta () { return SPLINE_DELTA; }

	/* (non-Javadoc)
	 * @see net.myorb.spline.SplineSupport#getSplineTicks()
	 */
	public int getSplineTicks () { return SPLINE_TICKS; }


	/*
	 * values derived for evaluated functions based on spline constants
	 */

	/* (non-Javadoc)
	 * @see net.myorb.spline.SplineSupport#getSplineSlope(double, double)
	 */
	public double getSplineSlope (double hi, double lo)
	{
		return deltaFor (lo, hi) / SPLINE_DELTA;
	}

	/* (non-Javadoc)
	 * @see net.myorb.spline.SplineSupport#getSlope(double, double)
	 */
	public double getSlope (double hi, double lo)
	{
		return (hi - lo) / SPLINE_RANGE;
	}

	/* (non-Javadoc)
	 * @see net.myorb.spline.SplineSupport#deltaFor(double, double)
	 */
	public double deltaFor (double lo, double hi)
	{
		return (hi - lo) / SPLINE_SPACES;
	}


	/**
	 * @param configuration a hash of parameter value for configuration
	 */
	public ChebyshevPolynomialSplineSupport
		(
			Parameterization configuration
		)
	{
		this.regression = new Regression <Double> (realManager);
		this.lud = new VC31LUD (configuration);
	}
	protected Regression <Double> regression;


	/*
	 * evaluate regression quality
	 */

	/**
	 * get regression model
	 * @param b vector to be solved
	 * @param samples data that should be within the fit model
	 * @return the regression model from the evaluation of the solution
	 */
	public Regression.Model <Double> performRegression
		(
			Vector <Double> b, DataSequence2D <Double> samples
		)
	{
		/*
		 * triangular matrix decomposition used to fit T-Polynomial to segment
		 */
		return regression.useChebyshevModel
			(
				lud.solve (b), samples
			);
	}
	protected VC31LUD lud;


	/* (non-Javadoc)
	 * @see net.myorb.spline.SplineSupport#getSolutionVector()
	 */
	public Vector <Double> getSolutionVector ()
	{
		/*
		 * segment samples taken in chunks sized in spline ticks
		 */
		return new Vector <Double> (SPLINE_TICKS, realManager);
	}


	/* (non-Javadoc)
	 * @see net.myorb.spline.SplineSupport#getSolutionVectorFor(net.myorb.spline.ParameterizedComponentSpline.Sequence)
	 */
	public Vector <Double> getSolutionVectorFor (Sequence samples)
	{
		Vector <Double> v = getSolutionVector ();
		for (int i = 1; i <= SPLINE_TICKS; i++)
		{ v.set (i, samples.at (i-1)); }
		return v;
	}


}

