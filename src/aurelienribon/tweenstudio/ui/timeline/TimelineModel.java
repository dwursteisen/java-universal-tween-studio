package aurelienribon.tweenstudio.ui.timeline;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TimelineModel {
    private final Map<String, Map<String, List<Node>>> nodeMap;

	public TimelineModel() {
		nodeMap = new LinkedHashMap<String, Map<String, List<Node>>>();
	}

	public TimelineModel addTarget(String target, String[] attrs) {
		nodeMap.put(target, new LinkedHashMap<String, List<Node>>());
		for (String attr : attrs)
			nodeMap.get(target).put(attr, new ArrayList<Node>());
		return this;
	}

	public TimelineModel addNode(String target, String attr, Node node) {
		nodeMap.get(target).get(attr).add(node);
		return this;
	}

	public String[] getTargets() {
		return nodeMap.keySet().toArray(new String[0]);
	}

	public String[] getAttrs(String target) {
		return nodeMap.get(target).keySet().toArray(new String[0]);
	}

	public Node[] getNodes(String target, String attr) {
		return nodeMap.get(target).get(attr).toArray(new Node[0]);
	}

	public static class Node {
		public int delayMillis;
		public int durationMillis;

		public Node(int delay, int duration) {
			this.delayMillis = delay;
			this.durationMillis = duration;
		}
	}
}
