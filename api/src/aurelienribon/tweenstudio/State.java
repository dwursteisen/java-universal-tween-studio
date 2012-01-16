package aurelienribon.tweenstudio;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class State {
	public final Object target;
	public final Class targetClass;
	public final int tweenType;
	public final float[] targets;

	public State(Object target, Class targetClass, int tweenType, float[] targets) {
		this.target = target;
		this.targetClass = targetClass;
		this.tweenType = tweenType;
		this.targets = targets;
	}
}
