package aurelienribon.tweenstudio.ui.timeline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TimelineModel {
    private final Element root = new Element("");
	private int duration = 0;

	// -------------------------------------------------------------------------
	// public API
	// -------------------------------------------------------------------------

	public Element addElement(String[] names) {
		Element added = null;
		Element elem = root;
		for (String name : names) {
			added = new Element(name);
			if (elem.getChild(name) == null)
				elem.getChildren().add(added);
			elem = elem.getChild(name);
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
	}

	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	public interface ElementAction {
		public boolean apply(Element elem);
	}

	public static class Element {
		private final String name;
		private final List<Element> children = new ArrayList<Element>();
		private final List<Node> nodes = new ArrayList<Node>();
		private boolean selectable = true;
		private Object userData = null;

		public Element(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public List<Element> getChildren() {
			return children;
		}

		public List<Node> getNodes() {
			return nodes;
		}

		public Element getChild(String name) {
			for (Element child : children)
				if (child.getName().equals(name))
					return child;
			return null;
		}

		public void addNode(int start, int duration) {
			nodes.add(new Node(start, duration));
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
		private int start;
		private int duration;

		public Node(int delay, int duration) {
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
			this.start = start;
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}
	}
}
