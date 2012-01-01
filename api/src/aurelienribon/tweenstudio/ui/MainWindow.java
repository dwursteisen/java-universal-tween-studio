package aurelienribon.tweenstudio.ui;

import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenstudio.NodeData;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import aurelienribon.tweenstudio.ui.timeline.TimelinePanel.Listener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class MainWindow extends javax.swing.JFrame {
	private final EaseChangeListener easeListener = new EaseChangeListener();
	private final JSpinner[] targetSpinners;
	private Callback callback;
	private Node selectedNode;

	public MainWindow() {
		initComponents();
		tweenAttrsPanel.setVisible(false);
		easingCbox.addActionListener(easeListener);
		timelinePanel.addListener(new Listener() {
			@Override public void playRequested() {callback.playRequested();}
			@Override public void pauseRequested() {callback.pauseRequested();}
			@Override public void selectedElementChanged(Element newElem, Element oldElem) {}

			@Override public void selectedNodesChanged(List<Node> newNodes, List<Node> oldNodes) {
				selectedNode = null;
				updateTargetsValues();
			}

			@Override public void currentTimeChanged(int newTime, int oldTime) {
				callback.timeCursorPositionChanged(oldTime, newTime);
			}
		});

		targetSpinners = new JSpinner[] {t1Spinner, t2Spinner, t3Spinner, t4Spinner, t5Spinner};
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public void setTimelineModel(TimelineModel model) {
		timelinePanel.setModel(model);
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public int getCurrentTime() {
		return timelinePanel.getCurrentTime();
	}

	public void setCurrentTime(int time) {
		timelinePanel.setCurrentTime(time);
	}

	public boolean isPlaying() {
		return timelinePanel.isPlaying();
	}

	public void setPlaying(boolean playing) {
		timelinePanel.setPlaying(playing);
	}

	public void updateTargetsValues() {
		updateTargetsValues(selectedNode);
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void updateTargetsValues(Node node) {
		easeListener.setNode(node);
		showTargets(0);

		if (node == null) {
			tweenAttrsPanel.setVisible(false);
			return;
		}

		NodeData nodeData = (NodeData) node.getUserData();
		float[] values = nodeData.getTargets();
		TweenEquation equation = nodeData.getEquation();

		tweenAttrsPanel.setVisible(true);
		easingCbox.setSelectedItem(equation.toString());
		showTargets(values.length);

		for (int i=0; i<values.length; i++) {
			for (ChangeListener l : targetSpinners[i].getChangeListeners())
				targetSpinners[i].removeChangeListener(l);
			targetSpinners[i].addChangeListener(new TargetChangeListener(node, i));
			targetSpinners[i].setValue(values[i]);
		}
	}

	private void showTargets(int cnt) {
		t1Lbl.setVisible(cnt >= 1);
		t1Spinner.setVisible(cnt >= 1);
		t2Lbl.setVisible(cnt >= 2);
		t2Spinner.setVisible(cnt >= 2);
		t3Lbl.setVisible(cnt >= 3);
		t3Spinner.setVisible(cnt >= 3);
		t4Lbl.setVisible(cnt >= 4);
		t4Spinner.setVisible(cnt >= 4);
		t5Lbl.setVisible(cnt >= 5);
		t5Spinner.setVisible(cnt >= 5);
	}

	private class EaseChangeListener implements ActionListener {
		private Node node;

		public void setNode(Node node) {
			this.node = node;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			assert node != null;

			String name = (String) easingCbox.getSelectedItem();
			if (name.startsWith("-")) {
				easingCbox.setSelectedIndex(0);
				name = "Linear.INOUT";
			}

			TweenEquation equation = TweenEquation.parse(name);

			NodeData nodeData = (NodeData) node.getUserData();
			nodeData.setEquation(equation);
			callback.nodeInfoChanged(node);
		}
	}

	private class TargetChangeListener implements ChangeListener {
		private final Node node;
		private final int targetIdx;

		public TargetChangeListener(Node node, int targetIdx) {
			this.node = node;
			this.targetIdx = targetIdx;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			JSpinner spinner = (JSpinner) e.getSource();
			NodeData nodeData = (NodeData) node.getUserData();

			float val = ((Number)spinner.getValue()).floatValue();
			nodeData.getTargets()[targetIdx] = val;
			callback.nodeInfoChanged(node);
		}
	}

	// -------------------------------------------------------------------------
	// Callback
	// -------------------------------------------------------------------------

	public interface Callback {
		public void timeCursorPositionChanged(int oldTime, int newTime);
		public void nodeInfoChanged(Node node);
		public void playRequested();
		public void pauseRequested();
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        timelinePanel = new aurelienribon.tweenstudio.ui.timeline.TimelinePanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tweenAttrsPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        easingCbox = new javax.swing.JComboBox();
        startLbl = new javax.swing.JLabel();
        durationLbl = new javax.swing.JLabel();
        t1Lbl = new javax.swing.JLabel();
        t1Spinner = new javax.swing.JSpinner();
        t2Lbl = new javax.swing.JLabel();
        t2Spinner = new javax.swing.JSpinner();
        t3Lbl = new javax.swing.JLabel();
        t3Spinner = new javax.swing.JSpinner();
        t4Lbl = new javax.swing.JLabel();
        t4Spinner = new javax.swing.JSpinner();
        t5Lbl = new javax.swing.JLabel();
        t5Spinner = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tween Studio");
        getContentPane().add(timelinePanel, java.awt.BorderLayout.CENTER);

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));

        jPanel2.setOpaque(false);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/aurelienribon/tweenstudio/gfx/logo.png"))); // NOI18N

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("<html> <p align=\"center\">v0.2 - 2011 - Aurélien Ribon<br/><font color=\"#6eccff\">www.aurelienribon.com</font></p>");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tweenAttrsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Selected tween attributes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(255, 255, 255))); // NOI18N
        tweenAttrsPanel.setOpaque(false);

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Start time:");

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Duration:");

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Easing:");

        easingCbox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Linear.INOUT", "----------", "Quad.IN", "Quad.OUT", "Quad.INOUT", "----------", "Cubic.IN", "Cubic.OUT", "Cubic.INOUT", "----------", "Quart.IN", "Quart.OUT", "Quart.INOUT", "----------", "Quint.IN", "Quint.OUT", "Quint.INOUT", "----------", "Circ.IN", "Circ.OUT", "Circ.INOUT", "----------", "Sine.IN", "Sine.OUT", "Sine.INOUT", "----------", "Expo.IN", "Expo.OUT", "Expo.INOUT", "----------", "Back.IN", "Back.OUT", "Back.INOUT", "----------", "Bounce.IN", "Bounce.OUT", "Bounce.INOUT", "----------", "Elastic.IN", "Elastic.OUT", "Elastic.INOUT" }));

        startLbl.setForeground(new java.awt.Color(255, 255, 255));
        startLbl.setText("---");

        durationLbl.setForeground(new java.awt.Color(255, 255, 255));
        durationLbl.setText("---");

        t1Lbl.setForeground(new java.awt.Color(255, 255, 255));
        t1Lbl.setText("Target 1:");

        t1Spinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));

        t2Lbl.setForeground(new java.awt.Color(255, 255, 255));
        t2Lbl.setText("Target 2:");

        t2Spinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));

        t3Lbl.setForeground(new java.awt.Color(255, 255, 255));
        t3Lbl.setText("Target 3:");

        t3Spinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));

        t4Lbl.setForeground(new java.awt.Color(255, 255, 255));
        t4Lbl.setText("Target 4:");

        t4Spinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));

        t5Lbl.setForeground(new java.awt.Color(255, 255, 255));
        t5Lbl.setText("Target 5:");

        t5Spinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));

        javax.swing.GroupLayout tweenAttrsPanelLayout = new javax.swing.GroupLayout(tweenAttrsPanel);
        tweenAttrsPanel.setLayout(tweenAttrsPanelLayout);
        tweenAttrsPanelLayout.setHorizontalGroup(
            tweenAttrsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tweenAttrsPanelLayout.createSequentialGroup()
                .addGroup(tweenAttrsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tweenAttrsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(tweenAttrsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(t1Lbl)))
                    .addGroup(tweenAttrsPanelLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(t2Lbl))
                    .addGroup(tweenAttrsPanelLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(tweenAttrsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(t3Lbl)
                            .addComponent(t4Lbl)))
                    .addGroup(tweenAttrsPanelLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(t5Lbl)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tweenAttrsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(t5Spinner, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .addComponent(t4Spinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .addComponent(t3Spinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .addComponent(t2Spinner, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .addComponent(durationLbl)
                    .addComponent(startLbl)
                    .addComponent(easingCbox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 98, Short.MAX_VALUE)
                    .addComponent(t1Spinner, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))
                .addContainerGap())
        );
        tweenAttrsPanelLayout.setVerticalGroup(
            tweenAttrsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tweenAttrsPanelLayout.createSequentialGroup()
                .addGroup(tweenAttrsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(startLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tweenAttrsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(durationLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tweenAttrsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(easingCbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tweenAttrsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(t1Lbl)
                    .addComponent(t1Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tweenAttrsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(t2Lbl)
                    .addComponent(t2Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tweenAttrsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(t3Lbl)
                    .addComponent(t3Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tweenAttrsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(t4Lbl)
                    .addComponent(t4Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tweenAttrsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(t5Lbl)
                    .addComponent(t5Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(tweenAttrsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tweenAttrsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(109, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel durationLbl;
    private javax.swing.JComboBox easingCbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel startLbl;
    private javax.swing.JLabel t1Lbl;
    private javax.swing.JSpinner t1Spinner;
    private javax.swing.JLabel t2Lbl;
    private javax.swing.JSpinner t2Spinner;
    private javax.swing.JLabel t3Lbl;
    private javax.swing.JSpinner t3Spinner;
    private javax.swing.JLabel t4Lbl;
    private javax.swing.JSpinner t4Spinner;
    private javax.swing.JLabel t5Lbl;
    private javax.swing.JSpinner t5Spinner;
    private aurelienribon.tweenstudio.ui.timeline.TimelinePanel timelinePanel;
    private javax.swing.JPanel tweenAttrsPanel;
    // End of variables declaration//GEN-END:variables
}
