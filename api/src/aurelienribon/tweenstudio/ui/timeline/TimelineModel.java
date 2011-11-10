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

	public void forAllElements(ElementAction action) {
		_forAllElements(root, action);
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

	public String[] getPath(Element elem) {
		String path = _getPath(elem, root);
		return path != null ? path.split("/") : null;
	}

	public int getDuration() {
		duration = 0;
		forAllElements(new ElementAction() {
			@Override public boolean apply(Element elem) {
				for (Node n : elem.getNodes())
					duration = Math.max(duration, n.getEnd());
				return false;
			}
		});
		return duration;
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private boolean _forAllElements(Element elem, ElementAction action) {
		if (elem != root && action.apply(elem)) return true;
		for (Element child : elem.getChildren())
			if (_forAllElements(child, action))
				return true;
		return false;
	}

	private String _getPath(Element searchedElem, Element elem) {
		if (elem == searchedElem) return searchedElem.getName();
		for (Element child : elem.getChildren()) {
			String path = _getPath(searchedElem, child);
			if (path != null) return elem != root ? elem.getName() + "/" + path : path;
		}
		return null;
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

	public interface ElementAction {
		public boolean apply(Element elem);
	}

	public static class Element {
		private final TimelineModel timelineModel;
		private final Element parent;
		private final String name;
		private final List<Element> children = new ArrayList<Element>(0);
		private final List<Node> nodes = new ArrayList<Node>(0);
		private boolean selectable = true;
		private Object userData = null;

		public Element(TimelineModel timelineModel, Element parent, String name) {
			this.timelineModel = timelineModel;
			this.parent = parent;
			this.name = name;
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
			timelineModel.fireStateChanged();
			return child;
		}

		public Node addNode(int start, int duration) {
			Node node = new Node(this, start, duration);
			nodes.add(node);
			timelineModel.fireStateChanged();
			return node;
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

		public interface Callback {
			public void stateChanged();
		}
	}

	public static class Node {
		private final Element parent;
		private int start;
		private int duration;

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
	}
}
