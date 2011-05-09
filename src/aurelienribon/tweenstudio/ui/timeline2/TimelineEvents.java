package aurelienribon.tweenstudio.ui.timeline2;

import aurelienribon.common.EventManager;
import aurelienribon.common.EventManager.SimpleEventListener;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import java.util.EventListener;

public class TimelineEvents extends EventManager {
	private static TimelineEvents instance;
	public static TimelineEvents instance() {
		if (instance == null)
			instance = new TimelineEvents();
		return instance;
	}

	public interface AddNodeRequestedListener extends SimpleEventListener {}
	public interface PlayRequestedListener extends SimpleEventListener {}
	public interface GoToFirstNodeRequestedListener extends SimpleEventListener {}
	public interface GoToPreviousNodeRequestedListener extends SimpleEventListener {}
	public interface GoToNextNodeRequestedListener extends SimpleEventListener {}
	public interface GoToLastNodeRequestedListener extends SimpleEventListener {}

	public interface CurrentTimeChangedListener extends EventListener {
		public void onEvent(int newMillis);
	}

	public void fireCurrentTimeChangedEvent(int newMillis) {
		for (EventListener listener : listeners)
			if (CurrentTimeChangedListener.class.isAssignableFrom(listener.getClass()))
				((CurrentTimeChangedListener)listener).onEvent(newMillis);
	}

	public interface ModelChangedListener extends EventListener {
		public void onEvent(TimelineModel newModel);
	}

	public void fireModelChangedEvent(TimelineModel newModel) {
		for (EventListener listener : listeners)
			if (ModelChangedListener.class.isAssignableFrom(listener.getClass()))
				((ModelChangedListener)listener).onEvent(newModel);
	}
}
