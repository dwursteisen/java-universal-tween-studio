package aurelienribon.tweenstudio.ui.timeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TimelineModel {
    private final Element root = new Element(this, null, "root");
	private boolean isSilent = false;

	// -------------------------------------------------------------------------
	// public API
	// -------------------------------------------------------------------------

	public Element addElement(String[] names) {
		Element added = null;
		Element elem = root;
		for (String name : names) {
			if (elem.getChild(name) == null) {
				elem = added = elem.addChild(name);
			} else {
				elem = elem.getChild(name);
			}
		}
		return added;
	}

	public Element addElement(String path) {
		if (path.equals("") || path.equals("/"))
			return root;

		String[] names = path.split("/");
		return addElement(names);
	}

	public Element getRoot() {
		return root;
	}

	public Element getElement(String[] names) {
		Element elem = root;
		for (String name : names) {
			elem = elem.getChild(name);
			if (elem == null)
				return null;
		}
		return elem;
	}

	public Element getElement(String path) {
		if (path.equals("") || path.equals("/"))
			return root;

		String[] names = path.split("/");
		return getElement(names);
	}

	public List<Element> getElements() {
		List<Element> elems = new ArrayList<Element>();
		_getElements(root, elems);
		return Collections.unmodifiableList(elems);
	}

	public int getDuration() {
		int duration = 0;
		for (Element elem : getElements())
			for (Node n : elem.getNodes())
				duration = Math.max(duration, n.getTime());
		return duration;
	}

	public void mute(boolean value) {
		isSilent = value;
		if (value == false) fireStateChanged();
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void _getElements(Element elem, List<Element> elems) {
		for (Element child : elem.getChildren()) {
			elems.add(child);
			_getElements(child, elems);
		}
	}

	// -------------------------------------------------------------------------
	// Events
	// -------------------------------------------------------------------------

	private final List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
	public void addListener(Listener listener) {listeners.add(listener);}

	public interface Listener {
		public void stateChanged();
	}

	private void fireStateChanged() {
		if (isSilent) return;
		for (Listener listener : listeners)
			listener.stateChanged();
	}

	// -------------------------------------------------------------------------
	// Inner classes -- Element
	// -------------------------------------------------------------------------

	public static class Element {
		private final TimelineModel model;
		private final Element parent;
		private final String name;
		private final List<Element> children = new ArrayList<Element>(0);
		private final List<Node> nodes = new ArrayList<Node>(0);
		private final int level;
		private boolean selectable = true;
		private Object userData = null;

		public Element(TimelineModel timelineModel, Element parent, String name) {
			this.model = timelineModel;
			this.parent = parent;
			this.name = name;
			this.level = parent != null ? parent.getLevel() + 1 : 0;
		}

		public TimelineModel getTimelineModel() {
			return model;
		}

		public Element getParent() {
			return parent;
		}

		public String getName() {
			return name;
		}

		public int getLevel() {
			return level;
		}

		public List<Element> getChildren() {
			return Collections.unmodifiableList(children);
		}

		public List<Node> getNodes() {
			return Collections.unmodifiableList(nodes);
		}

		public Element getChild(String name) {
			for (Element child : children)
				if (child.getName().equals(name))
					return child;
			return null;
		}

		public Element addChild(String name) {
			Element child = new Element(model, this, name);
			children.add(child);
			return child;
		}

		public Node addNode(int time) {
			Node node = new Node(this, time);
			nodes.add(node);
			sortNodes();
			model.fireStateChanged();
			return node;
		}

		public void removeNode(Node node) {
			nodes.remove(node);
			model.fireStateChanged();
		}

		public void setNodes(List<Node> nodes) {
			this.nodes.clear();
			this.nodes.addAll(nodes);
			model.fireStateChanged();
		}

		public boolean isSelectable() {
			return selectable;
		}

		public void setSelectable(boolean selectable) {
			this.selectable = selectable;
		}

		public Object getUserData() {
			return userData;
		}

		public void setUserData(Object userData) {
			this.userData = userData;
		}

		public boolean isDescendantOf(Element parent) {
			Element elem = this;
			while (elem.getLevel() > 0) {
				if (elem == parent) return true;
				elem = elem.getParent();
			}
			return false;
		}

		public void sortNodes() {
			Collections.sort(nodes, new Comparator<Node>() {
				@Override public int compare(Node o1, Node o2) {
					return o1.getTime() - o2.getTime();
				}
			});
		}
	}

	// -------------------------------------------------------------------------
	// Inner classes -- Node
	// -------------------------------------------------------------------------

	public static class Node {
		private final Element parent;
		private int time;
		private boolean isLinked = true;
		private Object userData = null;

		public Node(Element parent, int time) {
			this.parent = parent;
			this.time = time;
		}

		public int getTime() {
			return time;
		}

		public Element getParent() {
			return parent;
		}

		public void setTime(int time) {
			if (this.time != time) {
				this.time = time;
				parent.getTimelineModel().fireStateChanged();
			}
		}

		public boolean isLinked() {
			return isLinked;
		}

		public void setLinked(boolean isLinked) {
			this.isLinked = isLinked;
		}

		public Object getUserData() {
			return userData;
		}

		public void setUserData(Object userData) {
			this.userData = userData;
		}
	}
}
