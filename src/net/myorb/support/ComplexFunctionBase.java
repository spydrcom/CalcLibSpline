
package net.myorb.support;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.complexnumbers.ComplexLibrary;

import net.myorb.spline.ParameterizedAdaptiveAlgorithms;
import net.myorb.spline.ChebyshevPolynomialSplineSupport;

import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.math.expressions.symbols.ImportedFunctionWrapper;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.computational.Parameterization;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.data.abstractions.Configurable;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import java.util.Map;

public class ComplexFunctionBase implements Function<ComplexValue<Double>>,
	Environment.AccessAcceptance<ComplexValue<Double>>, Configurable
{


	protected Parameterization p = new Parameterization ();


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> t) { return null; }


	/*
	 * spline core objects
	 */

	public ParameterizedAdaptiveAlgorithms<ComplexValue<Double>> getParameterizedAlgorithmsFor
	(Function<ComplexValue<Double>> integrand, Parameterization p)
	{
		ChebyshevPolynomialSplineSupport s = new ChebyshevPolynomialSplineSupport (p);
		return new ParameterizedAdaptiveAlgorithms<ComplexValue<Double>> (integrand, mgr, p, s);
	}

	public ParameterizedAdaptiveAlgorithms<ComplexValue<Double>> getParameterizedAlgorithms
		(Function<ComplexValue<Double>> integrand, Parameterization p)
	{
		(lastParameterizedAlgorithms = getParameterizedAlgorithmsFor (integrand, p)).run ();
		return lastParameterizedAlgorithms;
	}

	public ParameterizedAdaptiveAlgorithms<ComplexValue<Double>>
		getLastParameterizedAlgorithms () { return lastParameterizedAlgorithms; }
	protected ParameterizedAdaptiveAlgorithms<ComplexValue<Double>> lastParameterizedAlgorithms;

	public void init (ComplexFunctionBase integrand)
	{ (spline = getParameterizedAlgorithmsFor (integrand, p)).run (); }
	public ParameterizedAdaptiveAlgorithms<ComplexValue<Double>> getSpline () { return spline; }
	protected ParameterizedAdaptiveAlgorithms<ComplexValue<Double>> spline;


	/*
	 * type managers for real and complex numbers
	 */
	protected static ExpressionFloatingFieldManager realmgr = new ExpressionFloatingFieldManager ();
	protected static ExpressionComplexFieldManager mgr = new ExpressionComplexFieldManager ();
	protected static ComplexLibrary<Double> lib = new ComplexLibrary<Double> (realmgr, mgr);


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription<ComplexValue<Double>> getSpaceDescription() { return mgr; }
	public SpaceManager<ComplexValue<Double>> getSpaceManager() { return mgr; }


	/*
	 * post to symbol table
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Environment.AccessAcceptance#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment <ComplexValue<Double>> environment)
	{
		environment.getSymbolMap ().add
		(
			new ImportedFunctionWrapper <ComplexValue<Double>>
			(named, "z", this)
		);
	}
	protected String named = "gamma";


	/*
	 * get symbol name from configuration parameters
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.LibraryManager.Configurable#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map<String, Object> parameters)
	{ if (parameters.containsKey (NAMED)) this.named = parameters.get (NAMED).toString (); }
	public static String NAMED = "named";


}

