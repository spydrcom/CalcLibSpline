
package net.myorb.library;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.computational.Parameterization;

import net.myorb.support.ComplexFunctionBase;

import java.util.Map;

public class Fresnel extends ComplexFunctionBase
{

	public Fresnel () { this.p = new Parameterization (); }
	public Fresnel (Parameterization p) { this.p = p; init (); }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> z)
	{
		return spline.evalIntegralOver (0.0, z.Re ());
	}

	public void addConfiguration (Map<String, Object> parameters)
	{
		super.addConfiguration (parameters);
		this.p = new Parameterization (parameters);
		this.init ();
	}
	public void init () { init (new FresnelIntegrand ()); }

}

class FresnelIntegrand extends ComplexFunctionBase
{

	static double PI_OVER_TWO = Math.PI / 2.0;

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> t)
	{
		double tre = t.Re (), tsq = tre * tre;
		double result = Math.cos (PI_OVER_TWO * tsq);
		return mgr.C (result, 0.0);
	}

}

