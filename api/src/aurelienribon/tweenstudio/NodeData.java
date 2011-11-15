package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Linear;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class NodeData {
	private final float[] targets;
	private TweenEquation equation;

	public NodeData(int targetsCount) {
		this.targets = new float[targetsCount];
		this.equation = Linear.INOUT;
	}

	public float[] getTargets() {
		return targets;
	}

	public TweenEquation getEquation() {
		return equation;
	}

	public void setEquation(TweenEquation equation) {
		this.equation = equation;
	}
}
