
package net.myorb.library;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.complexnumbers.ComplexValue;

import net.myorb.support.ComplexFunctionBase;

/**
 * complex GAMMA function realized using spline quadrature
 * @author Michael Druckman
 */
public class Gamma extends ComplexFunctionBase
{

	public Gamma () { this.p = new Parameterization (); }
	public Gamma (Parameterization p) { this.p = p; }
	protected Parameterization p;

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> z)
	{
		lastComputed = z;
		if (z.Re () < 0.0) return reflect (z);
		else if (z.Re () < 2.0) return recurrence (z);
		else if (z.Re () > 5.0) return reverseRecurrence (z);
		else return getParameterizedAlgorithms (new GammaIntegrand (z), p).evalIntegral ();
	}
	protected ComplexValue<Double> lastComputed;

	/**
	 * Euler reflection formula
	 *  GAMMA(z) * GAMMA(1-z) = PI / sin (PI * z)
	 * @param z the parameter to GAMMA being sought
	 * @return GAMMA(z)
	 */
	public ComplexValue<Double> reflect (ComplexValue<Double> z)
	{
		ComplexValue<Double> PI = mgr.C (Math.PI, 0.0),
			zPI = mgr.multiply (PI, z), isinZPI = mgr.invert (lib.sin (zPI)),
			piCsc = mgr.multiply (PI, isinZPI), negzP1 = mgr.add (mgr.negate (z), mgr.getOne ());
		return mgr.multiply (piCsc, mgr.invert (eval (negzP1)));
	}

	/**
	 * Gamma recurrence formula
	 *  GAMMA(z) = GAMMA(z+3) / ( z * (z+1) * (z+2) ) ...
	 *  useful for eval of GAMMA for z LT 1 avoiding t^(z-1) integral
	 * @param z the parameter to GAMMA being sought
	 * @return GAMMA(z)
	 */
	public ComplexValue<Double> recurrence (ComplexValue<Double> z)
	{
		ComplexValue<Double>
			zp3 = mgr.add (z, mgr.newScalar (3)), zp2 = mgr.add (z, mgr.newScalar (2)),
			zp1 = mgr.add (z, mgr.newScalar (1)), zzp1 = mgr.multiply (z, zp1),
			zzp1zp2 = mgr.multiply (zzp1, zp2), izzp1zp2 = mgr.invert (zzp1zp2);
		return mgr.multiply (eval (zp3), izzp1zp2);
	}

	/**
	 * Gamma recurrence formula
	 *  GAMMA(z+n) = GAMMA(z) * ( z * (z+1) * ... * (z+n-1) )
	 * @param z the parameter to GAMMA being sought
	 * @return GAMMA(z)
	 */
	public ComplexValue<Double> reverseRecurrence (ComplexValue<Double> z)
	{
		ComplexValue<Double> zpn = z, mul = mgr.getOne (),
			none = mgr.newScalar (-1);

		while (zpn.Re() > 5)
		{
			zpn = mgr.add (zpn, none);
			mul = mgr.multiply (mul, zpn);
		}

		return mgr.multiply (eval (zpn), mul);
	}

}

/**
 * t^(z-1) * exp(-t)
 */
class GammaIntegrand extends ComplexFunctionBase
{

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> t)
	{
		return mgr.multiply (lib.power (t, Zm1), lib.exp (mgr.negate (t)));
	}

	GammaIntegrand (ComplexValue<Double> Z)
	{
		this.Zm1 = mgr.add (Z, mgr.newScalar (-1));
	}
	protected ComplexValue<Double> Zm1;

}

