package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Quint;
import aurelienribon.tweenengine.equations.Sine;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TweenHelper {
	private static final TweenEquation[] equations = new TweenEquation[] {
		Linear.INOUT,
		Quad.IN, Quad.OUT, Quad.INOUT,
		Cubic.IN, Cubic.OUT, Cubic.INOUT,
		Quart.IN, Quart.OUT, Quart.INOUT,
		Quint.IN, Quint.OUT, Quint.INOUT,
		Expo.IN, Expo.OUT, Expo.INOUT,
		Sine.IN, Sine.OUT, Sine.INOUT,
		Circ.IN, Circ.OUT, Circ.INOUT,
		Bounce.IN, Bounce.OUT, Bounce.INOUT,
		Back.IN, Back.OUT, Back.INOUT,
		Elastic.IN, Elastic.OUT, Elastic.INOUT
	};

	public static TweenEquation[] getEquations() {
		return equations;
	}

	public static TweenEquation getEquation(String name) {
		if (name.startsWith("Linear")) name = "Linear.INOUT";
		for (TweenEquation equation : equations)
			if (equation.toString().equals(name))
				return equation;
		return Linear.INOUT;
	}
}
