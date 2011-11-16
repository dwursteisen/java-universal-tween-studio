package aurelienribon.tweenstudio;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class IoHelper {
	public static String readStream(InputStream is) throws IOException {
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		Reader reader = new BufferedReader(new InputStreamReader(is));

		int n;
		while ((n = reader.read(buffer)) != -1) {
			writer.write(buffer, 0, n);
		}

		return writer.toString();
	}

	public static void writeStream(OutputStream os, String str) throws IOException {
		Writer writer = new BufferedWriter(new OutputStreamWriter(os));
		writer.write(str);
	}

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
			}
		}

		return str;
	}

	public static TimelineModel stringToModel(String str) {
		TimelineModel model = new TimelineModel();
		return model;
	}
}
