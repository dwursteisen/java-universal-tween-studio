package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenstudio.Property.Field;
import aurelienribon.tweenstudio.ui.timeline.Theme;
import aurelienribon.tweenstudio.ui.timeline.TimelineHelper;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import aurelienribon.tweenstudio.ui.timeline.TimelinePanel;
import aurelienribon.tweenstudio.ui.timeline.TimelinePanel.Listener;
import aurelienribon.utils.io.FileUtils;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
	// -------------------------------------------------------------------------
	// Attributes
	// -------------------------------------------------------------------------

	private final Theme theme = new Theme();
	private final Map<Object, InitialState> currentInitialStatesMap = new HashMap<Object, InitialState>();
	private final float[] buffer = new float[Tween.MAX_COMBINED_TWEENS];

	private Timeline currentTimeline;
	private Timeline workingTimeline;
	private int playTime;
	private int playDuration;

	// -------------------------------------------------------------------------
	// Ctor
	// -------------------------------------------------------------------------

	public MainWindow(final Callback callback) {		
		initComponents();
		timelinePanel.setTheme(theme);
		timelinePanel.addListener(timelinePanelListener);
		end();

		easingCbox.addItemListener(new ItemListener() {
			@Override public void itemStateChanged(ItemEvent e) {
				String name = (String) easingCbox.getSelectedItem();
				TweenEquation equation = TweenEquation.parse(name);
				if (equation != null) {
					for (Node node : timelinePanel.getSelectedNodes()) {
						NodeData nodeData = (NodeData) node.getUserData();
						nodeData.setEquation(equation);
						recreateTimeline();
					}
				}
			}
		});

		saveAndStopBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				try {
					TimelineCreationHelper.copy(workingTimeline, currentTimeline);
					String str = ImportExportHelper.timelineToString(currentTimeline, TweenStudio.getTargetsNamesMap());
					FileUtils.writeStringToFile(str, TweenStudio.getCurrentAnimationFile());
					end();
					callback.editionComplete();
					currentTimeline.start();
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(MainWindow.this, "Sorry, can't write on animation file...");
				}
			}
		});
	}

	private final TimelinePanel.Listener timelinePanelListener = new Listener() {
		@Override public void playRequested() {
			playDuration = workingTimeline.getFullDuration();
			playTime = 0;
			timelinePanel.setPlaying(true);
		}

		@Override public void pauseRequested() {
			timelinePanel.setPlaying(false);
		}

		@Override public void currentTimeChanged(int newTime, int oldTime) {
			workingTimeline.update(newTime-oldTime);
			if (objectCard.isVisible()) updateObjectCard();
		}

		@Override public void selectedElementChanged(Element newElem, Element oldElem) {
			CardLayout cl = (CardLayout) propertiesPanel.getLayout();
			if (newElem != null) {
				cl.show(propertiesPanel, "objectCard");
				buildObjectCard();
				updateObjectCard();

				ElementData elemData = (ElementData) newElem.getUserData();
				TweenStudio.getCurrentEditor().selectedObjectChanged(elemData.getTarget());
			} else {
				cl.show(propertiesPanel, "nothingCard");
				TweenStudio.getCurrentEditor().selectedObjectChanged(null);
			}
		}

		@Override public void mouseOverElementChanged(Element newElem, Element oldElem) {
			if (newElem != null) {
				ElementData elemData = (ElementData) newElem.getUserData();
				TweenStudio.getCurrentEditor().mouseOverObjectChanged(elemData.getTarget());
			} else {
				TweenStudio.getCurrentEditor().mouseOverObjectChanged(null);
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
	};

	// -------------------------------------------------------------------------
	// Callback
	// -------------------------------------------------------------------------

	public interface Callback {
		public void editionComplete();
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public void initialize(Timeline timeline) {
		begin();
		animationNameField.setText(TweenStudio.getCurrentAnimationName());
		currentTimeline = timeline;

		createInitialStates();
		TimelineModel model = createModel();
		ImportExportHelper.timelineToModel(currentTimeline, model, TweenStudio.getTargetsNamesMap(), TweenStudio.getCurrentEditor());
		timelinePanel.setModel(model);
		recreateTimeline();

		model.addListener(new TimelineModel.Listener() {
			@Override public void stateChanged() {
				if (objectCard.isVisible()) updateObjectCard();
				else if (tweenCard.isVisible()) updateTweenCard();
			}
		});
	}

	public void update(int deltaMillis) {
		if (timelinePanel.isPlaying()) {
			playTime += deltaMillis;
			if (playTime <= playDuration) {
				setCurrentTime(playTime);
			} else {
				setCurrentTime(playDuration);
				timelinePanel.setPlaying(false);
			}
		}
	}

	public int getCurrentTime() {
		return timelinePanel.getCurrentTime();
	}

	public void setCurrentTime(int time) {
		timelinePanel.setCurrentTime(time);
	}

	public void selectedObjectChanged(Object obj) {
		for (Element elem : timelinePanel.getModel().getRoot().getChildren()) {
			ElementData elemData = (ElementData) elem.getUserData();
			if (elemData.getTarget() == obj) {
				timelinePanel.setSelectedElement(elem);
				return;
			}
		}

		timelinePanel.setSelectedElement(null);
	}

	public void mouseOverObjectChanged(Object obj) {
		for (Element elem : timelinePanel.getModel().getRoot().getChildren()) {
			ElementData elemData = (ElementData) elem.getUserData();
			if (elemData.getTarget() == obj) {
				timelinePanel.setMouseOverElement(elem);
				return;
			}
		}

		timelinePanel.setMouseOverElement(null);
	}

	public void targetStateChanged(Object target, String name, Set<Integer> tweenTypes) {
		for (int tweenType : tweenTypes) {
			String propertyName = TweenStudio.getCurrentEditor().getProperty(target.getClass(), tweenType).getName();
			Element elem = timelinePanel.getModel().getElement(name + "/" + propertyName);
			Node node = TimelineHelper.getNodeOrCreate(elem, getCurrentTime());
			NodeData nodeData = (NodeData) node.getUserData();

			TweenAccessor accessor = Tween.getRegisteredAccessor(target.getClass());
			accessor.getValues(target, tweenType, buffer);
			nodeData.setTargets(buffer);
		}

		recreateTimeline();
	}

	// -------------------------------------------------------------------------
	// Helpers -- timeline creation
	// -------------------------------------------------------------------------

	private void recreateTimeline() {
		if (workingTimeline != null) workingTimeline.free();

		workingTimeline = TimelineCreationHelper.createTimelineFromModel(
			timelinePanel.getModel(),
			getCurrentTime(),
			currentInitialStatesMap);

		if (objectCard.isVisible()) updateObjectCard();
	}

	// -------------------------------------------------------------------------
	// Helpers -- initialization
	// -------------------------------------------------------------------------

	private void createInitialStates() {
		currentInitialStatesMap.clear();
		for (Object target : TweenStudio.getRegisteredTargets()) {
			InitialState state = new InitialState(TweenStudio.getCurrentEditor(), target);
			currentInitialStatesMap.put(target, state);
		}
	}

	private TimelineModel createModel() {
		TimelineModel model = new TimelineModel();

		model.addListener(new TimelineModel.Listener() {
			@Override public void stateChanged() {recreateTimeline();}
		});

		for (Object target : TweenStudio.getRegisteredTargets()) {
			List<Property> properties = TweenStudio.getCurrentEditor().getProperties(target.getClass());
			Element elem = model.addElement(TweenStudio.getRegisteredName(target));
			elem.setSelectable(false);
			elem.setUserData(new ElementData(target, null));

			for (Property property : properties) {
				elem = model.addElement(TweenStudio.getRegisteredName(target) + "/" + property.getName());
				elem.setUserData(new ElementData(target, property));
			}
		}

		return model;
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void begin() {
		setCurrentTime(0);
		saveAndStopBtn.setEnabled(true);
		timelinePanel.setSelectedElement(null);
		timelinePanel.clearSelectedNodes();
	}

	private void end() {
		setCurrentTime(0);
		timelinePanel.setSelectedElement(null);
		timelinePanel.clearSelectedNodes();
		timelinePanel.setModel(new TimelineModel());
		animationNameField.setText("<nothing loaded>");
		saveAndStopBtn.setEnabled(false);
	}

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

		for (Property property : TweenStudio.getCurrentEditor().getProperties(elemData.getTarget().getClass())) {
			float[] values = new float[property.getFields().length];
			TweenAccessor accessor = Tween.getRegisteredAccessor(elemData.getTarget().getClass());
			accessor.getValues(elemData.getTarget(), property.getId(), values);

			for (int i=0; i<property.getFields().length; i++) {
				JPanel panel = (JPanel) objectPanel.getComponent(cnt);
				JSpinner spinner = (JSpinner) panel.getComponent(1);
				ValueChangeListener listener = (ValueChangeListener) spinner.getChangeListeners()[0];
				listener.setEnabled(false);
				spinner.setValue(new Double(values[i]));
				listener.setEnabled(true);
				cnt += 1;
			}
		}
	}

	private void buildObjectCard() {
		Element objectElem = timelinePanel.getSelectedElement();

		objectField.setText(objectElem.getName());
		objectPanel.removeAll();

		for (Element propertyElem : objectElem.getChildren()) {
			ElementData propElemData = (ElementData) propertyElem.getUserData();
			Property property = propElemData.getProperty();

			for (int i=0; i<property.getFields().length; i++) {
				Field field = property.getFields()[i];
				
				JLabel label = new JLabel(field.name + ": ");
				label.setForeground(Color.WHITE);
				label.setHorizontalAlignment(JLabel.RIGHT);

				SpinnerNumberModel model = new SpinnerNumberModel(field.min, field.min, field.max, field.step);
				JSpinner spinner = new JSpinner(model);
				spinner.addChangeListener(new ValueChangeListener(propertyElem, i));
				spinner.setMinimumSize(new Dimension(70, 20));
				spinner.setPreferredSize(new Dimension(70, 20));
				spinner.setMaximumSize(new Dimension(70, 20));

				JPanel panel = new JPanel(new BorderLayout());
				panel.setBorder(new EmptyBorder(0, 0, 2, 0));
				panel.setOpaque(false);
				panel.add(label, BorderLayout.CENTER);
				panel.add(spinner, BorderLayout.EAST);

				objectPanel.add(panel);
			}
		}

		objectPanel.revalidate();
	}

	private class ValueChangeListener implements ChangeListener {
		private final Element propertyElem;
		private final int fieldIdx;
		private boolean isEnabled = true;

		public ValueChangeListener(Element propertyElem, int fieldIdx) {
			this.propertyElem = propertyElem;
			this.fieldIdx = fieldIdx;
		}

		public void setEnabled(boolean isEnabled) {
			this.isEnabled = isEnabled;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			if (!isEnabled) return;

			JSpinner spinner = (JSpinner) e.getSource();
			float value = ((Number)spinner.getValue()).floatValue();

			Node node = TimelineHelper.getNodeOrCreate(propertyElem, getCurrentTime());
			NodeData nodeData = (NodeData) node.getUserData();
			nodeData.getTargets()[fieldIdx] = value;

			recreateTimeline();
		}
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
        animationNameField = new javax.swing.JTextField();
        saveAndStopBtn = new javax.swing.JButton();
        propertiesPanel = new javax.swing.JPanel();
        nothingCard = new javax.swing.JPanel();
        tweenCard = new javax.swing.JPanel();
        tweenPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        easingCbox = new javax.swing.JComboBox();
        objectCard = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        objectPanel = new javax.swing.JPanel();
        objectField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tween Studio");
        getContentPane().add(timelinePanel, java.awt.BorderLayout.CENTER);

        jPanel1.setBackground(theme.COLOR_GRIDPANEL_BACKGROUND);

        jPanel2.setOpaque(false);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/aurelienribon/tweenstudio/gfx/logo.png"))); // NOI18N

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("<html> <p align=\"center\">v0.3 - 2012 - Aurelien Ribon<br/><font color=\"#6eccff\">www.aurelienribon.com</font></p>");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
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

        jPanel3.setBackground(theme.COLOR_GRIDPANEL_SECTION);
        jPanel3.setBorder(new aurelienribon.utils.swing.GroupBorder());
        jPanel3.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.setOpaque(false);

        animationNameField.setEditable(false);
        animationNameField.setText("---");

        saveAndStopBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/aurelienribon/tweenstudio/gfx/ic_save.png"))); // NOI18N
        saveAndStopBtn.setText("Save and close animation");
        saveAndStopBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        saveAndStopBtn.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        saveAndStopBtn.setMargin(new java.awt.Insets(2, 3, 2, 3));
        saveAndStopBtn.setOpaque(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saveAndStopBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .addComponent(animationNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(animationNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveAndStopBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        propertiesPanel.setOpaque(false);
        propertiesPanel.setLayout(new java.awt.CardLayout());

        nothingCard.setOpaque(false);

        javax.swing.GroupLayout nothingCardLayout = new javax.swing.GroupLayout(nothingCard);
        nothingCard.setLayout(nothingCardLayout);
        nothingCardLayout.setHorizontalGroup(
            nothingCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        nothingCardLayout.setVerticalGroup(
            nothingCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 209, Short.MAX_VALUE)
        );

        propertiesPanel.add(nothingCard, "nothingCard");

        tweenCard.setOpaque(false);

        tweenPanel.setBackground(theme.COLOR_GRIDPANEL_SECTION);
        aurelienribon.utils.swing.GroupBorder groupBorder1 = new aurelienribon.utils.swing.GroupBorder();
        groupBorder1.setTitle("Tween properties");
        tweenPanel.setBorder(groupBorder1);
        tweenPanel.setForeground(new java.awt.Color(255, 255, 255));
        tweenPanel.setFont(new java.awt.Font("Tahoma", 1, 11));
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
                .addComponent(easingCbox, 0, 121, Short.MAX_VALUE)
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

        javax.swing.GroupLayout tweenCardLayout = new javax.swing.GroupLayout(tweenCard);
        tweenCard.setLayout(tweenCardLayout);
        tweenCardLayout.setHorizontalGroup(
            tweenCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tweenPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tweenCardLayout.setVerticalGroup(
            tweenCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tweenCardLayout.createSequentialGroup()
                .addComponent(tweenPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(167, Short.MAX_VALUE))
        );

        propertiesPanel.add(tweenCard, "tweenCard");

        objectCard.setOpaque(false);

        jPanel6.setBackground(theme.COLOR_GRIDPANEL_SECTION);
        aurelienribon.utils.swing.GroupBorder groupBorder2 = new aurelienribon.utils.swing.GroupBorder();
        groupBorder2.setTitle("Object properties");
        jPanel6.setBorder(groupBorder2);
        jPanel6.setForeground(new java.awt.Color(255, 255, 255));
        jPanel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jPanel6.setOpaque(false);

        objectPanel.setOpaque(false);
        objectPanel.setLayout(new javax.swing.BoxLayout(objectPanel, javax.swing.BoxLayout.PAGE_AXIS));

        objectField.setEditable(false);
        objectField.setText("---");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(objectField, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .addComponent(objectPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(objectField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(objectPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout objectCardLayout = new javax.swing.GroupLayout(objectCard);
        objectCard.setLayout(objectCardLayout);
        objectCardLayout.setHorizontalGroup(
            objectCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        objectCardLayout.setVerticalGroup(
            objectCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(objectCardLayout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(132, Short.MAX_VALUE))
        );

        propertiesPanel.add(objectCard, "objectCard");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(propertiesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(propertiesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField animationNameField;
    private javax.swing.JComboBox easingCbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel nothingCard;
    private javax.swing.JPanel objectCard;
    private javax.swing.JTextField objectField;
    private javax.swing.JPanel objectPanel;
    private javax.swing.JPanel propertiesPanel;
    private javax.swing.JButton saveAndStopBtn;
    private aurelienribon.tweenstudio.ui.timeline.TimelinePanel timelinePanel;
    private javax.swing.JPanel tweenCard;
    private javax.swing.JPanel tweenPanel;
    // End of variables declaration//GEN-END:variables
}
