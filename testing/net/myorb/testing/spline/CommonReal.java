package net.myorb.testing.spline;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.math.Function;
import net.myorb.math.SpaceManager;
import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.computational.TanhSinhQuadratureAlgorithms;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

public class CommonReal
{

	public SpaceManager<Double> getSpaceManager() { return realmgr; }
	public SpaceDescription<Double> getSpaceDescription() { return realmgr; }
	static ExpressionFloatingFieldManager realmgr = new ExpressionFloatingFieldManager ();
	
	public static double component (int number, ComplexValue<Double> from)
	{
		switch (number)
		{
			case 0: return from.Re ();
			default: return from.Im ();
		}
	}

	public static ComplexValue<Double> cvt (double x)
	{
		return CommonComplex.C (x, 0.0);
	}

	static double TSQ (Function<Double> f, double l, double h)
	{
		return TanhSinhQuadratureAlgorithms.Integrate (f, l, h, 0.0001, null);
	}

}
