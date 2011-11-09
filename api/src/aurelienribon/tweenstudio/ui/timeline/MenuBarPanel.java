package aurelienribon.tweenstudio.ui.timeline;

import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MenuBarPanel extends JPanel {
	private final ImageButton addNodeBtn;
	private final ImageButton playBtn;
	private final ImageButton goToFirstBtn;
	private final ImageButton goToPreviousBtn;
	private final ImageButton goToNextBtn;
	private final ImageButton goToLastBtn;
	private final JLabel timeLbl;
	private final int margin = 2;
	private final int bigMargin = 20;

    public MenuBarPanel() {
		addNodeBtn = new ImageButton("ic_addNode.png");
		playBtn = new ImageButton("ic_play.png");
		goToFirstBtn = new ImageButton("ic_goToFirst.png");
		goToPreviousBtn = new ImageButton("ic_goToPrevious.png");
		goToNextBtn = new ImageButton("ic_goToNext.png");
		goToLastBtn = new ImageButton("ic_goToLast.png");

		timeLbl = new JLabel();
		timeLbl.setFont(Theme.FONT);
		timeLbl.setForeground(Theme.COLOR_FOREGROUND);
		setTime(0);

		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
		btnPanel.setOpaque(false);
		btnPanel.add(addNodeBtn); btnPanel.add(Box.createHorizontalStrut(margin));
		btnPanel.add(timeLbl); btnPanel.add(Box.createHorizontalStrut(bigMargin));
		btnPanel.add(goToFirstBtn); btnPanel.add(Box.createHorizontalStrut(margin));
		btnPanel.add(goToPreviousBtn); btnPanel.add(Box.createHorizontalStrut(margin));
		btnPanel.add(playBtn); btnPanel.add(Box.createHorizontalStrut(margin));
		btnPanel.add(goToNextBtn); btnPanel.add(Box.createHorizontalStrut(margin));
		btnPanel.add(goToLastBtn);

		setBackground(Theme.COLOR_MENUBAR_BACKGROUND);
		setLayout(new BorderLayout());
		add(btnPanel, BorderLayout.EAST);
	}

	public void setTime(int millis) {
		String str = String.format("%02d,%03d", millis / 1000, millis % 1000);
		timeLbl.setText(str);
	}
}
