package aurelienribon.tweenstudio.ui.timeline;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
class MenuBarPanel extends JPanel {
	private final ImageButton magBtn;
	private final ImageButton minBtn;
	private final ImageButton addNodeBtn;
	private final ImageButton delNodeBtn;
	private final ImageButton playBtn;
	private final ImageButton goToFirstBtn;
	private final ImageButton goToPreviousBtn;
	private final ImageButton goToNextBtn;
	private final ImageButton goToLastBtn;
	private final JLabel timeLbl;
	private final int margin = 2;
	private final int bigMargin = 20;
	private Callback callback;

    public MenuBarPanel(final TimelinePanel parent) {
		Theme theme = parent.getTheme();

		magBtn = new ImageButton(theme.COLOR_MENUBAR_BACKGROUND, "ic_glassPlus.png");
		minBtn = new ImageButton(theme.COLOR_MENUBAR_BACKGROUND, "ic_glassMinus.png");
		addNodeBtn = new ImageButton(theme.COLOR_MENUBAR_BACKGROUND, "ic_addNode.png");
		delNodeBtn = new ImageButton(theme.COLOR_MENUBAR_BACKGROUND, "ic_delNode.png");
		playBtn = new ImageButton(theme.COLOR_MENUBAR_BACKGROUND, "ic_play.png").addImage("ic_pause.png");
		goToFirstBtn = new ImageButton(theme.COLOR_MENUBAR_BACKGROUND, "ic_goToFirst.png");
		goToPreviousBtn = new ImageButton(theme.COLOR_MENUBAR_BACKGROUND, "ic_goToPrevious.png");
		goToNextBtn = new ImageButton(theme.COLOR_MENUBAR_BACKGROUND, "ic_goToNext.png");
		goToLastBtn = new ImageButton(theme.COLOR_MENUBAR_BACKGROUND, "ic_goToLast.png");

		magBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				callback.magnifyRequested();
			}
		});

		minBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				callback.minifyRequested();
			}
		});

		addNodeBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				for (Element elem : parent.getSelectedElements()) {
					for (Element child : elem.getChildren())
						child.addNode(parent.getCurrentTime());
				}
			}
		});

		delNodeBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				List<Node> nodes = new ArrayList<Node>(parent.getSelectedNodes());
				parent.clearSelectedNodes();
				for (Node node : nodes) node.getParent().removeNode(node);
			}
		});

		playBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if (playBtn.getImageIdx() == 0) callback.playRequested();
				else callback.pauseRequested();
			}
		});

		goToFirstBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				int time = TimelineHelper.getFirstTime(parent.getModel().getRoot(), true);
				if (time > -1) parent.setCurrentTime(time);
			}
		});

		goToPreviousBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				int time = TimelineHelper.getPreviousTime(parent.getModel().getRoot(), parent.getCurrentTime(), true);
				if (time > -1) parent.setCurrentTime(time);
			}
		});

		goToNextBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				int time = TimelineHelper.getNextTime(parent.getModel().getRoot(), parent.getCurrentTime(), true);
				if (time > -1) parent.setCurrentTime(time);
			}
		});

		goToLastBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				int time = TimelineHelper.getLastTime(parent.getModel().getRoot(), true);
				if (time > -1) parent.setCurrentTime(time);
			}
		});

		timeLbl = new JLabel();
		timeLbl.setForeground(theme.COLOR_FOREGROUND);
		timeLbl.setFont(theme.FONT);
		setTime(0);

		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
		btnPanel.setOpaque(false);

		btnPanel.add(magBtn); btnPanel.add(Box.createHorizontalStrut(margin));
		btnPanel.add(minBtn); btnPanel.add(Box.createHorizontalStrut(bigMargin));

		btnPanel.add(addNodeBtn); btnPanel.add(Box.createHorizontalStrut(margin));
		btnPanel.add(delNodeBtn); btnPanel.add(Box.createHorizontalStrut(bigMargin));

		btnPanel.add(timeLbl); btnPanel.add(Box.createHorizontalStrut(bigMargin));

		btnPanel.add(goToFirstBtn); btnPanel.add(Box.createHorizontalStrut(margin));
		btnPanel.add(goToPreviousBtn); btnPanel.add(Box.createHorizontalStrut(margin));
		btnPanel.add(playBtn); btnPanel.add(Box.createHorizontalStrut(margin));
		btnPanel.add(goToNextBtn); btnPanel.add(Box.createHorizontalStrut(margin));
		btnPanel.add(goToLastBtn);

		setBackground(theme.COLOR_MENUBAR_BACKGROUND);
		setLayout(new BorderLayout());
		add(btnPanel, BorderLayout.EAST);
	}

	public final void setTime(int time) {
		int millis = time / 1000;
		String str = String.format("%02d,%03d", millis / 1000, millis % 1000);
		timeLbl.setText(str);
	}

	public void themeChanged(Theme theme) {
		timeLbl.setForeground(theme.COLOR_FOREGROUND);
		timeLbl.setFont(theme.FONT);
		setBackground(theme.COLOR_MENUBAR_BACKGROUND);
		repaint();
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public void setPlayBtnVisible() {
		playBtn.setImageIdx(0);
	}

	public void setPauseBtnVisible() {
		playBtn.setImageIdx(1);
	}

	// -------------------------------------------------------------------------
	// Callback
	// -------------------------------------------------------------------------

	public interface Callback {
		public void magnifyRequested();
		public void minifyRequested();
		public void playRequested();
		public void pauseRequested();
	}
}
