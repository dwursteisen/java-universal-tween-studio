package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;

public class TweenDescription {
	private Tween tween;
	public TweenEquation equation;
	public int durationMillis;
	public int delayMillis;
	public float[] targetValues;

	public TweenDescription(Tween tween) {
		this.tween = tween;
		this.equation = tween.getEquation();
		this.targetValues = tween.getTargetValues().clone();
		this.durationMillis = tween.getDurationMillis();
		this.delayMillis = tween.getDelayMillis();
	}

	public TweenStudioObject getTarget() {
		return (TweenStudioObject) tween.getTarget();
	}

	public Tween buildTween() {
		switch (tween.getMode()) {
			case Tween.MODE_TO:
				return Tween.to(tween.getTarget(), tween.getTweenType(), equation, durationMillis, targetValues).delay(delayMillis);

			case Tween.MODE_FROM:
				return Tween.from(tween.getTarget(), tween.getTweenType(), equation, durationMillis, targetValues).delay(delayMillis);

			case Tween.MODE_SET:
				return Tween.set(tween.getTarget(), tween.getTweenType(), targetValues).delay(delayMillis);

			default:
				assert false;
				return null;
		}
	}
}
