package aurelienribon.tweenstudio.ui.timeline2;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import aurelienribon.tweenstudio.ui.timeline.components.Scrollable;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TimelinePanel extends JPanel {
	public static void main(String[] args) {
		TimelineModel model = new TimelineModel()
			.addTarget("Sprite 1", new String[] {"Position", "Rotation", "Opacity"})
			.addTarget("Sprite 2", new String[] {"Position", "Rotation", "Opacity"})
			.addTarget("Sprite 3", new String[] {"Position", "Rotation", "Opacity"})
			.addNode("Sprite 1", "Position", new Node(600, 1000))
			.addNode("Sprite 2", "Rotation", new Node(200, 800));

		final TimelinePanel panel = new TimelinePanel();
		panel.setModel(model);

		JFrame frame = new JFrame();
		frame.getContentPane().add(panel);
		frame.setSize(1000, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private TimelineModel model;
	
    public TimelinePanel() {
		setOpaque(true);
		setBackground(Theme.COLOR_BACKGROUND);
		setLayout(new BorderLayout());
		add(new MenuBarPanel(), BorderLayout.NORTH);
		add(new ScrollPanel(new Scrollable() {

			@Override
			public void requestHorizontalScroll(float speed) {
			}

			@Override
			public void requestVerticalScroll(float position) {
			}

			@Override
			public int getPreferredHeight() {
				return 100;
			}

			@Override
			public int getVerticalOffset() {
				return 0;
			}
		}), BorderLayout.CENTER);
	}

	public TimelineModel getModel() {
		return model;
	}

	public void setModel(TimelineModel model) {
		this.model = model;
	}
}
