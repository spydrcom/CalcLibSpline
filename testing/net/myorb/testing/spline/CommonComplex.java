
package net.myorb.testing.spline;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.math.Function;
import net.myorb.math.SpaceManager;
import net.myorb.math.complexnumbers.ComplexLibrary;
import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;

public class CommonComplex
{

	static ExpressionComplexFieldManager mgr = new ExpressionComplexFieldManager ();
	public static ComplexValue<Double> C (double r, double i) { return mgr.C (r, i); }
	static ComplexLibrary<Double> lib = new ComplexLibrary<Double> (CommonReal.realmgr, mgr);
	public SpaceDescription<ComplexValue<Double>> getSpaceDescription() { return mgr; }
	public SpaceManager<ComplexValue<Double>> getSpaceManager() { return mgr; }

	static ComplexValue<Double> integralOf
	(Function<ComplexValue<Double>> f, double l, double h)
	{
		double
			tsqr = CommonReal.TSQ (new Component (f, 0), l, h),
			tsqi = CommonReal.TSQ (new Component (f, 1), l, h);
		return C (tsqr, tsqi);
	}

}
