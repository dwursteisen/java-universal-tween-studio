package aurelienribon.tweenstudio.ui.timeline;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TimelineHelper {
	public static int getFirstTime(TimelineModel model) {
		int time = -1;
		for (Element elem : model.getElements())
			for (Node node : elem.getNodes())
				time = time == -1 ? node.getEnd() : Math.min(time, node.getEnd());
		return time == -1 ? 0 : time;
	}

	public static int getPreviousTime(TimelineModel model, int currentTime) {
		int time = -1;
		for (Element elem : model.getElements())
			for (Node node : elem.getNodes())
				if (node.getStart() < currentTime)
					time = Math.max(time, node.getEnd());
		return time == -1 ? currentTime : time;
	}

	public static int getNextTime(TimelineModel model, int currentTime) {
		int time = Integer.MAX_VALUE;
		for (Element elem : model.getElements())
			for (Node node : elem.getNodes())
				if (node.getStart() > currentTime)
					time = Math.min(time, node.getEnd());
		return time == Integer.MAX_VALUE ? currentTime : time;
	}

	public static int getLastTime(TimelineModel model) {
		int time = 0;
		for (Element elem : model.getElements())
			for (Node node : elem.getNodes())
				time = Math.max(time, node.getEnd());
		return time;
	}
}
