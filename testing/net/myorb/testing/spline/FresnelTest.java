
package net.myorb.testing.spline;

import net.myorb.library.Fresnel;
import net.myorb.support.ComplexFunctionBase;

import net.myorb.math.complexnumbers.ComplexValue;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.computational.splines.StorageFormat;

import net.myorb.data.notations.json.JsonPrettyPrinter;

public class FresnelTest extends ComplexFunctionBase
{

	static final boolean TRACE = false;

	public static void main (String[] a) throws Exception
	{

		Parameterization p = new Parameterization ();

		p.set ("initlo", 0); p.set ("ubound", 10);
		if (TRACE) p.set ("trace", 1);

		Fresnel C = new Fresnel (p);
		ComplexValue<Double> Z = lib.C (1.0, 0.0);

		double A099290 = 0.77989340037;
		ComplexValue<Double> C1 = C.getSpline ().evalIntegralOver (0.0, 1.0);
		System.out.println (); System.out.println ("C1"); System.out.println (C1);
		System.out.println (C1.Re () - A099290); System.out.println ("======");
		System.out.println ();

		if (TRACE)
		{
			JsonPrettyPrinter.sendTo
			(
				StorageFormat.express
				(
					"C", "x",
					"Fresnel cosine function",
					C.getSpline ()
				),
				System.out
			);
		}

		for (double x = 1.0; x <= 8.0; x += 0.1)
		{
			System.out.print (x); System.out.print (": ");
			Z = lib.C (x, 0.0); System.out.println (C.eval (Z));
		}

	}

}
