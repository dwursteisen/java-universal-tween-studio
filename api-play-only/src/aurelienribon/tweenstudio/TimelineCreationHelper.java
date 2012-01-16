package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import java.util.List;
import java.util.Map;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
class TimelineCreationHelper {
	public static Timeline buildTimelineFromDummy(Timeline dummyTimeline, List<Object> targets, Map<Object, String> targetsNamesMap) {
		Timeline tl = Timeline.createParallel();

		for (BaseTween child : dummyTimeline.getChildren()) {
			Tween t = (Tween) child;
			Object target = getTargetFromName((String) t.getUserData(), targets, targetsNamesMap);

			Tween tween = Tween.to(target, t.getType(), t.getDuration())
				.cast(t.getTargetClass())
				.target(t.getTargetValues())
				.ease(t.getEasing())
				.delay(t.getDelay())
				.build();

			tl.push(tween);
		}

		return tl;
	}

	private static Object getTargetFromName(String name, List<Object> targets, Map<Object, String> targetsNamesMap) {
		for (Object target : targets)
			if (name.equals(targetsNamesMap.get(target)))
				return target;
		return null;
	}
}
