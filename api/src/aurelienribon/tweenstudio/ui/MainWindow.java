package aurelienribon.tweenstudio.ui;

import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenstudio.NodeData;
import aurelienribon.tweenstudio.TweenHelper;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import aurelienribon.tweenstudio.ui.timeline.TimelinePanel.EventListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class MainWindow extends javax.swing.JFrame {
	private final EaseChangeListener easeListener = new EaseChangeListener();
	private Callback callback;
	private Node selectedNode;

	public MainWindow() {
		initComponents();
		easeFuncPanel.setVisible(false);

		easeTypeCBox.addActionListener(easeListener);
		easeEquationCbox.addActionListener(easeListener);

		timelinePanel.addListener(new EventListener() {
			@Override public void selectedElementChanged(Element element) {}
			@Override public void selectedNodeChanged(Node node) {selectedNode = node; updateTargetsValues(node);}
			@Override public void playRequested() {callback.playRequested();}
			@Override public void pauseRequested() {callback.pauseRequested();}
			@Override public void timeCursorPositionChanged(int oldTime, int newTime) {callback.timeCursorPositionChanged(oldTime, newTime);}
		});
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

	public int getTimeCursorPosition() {
		return timelinePanel.getTimeCursorPosition();
	}

	public void setTimeCursorPosition(int time) {
		timelinePanel.setTimeCursorPosition(time);
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
		targetsPanel.removeAll();

		if (node == null) {
			easeFuncPanel.setVisible(false);
			targetsPanel.revalidate();
			targetsPanel.repaint();
			return;
		}

		NodeData nodeData = (NodeData) node.getUserData();
		float[] values = nodeData.getTargets();
		TweenEquation equation = nodeData.getEquation();

		easeFuncPanel.setVisible(true);
		String[] equationParts = equation.toString().split("\\.");
		easeEquationCbox.setSelectedItem(equationParts[0]);
		easeTypeCBox.setSelectedItem(equationParts[1]);

		for (int i=0; i<values.length; i++) {
			JLabel label = new JLabel("Target " + (i+1) + ": ");
			label.setForeground(Color.WHITE);

			SpinnerNumberModel spinnerModel = new SpinnerNumberModel();
			spinnerModel.setValue(values[i]);
			JSpinner spinner = new JSpinner(spinnerModel);
			spinner.addChangeListener(new TargetChangeListener(node, i));

			JPanel panel = new JPanel(new BorderLayout());
			panel.setBorder(new EmptyBorder(2, 0, 2, 0));
			panel.setOpaque(false);
			panel.add(label, BorderLayout.WEST);
			panel.add(spinner, BorderLayout.CENTER);
			panel.setMaximumSize(new Dimension(2000, 25));
			targetsPanel.add(panel);
		}

		targetsPanel.revalidate();
		targetsPanel.repaint();
	}

	private class EaseChangeListener implements ActionListener {
		private Node node;

		public void setNode(Node node) {
			this.node = node;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			assert node != null;
			String name = easeEquationCbox.getSelectedItem() + "." + easeTypeCBox.getSelectedItem();
			TweenEquation equation = TweenHelper.getEquation(name);

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
        jPanel3 = new javax.swing.JPanel();
        easeFuncPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        easeTypeCBox = new javax.swing.JComboBox();
        easeEquationCbox = new javax.swing.JComboBox();
        targetsPanel = new javax.swing.JPanel();

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
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE))
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

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Selected tween attributes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel3.setOpaque(false);

        easeFuncPanel.setOpaque(false);

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Easing function");

        easeTypeCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "IN", "OUT", "INOUT" }));

        easeEquationCbox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Linear", "Quad", "Cubic", "Quart", "Quint", "Expo", "Sine", "Circ", "Bounce", "Back", "Elastic" }));

        javax.swing.GroupLayout easeFuncPanelLayout = new javax.swing.GroupLayout(easeFuncPanel);
        easeFuncPanel.setLayout(easeFuncPanelLayout);
        easeFuncPanelLayout.setHorizontalGroup(
            easeFuncPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, easeFuncPanelLayout.createSequentialGroup()
                .addComponent(easeEquationCbox, 0, 67, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(easeTypeCBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        easeFuncPanelLayout.setVerticalGroup(
            easeFuncPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(easeFuncPanelLayout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(easeFuncPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(easeEquationCbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(easeTypeCBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        targetsPanel.setOpaque(false);
        targetsPanel.setLayout(new javax.swing.BoxLayout(targetsPanel, javax.swing.BoxLayout.PAGE_AXIS));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(easeFuncPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(targetsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(easeFuncPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(targetsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox easeEquationCbox;
    private javax.swing.JPanel easeFuncPanel;
    private javax.swing.JComboBox easeTypeCBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel targetsPanel;
    private aurelienribon.tweenstudio.ui.timeline.TimelinePanel timelinePanel;
    // End of variables declaration//GEN-END:variables
}
