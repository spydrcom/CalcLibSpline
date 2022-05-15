
package net.myorb.library;

import net.myorb.math.computational.Parameterization;

import net.myorb.math.complexnumbers.ComplexValue;

import net.myorb.support.ComplexFunctionBase;

public class GammaZetaProduct extends ComplexFunctionBase
{

	public GammaZetaProduct () { this.p = new Parameterization (); }
	public GammaZetaProduct (Parameterization p) { this.p = p; }
	protected Parameterization p;

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> z)
	{
		return getParameterizedAlgorithms (new GammaZetaIntegrand (z), p).evalIntegral ();
	}

}

/**
 * t^(z-1) * 1/(1-exp(-t)) * exp(-t)
 */
class GammaZetaIntegrand extends ComplexFunctionBase
{

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> t)
	{
		ComplexValue<Double> et = lib.exp (mgr.negate (t));
		return mgr.multiply (ratio (t, et), et);
	}

	public ComplexValue<Double> ratio 
	(ComplexValue<Double> t, ComplexValue<Double> et)
	{
		ComplexValue<Double> oneMinusEt =
				mgr.add (mgr.getOne (), mgr.negate (et));
		return mgr.multiply (lib.power (t, Zm1), mgr.invert (oneMinusEt));
	}

	GammaZetaIntegrand (ComplexValue<Double> Z)
	{
		this.Zm1 = mgr.add (Z, mgr.newScalar (-1));
	}
	ComplexValue<Double> Zm1;

}
