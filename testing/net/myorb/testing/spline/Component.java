package net.myorb.testing.spline;

import net.myorb.math.Function;
import net.myorb.math.complexnumbers.ComplexValue;

public class Component extends CommonReal implements Function<Double>
{

	Component
	(Function<ComplexValue<Double>> f, int part)
	{ this.part = part; this.f = f; }

	public Double eval (Double x)
	{ return component (part, f.eval (CommonReal.cvt (x))); }
	Function<ComplexValue<Double>> f;
	int part;

	public static double component (int number, ComplexValue<Double> from)
	{
		switch (number)
		{
			case 0: return from.Re ();
			default: return from.Im ();
		}
	}

}
