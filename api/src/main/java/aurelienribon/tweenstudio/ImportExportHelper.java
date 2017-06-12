package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.*;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import java.util.Locale;
import java.util.Map;

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

	public static void timelineToModel(Timeline timeline, TimelineModel model, Map<Object, String> targetsNamesMap, Editor editor) {
		for (BaseTween child : timeline.getChildren()) {
			Tween tween = (Tween) child;

			String targetName = targetsNamesMap.get(tween.getTarget());
			String propertyName = editor.getProperty(tween.getTarget(), tween.getAccessor(), tween.getType()).name;

			Element elem = model.getElement(targetName + "/" + propertyName);
			if (elem != null) {
				NodeData nodeData = new NodeData(tween.getCombinedAttributesCount());
				nodeData.setEquation(tween.getEasing());
				nodeData.setTargets(tween.getTargetValues());

				Node node = elem.addNode((int)tween.getFullDuration()*1000);
				node.setLinked(true);
				node.setUserData(nodeData);
			} else {
				System.err.println("'" + targetName + "/" + propertyName + "' was not found in the model.");
			}
		}
	}

	public static String timelineToString(Timeline timeline, Map<Object, String> targetsNamesMap) {
		String str = "";

		for (BaseTween child : timeline.getChildren()) {
			Tween tween = (Tween) child;

			str += String.format(Locale.US, "%s;%s;%d;%d;%d;%s",
				targetsNamesMap.get(tween.getTarget()),
				tween.getTargetClass().getName(),
				tween.getType(),
				tween.getDelay(),
				tween.getDuration(),
				tween.getEasing().toString());

			for (int i=0; i<tween.getCombinedAttributesCount(); i++)
				str += String.format(Locale.US, ";%f", tween.getTargetValues()[i]);

			str += "\n";
		}

		return str;
	}
}
