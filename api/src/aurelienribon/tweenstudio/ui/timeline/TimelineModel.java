package aurelienribon.tweenstudio.ui.timeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TimelineModel {
    private final Element root = new Element(this, null, "root");
	private int duration = 0;

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
		duration = 0;
		for (Element elem : getElements())
			for (Node n : elem.getNodes())
				duration = Math.max(duration, n.getEnd());
		return duration;
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

	private final List<EventListener> listeners = new CopyOnWriteArrayList<EventListener>();
	public void addListener(EventListener listener) {listeners.add(listener);}

	public interface EventListener {
		public void stateChanged();
	}

	private void fireStateChanged() {
		for (EventListener listener : listeners)
			listener.stateChanged();
	}

	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	public static class Element {
		private final TimelineModel timelineModel;
		private final Element parent;
		private final String name;
		private final List<Element> children = new ArrayList<Element>(0);
		private final List<Node> nodes = new ArrayList<Node>(0);
		private final int level;
		private boolean selectable = true;
		private Object userData = null;

		public Element(TimelineModel timelineModel, Element parent, String name) {
			this.timelineModel = timelineModel;
			this.parent = parent;
			this.name = name;
			this.level = parent != null ? parent.getLevel() + 1 : -1;
		}

		public TimelineModel getTimelineModel() {
			return timelineModel;
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
			Element child = new Element(timelineModel, this, name);
			children.add(child);
			return child;
		}

		public Node addNode(int start, int duration) {
			Node node = new Node(this, start, duration);
			nodes.add(node);
			timelineModel.fireStateChanged();
			return node;
		}

		public void removeNode(Node node) {
			nodes.remove(node);
			timelineModel.fireStateChanged();
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
	}

	public static class Node {
		private final Element parent;
		private int start;
		private int duration;
		private Object userData = null;

		public Node(Element parent, int delay, int duration) {
			this.parent = parent;
			this.start = delay;
			this.duration = duration;
		}

		public int getStart() {
			return start;
		}

		public int getDuration() {
			return duration;
		}

		public int getEnd() {
			return start + duration;
		}

		public Element getParent() {
			return parent;
		}

		public void setStart(int start) {
			if (this.start != start) {
				this.start = start;
				parent.getTimelineModel().fireStateChanged();
			}
		}

		public void setDuration(int duration) {
			if (this.duration != duration) {
				this.duration = duration;
				parent.getTimelineModel().fireStateChanged();
			}
		}

		public Object getUserData() {
			return userData;
		}

		public void setUserData(Object userData) {
			this.userData = userData;
		}
	}
}
