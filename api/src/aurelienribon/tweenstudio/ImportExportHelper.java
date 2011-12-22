package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class ImportExportHelper {
	public static String modelToString(TimelineModel model) {
		String str = "";

		for (Element elem : model.getElements()) {
			if (!elem.isSelectable() || elem.getNodes().isEmpty()) continue;

			for (Node node : elem.getNodes()) {
				NodeData nodeData = (NodeData) node.getUserData();

				str += elem.getParent().getName() + ";" + elem.getName() + ";"
					+ node.getStart() + ";" + node.getDuration() + ";"
					+ nodeData.getEquation().toString();

				for (int i=0; i<nodeData.getTargets().length; i++) {
					str += ";" + nodeData.getTargets()[i];
				}

				str += "\n";
			}
		}

		return str;
	}

	public static void stringToModel(String input, TimelineModel model) {
		String[] lines = input.split("\n");

		for (String line : lines) {
			String[] parts = line.split(";");
			if (parts.length < 5) continue;

			String elementPath = parts[0] + "/" + parts[1];
			int delay = Integer.parseInt(parts[2]);
			int duration = Integer.parseInt(parts[3]);
			TweenEquation equation = TweenEquation.parse(parts[4]);

			float[] targets = new float[parts.length-5];
			for (int i=0; i<targets.length; i++)
				targets[i] = Float.parseFloat(parts[i+5]);

			Element elem = model.getElement(elementPath);
			if (elem != null) {
				NodeData nodeData = new NodeData(targets.length);
				nodeData.setEquation(equation);
				nodeData.setTargets(targets);

				Node node = elem.addNode(delay, duration);
				node.setUserData(nodeData);

			} else {
				System.err.println("[W] '" + elementPath + "' is not part of the configured model.");
			}
		}
	}

	public static Timeline stringToTimeline(String input) {
		return null;
	}
}
