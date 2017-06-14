package aurelienribon.tweenstudio;

import java.util.Map;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
class ImportExportStudioHelper {

	public static void timelineToModel(Timeline timeline, TimelineModel model, Map<Object, String> targetsNamesMap, Editor editor) {
		for (BaseTween child : timeline.getChildren()) {
			Tween tween = (Tween) child;

			String targetName = targetsNamesMap.get(tween.getTarget());
			String propertyName = editor.getProperty(tween.getTarget(), tween.getAccessor(), tween.getType()).name;

			Element elem = model.getElement(targetName + "/" + propertyName);
			if (elem != null) {
				NodeData nodeData = new NodeData(tween.getCombinedAttributesCount());
				nodeData.setEquation(tween.getEasing());
				nodeData.setTargets(tween.getTargetValues());

				Node node = elem.addNode((int)tween.getFullDuration()*1000);
				node.setLinked(true);
				node.setUserData(nodeData);
			} else {
				System.err.println("'" + targetName + "/" + propertyName + "' was not found in the model.");
			}
		}
	}

}
