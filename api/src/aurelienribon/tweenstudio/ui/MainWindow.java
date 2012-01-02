package aurelienribon.tweenstudio.ui;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenstudio.Editor;
import aurelienribon.tweenstudio.ElementData;
import aurelienribon.tweenstudio.NodeData;
import aurelienribon.tweenstudio.Property;
import aurelienribon.tweenstudio.Property.Field;
import aurelienribon.tweenstudio.ui.timeline.Theme;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import aurelienribon.tweenstudio.ui.timeline.TimelinePanel.Listener;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.Box;
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
	private final Theme theme = new Theme();
	private Editor editor;
	private Callback callback;

	public MainWindow() {
		initComponents();

		easingCbox.addItemListener(easeListener);
		timelinePanel.setTheme(theme);

		timelinePanel.addListener(new Listener() {
			@Override public void playRequested() {callback.playRequested();}
			@Override public void pauseRequested() {callback.pauseRequested();}
			@Override public void currentTimeChanged(int newTime, int oldTime) {callback.currentTimeChanged(newTime, oldTime);}

			@Override public void selectedElementChanged(Element newElem, Element oldElem) {
				CardLayout cl = (CardLayout) propertiesPanel.getLayout();
				if (newElem != null) {
					cl.show(propertiesPanel, "objectCard");
					buildObjectCard();
					updateObjectCard();
				} else {
					cl.show(propertiesPanel, "nothingCard");
				}
			}

			@Override public void selectedNodesChanged(List<Node> newNodes, List<Node> oldNodes) {
				CardLayout cl = (CardLayout) propertiesPanel.getLayout();
				if (!newNodes.isEmpty()) {
					cl.show(propertiesPanel, "tweenCard");
					updateTweenCard();
				} else {
					cl.show(propertiesPanel, "nothingCard");
				}
			}
		});
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public void initialize(Editor editor, TimelineModel model, Callback callback) {
		this.editor = editor;
		this.callback = callback;
		timelinePanel.setModel(model);

		model.addListener(new TimelineModel.Listener() {
			@Override public void stateChanged() {
				CardLayout cl = (CardLayout) propertiesPanel.getLayout();
				if (cl.toString().equals("objectCard")) updateObjectCard();
				else if (cl.toString().equals("tweenCard")) updateTweenCard();
			}
		});
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

	public void nodeDataChanged() {
		CardLayout cl = (CardLayout) propertiesPanel.getLayout();
		if (cl.toString().equals("objectCard")) updateObjectCard();
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void updateTweenCard() {
		TweenEquation commonEq = null;
		for (Node node : timelinePanel.getSelectedNodes()) {
			NodeData nodeData = (NodeData) node.getUserData();
			TweenEquation eq = nodeData.getEquation();
			if (commonEq == null) commonEq = eq;
			if (eq != commonEq) {commonEq = null; break;}
		}

		easingCbox.setEditable(true);
		easingCbox.setSelectedItem(commonEq != null ? commonEq.toString() : "---");
		easingCbox.setEditable(false);
	}

	private void updateObjectCard() {
		Element elem = timelinePanel.getSelectedElement();
		ElementData elemData = (ElementData) elem.getUserData();

		int cnt = 0;

		for (Property property : editor.getProperties(elemData.getTarget().getClass())) {
			float[] values = new float[property.getFields().length];
			TweenAccessor accessor = Tween.getRegisteredAccessor(elemData.getTarget().getClass());
			accessor.getValues(elemData.getTarget(), property.getId(), values);

			for (int i=0; i<property.getFields().length; i++) {
				JPanel panel = (JPanel) objectPanel.getComponent(cnt+1);
				JSpinner spinner = (JSpinner) panel.getComponent(1);
				spinner.setValue(values[i]);
				cnt += 1;
			}
		}
	}

	private void buildObjectCard() {
		Element elem = timelinePanel.getSelectedElement();
		ElementData elemData = (ElementData) elem.getUserData();

		objectLbl.setText("> " + elem.getName());
		
		objectPanel.removeAll();
		objectPanel.add(Box.createVerticalStrut(10));

		for (Property property : editor.getProperties(elemData.getTarget().getClass())) {
			for (int i=0; i<property.getFields().length; i++) {
				Field field = property.getFields()[i];
				
				JLabel label = new JLabel(field.name + ": ");
				label.setForeground(Color.WHITE);
				label.setHorizontalAlignment(JLabel.RIGHT);

				SpinnerNumberModel model = new SpinnerNumberModel(field.min, field.min, field.max, field.step);
				JSpinner spinner = new JSpinner(model);
				spinner.setPreferredSize(new Dimension(70, 20));

				JPanel panel = new JPanel(new BorderLayout());
				panel.setBorder(new EmptyBorder(0, 0, 2, 0));
				panel.setOpaque(false);
				panel.add(label, BorderLayout.CENTER);
				panel.add(spinner, BorderLayout.EAST);

				objectPanel.add(panel);
			}
		}

		objectPanel.add(Box.createVerticalStrut(10));
		objectPanel.revalidate();
	}

	/*private void updateTargetsValues(Node node) {
		easeListener.setNode(node);
		showTargets(0);

		if (node == null) {
			tweenPanel.setVisible(false);
			return;
		}

		NodeData nodeData = (NodeData) node.getUserData();
		float[] values = nodeData.getTargets();
		TweenEquation equation = nodeData.getEquation();

		tweenPanel.setVisible(true);
		easingCbox.setSelectedItem(equation.toString());
		showTargets(values.length);

		for (int i=0; i<values.length; i++) {
			for (ChangeListener l : targetSpinners[i].getChangeListeners())
				targetSpinners[i].removeChangeListener(l);
			targetSpinners[i].addChangeListener(new TargetChangeListener(node, i));
			targetSpinners[i].setValue(values[i]);
		}
	}*/

	private final ItemListener easeListener = new ItemListener() {
		@Override public void itemStateChanged(ItemEvent e) {
			String name = (String) easingCbox.getSelectedItem();
			if (!name.startsWith("-")) {
				TweenEquation equation = TweenEquation.parse(name);
				for (Node node : timelinePanel.getSelectedNodes()) {
					NodeData nodeData = (NodeData) node.getUserData();
					nodeData.setEquation(equation);
					callback.nodeInfoChanged(node);
				}
			}
		}
	};

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
		public void currentTimeChanged(int newTime, int oldTime);
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
        propertiesPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        tweenPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        easingCbox = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        objectLbl = new javax.swing.JLabel();
        objectPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tween Studio");
        getContentPane().add(timelinePanel, java.awt.BorderLayout.CENTER);

        jPanel1.setBackground(theme.COLOR_GRIDPANEL_BACKGROUND);

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
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
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

        propertiesPanel.setOpaque(false);
        propertiesPanel.setLayout(new java.awt.CardLayout());

        jPanel3.setOpaque(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 164, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 276, Short.MAX_VALUE)
        );

        propertiesPanel.add(jPanel3, "nothingCard");

        jPanel4.setOpaque(false);

        tweenPanel.setBackground(theme.COLOR_GRIDPANEL_SECTION);
        aurelienribon.utils.swing.GroupBorder groupBorder1 = new aurelienribon.utils.swing.GroupBorder();
        groupBorder1.setTitle("Tween properties");
        tweenPanel.setBorder(groupBorder1);
        tweenPanel.setForeground(new java.awt.Color(255, 255, 255));
        tweenPanel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        tweenPanel.setOpaque(false);

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Easing:");

        easingCbox.setMaximumRowCount(12);
        easingCbox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Linear.INOUT", "----------", "Quad.IN", "Quad.OUT", "Quad.INOUT", "----------", "Cubic.IN", "Cubic.OUT", "Cubic.INOUT", "----------", "Quart.IN", "Quart.OUT", "Quart.INOUT", "----------", "Quint.IN", "Quint.OUT", "Quint.INOUT", "----------", "Circ.IN", "Circ.OUT", "Circ.INOUT", "----------", "Sine.IN", "Sine.OUT", "Sine.INOUT", "----------", "Expo.IN", "Expo.OUT", "Expo.INOUT", "----------", "Back.IN", "Back.OUT", "Back.INOUT", "----------", "Bounce.IN", "Bounce.OUT", "Bounce.INOUT", "----------", "Elastic.IN", "Elastic.OUT", "Elastic.INOUT" }));

        javax.swing.GroupLayout tweenPanelLayout = new javax.swing.GroupLayout(tweenPanel);
        tweenPanel.setLayout(tweenPanelLayout);
        tweenPanelLayout.setHorizontalGroup(
            tweenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tweenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(easingCbox, 0, 105, Short.MAX_VALUE)
                .addContainerGap())
        );
        tweenPanelLayout.setVerticalGroup(
            tweenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tweenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tweenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(easingCbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tweenPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(tweenPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(234, Short.MAX_VALUE))
        );

        propertiesPanel.add(jPanel4, "tweenCard");

        jPanel5.setOpaque(false);

        jPanel6.setBackground(theme.COLOR_GRIDPANEL_SECTION);
        aurelienribon.utils.swing.GroupBorder groupBorder2 = new aurelienribon.utils.swing.GroupBorder();
        groupBorder2.setTitle("Object properties");
        jPanel6.setBorder(groupBorder2);
        jPanel6.setForeground(new java.awt.Color(255, 255, 255));
        jPanel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jPanel6.setOpaque(false);

        objectLbl.setForeground(new java.awt.Color(255, 255, 255));
        objectLbl.setText("---");

        objectPanel.setOpaque(false);
        objectPanel.setLayout(new javax.swing.BoxLayout(objectPanel, javax.swing.BoxLayout.PAGE_AXIS));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(objectPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                    .addComponent(objectLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(objectLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(objectPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(222, Short.MAX_VALUE))
        );

        propertiesPanel.add(jPanel5, "objectCard");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(propertiesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(propertiesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox easingCbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JLabel objectLbl;
    private javax.swing.JPanel objectPanel;
    private javax.swing.JPanel propertiesPanel;
    private aurelienribon.tweenstudio.ui.timeline.TimelinePanel timelinePanel;
    private javax.swing.JPanel tweenPanel;
    // End of variables declaration//GEN-END:variables
}
