
package net.myorb.spline;

import net.myorb.math.matrices.Vector;

import net.myorb.math.computational.splines.*;
import net.myorb.math.computational.Regression;
import net.myorb.math.computational.Parameterization;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;

import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.DataSequence;

import net.myorb.math.Function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * mechanisms for construction of spline segments for multiple component data spaces
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class ParameterizedComponentSpline <T>
	extends SegmentOperations <T> implements Representation
{


	public static boolean TRACING = false;


	/**
	 * identify lo, delta, and max for a linear space
	 */
	public interface DataContext extends SegmentRepresentation
	{
		/**
		 * @param newValue value to note as hi
		 */
		void setMax (double newValue);

		/**
		 * @return most recent setting
		 */
		double getMax ();
	}

	/**
	 * meta-data management for spline segments
	 */
	public static class ContextBase extends SegmentParameters implements DataContext
	{
		public void setDelta (double to) { this.delta = to; }
		public void setMax (double newValue) { this.maxIs = newValue; }
		public double getMax () { return maxIs; }
		protected double maxIs = 0;

		public void copyRange (DataContext context)
		{
			this.lo = context.getSegmentLo ();
			this.delta = context.getSegmentDelta ();
			this.hi = context.getSegmentHi ();
		}

		public String formatted ()
		{
			StringBuffer buf = new StringBuffer ();
			buf.append ("CTX LO ").append (lo).append (" DT ").append (delta);
			buf.append (" HI ").append (hi).append (" MAX ").append (maxIs);
			return buf.toString ();
		}

		public void establishRange (double lo, double delta, double hi)
		{ this.lo = lo; this.delta = delta; this.hi = hi; }
	}

	/**
	 * a linear sequence of data points
	 */
	public class DataPoints extends ContextBase
	{
		protected DataSequence <T> data = new DataSequence <T> ();
		public T at (int index) { return data.get (index); }
		public void add (T t) { data.add (t); }
	}

	/**
	 * sequence of single dimensional real numbers
	 */
	public static class Sequence extends ContextBase
	{
		public void add (Double t) { data.add (t); }
		public Double at (int index) { return data.get (index); }
		protected DataSequence <Double> data = new DataSequence <Double> ();
		public double offsetBy (double value) { return lo + value * delta; }
		public Sequence (DataSequence<Double> data) { this.data.addAll (data); }
		public Sequence () {}
	}

	/**
	 * (X, Y) coordinate pairs describing a 2 dimensional function mapping
	 */
	public static class TwoDimSpace extends ContextBase
	{
		public TwoDimSpace (Sequence domain, Sequence range)
		{ data = new DataSequence2D<Double> (domain.data, range.data); }
		public TwoDimSpace (DataSequence2D<Double> sequence) { data = sequence; }

		public Sequence getRange ()
		{
			Sequence seq = new Sequence (data.yAxis);
			seq.copyRange (this);
			return seq;
		}

		public DataSequence2D<Double> getData () { return data; }
		protected DataSequence2D <Double> data = new DataSequence2D <> ();
	}

	/**
	 * component index maps to the spline coefficients
	 * for the polynomial that will cover the component
	 * for the segment range connected to this segment
	 */
	public static class SegmentModels extends ContextBase
	{
		public TwoDimSpace getSpace (int component)
		{
			TwoDimSpace space = new TwoDimSpace
				(models.get (component).adjustedDataSet ());
			space.copyRange (this);
			return space;
		}

		public double maxError ()
		{
			double max = 0.0;
			for (Regression.Model<Double> model : models)
			{ max = Math.max (max, model.computedSSE ()); }
			return max;
		}

		public void trace ()
		{
			System.out.print ("+++ ");
			System.out.print (getSegmentLo ()); System.out.print (" .. ");
			System.out.print (getSegmentHi ()); System.out.print (" * ");
			System.out.print (getSegmentDelta ()); System.out.print (" ERR: ");
			System.out.print (maxError ());
		}

		public void display ()
		{
			if (!TRACING) return;
			System.out.println (); System.out.print (getSegmentLo ());
			System.out.print (" .. "); System.out.print (getSegmentHi ());
			System.out.print ("  ==  "); System.out.println ("SSE: " + maxError ());
			for (int i=0; i<count (); i++) dump ("("+i+")", (DataContext) getSpace (i));
			System.out.println ();
		}

		public static void dump
		(String kind, ParameterizedComponentSpline.DataContext context)
		{
			if (!TRACING) return;
			System.out.print (kind);
			System.out.print (" Lo: "); System.out.print (context.getSegmentLo ());
			System.out.print (" Hi: "); System.out.print (context.getSegmentHi ());
			System.out.print (" Delta: "); System.out.print (context.getSegmentDelta ());
			System.out.print (" Max: "); System.out.println (context.getMax ());
		}

		protected ArrayList < Regression.Model <Double> >
			models = new ArrayList < Regression.Model <Double> > ();
		public List < Regression.Model <Double> > getData () { return models; }
		public Regression.Model <Double> modelFor (int component) { return models.get (component); }
		public int count () { return models.size (); }
	}


	/**
	 * workspace for models not yet verified
	 */
	public static class ModelStack extends ArrayList <SegmentModels>
	{ private static final long serialVersionUID = -6548270615731338960L; }


	/**
	 * collect segment descriptions.
	 * a domain range is mapped to the spline coefficients.
	 */
	public static class ComponentSpline
		extends SegmentParameters implements SegmentRepresentation
	{

		/**
		 * @param models the spline models for the components
		 * @param lo the lo end of the range described by this sequence
		 * @param hi the hi end of the range described by this sequence
		 * @param support a helper for the spline mechanisms
		 */
		public ComponentSpline
			(
				SegmentModels models, double lo, double hi,
				SplineSupport support
			)
		{
			this.support = support;
			this.models = models; this.lo = lo; this.hi = hi;
			this.splineSlope = support.getSplineSlope (hi, lo);
			this.describeRestrictions (models.count ());
			this.traceSplineSegment ();
		}
		protected SplineSupport support;
		protected SegmentModels models;
		protected double splineSlope;

		/**
		 * @return largest SSE found in component models
		 */
		public double maxModelError ()
		{
			double max = 0.0;
			for (Regression.Model<Double> model : models.getData ())
			{
				double sse = model.computedSSE (); 
				max = sse>max? sse: max;
			}
			return max;
		}

		/**
		 * get a model for a specific component
		 * @param component the index identifying a component
		 * @return the model to use for the component
		 */
		public Regression.Model<Double> modelFor (int component)
		{
			return models.modelFor (component);
		}

		/**
		 * covert parameter from function to spline coordinate
		 * @param from function parameter coordinate
		 * @return spline parameter coordinate
		 */
		public double translate (double from)
		{
			return support.getSplineLo () + (from - lo) / splineSlope;
		}

		/**
		 * generate trace for new segment addition to spline
		 */
		public void traceSplineSegment ()
		{
			if (!TRACING) return;
			System.out.print (">> " + lo + " .. " + hi);
			System.out.print (": delta = "); System.out.print (models.getSegmentDelta ());
			System.out.print (": SSE = "); System.out.println (maxModelError ());
		}


		/*
		 * flags for component restrictions
		 */

		/**
		 * @param component index of component
		 * @return TRUE implies restriction
		 */
		public boolean isRestricted (int component)
		{ return restricted[component]; }
		protected boolean[] restricted;

		/**
		 * @param components number of components to represent
		 */
		public void describeRestrictions (int components)
		{
			this.restricted = new boolean[components];
			Arrays.fill (restricted, false);
		}


		/*
		 * implementation of the SegmentRepresentation interface
		 */

		/* (non-Javadoc)
		 * @see net.myorb.math.computational.splines.SegmentParameters#getCoefficients()
		 */
		public List < List <Double> > getCoefficients ()
		{
			List < List <Double> > c = new ArrayList < List <Double> > ();
			for (int i = 0; i < models.count (); i++)
			{ c.add (getCoefficientsFor (i)); }
			return c;
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.computational.splines.SegmentRepresentation#getCoefficientsFor(int)
		 */
		public List<Double> getCoefficientsFor (int component)
		{ return models.getData ().get (component).getCoefficients (); }
		public int getComponentCount () { return models.count (); }
		public double getUnitSlope () { return 1 / splineSlope; }

		/* (non-Javadoc)
		 * @see net.myorb.math.computational.splines.SegmentRepresentation#getSegmentLo()
		 */
		public double getSegmentLo () { return lo; }
		public double getSegmentDelta () { return models.getSegmentDelta (); }
		public double getSegmentSlope () { return support.getSlope (hi, lo); }
		public double getSegmentError () { return maxModelError (); }
		public double getSegmentHi () { return hi; }

	}


	/**
	 * @param f a multi-dimensional unary function
	 * @param mgr a space manager for the data type enabled for component manipulation
	 * @param configuration a hash of configuration parameters
	 * @param support a helper for the spline mechanisms
	 */
	public ParameterizedComponentSpline
		(
			Function <T> f,
			ExpressionComponentSpaceManager <T> mgr,
			Parameterization configuration,
			SplineSupport support
		)
	{
		super (mgr, support);
		this.initFrom (mgr, configuration, support);
		this.setFunction (f);
	}

	/**
	 * @param mgr a space manager for the data type enabled for component manipulation
	 * @param configuration a hash of configuration parameters
	 * @param support a helper for the spline mechanisms
	 */
	public void initFrom
		(
			ExpressionComponentSpaceManager <T> mgr,
			Parameterization configuration,
			SplineSupport support
		)
	{
		this.mgr = mgr;
		this.support = support;
		this.configuration = configuration;
		this.splineSegments = new ArrayList <> ();
		this.components = mgr.getComponentCount ();
	}
	protected Parameterization configuration;
	protected ExpressionComponentSpaceManager <T> mgr;
	protected int components;


	/**
	 * @param f function to be fit
	 */
	public void setFunction (Function <T> f) { this.f = f; }
	protected Function <T> f;


	/**
	 * build a spline 
	 *  for the function over a range
	 * @param lo the lo end of the range
	 * @param hi the hi end of the range
	 */
	public void addSegment (double lo, double hi)
	{
		ComponentSpline spline =
			new ComponentSpline
			(
				generateModels (lo, hi),
				lo, hi, support
			);
		splineSegments.add (spline);
	}
	protected List<ComponentSpline> splineSegments;


	/**
	 * @return sorted list of segments
	 */
	public List<ComponentSpline> sorted ()
	{
		List<ComponentSpline> segs = new ArrayList<ComponentSpline> ();
		segs.addAll (splineSegments);
		segs.sort
		(
			new java.util.Comparator<ComponentSpline> ()
			{
				public int compare (ComponentSpline s1, ComponentSpline s2)
				{
					double s1l = s1.getSegmentLo (), s2l = s2.getSegmentLo ();
					return s1l == s2l ? 0 : s1l < s2l ? -1: 1;
				}
			}
		);
		return segs;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.Representation#getSegmentList()
	 */
	public List<SegmentRepresentation> getSegmentList ()
	{
		List<SegmentRepresentation> segs =
				new ArrayList<SegmentRepresentation> ();
		segs.addAll (sorted ());
		return segs;
	}


	/**
	 * @param models a model for each component
	 * @param lo the lo end of the range covered by these models
	 * @param hi the hi end of the range covered by these models
	 */
	public void addSegment
		(
			SegmentModels models,
			double lo, double hi
		)
	{
		splineSegments.add
		(
			new ComponentSpline (models, lo, hi, support)
		);
	}


	/**
	 * generate segment models
	 * @param lo the lo end of the range
	 * @param hi the hi end of the range
	 * @return the array of models for the segment
	 */
	public SegmentModels generateModels (double lo, double hi)
	{
		DataPoints points = new DataPoints (), combRange = new DataPoints ();
		generateData (points, combRange, lo, support.deltaFor (lo, hi));
		return generateModels (points, combRange);
	}


	/**
	 * @param points data points collection lo + delta
	 * @param combRange data points collection lo + delta/2
	 * @param lo the starting value for the sequence
	 * @param delta the space between samples
	 */
	public void generateData
		(
			DataPoints points, DataPoints combRange,
			double lo, double delta
		)
	{
		calculatePoints (lo + delta/2, delta, combRange);
		calculatePoints (lo, delta, points);
	}


	/**
	 * divide a segment in half
	 * @param models the models of the original range
	 * @param workspace a stack of models for the split segments
	 */
	public void splitSegment (SegmentModels models, ModelStack workspace)
	{
		SegmentModels
			loModels = new SegmentModels (),
			hiModels = new SegmentModels ();
		int tickSpaces = support.getSplineTicks () - 1;
		TwoDimSpace space; Sequence axis, loEnd, hiEnd;
		Sequence combSequenceLo, combSequenceHi;

		DataPoints combRangeLo = new DataPoints (), combRangeHi = new DataPoints ();
		double lo = models.getSegmentLo (), delta = models.getSegmentDelta (), halfDelta = delta / 2;
		double loEndLo = lo + halfDelta, HiEndLo = lo + tickSpaces * delta + halfDelta;

		calculatePoints (loEndLo, delta, combRangeLo);
		calculatePoints (HiEndLo, delta, combRangeHi);

		for (int c = 0; c < mgr.getComponentCount (); c++)
		{

			loEnd = new Sequence (); hiEnd = new Sequence ();
			space = models.getSpace (c); axis = space.getRange ();
			if (TRACING) System.out.println ("AXIS: " + axis.formatted ());
			splitSegment (axis, loEnd, 0, tickSpaces); splitSegment (axis, hiEnd, tickSpaces, tickSpaces);

			calculateComponentAxis (c, combRangeLo, combSequenceLo = new Sequence ());
			calculateComponentAxis (c, combRangeHi, combSequenceHi = new Sequence ());

			generateModel (loEnd, combSequenceLo, loModels);
			generateModel (hiEnd, combSequenceHi, hiModels);

		}

		workspace.add (loModels);
		workspace.add (hiModels);
	}


	/**
	 * split prior generation sequence into 2 halves
	 * @param full the prior generation sequence holding points and comb
	 * @param part the sequence to copy half of the original sequence into
	 * @param starting the starting index for the half to be copied
	 * @param ticks the number of data points to copy
	 */
	public void splitSegment (Sequence full, Sequence part, int starting, int ticks)
	{
		for (int i = 0; i <= ticks; i++) { part.add (full.at (starting + i)); }
		double segBreak = full.offsetBy (starting), delta = full.getSegmentDelta ();
		part.establishRange (segBreak, delta, segBreak + ticks * delta);
	}


	/**
	 * generate a regression model of a portion of the prior generation sequence
	 * @param points the sequence of original points from the split request
	 * @param comb the sequence of half-points between the original points
	 * @param models the collection of models to be appended
	 */
	public void generateModel
	(Sequence points, Sequence comb, SegmentModels models)
	{
		models.copyRange (points);
		models.setDelta (points.getSegmentDelta () / 2);

		models.getData ().add
		(
			performRegression
			(
				support.getSolutionVectorFor (points),
				points, comb
			)
		);

		if (TRACING)
		{
			System.out.println ();
			System.out.println ("+>>> GEN MODELS");
			System.out.println (points.formatted ());
			models.trace (); System.out.println ();
			System.out.println ("-<<< GEN MODELS");
		}
	}


	/**
	 * @param points data points collection lo + delta
	 * @param combRange data points collection lo + delta/2
	 * @return the models from the components regressions
	 */
	public SegmentModels generateModels (DataPoints points, DataPoints combRange)
	{
		SegmentModels models = new SegmentModels ();
		Sequence axis, combSequence;
		models.copyRange (points);

		for (int c = 0; c < mgr.getComponentCount (); c++)
		{
			calculateComponentAxis (c, combRange, combSequence = new Sequence ());
			Vector<Double> toBeSolved = calculateComponentAxis (c, points, axis = new Sequence ());
			models.getData ().add (performRegression (toBeSolved, axis, combSequence));
		}

		models.setDelta (combRange.getSegmentDelta () / 2);
		models.setMax (points.maxIs);

		if (TRACING)
		{
			System.out.println ();
			System.out.println (">>> GEN MODELS");
			models.trace (); System.out.println ();
			System.out.println ("<<< GEN MODELS");
		}

		return models;
	}


	/**
	 * collect the samples for the solution and regression evaluation
	 * @param component the index of the components being evaluated
	 * @param points the values found at the sample locations
	 * @param axis the computed values along the axis
	 * @return the vector to be solved via LUxB
	 */
	public Vector<Double> calculateComponentAxis
	(int component, DataPoints points, Sequence axis)
	{
		double computed;
		axis.copyRange (points);
		Vector<Double> v = support.getSolutionVector ();
		for (int i = 1; i <= support.getSplineTicks (); i++)
		{
			computed = mgr.component (points.at (i-1), component);
			v.set (i, computed); axis.add (computed);
		}
		return v;
	}


	/**
	 * collect evenly spaced sample points
	 * @param lo the lo value of the evaluation range
	 * @param delta the delta forming evenly spaced samples
	 * @param points the values found at the sample locations
	 */
	public void calculatePoints
	(double lo, double delta, DataPoints points)
	{
		if (TRACING)
		{
			System.out.print ("CALC");
			System.out.print (" LO " + lo);
			System.out.print (" DT " + delta);
		}

		double x = lo, max = 0.0, highest = 0.0; T t;
		for (int i = 0; i < support.getSplineTicks (); i++)
		{
			points.add (t = f.eval (mgr.convertFromDouble (highest = x)));
			max = maxOf (t, max);
			x += delta;
		}

		if (TRACING) System.out.println (" HI " + highest);
		points.establishRange (lo, delta, highest);
		points.maxIs = max;
	}


	/**
	 * @param newVal a multi-dimension points being checked
	 * @param oldMax the prior maximum value found in the evaluation
	 * @return the largest of the values found so far
	 */
	public double maxOf (T newVal, double oldMax)
	{
		double max = oldMax, val;
		for (int c = 0; c < components; c++)
		{
			val = Math.abs (mgr.component (newVal, c));
			max = Math.max (val, max);
		}
		return max;
	}


	/**
	 * get regression model
	 * @param b vector to be solved
	 * @param axis computed values for component being evaluated
	 * @param comb computed values for half delta test points on axis
	 * @return the regression model from the evaluation of the solution
	 */
	public Regression.Model<Double> performRegression
		(
			Vector<Double> b, Sequence axis, Sequence comb
		)
	{
		return support.performRegression (b, mergeOf (axis, comb));
	}
	protected SplineSupport support;


	/**
	 * @param regressionPoints the points used to build the best-fit model
	 * @param regressionMidPoints the comb points between the points used for the model
	 * @return the TwoDimSpace data holding the ordered points merged together
	 */
	public DataSequence2D<Double> mergeOf
		(
			Sequence regressionPoints,
			Sequence regressionMidPoints
		)
	{
		double largest = 0.0, y1, y2;
		Sequence x = new Sequence (), y = new Sequence ();

		for (int i = 0; i < support.getSplineTicks (); i++)
		{
			y.add (y1 = regressionPoints.at (i)); y.add (y2 = regressionMidPoints.at (i));
			largest = Math.max (largest, Math.max (Math.abs (y1), Math.abs (y2)));
		}

		support.copyDomian (x);
		y.setMax (largest); y.copyRange (regressionPoints);
		y.setDelta (regressionPoints.getSegmentDelta () / 2);

		return new TwoDimSpace (x, y).getData ();
	}


	/**
	 * prepare component splines for the segment list
	 */
	public void addComponentSplinesToSegmentList ()
	{
		for (ComponentSpline segment : splineSegments)
		{
			this.segments.add
			(
				new SegmentProperties <T> (mgr, spline)
				.connectFunction (segment)
			);
		}
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.Representation#getInterpretation()
	 */
	public String getInterpretation () { return support.getInterpreterPath (); }


}

