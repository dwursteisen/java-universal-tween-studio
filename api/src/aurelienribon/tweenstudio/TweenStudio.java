package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.Tweenable;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenstudio.ui.MainWindow;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TweenStudio {
	private final TweenManager tweenManager;
	private final List<Tweenable> tweenables = new ArrayList<Tweenable>(5);
	private final Map<Tweenable, String> namesMap = new HashMap<Tweenable, String>(5);
	private Map<Tweenable, InitialState> initialStatesMap;

	public TweenStudio(TweenManager tweenManager) {
		this.tweenManager = tweenManager;
	}

	public void registerTweenable(Tweenable tweenable, String name) {
		tweenables.add(tweenable);
		namesMap.put(tweenable, name);
	}

	public void edit(Editor editor) {
		editor.initialize();

		initialStatesMap = new HashMap<Tweenable, InitialState>();
		for (Tweenable tweenable : tweenables) {
			InitialState state = new InitialState(editor, tweenable);
			initialStatesMap.put(tweenable, state);
		}

		final TimelineModel model = new TimelineModel();
		for (Tweenable tweenable : tweenables) {
			Element elem = model.addElement(namesMap.get(tweenable));
			elem.setSelectable(false);

			for (Property property : editor.getProperties(tweenable.getClass())) {
				elem = model.addElement(namesMap.get(tweenable) + "/" + property.getName());
				elem.setUserData(new PropertyTuple(tweenable, property.getId()));
			}
		}

		MainWindow mw = new MainWindow();
		mw.setTimelineModel(model);
		mw.setSize(800, 500);
		mw.setVisible(true);
		mw.addWindowListener(new WindowAdapter() {
			@Override public void windowClosed(WindowEvent e) {
				run(model);
			}
		});
	}

	private void run(final TimelineModel model) {
		model.forAllElements(new TimelineModel.ElementAction() {
			@Override public boolean apply(Element elem) {
				if (elem.getNodes().isEmpty()) return false;
				PropertyTuple tuple = (PropertyTuple) elem.getUserData();

				for (Node n : elem.getNodes()) {
					Tween.to(tuple.getTweenable(), tuple.getTweenType(), n.getDuration(), Linear.INOUT)
						.repeat(0, model.getDuration() - n.getEnd())
						.addToManager(tweenManager);
				}
				return false;
			}
		});
	}

	// -------------------------------------------------------------------------
	// Classes
	// -------------------------------------------------------------------------

	private static class PropertyTuple {
		private final Tweenable tweenable;
		private final int tweenType;

		public PropertyTuple(Tweenable tweenable, int tweenType) {
			this.tweenable = tweenable;
			this.tweenType = tweenType;
		}

		public Tweenable getTweenable() {
			return tweenable;
		}

		public int getTweenType() {
			return tweenType;
		}
	}
}
