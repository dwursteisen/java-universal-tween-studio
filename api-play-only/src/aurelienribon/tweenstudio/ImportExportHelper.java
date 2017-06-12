package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenUtils;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
class ImportExportHelper {
	public static Timeline stringToDummyTimeline(String str) {
		Timeline tl = Timeline.createParallel();
		String[] lines = str.split("\n");

		try {
			for (String line : lines) {
				String[] parts = line.split(";");
				if (parts.length < 7) continue;

				String targetName = parts[0];
				Class targetClass = Class.forName(parts[1]);
				int tweenType = Integer.parseInt(parts[2]);
				int delay = Integer.parseInt(parts[3]);
				int duration = Integer.parseInt(parts[4]);
				TweenEquation equation = TweenUtils.parseEasing(parts[5]);

				float[] targets = new float[parts.length - 6];
				for (int i = 0; i < targets.length; i++)
					targets[i] = Float.parseFloat(parts[i + 6]);

				Tween tween = Tween.to(null, tweenType, duration)
					.cast(targetClass)
					.target(targets)
					.ease(equation)
					.delay(delay)
					.setUserData(targetName);

				tl.push(tween);
			}

		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		} catch (NumberFormatException ex) {
			throw new RuntimeException(ex);
		}

		return tl;
	}
}
