package aurelienribon.common;

import java.util.ArrayList;
import java.util.EventListener;

public class EventManager {
	public interface SimpleEventListener extends EventListener {
		public void onEvent();
	}

	protected final ArrayList<EventListener> listeners = new ArrayList<EventListener>();

	public <T extends EventListener> void addListener(T listener) {
		listeners.add(listener);
	}

	public <T extends EventListener> void removeListener(T listener) {
		listeners.remove(listener);
	}

	public <T extends SimpleEventListener> void fireSimpleEvent(Class<T> listenersClass) {
		for (EventListener listener : listeners)
			if (listenersClass.isAssignableFrom(listener.getClass()))
				((SimpleEventListener)listener).onEvent();
	}
}
