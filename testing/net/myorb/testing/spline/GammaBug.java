
package net.myorb.testing.spline;

import net.myorb.library.Gamma;
import net.myorb.math.SpaceManager;
import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.computational.Parameterization;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;

public class GammaBug
{

	static SpaceManager<Double> dmgr = new ExpressionFloatingFieldManager ();
	static SpaceManager<ComplexValue<Double>> cmgr = new ExpressionComplexFieldManager ();

	static ComplexValue<Double> expected = new ComplexValue<Double> 
		(-0.32681798741115514, 0.31003000145338838, dmgr);

	public static void main (String[] a) throws Exception
	{

		Parameterization p = new Parameterization ();

		p.set ("maxsegments", 70);
		p.set ("maxerror", 1E-11); // works
		//p.set ("maxerror", 1E-12); // bug
		p.set ("mindelta", 1E-6);
		p.set ("trace", 1);

		Gamma G = new Gamma (p);
		ComplexValue<Double> Z, gz;
		gz = G.eval (Z = new ComplexValue<Double> (5.0, 6.75, dmgr));
		System.out.println ("Gamma " + Z + " = " + gz);

		ComplexValue<Double> dif = cmgr.add (gz, expected);
		System.out.println ("error = " + dif);

	}
//mpmath.gamma (5+6.75j) : mpc(real='0.32681798741115514', imag='-0.31003000145338838')
}
