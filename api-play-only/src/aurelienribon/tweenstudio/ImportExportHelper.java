package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenstudio.TweenStudio.DummyTweenAccessor;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
class ImportExportHelper {
	public static Timeline stringToTimeline(String str) {
		Timeline tl = Timeline.createParallel();
		String[] lines = str.split("\n");

		for (String line : lines) {
			String[] parts = line.split(";");
			if (parts.length < 6) continue;

			Object target = new DummyTweenAccessor(parts[0]); // name instead of real target
			int tweenType = Integer.parseInt(parts[1]);
			int delay = Integer.parseInt(parts[2]);
			int duration = Integer.parseInt(parts[3]);
			TweenEquation equation = TweenEquation.parse(parts[4]);

			float[] targets = new float[parts.length-5];
			for (int i=0; i<targets.length; i++) targets[i] = Float.parseFloat(parts[i+5]);

			Tween tween = Tween.to(target, tweenType, duration)
				.target(targets)
				.ease(equation)
				.delay(delay);
			
			tl.push(tween);
		}

		return tl;
	}
}
