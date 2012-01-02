package aurelienribon.tweenstudio.ui.timeline;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TimelineHelper {
	public static int getFirstTime(Element elem, boolean testChildren) {
		int t = -1;

		for (Node node : elem.getNodes())
			t = t == -1 ? node.getTime() : Math.min(t, node.getTime());

		if (testChildren) {
			for (Element child : elem.getChildren()) {
				int childT = getFirstTime(child, true);
				if (childT != -1) t = t == -1 ? childT : Math.min(t, childT);
			}
		}

		return t;
	}

	public static int getPreviousTime(Element elem, int time, boolean testChildren) {
		int t = -1;

		for (Node node : elem.getNodes())
			if (node.getTime() < time)
				t = t == -1 ? node.getTime() : Math.max(t, node.getTime());

		if (testChildren) {
			for (Element child : elem.getChildren()) {
				int childT = getPreviousTime(child, time, true);
				if (childT != -1) t = t == -1 ? childT : Math.max(t, childT);
			}
		}

		return t;
	}

	public static int getNextTime(Element elem, int time, boolean testChildren) {
		int t = -1;

		for (Node node : elem.getNodes())
			if (node.getTime() > time)
				t = t == -1 ? node.getTime() : Math.min(t, node.getTime());

		if (testChildren) {
			for (Element child : elem.getChildren()) {
				int childT = getNextTime(child, time, true);
				if (childT != -1) t = t == -1 ? childT : Math.min(t, childT);
			}
		}

		return t;
	}

	public static int getLastTime(Element elem, boolean testChildren) {
		int t = -1;

		for (Node node : elem.getNodes())
			t = t == -1 ? node.getTime() : Math.max(t, node.getTime());

		if (testChildren) {
			for (Element child : elem.getChildren()) {
				int childT = getLastTime(child, true);
				if (childT != -1) t = t == -1 ? childT : Math.max(t, childT);
			}
		}

		return t;
	}

	public static int getDuration(Node node) {
		if (!node.isLinked()) return 0;
		int t = getPreviousTime(node.getParent(), node.getTime(), false);
		return t == -1 ? node.getTime() : node.getTime() - t;
	}

	public static Node getNodeOrCreate(Element elem, int time) {
		Node node = null;

		for (Node n : elem.getNodes()) {
			if (n.getTime() == time) {
				node = n;
				break;
			}
		}

		if (node == null) node = elem.addNode(time);
		return node;
	}
}
