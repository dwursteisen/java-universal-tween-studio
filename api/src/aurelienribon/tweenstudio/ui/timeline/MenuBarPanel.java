package aurelienribon.tweenstudio.ui.timeline;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public MenuBarPanel(Theme theme) {
		magBtn = new ImageButton("ic_glassPlus.png");
		minBtn = new ImageButton("ic_glassMinus.png");
		addNodeBtn = new ImageButton("ic_addNode.png");
		delNodeBtn = new ImageButton("ic_delNode.png");
		playBtn = new ImageButton("ic_play.png");
		goToFirstBtn = new ImageButton("ic_goToFirst.png");
		goToPreviousBtn = new ImageButton("ic_goToPrevious.png");
		goToNextBtn = new ImageButton("ic_goToNext.png");
		goToLastBtn = new ImageButton("ic_goToLast.png");

		magBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {callback.magnifyRequested();}
		});

		minBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {callback.minifyRequested();}
		});

		addNodeBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {callback.addNodeRequested();}
		});

		delNodeBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {callback.delNodeRequested();}
		});

		playBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {callback.playRequested();}
		});

		goToFirstBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {callback.goToFirstRequested();}
		});

		goToPreviousBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {callback.goToPreviousRequested();}
		});

		goToNextBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {callback.goToNextRequested();}
		});

		goToLastBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {callback.goToLastRequested();}
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

	public final void setTime(int millis) {
		String str = String.format("%02d,%03d", millis / 1000, millis % 1000);
		timeLbl.setText(str);
	}

	public void setTheme(Theme theme) {
		timeLbl.setForeground(theme.COLOR_FOREGROUND);
		timeLbl.setFont(theme.FONT);
		setBackground(theme.COLOR_MENUBAR_BACKGROUND);
		repaint();
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	// -------------------------------------------------------------------------
	// Callback
	// -------------------------------------------------------------------------

	public interface Callback {
		public void magnifyRequested();
		public void minifyRequested();
		public void addNodeRequested();
		public void delNodeRequested();
		public void playRequested();
		public void goToFirstRequested();
		public void goToPreviousRequested();
		public void goToNextRequested();
		public void goToLastRequested();
	}
}
