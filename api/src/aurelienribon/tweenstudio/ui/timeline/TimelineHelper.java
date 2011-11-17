package aurelienribon.tweenstudio.ui.timeline;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TimelineHelper {
	public static enum NodePart {START, END}

	public static int getFirstTime(TimelineModel model, NodePart nodePart) {
		int time = -1;
		for (Element elem : model.getElements()) {
			for (Node node : elem.getNodes()) {
				int t = nodePart == NodePart.START ? node.getStart() : node.getEnd();
				time = time == -1 ? t : Math.min(time, t);
			}
		}
		return time == -1 ? 0 : time;
	}

	public static int getPreviousTime(TimelineModel model, int currentTime, NodePart nodePart) {
		int time = -1;
		for (Element elem : model.getElements())  {
			for (Node node : elem.getNodes()) {
				int t = nodePart == NodePart.START ? node.getStart() : node.getEnd();
				if (t < currentTime) time = Math.max(time, t);
			}
		}
		return time == -1 ? currentTime : time;
	}

	public static int getNextTime(TimelineModel model, int currentTime, NodePart nodePart) {
		int time = Integer.MAX_VALUE;
		for (Element elem : model.getElements()) {
			for (Node node : elem.getNodes()) {
				int t = nodePart == NodePart.START ? node.getStart() : node.getEnd();
				if (t > currentTime) time = Math.min(time, t);
			}
		}
		return time == Integer.MAX_VALUE ? currentTime : time;
	}

	public static int getLastTime(TimelineModel model, NodePart nodePart) {
		int time = 0;
		for (Element elem : model.getElements()) {
			for (Node node : elem.getNodes()) {
				int t = nodePart == NodePart.START ? node.getStart() : node.getEnd();
				time = Math.max(time, t);
			}
		}
		return time;
	}
}
