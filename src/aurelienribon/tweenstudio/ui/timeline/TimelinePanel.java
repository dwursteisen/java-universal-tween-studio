package aurelienribon.tweenstudio.ui.timeline;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TimelinePanel extends JPanel {
	private final TimelineModel model;
	private final Thread updateThread;
	private final List<Runnable> updateRunnables;

	private final DrawableMenuBar menuBarPanel;
	private final DrawableGrid gridPanel;
	private final DrawableScrollBars scrollBarsPanel;

	private int currentTime = 0;
	private int selectedLine = 2;

	public static void main(String[] args) {
		TimelineModel model = new TimelineModel()
			.addTarget("Sprite 1", new String[] {"Position", "Rotation", "Opacity"})
			.addTarget("Sprite 2", new String[] {"Position", "Rotation", "Opacity"})
			.addTarget("Sprite 3", new String[] {"Position", "Rotation", "Opacity"})
			.addNode("Sprite 1", "Position", new Node(600, 1000))
			.addNode("Sprite 2", "Rotation", new Node(200, 800));

		final TimelinePanel panel = new TimelinePanel(model);

		JFrame frame = new JFrame();
		frame.getContentPane().add(panel);
		frame.setSize(1000, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	public TimelinePanel(TimelineModel model) {
		this.model = model;
		this.updateThread = new Thread(masterUpdateRunnable);
		this.updateRunnables = new ArrayList<Runnable>();

		this.menuBarPanel = new DrawableMenuBar(this);
		this.gridPanel = new DrawableGrid(this);
		this.scrollBarsPanel = new DrawableScrollBars(this, gridPanel);

		addMouseListener(menuBarPanel.mouseAdapter);
		addMouseMotionListener(menuBarPanel.mouseAdapter);
		addMouseWheelListener(menuBarPanel.mouseAdapter);

		addMouseListener(gridPanel.mouseAdapter);
		addMouseMotionListener(gridPanel.mouseAdapter);
		addMouseWheelListener(gridPanel.mouseAdapter);

		addMouseListener(scrollBarsPanel.mouseAdapter);
		addMouseMotionListener(scrollBarsPanel.mouseAdapter);
		addMouseWheelListener(scrollBarsPanel.mouseAdapter);

		updateThread.start();
	}

	public TimelineModel getModel() {
		return model;
	}

	public int getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(int currentTime) {
		this.currentTime = currentTime;
	}

	public int getSelectedLine() {
		return selectedLine;
	}

	public void setSelectedLine(int selectedLine) {
		this.selectedLine = selectedLine;
	}

	void requestNewNode() {
		if (selectedLine < 0)
			return;
		
		int line = -1;
		String[] targets = model.getTargets();
		for (int i=0; i<targets.length; i++) {
			line += 1;
			String target = targets[i];
			String[] attrs = model.getAttrs(target);
			for (int j=0; j<attrs.length; j++) {
				line += 1;
				String attr = attrs[j];
				if (selectedLine == line)
					model.addNode(target, attr, new Node(currentTime, 0));
			}
		}
	}

	void registerUpdateRunnable(Runnable runnable) {
		updateRunnables.add(runnable);
	}

	// -------------------------------------------------------------------------
	// DRAWING
	// -------------------------------------------------------------------------

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D gg = (Graphics2D)g;
		
		gridPanel.draw(gg);
		menuBarPanel.draw(gg);
		scrollBarsPanel.draw(gg);
	}

	// -------------------------------------------------------------------------
	// UPDATE
	// -------------------------------------------------------------------------

	private Runnable masterUpdateRunnable = new Runnable() {
		@Override
		public void run() {
			while (true) {
				for (Runnable runnable : updateRunnables)
					runnable.run();
				try { Thread.sleep(16);
				} catch (InterruptedException ex) {
				}
			}
		}
	};
}
