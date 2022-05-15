
package net.myorb.testing.spline;

import net.myorb.library.GammaZetaProduct;
import net.myorb.math.computational.Parameterization;
import net.myorb.support.ComplexFunctionBase;

public class GammaZetaTest extends ComplexFunctionBase
{

	public static void main (String[] a) throws Exception
	{
		Parameterization p = new Parameterization ();
		p.set ("initlo", 0.0001);

		GammaZetaProduct G = new GammaZetaProduct (p);

		System.out.println (G.eval (lib.C (2.0, 5.0)));
	}

}

/*
 * 
z=0.5+10j

mpmath.gamma(z) * mpmath.zeta(z)
Out[20]: mpc(real='5.4146210853150471e-7', imag='2.2202092629639758e-7')

mpmath.gamma(z)
Out[29]: mpc(real='3.378724376234236e-7', imag='1.689369839038919e-7')

mpmath.zeta(z)
Out[34]: mpc(real='1.5448952202967527', imag='-0.11533646527127338')


z=0.5+10*i

calc GAMMA z
(3.378724376234524E-7 + 1.6893698390390315E-7*i)

zz = 1.5448952202967527 -0.11533646527127338*i

zg = zz * GAMMA z

calc zg
(5.414621085315506E-7 + 2.220209262964116E-7*i)

 */