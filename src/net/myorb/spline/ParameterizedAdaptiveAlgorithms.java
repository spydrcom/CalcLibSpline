
package net.myorb.spline;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;

import net.myorb.math.computational.Parameterization;

import net.myorb.math.Function;

/**
 * adjust spline segment sizes until sought precision is reached or thresholds hit
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class ParameterizedAdaptiveAlgorithms <T>
	extends ParameterizedComponentSpline <T>
{


	public ParameterizedAdaptiveAlgorithms
		(
			Function <T> f,
			ExpressionComponentSpaceManager <T> mgr,
			Parameterization configuration,
			SplineSupport support
		)
	{
		super (f, mgr, configuration, support);
		this.initializeParamteres ();
	}


	/**
	 * read parameters from configuration object
	 */
	public void initializeParamteres ()
	{
		this.delta = getValue ("initdelta", delta);
		this.lo = getValue ("initlo", lo); this.ubound = getValue ("ubound", ubound);
		this.convergenceLevel = getValue ("converge", convergenceLevel);
		this.segmentMax = (int) getValue ("maxsegments", segmentMax);
		this.deltaMinimum = getValue ("mindelta", deltaMinimum);
		this.errorMax = getValue ("maxerror", errorMax);
		TRACING = getValue ("trace", 0) != 0;
	}
	protected ModelStack stack = new ModelStack ();
	protected double lo = 0, hi = 10, delta = 10, ubound = 100; 
	protected double convergenceLevel = 1E-15;
	protected double deltaMinimum = 1E-5;
	protected double errorMax = 1E-8;
	protected int segmentMax = 20;
	protected int segments = 0;


	/**
	 * @param named the name of the parameter
	 * @param defaultValue value to be used if not configured
	 * @return parameter value or default if absent
	 */
	public double getValue (String named, double defaultValue)
	{
		return configuration.getValue (named, defaultValue).doubleValue ();
	}


	/**
	 * produce segments based on initialization configuration
	 */
	public void initializeSegements ()
	{
		while (true)
		{
			if ((hi = lo + delta) > ubound) { hi = ubound; }
			checkSegmentCount (); stack.add (lastSegment = segment (lo, hi));
			if (lastSegment.getMax () < convergenceLevel) return;
			if (hi >= ubound) return;
			lo = hi;
		}
	}
	public void checkSegmentCount ()
	{
		if (segments++ > segmentMax)
		{ throw new RuntimeException ("Segment max exceeded"); }
	}
	protected SegmentModels lastSegment;


	/**
	 * adjust segment sizes to meet configuration criteria
	 */
	public void adapt ()
	{
		SegmentModels seg; double l, h;

		while (stack.size () > 0)
		{

			seg = stack.remove (stack.size () - 1);
			l = seg.getSegmentLo (); h = seg.getSegmentHi ();
			if (TRACING) seg.trace ();

			if (seg.maxError () < errorMax)
			{
				if (TRACING) System.out.println (" DROP ");
				addSegment (seg, l, h);
			}
			else if (seg.getSegmentDelta() < deltaMinimum)
			{
				if (TRACING) System.out.println (" DELTA ");
				addSegment (seg, l, h);
			}
			else if (segments >= segmentMax)
			{
				if (TRACING) System.out.println (" MAX ");
				addSegment (seg, l, h);
			}
			else
			{
				segments++;
				if (TRACING) System.out.println (" SPLIT ");
				stack.addAll (split (seg));
			}

		}
	}


	/**
	 * split the models into halves
	 * @param models the models to be split
	 * @return a list of the half sized segments
	 */
	public ModelStack split (SegmentModels models)
	{
		ModelStack modelStack = new ModelStack ();
		splitSegment (models, modelStack);

		if (TRACING)
		{
			System.out.println ("---");
			modelStack.get(0).display ();
			modelStack.get(1).display ();
			System.out.println ("---");
		}

		return modelStack;
	}


	/**
	 * display final segment count
	 *  and maximum error of final segments
	 */
	public void configurationDisplay ()
	{
		System.out.println ();
		System.out.println ("---");
		System.out.println ("segments: " + segments);
		System.out.println ("segment max: " + lastSegment.getMax ());
		System.out.println ("---");
		System.out.println ();
	}


	/**
	 * report details of adapted spline segments
	 */
	public void configurationDetails ()
	{
		if (!TRACING) return;
		System.out.println ();
		System.out.println ("Sorted Segments");
		System.out.println ("===============");
		for (ComponentSpline cs : sorted ())
		{ cs.traceSplineSegment (); }
	}


	/**
	 * @param l the lo end of the range
	 * @param h the hi end of the range
	 * @return the models for the segments
	 */
	public SegmentModels segment (double l, double h)
	{
		SegmentModels models = generateModels (l, h);
		models.display ();
		return models;
	}


	/**
	 * initialize segments and execute adaption algorithms
	 */
	public void run ()
	{
		this.initializeSegements (); if (TRACING) configurationDisplay ();
		this.adapt (); if (TRACING) configurationDetails ();
		this.addComponentSplinesToSegmentList ();
	}


}

