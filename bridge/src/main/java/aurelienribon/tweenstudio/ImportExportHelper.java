package aurelienribon.tweenstudio;

import java.util.Locale;
import java.util.Map;

import aurelienribon.tweenengine.BaseTween;
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
				if (parts.length < 7)
					continue;

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

		}
		catch (ClassNotFoundException | NumberFormatException ex) {
			throw new RuntimeException(ex);
		}

		return tl;
	}

	public static String timelineToString(Timeline timeline, Map<Object, String> targetsNamesMap) {
		StringBuilder str = new StringBuilder();

		for (BaseTween child : timeline.getChildren()) {
			Tween tween = (Tween) child;

			str.append(String.format(Locale.US, "%s;%s;%d;%d;%d;%s",
					targetsNamesMap.get(tween.getTarget()),
					tween.getTargetClass().getName(),
					tween.getType(),
					(int) tween.getDelay(),
					(int) tween.getDuration(),
					tween.getEasing().toString()));

			for (int i = 0; i < tween.getCombinedAttributesCount(); i++)
				str.append(String.format(Locale.US, ";%f", tween.getTargetValues()[i]));

			str.append("\n");
		}

		return str.toString();
	}
}
