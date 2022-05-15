
package net.myorb.spline;

import net.myorb.data.abstractions.DataSequence2D;

import net.myorb.math.computational.splines.SplineMechanisms;
import net.myorb.spline.ParameterizedComponentSpline.Sequence;

import net.myorb.math.computational.Regression;
import net.myorb.math.GeneratingFunctions;
import net.myorb.math.matrices.Vector;

/**
 * specifications for aspects of the spline mechanisms for the technology being used
 * @author Michael Druckman
 */
public interface SplineSupport extends SplineMechanisms
{

	/**
	 * @return the lo end of the optimal range the spline should use
	 */
	public double getSplineLo ();

	/**
	 * @return the ratio of the spline optimal range to the number of ticks
	 */
	public double getSplineDelta ();

	/**
	 * @param hi the hi end of the original coordinate set
	 * @param lo the lo end of the original coordinate set
	 * @return ratio of function delta to spline delta
	 */
	public double getSplineSlope (double hi, double lo);

	/**
	 * @return the number of sample ticks in the optimal spline model
	 */
	public int getSplineTicks ();

	/**
	 * @param hi the hi end of the original coordinate set
	 * @param lo the lo end of the original coordinate set
	 * @return the slope for the coordinate translation
	 */
	public double getSlope (double hi, double lo);

	/**
	 * @param lo the lo end of the original coordinate set
	 * @param hi the hi end of the original coordinate set
	 * @return the difference between ticks
	 */
	public double deltaFor (double lo, double hi);

	/**
	 * @param to a sequence to be set to domain values
	 */
	public void copyDomian (Sequence to);

	/**
	 * @return a Vector object of correct size
	 */
	public Vector<Double> getSolutionVector ();

	/**
	 * @param samples the data points for regression
	 * @return the solution vector for the regression
	 */
	public Vector<Double> getSolutionVectorFor (Sequence samples);

	/**
	 * @param coefficients the polynomial coefficients of the spline
	 * @param lo the lo end of the integral evaluation range to be used
	 * @param hi the hi end of the integral evaluation range to be used
	 * @return the evaluation of the integral over the specified range
	 */
	public double evaluatePolynomialIntegral
	(
		GeneratingFunctions.Coefficients<Double> coefficients, 
		double lo, double hi
	);

	/**
	 * @param coefficients the polynomial coefficients of the spline
	 * @param at the domain value at which to evaluate the integral
	 * @return the evaluation of the integral at specified point
	 */
	public double evaluatePolynomialIntegral
	(
		GeneratingFunctions.Coefficients<Double> coefficients, 
		double at
	);

	/**
	 * @param b the solution vector for the luXb decomposition equation
	 * @param samples the sample data that should be used to verify the solution
	 * @return the coefficients of the spline solution attributed with evaluations
	 */
	public Regression.Model<Double> performRegression
	(
		Vector<Double> b, DataSequence2D<Double> samples
	);

	/**
	 * @return class-path for interpretation object
	 */
	String getInterpreterPath ();

}
