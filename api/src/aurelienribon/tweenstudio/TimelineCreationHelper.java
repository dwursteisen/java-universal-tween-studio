package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenstudio.ui.timeline.TimelineHelper;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import java.util.Map;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
class TimelineCreationHelper {
	public static void copy(Timeline tl1, Timeline tl2) {
		for (int i=tl2.getChildren().size()-1; i>=0; i--)
			tl2.getChildren().remove(i);

		for (BaseTween child : tl1.getChildren()) {
			Tween t = (Tween) child;
			Tween tween = Tween.to(t.getTarget(), t.getType(), t.getDuration())
				.target(t.getTargetValues())
				.ease(t.getEasing())
				.delay(t.getDelay());
			tl2.push(tween);
		}
	}

	public static Timeline createTimelineFromModel(TimelineModel model, int currentTime, Map<Object, InitialState> initialStatesMap) {
		Timeline tl = Timeline.createParallel();

		for (Element elem : model.getElements()) {
			if (!elem.isSelectable()) continue;

			for (Node node : elem.getNodes())
				if (node.getUserData() == null)
					createNodeData(node);

			ElementData elemData = (ElementData) elem.getUserData();

			createTweens(tl, elem, elemData.getTarget(), elemData.getProperty().getId());
			setToInitialState(initialStatesMap, elemData.getTarget(), elemData.getProperty().getId());
		}

		tl.start();

		int time = -1, lastTime = 0, acc = 0;

		while (true) {
			time = TimelineHelper.getNextTime(model.getRoot(), time, true);
			if (time < 0) break;

			int inc = (int) Math.ceil((time - lastTime) / 1000f);
			tl.update(inc);
			acc += inc;
			lastTime = time;
		}

		tl.update((int) (Math.ceil(currentTime / 1000f)) - acc);
		return tl;
	}

	private static void createTweens(Timeline tl, Element elem, Object target, int tweenType) {
		for (Node node : elem.getNodes()) {
			NodeData nodeData = (NodeData) node.getUserData();

			int duration = TimelineHelper.getDuration(node) / 1000;
			int delay = node.getTime() / 1000 - duration;

			Tween tween = Tween.to(target, tweenType, duration)
				.target(nodeData.getTargets())
				.ease(nodeData.getEquation())
				.delay(delay);

			tl.push(tween);
		}
	}

	private static void createNodeData(Node node) {
		ElementData elemData = (ElementData) node.getParent().getUserData();
		Property property = elemData.getProperty();

		NodeData nodeData = new NodeData(property.getFields().length);
		TweenAccessor accessor = Tween.getRegisteredAccessor(elemData.getTarget().getClass());
		accessor.getValues(elemData.getTarget(), property.getId(), nodeData.getTargets());
		node.setUserData(nodeData);
	}

	private static void setToInitialState(Map<Object, InitialState> initialStatesMap, Object target, int tweenType) {
		InitialState initState = initialStatesMap.get(target);
		float[] initValues = initState.getValues(tweenType);

		TweenAccessor accessor = Tween.getRegisteredAccessor(target.getClass());
		accessor.setValues(target, tweenType, initValues);
	}
}
