package aurelienribon.tweenstudio.ui.timeline2;

import aurelienribon.tweenstudio.ui.timeline.components.ImageButton;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

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
		addNodeBtn = new ImageButton(getClass().getResource("../timeline/gfx/ic_addNode.png"));
		playBtn = new ImageButton(getClass().getResource("../timeline/gfx/ic_play.png"));
		goToFirstBtn = new ImageButton(getClass().getResource("../timeline/gfx/ic_goToFirst.png"));
		goToPreviousBtn = new ImageButton(getClass().getResource("../timeline/gfx/ic_goToPrevious.png"));
		goToNextBtn = new ImageButton(getClass().getResource("../timeline/gfx/ic_goToNext.png"));
		goToLastBtn = new ImageButton(getClass().getResource("../timeline/gfx/ic_goToLast.png"));
		initEvents();

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

		setBorder(new CompoundBorder(
			new MatteBorder(0, 0, 1, 0, Theme.COLOR_FOREGROUND),
			new EmptyBorder(margin, margin, margin, margin)));
		setOpaque(false);
		setLayout(new BorderLayout());
		add(btnPanel, BorderLayout.EAST);
	}

	private void setTime(int millis) {
		String str = String.format("%02d,%03d", millis / 1000, millis % 1000);
		timeLbl.setText(str);
	}

	private void initEvents() {
		addNodeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TimelineEvents.instance().fireSimpleEvent(TimelineEvents.AddNodeRequestedListener.class);
			}
		});
		playBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TimelineEvents.instance().fireSimpleEvent(TimelineEvents.PlayRequestedListener.class);
			}
		});
		goToFirstBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TimelineEvents.instance().fireSimpleEvent(TimelineEvents.GoToFirstNodeRequestedListener.class);
			}
		});
		goToPreviousBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TimelineEvents.instance().fireSimpleEvent(TimelineEvents.GoToPreviousNodeRequestedListener.class);
			}
		});
		goToNextBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TimelineEvents.instance().fireSimpleEvent(TimelineEvents.GoToNextNodeRequestedListener.class);
			}
		});
		goToLastBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TimelineEvents.instance().fireSimpleEvent(TimelineEvents.GoToLastNodeRequestedListener.class);
			}
		});

		TimelineEvents.instance().addListener(new TimelineEvents.CurrentTimeChangedListener() {
			@Override
			public void onEvent(int millis) {
				setTime(millis);
			}
		});
	}
}
