package aurelienribon.tweenstudio.ui.timeline;

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
			@Override public void actionPerformed(ActionEvent e) {fireMagnifyRequested();}
		});

		minBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {fireMinifyRequested();}
		});

		addNodeBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {fireAddNodeRequested();}
		});

		delNodeBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {fireDelNodeRequested();}
		});

		playBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {firePlayRequested();}
		});

		goToFirstBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {fireGoToFirstRequested();}
		});

		goToPreviousBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {fireGoToPreviousRequested();}
		});

		goToNextBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {fireGoToNextRequested();}
		});

		goToLastBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {fireGoToLastRequested();}
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

	// -------------------------------------------------------------------------
	// Events
	// -------------------------------------------------------------------------

	private final List<EventListener> listeners = new ArrayList<EventListener>(1);
	public void addListener(EventListener listener) {listeners.add(listener);}

	public interface EventListener {
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

	private void fireMagnifyRequested() {
		for (EventListener listener : listeners)
			listener.magnifyRequested();
	}

	private void fireMinifyRequested() {
		for (EventListener listener : listeners)
			listener.minifyRequested();
	}

	private void fireAddNodeRequested() {
		for (EventListener listener : listeners)
			listener.addNodeRequested();
	}

	private void fireDelNodeRequested() {
		for (EventListener listener : listeners)
			listener.delNodeRequested();
	}

	private void firePlayRequested() {
		for (EventListener listener : listeners)
			listener.playRequested();
	}

	private void fireGoToFirstRequested() {
		for (EventListener listener : listeners)
			listener.goToFirstRequested();
	}

	private void fireGoToPreviousRequested() {
		for (EventListener listener : listeners)
			listener.goToPreviousRequested();
	}

	private void fireGoToNextRequested() {
		for (EventListener listener : listeners)
			listener.goToNextRequested();
	}

	private void fireGoToLastRequested() {
		for (EventListener listener : listeners)
			listener.goToLastRequested();
	}
}
