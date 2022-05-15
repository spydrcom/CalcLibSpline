
package net.myorb.testing.spline;

import net.myorb.spline.SplineSupport;
import net.myorb.spline.ChebyshevSplineGenerator;

import net.myorb.support.ComplexFunctionBase;
import net.myorb.library.Gamma;

import net.myorb.math.complexnumbers.ComplexValue;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.computational.Spline;

//import net.myorb.math.computational.splines.StorageFormat;
//import net.myorb.math.expressions.ExpressionComponentSpaceManager;

import net.myorb.math.Function;

//import net.myorb.data.notations.json.JsonPrettyPrinter;

public class GammaSplineTest<T> extends ComplexFunctionBase
{

	public static void main (String[] a) throws Exception
	{
		Parameterization p = new Parameterization ();
//		p.set ("maxerror", 1E-4);
//		p.set ("mindelta", 1E-3);
		
		Gamma G = new Crit (p);
		//Gamma G = new Gamma (p);
		
//		generateSpline (G, 1, 10, p).getRepresentation ();

		p = new Parameterization ();
		p.set ("maxerror", 1E-4);
		p.set ("mindelta", 1E-3);

//		JsonPrettyPrinter.sendTo
//		(
//			StorageFormat.express ("Gamma", "z", "Complex GAMMA function", generateSpline (G, 1, 2, p).getRepresentation ()),
//			System.out
//		);


		for (double x = 1.0; x <= 8.0; x += 0.1)
		{
			System.out.print (x); System.out.print (": ");
			ComplexValue<Double> Z = lib.C (x, 5.0);
			System.out.println (G.eval (Z));
		}

		ComplexValue<Double> Z = lib.C (5.0, 6.0);
		System.out.println (G.eval (Z));

//		ComplexValue<Double> Z = lib.C (0.5, 15.5);
//		Object result = G.eval (Z);
//
//		System.out.println ();
//		System.out.println ("Computed Integral");
//		System.out.println ("=================");
//		System.out.println (result);
//
//		JsonPrettyPrinter.sendTo
//		(
//			//StorageFormat.express ("Gamma", "z", "Complex GAMMA function", G.getLastAlgorithms ()),
//			StorageFormat.express ("Gamma", "z", "Complex GAMMA function", G.getLastParameterizedAlgorithms ()),
//			System.out
//		);

	}

	// G(0.5+14j) = mpc(real='9.5313792135428011e-8', imag='1.0137331065115476e-7')
	// G(0.5+28j)	mpc(real='-1.5558175425990789e-19', imag='1.2331886177162815e-19')
	// G(0.5,5) 	mpc(real='-0.00096948070526994953', imag='8.3630391299613721e-5')
	// G(5,5)		(-0.9743951793575611 + 2.0066898030286513*i)

	public static Spline.Operations <ComplexValue<Double>> generateSpline
		(
			Function <ComplexValue<Double>> f, double lo, double hi,
			Parameterization configuration
		)
	{
		ChebyshevSplineGenerator <ComplexValue<Double>> factory = new ChebyshevSplineGenerator <> (f, mgr, configuration);
		SplineSupport  support = ChebyshevSplineGenerator.getChebyshevSplineSupport (configuration);
		factory.initFrom (mgr, configuration, support); factory.setFunction (f);
		factory.generateSpline (lo, hi);
		return factory;
	}
	//protected static ExpressionComponentSpaceManager <ComplexValue<Double>> mgr;

}

class Crit extends Gamma
{
	public Crit (Parameterization p) { super (p); }

	public ComplexValue<Double> eval (ComplexValue<Double> z)
	{
		System.out.println (z);
		if (z.Im() == 0)return super.eval (lib.C (0.5, z.Re()));
		else return super.eval (z);
	}

}

///*
//Python 3.8.8 (default, Apr 13 2021, 15:08:03) [MSC v.1916 64 bit (AMD64)]
//Type "copyright", "credits" or "license" for more information.
//
//IPython 7.22.0 -- An enhanced Interactive Python.
//
//import mpmath
//
//mpmath.gamma ( 0.5+1j )
//Out[2]: mpc(real='0.30069461726065583', imag='-0.42496787943312381')
//
//mpmath.gamma ( 0.5+1.1j )
//Out[3]: mpc(real='0.25716753374261297', imag='-0.36330041114881473')
//
//mpmath.gamma ( 0.5+1.2j )
//Out[4]: mpc(real='0.22298482861259625', imag='-0.3083083988079301')
//
//mpmath.gamma ( 0.5+2j )
//Out[5]: mpc(real='0.08985517670643163', imag='-0.060493760292887569')
//
//mpmath.gamma ( 0.5+1.5j )
//Out[6]: mpc(real='0.15443097618696283', imag='-0.18052756337372855')
//
//mpmath.gamma ( 0.5+2.7j )
//Out[7]: mpc(real='0.0360728_38084162659', imag='-9.590_0985510441198e-5') * */
// */