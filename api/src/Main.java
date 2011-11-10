
import aurelienribon.tweenstudio.ui.MainWindow;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class Main {
	public static void main(String[] args) {
		TimelineModel model = new TimelineModel();
		model.addElement("Sprite 1/Position");
		model.addElement("Sprite 1/Rotation");
		model.addElement("Sprite 1/Opacity");
		model.addElement("Sprite 2/Position");
		model.addElement("Sprite 2/Rotation");
		model.addElement("Sprite 2/Opacity");
		model.addElement("Sprite 3/Position");
		model.addElement("Sprite 3/Rotation");
		model.addElement("Sprite 3/Opacity");

		model.getElement("Sprite 1").setSelectable(false);
		model.getElement("Sprite 2").setSelectable(false);
		model.getElement("Sprite 3").setSelectable(false);
		model.getElement("Sprite 1/Position").addNode(600, 1000);
		model.getElement("Sprite 2/Rotation").addNode(200, 800);

		MainWindow mw = new MainWindow();
		mw.setTimelineModel(model);
		mw.setSize(800, 500);
		mw.setVisible(true);
	}
}
