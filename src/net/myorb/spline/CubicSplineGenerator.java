
package net.myorb.spline;

import net.myorb.math.Polynomial;

import net.myorb.math.computational.Spline;

import net.myorb.math.computational.Parameterization;

import net.myorb.math.computational.splines.Representation;
import net.myorb.math.computational.splines.SplineMechanisms;
import net.myorb.math.computational.splines.SegmentOperations;
import net.myorb.math.computational.splines.SegmentRepresentation;
import net.myorb.math.computational.splines.CubicSplineInterpreter;
import net.myorb.math.computational.splines.CubicSplinePolynomial;
import net.myorb.math.computational.splines.CubicSpline;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.data.abstractions.Function;

import net.myorb.math.SpaceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * spline factory for generic data type quadrature 
 *  using adaptive spline algorithm and Cubic Spline algorithms
 * @param <T> data type on which operations are to be executed
 * @author Michael Druckman
 */
public class CubicSplineGenerator <T> extends SegmentOperations <T>
	implements Spline.Operations <T>, Environment.AccessAcceptance <T>, Representation
{


	public CubicSplineGenerator
		(
			Function <T> f,
			SplineMechanisms spline,
			ExpressionComponentSpaceManager <T> mgr,
			Parameterization configuration
		)
	{
		super (mgr, spline);
		this.segments = new ArrayList <SegmentRepresentation> ();
		this.spline = new CubicSplinePolynomial <T> (mgr);
		this.configuration = configuration;
		this.mgr = mgr;
		this.f = f;
	}
	protected ExpressionComponentSpaceManager <T> mgr;
	protected CubicSplinePolynomial <T> spline;
	

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Environment.AccessAcceptance#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment <T> environment)
	{
		this.mgr = (ExpressionComponentSpaceManager <T>) environment.getSpaceManager ();
		this.environment = environment;
	}
	protected Environment <T> environment;


	/**
	 * identify domain to cover for spline
	 * @param lo the lo end of the range
	 * @param hi the hi end of the range
	 */
	public void generateSpline (double lo, double hi)
	{ setInterpolation (spline.interpolationFor (f, getKnotPoints (lo, hi), getDelta ())); }
	protected Parameterization configuration;
	protected Function <T> f;


	/**
	 * identify values of the knot points
	 * @param lo the lo end value of the range
	 * @param hi the hi end value of the range
	 * @return a list of values to use as knot points
	 */
	List <T> getKnotPoints (double lo, double hi)
	{
		T every = mgr.convertFromDouble
				(configuration.getValue ("every").doubleValue ());
		T x = mgr.convertFromDouble (lo), upTo = mgr.convertFromDouble (hi);
		List <T> knots = new ArrayList <T> ();
		while ( ! mgr.lessThan (upTo, x) )
		{
			knots.add (x);
			x = mgr.add (x, every);
		}
		return knots;
	}

	/**
	 * @return read delta parameter from configuration
	 */
	T getDelta ()
	{
		return mgr.convertFromDouble (configuration.getValue ("delta").doubleValue ());
	}
	

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x) { return interpolation.eval (x); }


	/**
	 * capture the segment data for the spline
	 * @param interpolation the description of the spline
	 */
	public void setInterpolation (CubicSpline.Interpolation <T> interpolation)
	{
		CubicSplineSegment <T> s; Function <T> f;
		for (CubicSpline.Knot <T> k : interpolation.getKnots ())
		{
			segments.add (s = new CubicSplineSegment <T> (k, mgr));
			if ((f =  k.getComputer ()) == null) continue;
			s.c.add (coefficientsFor (f));
		}
		this.interpolation = interpolation;
	}
	protected CubicSpline.Interpolation <T> interpolation;


	/**
	 * @param f power function built for spline segment
	 * @return the coefficients for the segment
	 */
	public List <Double> coefficientsFor (Function <T> f)
	{
		List <Double> c = new ArrayList <Double> ();
		Polynomial.PowerFunction <T> pf = (Polynomial.PowerFunction <T>) f;
		for (T t : pf.getCoefficients ()) { c.add (mgr.convertToDouble (t)); }
		return c;
	}


	/* (non-Javadoc)
	 * @see net.myorb.spline.ParameterizedComponentSpline#getSegmentList()
	 */
	public List<SegmentRepresentation> getSegmentList () {
		return segments;
	}
	protected List<SegmentRepresentation> segments;


	/* (non-Javadoc)
	 * @see net.myorb.spline.ParameterizedComponentSpline#getInterpretation()
	 */
	public String getInterpretation () {
		return CubicSplineInterpreter.class.getCanonicalName ();
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentOperations#getSpaceManager()
	 */
	public SpaceManager<T> getSpaceManager () {
		return mgr;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentOperations#getSpaceDescription()
	 */
	public SpaceDescription<T> getSpaceDescription () {
		return mgr;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Spline.Operations#getRepresentation()
	 */
	public Representation getRepresentation ()
	{
		return this;
	}


}


/**
 * representation for properties of a segment of a cubic spline
 * @param <T> data type on which operations are to be executed
 */
class CubicSplineSegment <T> implements SegmentRepresentation
{


	CubicSplineSegment
		(
			CubicSpline.Knot <T> knot,
			ExpressionComponentSpaceManager <T> mgr
		)
	{
		this.c = new ArrayList < List < Double > > ();
		this.knot = knot;
		this.mgr = mgr;
	}
	protected ExpressionComponentSpaceManager <T> mgr;
	protected CubicSpline.Knot <T> knot;
	protected List < List <Double> > c;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getComponentCount()
	 */
	public int getComponentCount ()
	{
		return 1;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getCoefficientsFor(int)
	 */
	public List<Double>
	getCoefficientsFor (int component)
	{
		if (c.size () == 0) return null;
		return c.get (0);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getCoefficients()
	 */
	public List < List < Double > > getCoefficients ()
	{
		return c;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#setCoefficients(java.util.List)
	 */
	public void setCoefficients
	(List < List < Double > > componentCoefficients)
	{
		this.c = componentCoefficients;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getSegmentLo()
	 */
	public double getSegmentLo ()
	{
		if (knot.prior () == null) return 0;
		return mgr.convertToDouble (knot.prior ().t ());
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getSegmentHi()
	 */
	public double getSegmentHi ()
	{
		return mgr.convertToDouble (knot.t ());
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getSegmentDelta()
	 */
	public double getSegmentDelta ()
	{
		return 0.01;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getSegmentError()
	 */
	public double getSegmentError ()
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getSegmentSlope()
	 */
	public double getSegmentSlope ()
	{
		return 0.0;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getUnitSlope()
	 */
	public double getUnitSlope ()
	{
		return 0.0;
	}
	
}

